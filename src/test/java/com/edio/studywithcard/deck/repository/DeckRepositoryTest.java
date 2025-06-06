package com.edio.studywithcard.deck.repository;

import com.edio.common.config.JpaConfig;
import com.edio.studywithcard.category.domain.Category;
import com.edio.studywithcard.category.repository.CategoryRepository;
import com.edio.studywithcard.deck.domain.Deck;
import com.edio.studywithcard.folder.domain.Folder;
import com.edio.studywithcard.folder.repository.FolderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.NoSuchElementException;
import java.util.Optional;

import static com.edio.common.TestConstants.Category.CATEGORY_NAME;
import static com.edio.common.TestConstants.Deck.DECK_DESCRIPTION;
import static com.edio.common.TestConstants.Deck.DECK_NAME;
import static com.edio.common.TestConstants.Folder.FOLDER_NAME;
import static com.edio.common.TestConstants.NON_EXISTENT_ID;
import static com.edio.common.TestConstants.User.ACCOUNT_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(JpaConfig.class)
public class DeckRepositoryTest {

    @Autowired
    private DeckRepository deckRepository;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Deck testDeck;
    private Folder testFolder;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testFolder = folderRepository.save(Folder.builder()
                .accountId(ACCOUNT_ID)
                .name(FOLDER_NAME)
                .build());
        testCategory = categoryRepository.save(Category.builder()
                .name(CATEGORY_NAME)
                .build());

        testDeck = deckRepository.save(Deck.builder()
                .folder(testFolder)
                .category(testCategory)
                .name(DECK_NAME)
                .description(DECK_DESCRIPTION)
                .build());
    }

    @Test
    @DisplayName("덱 ID로 조회 -> (성공)")
    void saveAndFindDeck() {
        // When
        Deck deck = deckRepository.findByIdAndIsDeletedFalse(testDeck.getId())
                .orElseThrow();

        // Then
        assertThat(deck.getName()).isEqualTo(testDeck.getName());
        assertThat(deck.getDescription()).isEqualTo(testDeck.getDescription());
        assertThat(deck.getFolder().getName()).isEqualTo(testDeck.getFolder().getName());
        assertThat(deck.getCategory().getName()).isEqualTo(testDeck.getCategory().getName());
    }

    @Test
    @DisplayName("존재하지 않는 덱 ID로 조회 -> (실패)")
    void findDeckByNonExistentId() {
        // When & Then
        assertThatThrownBy(() ->
                deckRepository.findByIdAndIsDeletedFalse(NON_EXISTENT_ID)
                        .orElseThrow()
        ).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("덱 Soft Delete 동작 확인 -> (성공)")
    void softDeleteDeck() {
        // When
        deckRepository.delete(testDeck);
        deckRepository.flush();

        Optional<Deck> deck;
        // Then
        deck = deckRepository.findById(testDeck.getId());
        assertThat(deck).isPresent();
        assertThat(deck.get().isDeleted()).isTrue();

        // isDeleted = false 조건으로 조회
        deck = deckRepository.findByIdAndIsDeletedFalse(testDeck.getId());
        assertThat(deck).isEmpty();
    }
}
