package com.edio.studywithcard.card.service;

import com.edio.common.exception.base.ErrorMessages;
import com.edio.studywithcard.attachment.domain.Attachment;
import com.edio.studywithcard.attachment.domain.AttachmentCardTarget;
import com.edio.studywithcard.attachment.domain.enums.AttachmentFolder;
import com.edio.studywithcard.attachment.domain.enums.FileTarget;
import com.edio.studywithcard.attachment.service.AttachmentService;
import com.edio.studywithcard.card.domain.Card;
import com.edio.studywithcard.card.dto.AttachmentBulkData;
import com.edio.studywithcard.card.model.request.CardBulkRequest;
import com.edio.studywithcard.card.model.request.CardBulkRequestWrapper;
import com.edio.studywithcard.card.repository.CardRepository;
import com.edio.studywithcard.deck.domain.Deck;
import com.edio.studywithcard.deck.repository.DeckRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;

    private final DeckRepository deckRepository;

    private final AttachmentService attachmentService;

    @Override
    @Transactional
    public void upsert(Long accountId, CardBulkRequestWrapper cardBulkRequestWrapper) {
        List<Card> newCards = new ArrayList<>();
        List<AttachmentBulkData> newAttachments = new ArrayList<>();

        // 소유권 검증 수행(JQPL)
        // 첫 요청에서 Deck ID를 가져옴
        Long deckId = cardBulkRequestWrapper.getRequests().get(0).getDeckId();
        Long ownerId = deckRepository.findAccountIdByDeckId(deckId);
        validateOwnership(accountId, ownerId);

        for (CardBulkRequest request : cardBulkRequestWrapper.getRequests()) {
            log.info("bulkRequest : {}", request.toString());
            if (request.getCardId() == null) {
                // 신규 카드: 객체만 생성하여 리스트에 추가
                Card card = createCard(request);
                newCards.add(card);

                // 이미지 첨부파일이 존재하면 벌크 데이터에 추가
                if (request.getImage() != null && !request.getImage().isEmpty()) {
                    newAttachments.add(new AttachmentBulkData(
                            request.getImage(), card,
                            AttachmentFolder.IMAGE.name(),
                            FileTarget.CARD.name()
                    ));
                }
                // 오디오 첨부파일이 존재하면 벌크 데이터에 추가
                if (request.getAudio() != null && !request.getAudio().isEmpty()) {
                    newAttachments.add(new AttachmentBulkData(
                            request.getAudio(), card,
                            AttachmentFolder.AUDIO.name(),
                            FileTarget.CARD.name()
                    ));
                }
            } else {
                // 기존 카드 업데이트는 Dirty Checking을 활용
                processCardUpdate(request);
            }
        }

        // 신규 카드가 있다면 한 번에 배치 저장
        if (!newCards.isEmpty()) {
            cardRepository.saveAll(newCards);
        }

        // 첨부파일이 있다면 벌크 처리
        if (!newAttachments.isEmpty()) {
            attachmentService.saveAllAttachments(newAttachments);
        }
    }

    /*
       카드 삭제
   */
    @Override
    @Transactional
    public void deleteCards(Long accountId, Long deckId, List<Long> cardIds) {
        // 소유권 검증 수행(JQPL)
        Long ownerId = deckRepository.findAccountIdByDeckId(deckId);
        validateOwnership(accountId, ownerId);

        List<Card> existingCards = cardRepository.findAllById(cardIds).stream()
                .filter(card -> !card.isDeleted())
                .toList();

        if (existingCards.isEmpty()) {
            throw new EntityNotFoundException(Card.class.getSimpleName(), null);
        }

        List<String> fileKeys = existingCards.stream()
                .flatMap(card -> card.getAttachmentCardTargets().stream())
                .map(AttachmentCardTarget::getAttachment)
                .filter(attachment -> !attachment.isDeleted())
                .map(Attachment::getFileKey)
                .collect(Collectors.toList());

        if (!fileKeys.isEmpty()) {
            attachmentService.deleteAllAttachments(fileKeys);
        }

        existingCards.forEach(card -> card.setDeleted(true));
    }

    // 카드 객체 생성
    private Card createCard(CardBulkRequest request) {
        Deck deck = deckRepository.findById(request.getDeckId())
                .orElseThrow(() -> new EntityNotFoundException(
                        ErrorMessages.NOT_FOUND_ENTITY.format(Deck.class.getSimpleName(), request.getDeckId())
                ));

        // Card 객체 생성 (아직 DB에 저장하지 않음)
        return Card.builder()
                .deck(deck)
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }

    // 카드 수정
    private void processCardUpdate(CardBulkRequest request) {
        Card existingCard = cardRepository.findByIdAndIsDeletedFalse(request.getCardId())
                .orElseThrow(() -> new EntityNotFoundException(
                        ErrorMessages.NOT_FOUND_ENTITY.format(Card.class.getSimpleName(), request.getCardId())
                ));

        if (StringUtils.hasText(request.getName())) {
            existingCard.setName(request.getName());
        }
        if (StringUtils.hasText(request.getDescription())) {
            existingCard.setDescription(request.getDescription());
        }

        processUpdatedAttachment(request.getImage(), "image", existingCard);
        processUpdatedAttachment(request.getAudio(), "audio", existingCard);
    }

    // 수정 권한 검증
    private void validateOwnership(Long accountId, Long ownerId) {
        if (!accountId.equals(ownerId)) {
            throw new AccessDeniedException(ErrorMessages.FORBIDDEN_NOT_OWNER.getMessage());
        }
    }

    /*
        파일 처리 메서드
     */
    private void processAttachment(MultipartFile file, Card card) throws IOException {
        String contentType = file.getContentType();

        if (contentType != null) {
            Attachment attachment;
            if (contentType.startsWith("image/")) {
                // 이미지 파일 처리
                attachment = attachmentService.saveAttachment(file, AttachmentFolder.IMAGE.name(), FileTarget.CARD.name());
            } else if (contentType.startsWith("audio/")) {
                // 오디오 파일 처리
                attachment = attachmentService.saveAttachment(file, AttachmentFolder.AUDIO.name(), FileTarget.CARD.name());
            } else {
                throw new UnsupportedMediaTypeStatusException(ErrorMessages.FILE_PROCESSING_UNSUPPORTED.getMessage());
            }
            // Card Target 저장
            attachmentService.saveAttachmentCardTarget(attachment, card);
        } else {
            throw new IllegalStateException(ErrorMessages.FILE_PROCESSING_UNSUPPORTED.getMessage());
        }
    }

    // 기존 첨부파일 수정 처리 (수정 시)
    private void processUpdatedAttachment(MultipartFile file, String fileType, Card card) {
        if (file == null) return; // 요청에 필드 자체가 없으면 무시

        String fileKey = card.getAttachmentCardTargets().stream()
                .map(AttachmentCardTarget::getAttachment)
                .filter(attachment -> !attachment.isDeleted() && attachment.getFileType().contains(fileType))
                .map(Attachment::getFileKey)
                .findFirst()
                .orElse(null);

        // 삭제 및 추가
        if(fileKey != null) {
            attachmentService.deleteAttachment(fileKey);
        }
        if(!file.isEmpty()) {
            try {
                processAttachment(file, card);
            } catch (IOException e) {
                log.error(e.getMessage());
                throw new IllegalStateException(ErrorMessages.FILE_PROCESSING_ERROR.getMessage()); // 422
            }
        }
    }
}
