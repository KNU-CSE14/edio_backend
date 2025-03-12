package com.edio.studywithcard.deck.repository;

import com.edio.common.config.JpaConfig;
import com.edio.studywithcard.category.domain.Category;
import com.edio.studywithcard.category.repository.CategoryRepository;
import com.edio.studywithcard.deck.domain.Deck;
import com.edio.studywithcard.folder.domain.Folder;
import com.edio.studywithcard.folder.repository.FolderRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaConfig.class)
public class DeckRepositoryTest {

    @Autowired
    private DeckRepository deckRepository;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private Deck testDeck;
    private Folder testFolder;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testFolder = folderRepository.save(Folder.builder()
                .accountId(1L)
                .name("testFolder")
                .build());
        testCategory = categoryRepository.save(Category.builder()
                .name("testCategory")
                .build());

        testDeck = Deck.builder()
                .folder(testFolder)
                .category(testCategory)
                .name("testDeck")
                .description("testDescription")
                .build();
    }

    /**
     * 덱 저장 & 조회
     */
    @Test
    @DisplayName("Save And FindDeck -> (성공)")
    void saveAndFindDeck() {
        // Given
        deckRepository.save(testDeck);

        // When
        Optional<Deck> findDeck = deckRepository.findByIdAndIsDeletedFalse(testDeck.getId());

        // Then
        assertThat(findDeck).isPresent();
        assertThat(findDeck.get().getName()).isEqualTo("testDeck");
        assertThat(findDeck.get().getDescription()).isEqualTo("testDescription");
        assertThat(findDeck.get().getFolder().getName()).isEqualTo("testFolder");
        assertThat(findDeck.get().getCategory().getName()).isEqualTo("testCategory");
    }

    /**
     * 존재하지 않는 덱 조회 -> (실패)
     */
    @Test
    @DisplayName("FindDeck by Non-existent Id -> (실패)")
    void findDeckByNonExistentId() {
        // Given
        Long nonExistentId = 999L;

        // When
        Optional<Deck> findDeck = deckRepository.findByIdAndIsDeletedFalse(nonExistentId);

        // Then
        assertThat(findDeck).isEmpty();
    }


    /**
     * Soft Delete 후 데이터 유지 여부
     */
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
