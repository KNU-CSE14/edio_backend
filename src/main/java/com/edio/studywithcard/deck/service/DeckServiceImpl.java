package com.edio.studywithcard.deck.service;

import com.edio.common.exception.ConflictException;
import com.edio.common.exception.NotFoundException;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class DeckServiceImpl implements DeckService {

    private final DeckRepository deckRepository;

    private final FolderRepository folderRepository;

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public DeckResponse createDeck(DeckCreateRequest request) {
        try {
            // Folder와 Category를 조회
            Folder folder = folderRepository.findById(request.getFolderId())
                    .orElseThrow(() -> new NotFoundException(Folder.class, request.getFolderId()));
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new NotFoundException(Category.class, request.getCategoryId()));

            // Deck 생성 및 저장
            Deck deck = Deck.builder()
                    .folder(folder)
                    .category(category)
                    .name(request.getName())
                    .description(request.getDescription())
                    .deckType(request.getDeckType())
                    .build();
            Deck savedDeck = deckRepository.save(deck);
            return DeckResponse.from(savedDeck);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(Deck.class, request.getName());
        }
    }
}
