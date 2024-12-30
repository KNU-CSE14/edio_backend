package com.edio.studywithcard.deck.service;

import com.edio.common.exception.custom.ConflictException;
import com.edio.common.exception.custom.InternalServerException;
import com.edio.common.exception.custom.NotFoundException;
import com.edio.studywithcard.attachment.domain.Attachment;
import com.edio.studywithcard.attachment.domain.AttachmentDeckTarget;
import com.edio.studywithcard.attachment.domain.enums.AttachmentFolder;
import com.edio.studywithcard.attachment.domain.enums.FileTarget;
import com.edio.studywithcard.attachment.service.AttachmentService;
import com.edio.studywithcard.category.domain.Category;
import com.edio.studywithcard.category.repository.CategoryRepository;
import com.edio.studywithcard.deck.domain.Deck;
import com.edio.studywithcard.deck.model.request.DeckCreateRequest;
import com.edio.studywithcard.deck.model.request.DeckDeleteRequest;
import com.edio.studywithcard.deck.model.request.DeckMoveRequest;
import com.edio.studywithcard.deck.model.request.DeckUpdateRequest;
import com.edio.studywithcard.deck.model.response.DeckResponse;
import com.edio.studywithcard.deck.repository.DeckRepository;
import com.edio.studywithcard.folder.domain.Folder;
import com.edio.studywithcard.folder.repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeckServiceImpl implements DeckService {

    private final DeckRepository deckRepository;

    private final FolderRepository folderRepository;

    private final CategoryRepository categoryRepository;

    private final AttachmentService attachmentService;

    /*
        덱 조회
     */
    @Override
    @Transactional(readOnly = true)
    public DeckResponse getDeck(Long id) {
        Deck deck = deckRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException(Deck.class, id));
        return DeckResponse.from(deck);
    }

    /*
        덱 생성
     */
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
                Attachment attachment = attachmentService.saveAttachment(file, AttachmentFolder.IMAGE.name(), FileTarget.DECK.name());

                // AttachmentDeckTarget 저장
                attachmentService.saveAttachmentDeckTarget(attachment, savedDeck);
            }

            return DeckResponse.from(savedDeck);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(Deck.class, request.name());
        } catch (IOException e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    /*
        덱 수정
     */
    @Override
    @Transactional
    public void updateDeck(DeckUpdateRequest request, MultipartFile file) {
        Deck existingDeck = deckRepository.findByIdAndIsDeletedFalse(request.id())
                .orElseThrow(() -> new NotFoundException(Deck.class, request.id()));

        // 카테고리
        if (request.categoryId() != null) {
            Category newCategory = categoryRepository.getReferenceById(request.categoryId());
            existingDeck.setCategory(newCategory);
        }
        // 덱 이름
        if (StringUtils.hasText(request.name())) {
            existingDeck.setName(request.name());
        }
        // 덱 설명
        if (StringUtils.hasText(request.description())) {
            existingDeck.setDescription(request.description());
        }
        // 덱 즐겨찾기 여부
        if (request.isFavorite() != null) {
            existingDeck.setFavorite(request.isFavorite());
        }

        // 첨부파일 수정
        if (file != null && !file.isEmpty()) {
            try {
                // 기존 첨부파일 삭제(Bulk)
                List<String> fileKeys = existingDeck.getAttachmentDeckTargets().stream()
                        .map(AttachmentDeckTarget::getAttachment)
                        .filter(attachment -> !attachment.isDeleted())
                        .map(Attachment::getFileKey)
                        .collect(Collectors.toList());

                if (!fileKeys.isEmpty()) {
                    attachmentService.deleteAllAttachments(fileKeys);
                }

                // 새 첨부파일 저장
                Attachment attachment = attachmentService.saveAttachment(file, AttachmentFolder.IMAGE.name(), FileTarget.DECK.name());

                attachmentService.saveAttachmentDeckTarget(attachment, existingDeck);
            } catch (IOException e) {
                throw new InternalServerException(e.getMessage());
            }
        }
    }

    /*
        덱 이동
     */
    @Override
    @Transactional
    public void moveDeck(DeckMoveRequest request) {
        // 이동할 덱 조회
        Deck deck = deckRepository.findById(request.id())
                .orElseThrow(() -> new NotFoundException(Deck.class, request.id()));

        // 새로운 폴더 조회
        Folder newFolder = null;
        if (request.parentId() != null) {
            newFolder = folderRepository.getReferenceById(request.parentId());
        }

        // 덱의 폴더 변경
        deck.setFolder(newFolder);
    }

    /*
        덱 삭제
     */
    @Override
    @Transactional
    public void deleteDeck(DeckDeleteRequest request) {
        Deck existingDeck = deckRepository.findByIdAndIsDeletedFalse(request.id())
                .orElseThrow(() -> new NotFoundException(Deck.class, request.id()));

        // 기존 첨부파일 삭제(Bulk)
        List<String> fileKeys = existingDeck.getAttachmentDeckTargets().stream()
                .map(AttachmentDeckTarget::getAttachment)
                .filter(attachment -> !attachment.isDeleted())
                .map(Attachment::getFileKey)
                .collect(Collectors.toList());

        if (!fileKeys.isEmpty()) {
            attachmentService.deleteAllAttachments(fileKeys);
        }

        existingDeck.setDeleted(true);
    }
}
