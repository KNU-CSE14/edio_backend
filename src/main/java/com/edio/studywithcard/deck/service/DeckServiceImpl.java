package com.edio.studywithcard.deck.service;

import com.edio.common.exception.ConflictException;
import com.edio.common.exception.InternalServerException;
import com.edio.common.exception.NotFoundException;
import com.edio.studywithcard.attachment.domain.Attachment;
import com.edio.studywithcard.attachment.domain.AttachmentDeckTarget;
import com.edio.studywithcard.attachment.domain.enums.AttachmentFolder;
import com.edio.studywithcard.attachment.repository.AttachmentDeckTargetRepository;
import com.edio.studywithcard.attachment.service.AttachmentService;
import com.edio.studywithcard.category.domain.Category;
import com.edio.studywithcard.category.repository.CategoryRepository;
import com.edio.studywithcard.deck.domain.Deck;
import com.edio.studywithcard.deck.model.request.DeckCreateRequest;
import com.edio.studywithcard.deck.model.response.DeckResponse;
import com.edio.studywithcard.deck.repository.DeckRepository;
import com.edio.studywithcard.folder.domain.Folder;
import com.edio.studywithcard.folder.repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeckServiceImpl implements DeckService {

    private final DeckRepository deckRepository;

    private final FolderRepository folderRepository;

    private final CategoryRepository categoryRepository;

    private final AttachmentService attachmentService;

    private final AttachmentDeckTargetRepository attachmentDeckTargetRepository;

    @Override
    @Transactional
    public DeckResponse createDeck(DeckCreateRequest request, MultipartFile file) {
        try {
            // Folder와 Category를 조회
            Folder folder = folderRepository.findById(request.folderId())
                    .orElseThrow(() -> new NotFoundException(Folder.class, request.folderId()));
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new NotFoundException(Category.class, request.categoryId()));

            // 1. Deck 생성 및 저장
            Deck deck = Deck.builder()
                    .folder(folder)
                    .category(category)
                    .name(request.name())
                    .description(request.description())
                    .isShared(request.isShared())
                    .build();
            Deck savedDeck = deckRepository.save(deck);

            // 2. 첨부파일 처리
            if (file != null && !file.isEmpty()) {
                // Attachment 저장
                Attachment attachment = attachmentService.saveAttachment(file, String.valueOf(AttachmentFolder.image));

                // AttachmentDeckTarget 저장
                AttachmentDeckTarget attachmentDeckTarget = AttachmentDeckTarget.builder()
                        .attachment(attachment)
                        .deck(deck)
                        .build();
                attachmentDeckTargetRepository.save(attachmentDeckTarget);
            }
            return DeckResponse.from(savedDeck);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(Deck.class, request.name());
        } catch (IOException e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    /*
        Deck 이동
     */
    @Override
    @Transactional
    public void moveDeck(Long deckId, Long newFolderId) {
        // 이동할 덱 조회
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new NotFoundException(Deck.class, deckId));

        // 새로운 폴더 조회
        Folder newFolder = null;
        if (newFolderId != null) {
            newFolder = folderRepository.getReferenceById(newFolderId);
        }

        // 덱의 폴더 변경
        deck.setFolder(newFolder);
    }
}
