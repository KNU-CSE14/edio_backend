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
import org.springframework.test.context.ActiveProfiles;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(JpaConfig.class)
@ActiveProfiles("h2")
public class DeckRepositoryTest {

    @Autowired
    private DeckRepository deckRepository;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private static final Long accountId = 1L;
    private static final String folderName = "testFolder";
    private static final String categoryName = "testCategory";
    private static final String deckName = "testDeck";
    private static final String deckDescription = "deckDescription";
    private static final Long nonExistentId = 999L;

    private Deck testDeck;
    private Folder testFolder;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testFolder = folderRepository.save(Folder.builder()
                .accountId(accountId)
                .name(folderName)
                .build());
        testCategory = categoryRepository.save(Category.builder()
                .name(categoryName)
                .build());

        testDeck = deckRepository.save(Deck.builder()
                .folder(testFolder)
                .category(testCategory)
                .name(deckName)
                .description(deckDescription)
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
                deckRepository.findByIdAndIsDeletedFalse(nonExistentId)
                        .orElseThrow()
        ).isInstanceOf(NoSuchElementException.class);
    }

    /*
        TODO: SQLDelete 사용한 Soft Delete 코드 merge 후 추가 테스트 예정
    @Test
    @DisplayName("Soft Delete And NotFoundDeck -> (성공)")
    void softDeleteCard(){
        // Given
        deckRepository.save(testDeck);
        entityManager.flush();
        entityManager.clear();

        // When
        deckRepository.delete(testDeck);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<Deck> deletedDeck = deckRepository.findByIdAndIsDeletedFalse(testDeck.getId());
        assertThat(deletedDeck).isEmpty();

        Long count = (Long) entityManager.createQuery(
                "SELECT COUNT(d) FROM deck d where d.id = :id")
                .setParameter("id", testDeck.getId())
                .getSingleResult();

        assertThat(count).isEqualTo(1);
    }
    */
}
