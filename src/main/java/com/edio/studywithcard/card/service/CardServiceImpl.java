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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
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
    public void createOrUpdateCards(CardBulkRequestWrapper cardBulkRequestWrapper) {

        for (CardBulkRequest request : cardBulkRequestWrapper.getRequests()) {
            log.info("[Test] bulkRequest : {}", request);
            if (request.getCardId() == null) { // 등록
                try {
                    // Deck 조회
                    Deck deck = deckRepository.findById(request.getDeckId())
                            .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.NOT_FOUND_ENTITY.format(Deck.class.getSimpleName(), request.getDeckId())));

                    // 1. Card 생성 및 저장
                    Card card = Card.builder()
                            .deck(deck)
                            .name(request.getName())
                            .description(request.getDescription())
                            .build();
                    cardRepository.save(card);

                    // 2. 첨부파일 처리
                    for (MultipartFile file : Optional.ofNullable(request.getFiles()).orElse(new MultipartFile[0])) {
                        if (file != null && !file.isEmpty()) {
                            processAttachment(file, card);
                        }
                    }
                } catch (IOException e) {
                    log.error(e.getMessage());
                    throw new IllegalStateException(ErrorMessages.FILE_PROCESSING_ERROR.getMessage()); // 422
                }
            } else { // 수정
                Card existingCard = cardRepository.findByIdAndIsDeletedFalse(request.getCardId())
                        .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.NOT_FOUND_ENTITY.format(Card.class.getSimpleName(), request.getCardId())));

                // 카드 이름
                if (StringUtils.hasText(request.getName())) {
                    existingCard.setName(request.getName());
                }
                // 카드 설명
                if (StringUtils.hasText(request.getDescription())) {
                    existingCard.setDescription(request.getDescription());
                }

                // 기존 첨부파일 삭제(Bulk 작업)
                List<String> fileKeys = existingCard.getAttachmentCardTargets().stream()
                        .map(AttachmentCardTarget::getAttachment)
                        .filter(attachment -> !attachment.isDeleted())
                        .map(Attachment::getFileKey)
                        .collect(Collectors.toList());

                if (!fileKeys.isEmpty()) {
                    attachmentService.deleteAllAttachments(fileKeys);
                }

                // 업데이트 첨부파일 저장
                for (MultipartFile file : Optional.ofNullable(request.getFiles()).orElse(new MultipartFile[0])) {
                    if (file != null && !file.isEmpty()) {
                        try {
                            processAttachment(file, existingCard);
                        } catch (IOException e) {
                            log.error(e.getMessage());
                            throw new IllegalStateException(ErrorMessages.FILE_PROCESSING_ERROR.getMessage()); // 422
                        }
                    }
                }
            }
        }
    }

    /*
       카드 삭제
   */
    @Override
    @Transactional
    public void deleteCards(List<Long> request) {
        for (long cardId : request) {
            Card existingCard = cardRepository.findByIdAndIsDeletedFalse(cardId)
                    .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.NOT_FOUND_ENTITY.format(Card.class.getSimpleName(), cardId)));

            // Bulk 작업
            List<String> fileKeys = existingCard.getAttachmentCardTargets().stream()
                    .map(AttachmentCardTarget::getAttachment)
                    .filter(attachment -> !attachment.isDeleted())
                    .map(Attachment::getFileKey)
                    .collect(Collectors.toList());

            if (!fileKeys.isEmpty()) {
                attachmentService.deleteAllAttachments(fileKeys);
            }

            existingCard.setDeleted(true);
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
}
