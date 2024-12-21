package com.edio.studywithcard.card.service;

import com.edio.common.exception.ConflictException;
import com.edio.common.exception.IllegalArgumentException;
import com.edio.common.exception.InternalServerException;
import com.edio.common.exception.NotFoundException;
import com.edio.studywithcard.attachment.domain.Attachment;
import com.edio.studywithcard.attachment.domain.enums.AttachmentFolder;
import com.edio.studywithcard.attachment.domain.enums.FileTarget;
import com.edio.studywithcard.attachment.service.AttachmentService;
import com.edio.studywithcard.card.domain.Card;
import com.edio.studywithcard.card.model.request.CardCreateRequest;
import com.edio.studywithcard.card.model.response.CardResponse;
import com.edio.studywithcard.card.repository.CardRepository;
import com.edio.studywithcard.deck.domain.Deck;
import com.edio.studywithcard.deck.repository.DeckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
                    .orElseThrow(() -> new NotFoundException(Deck.class, request.deckId()));

            // 1. Deck 생성 및 저장
            Card card = Card.builder()
                    .deck(deck)
                    .name(request.name())
                    .description(request.description())
                    .build();
            Card savedCard = cardRepository.save(card);

            // 2. 첨부파일 처리
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    String contentType = file.getContentType();

                    if (contentType != null) {
                        if (contentType.startsWith("image/")) {
                            // 이미지 파일 처리
                            Attachment attachment = attachmentService.saveAttachment(file, AttachmentFolder.IMAGE.name(), FileTarget.CARD.name());
                            attachmentService.saveAttachmentCardTarget(attachment, savedCard);
                        } else if (contentType.startsWith("audio/")) {
                            // 오디오 파일 처리
                            Attachment attachment = attachmentService.saveAttachment(file, AttachmentFolder.AUDIO.name(), FileTarget.CARD.name());
                            attachmentService.saveAttachmentCardTarget(attachment, savedCard);
                        } else {
                            throw new IllegalArgumentException("Unsupported file type: " + contentType);
                        }
                    } else {
                        throw new IllegalArgumentException("File content type is null");
                    }
                }
            }
            return CardResponse.from(savedCard);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(Deck.class, request.name());
        } catch (IOException e) {
            throw new InternalServerException(e.getMessage());
        }
    }
}
