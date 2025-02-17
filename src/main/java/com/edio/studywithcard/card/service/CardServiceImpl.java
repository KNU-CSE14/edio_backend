package com.edio.studywithcard.card.service;

import com.edio.common.exception.base.ErrorMessages;
import com.edio.studywithcard.attachment.domain.Attachment;
import com.edio.studywithcard.attachment.domain.AttachmentCardTarget;
import com.edio.studywithcard.attachment.domain.enums.AttachmentFolder;
import com.edio.studywithcard.attachment.domain.enums.FileTarget;
import com.edio.studywithcard.attachment.service.AttachmentService;
import com.edio.studywithcard.card.domain.Card;
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

    /*
        카드 생성 or 수정
     */
    @Override
    @Transactional
    public void upsert(Long accountId, CardBulkRequestWrapper cardBulkRequestWrapper) {
        List<Card> newCards = new ArrayList<>();

        // 첫 요청에서 Deck ID를 가져옴
        Long deckId = cardBulkRequestWrapper.getRequests().get(0).getDeckId();
        Deck deck = deckRepository.findById(deckId).orElseThrow(() -> new EntityNotFoundException(ErrorMessages.NOT_FOUND_ENTITY.format(Deck.class.getSimpleName(), deckId)));

        // 소유권 검증 수행
        validateOwnership(accountId, deck);

        for (CardBulkRequest request : cardBulkRequestWrapper.getRequests()) {
            log.info("bulkRequest : {}", request.toString());
            if (request.getCardId() == null) {
                // 신규 카드: 객체만 생성하여 리스트에 추가
                try {
                    Card card = createCard(accountId, request);
                    newCards.add(card);

                    // 첨부파일은 개별 처리
                    processNewAttachment(request.getImage(), card);
                    processNewAttachment(request.getAudio(), card);
                } catch (IOException e) {
                    log.error(e.getMessage());
                    throw new IllegalStateException(ErrorMessages.FILE_PROCESSING_ERROR.getMessage());
                }
            } else {
                // 기존 카드 업데이트는 Dirty Checking을 활용
                processCardUpdate(accountId, request);
            }
        }

        // 신규 카드가 있다면 한 번에 배치 저장
        if (!newCards.isEmpty()) {
            cardRepository.saveAll(newCards);
        }
    }

    /*
       카드 삭제
   */
    @Override
    @Transactional
    public void deleteCards(List<Long> cardIds) {
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
    private Card createCard(Long accountId, CardBulkRequest request) {
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
    private void processCardUpdate(Long accountId, CardBulkRequest request) {
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
    private void validateOwnership(Long accountId, Deck deck) {
        if (!deck.getFolder().getAccountId().equals(accountId)) {
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

    // 신규 첨부파일 저장 (등록 시)
    private void processNewAttachment(MultipartFile file, Card card) throws IOException {
        if (file != null && !file.isEmpty()) {
            processAttachment(file, card);
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

        if (file.isEmpty()) {
            // 빈 파일이면 기존 파일 삭제
            if (fileKey != null) {
                attachmentService.deleteAttachment(fileKey);
            }
        } else {
            // 새 파일이 들어오면 기존 파일 삭제 후 저장
            if (fileKey != null) {
                attachmentService.deleteAttachment(fileKey);
            }
            try {
                processAttachment(file, card);
            } catch (IOException e) {
                log.error(e.getMessage());
                throw new IllegalStateException(ErrorMessages.FILE_PROCESSING_ERROR.getMessage()); // 422
            }
        }
    }
}
