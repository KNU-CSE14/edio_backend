package com.edio.studywithcard.deck.service;

import com.edio.studywithcard.attachment.domain.Attachment;
import com.edio.studywithcard.attachment.domain.AttachmentDeckTarget;
import com.edio.studywithcard.attachment.domain.enums.AttachmentFolder;
import com.edio.studywithcard.attachment.domain.enums.FileTarget;
import com.edio.studywithcard.attachment.service.AttachmentService;
import com.edio.studywithcard.category.domain.Category;
import com.edio.studywithcard.category.repository.CategoryRepository;
import com.edio.studywithcard.deck.domain.Deck;
import com.edio.studywithcard.deck.model.request.DeckCreateRequest;
import com.edio.studywithcard.deck.model.request.DeckMoveRequest;
import com.edio.studywithcard.deck.model.request.DeckUpdateRequest;
import com.edio.studywithcard.deck.model.response.DeckResponse;
import com.edio.studywithcard.deck.repository.DeckRepository;
import com.edio.studywithcard.folder.domain.Folder;
import com.edio.studywithcard.folder.repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

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
        Deck deck = deckRepository.findByIdAndIsDeletedFalse(id).get();
        return DeckResponse.from(deck);
    }

    /*
        덱 생성
     */
    @Override
    @Transactional
    public DeckResponse createDeck(DeckCreateRequest request, MultipartFile file) {
        // Folder와 Category를 조회
        Folder folder = folderRepository.getReferenceById(request.folderId());
        Category category = categoryRepository.getReferenceById(request.categoryId());

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
    }

    /*
        덱 수정
     */
    @Override
    @Transactional
    public void updateDeck(DeckUpdateRequest request, MultipartFile file) {
        Deck existingDeck = deckRepository.findByIdAndIsDeletedFalse(request.id()).get();

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
        // 폴더
        if (request.folderId() != null) {
            Folder newFolder = null;
            newFolder = folderRepository.getReferenceById(request.folderId());
            existingDeck.setFolder(newFolder);
        }

        // 첨부파일 수정
        if (file != null) {
            // 기존 첨부파일 삭제 (Bulk)
            List<String> fileKeys = existingDeck.getAttachmentDeckTargets().stream()
                    .map(AttachmentDeckTarget::getAttachment)
                    .filter(attachment -> !attachment.isDeleted())
                    .map(Attachment::getFileKey)
                    .collect(Collectors.toList());

            attachmentService.deleteAllAttachments(fileKeys);

            // 새 첨부파일 저장
            if (!file.isEmpty()) {
                Attachment attachment = attachmentService.saveAttachment(file, AttachmentFolder.IMAGE.name(), FileTarget.DECK.name());
                attachmentService.saveAttachmentDeckTarget(attachment, existingDeck);
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
        Deck deck = deckRepository.findById(request.id()).get();

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
    public void deleteDeck(Long id) {
        Deck existingDeck = deckRepository.findByIdAndIsDeletedFalse(id).get();

        // 기존 첨부파일 삭제(Bulk)
        List<String> fileKeys = existingDeck.getAttachmentDeckTargets().stream()
                .map(AttachmentDeckTarget::getAttachment)
                .filter(attachment -> !attachment.isDeleted())
                .map(Attachment::getFileKey)
                .collect(Collectors.toList());

        attachmentService.deleteAllAttachments(fileKeys);

        deckRepository.delete(existingDeck);
    }
}
