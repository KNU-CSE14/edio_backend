package com.edio.studywithcard.card.service;

import com.edio.common.exception.base.ErrorMessages;
import com.edio.studywithcard.attachment.domain.Attachment;
import com.edio.studywithcard.attachment.domain.AttachmentCardTarget;
import com.edio.studywithcard.attachment.domain.enums.AttachmentFolder;
import com.edio.studywithcard.attachment.domain.enums.FileTarget;
import com.edio.studywithcard.attachment.service.AttachmentService;
import com.edio.studywithcard.card.domain.Card;
import com.edio.studywithcard.card.model.request.CardCreateRequest;
import com.edio.studywithcard.card.model.request.CardDeleteRequest;
import com.edio.studywithcard.card.model.request.CardUpdateRequest;
import com.edio.studywithcard.card.model.response.CardResponse;
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
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;

    private final DeckRepository deckRepository;

    private final AttachmentService attachmentService;

    /*
        카드 생성
     */
    @Override
    @Transactional
    public CardResponse createCard(CardCreateRequest request, MultipartFile[] files) {
        try {
            // Deck 조회
            Deck deck = deckRepository.findById(request.deckId())
                    .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.NOT_FOUND_ENTITY.format(Deck.class.getSimpleName(), request.deckId())));

            // 1. Card 생성 및 저장
            Card card = Card.builder()
                    .deck(deck)
                    .name(request.name())
                    .description(request.description())
                    .build();
            cardRepository.save(card);

            // 2. 첨부파일 처리
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    processAttachment(file, card);
                }
            }
            return CardResponse.from(card);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new IllegalStateException(ErrorMessages.FILE_PROCESSING_ERROR.getMessage()); // 422
        }
    }

    /*
        카드 수정
     */
    @Override
    @Transactional
    public void updateCard(CardUpdateRequest request, MultipartFile[] files) {
        Card existingCard = cardRepository.findByIdAndIsDeletedFalse(request.id())
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.NOT_FOUND_ENTITY.format(Card.class.getSimpleName(), request.id())));

        // 카드 이름
        if (StringUtils.hasText(request.name())) {
            existingCard.setName(request.name());
        }
        // 카드 설명
        if (StringUtils.hasText(request.description())) {
            existingCard.setDescription(request.description());
        }

        // 첨부파일 수정
        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                try {
                    // 기존 첨부파일 삭제(Bulk 작업)
                    List<String> fileKeys = existingCard.getAttachmentCardTargets().stream()
                            .map(AttachmentCardTarget::getAttachment)
                            .filter(attachment -> !attachment.isDeleted())
                            .map(Attachment::getFileKey)
                            .collect(Collectors.toList());

                    if (!fileKeys.isEmpty()) {
                        attachmentService.deleteAllAttachments(fileKeys);
                    }

                    processAttachment(file, existingCard);
                } catch (IOException e) {
                    log.error(e.getMessage());
                    throw new IllegalStateException(ErrorMessages.FILE_PROCESSING_ERROR.getMessage()); // 422
                }
            }
        }
    }

    /*
        카드 삭제
    */
    @Override
    @Transactional
    public void deleteCard(CardDeleteRequest request) {
        Card existingCard = cardRepository.findByIdAndIsDeletedFalse(request.id())
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.NOT_FOUND_ENTITY.format(Card.class.getSimpleName(), request.id())));

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
