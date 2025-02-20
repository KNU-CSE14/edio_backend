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
import java.util.Objects;
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
        List<AttachmentBulkData> newAttachments = new ArrayList<>();
        List<AttachmentBulkData> updateAttachments = new ArrayList<>();

        // 소유권 검증
        Long deckId = cardBulkRequestWrapper.getRequests().get(0).getDeckId();
        Long ownerId = deckRepository.findAccountIdByDeckId(deckId);
        validateOwnership(accountId, ownerId);

        // 각 요청에 대해 처리
        for (CardBulkRequest request : cardBulkRequestWrapper.getRequests()) {
            log.info("bulkRequest : {}", request);
            if (request.getCardId() == null) {
                processCardCreate(request, newCards, newAttachments);
            } else {
                processCardUpdate(request, updateAttachments);
            }
        }

        // 신규 카드 저장
        if (!newCards.isEmpty()) {
            cardRepository.saveAll(newCards);
        }
        // 신규 첨부파일 저장
        if (!newAttachments.isEmpty()) {
            attachmentService.saveAllAttachments(newAttachments);
        }
        // 업데이트 첨부파일 처리: 기존 파일 및 신규 파일 처리
        processUpdateAttachments(updateAttachments);
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

    // 카드 생성
    private void processCardCreate(CardBulkRequest request, List<Card> newCards, List<AttachmentBulkData> newAttachments){
        // 신규 카드: 객체만 생성하여 리스트에 추가
        Card card = createCard(request);
        newCards.add(card);

        // 이미지 첨부파일이 존재하면 벌크 데이터에 추가
        if (!request.getImage().isEmpty()) {
            newAttachments.add(new AttachmentBulkData(
                    request.getImage(), card,
                    AttachmentFolder.IMAGE.name(),
                    FileTarget.CARD.name(),
                    null
            ));
        }
        // 오디오 첨부파일이 존재하면 벌크 데이터에 추가
        if (!request.getAudio().isEmpty()) {
            newAttachments.add(new AttachmentBulkData(
                    request.getAudio(), card,
                    AttachmentFolder.AUDIO.name(),
                    FileTarget.CARD.name(),
                    null
            ));
        }
    }

    // 카드 수정
    private void processCardUpdate(CardBulkRequest request, List<AttachmentBulkData> updateAttachments) {
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

        // 이미지 및 오디오 업데이트 처리 공통 로직 호출
        processAttachmentUpdate(request.getImage(), existingCard, AttachmentFolder.IMAGE.name(), FileTarget.CARD.name(), updateAttachments);
        processAttachmentUpdate(request.getAudio(), existingCard, AttachmentFolder.AUDIO.name(), FileTarget.CARD.name(), updateAttachments);
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

    // 첨부파일 업데이트 처리: 파일이 null이면 아무 작업도 안하고, 빈 파일이면 삭제 예약, 파일 있으면 삭제 후 업로드 예약
    private void processAttachmentUpdate(MultipartFile file, Card card, String folder, String target, List<AttachmentBulkData> updateAttachments) {
        if (file == null) return; // 필드 자체가 없으면 아무 작업도 하지 않음

        String oldFileKey = card.getAttachmentCardTargets().stream()
                .map(AttachmentCardTarget::getAttachment)
                .filter(attachment -> !attachment.isDeleted() && attachment.getFileType().contains(folder.toLowerCase()))
                .map(Attachment::getFileKey)
                .findFirst()
                .orElse(null);

        if (file.isEmpty()) {
            if (oldFileKey != null) {
                // 파일은 없으나 기존 파일 삭제만 수행
                updateAttachments.add(new AttachmentBulkData(null, card, folder, target, oldFileKey));
            }
        } else {
            updateAttachments.add(new AttachmentBulkData(file, card, folder, target, oldFileKey));
        }
    }

    // 업데이트 첨부파일 처리: 기존 파일 및 새 파일 처리
    private void processUpdateAttachments(List<AttachmentBulkData> updateAttachments) {
        if (updateAttachments.isEmpty()) return;

        // 삭제 대상 파일 키 수집 (null이 아닌 경우)
        List<String> oldFileKeys = updateAttachments.stream()
                .map(AttachmentBulkData::getOldFileKey)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (!oldFileKeys.isEmpty()) {
            attachmentService.deleteAllAttachments(oldFileKeys);
        }

        // 파일이 존재하는 항목만 새 파일 업로드 대상으로 처리
        List<AttachmentBulkData> attachmentsToUpload = updateAttachments.stream()
                .filter(data -> data.getFile() != null)
                .collect(Collectors.toList());
        if (!attachmentsToUpload.isEmpty()) {
            attachmentService.saveAllAttachments(attachmentsToUpload);
        }
    }

    // 수정 권한 검증
    private void validateOwnership(Long accountId, Long ownerId) {
        if (!accountId.equals(ownerId)) {
            throw new AccessDeniedException(ErrorMessages.FORBIDDEN_NOT_OWNER.getMessage());
        }
    }
}
