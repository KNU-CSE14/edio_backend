package com.edio.studywithcard.card.repository;

import com.edio.common.config.JpaConfig;
import com.edio.studywithcard.card.domain.Card;
import com.edio.studywithcard.category.domain.Category;
import com.edio.studywithcard.category.repository.CategoryRepository;
import com.edio.studywithcard.deck.domain.Deck;
import com.edio.studywithcard.deck.repository.DeckRepository;
import com.edio.studywithcard.folder.domain.Folder;
import com.edio.studywithcard.folder.repository.FolderRepository;
import com.edio.user.domain.Account;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import(JpaConfig.class)
public class CardRepositoryTest {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private DeckRepository deckRepository;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Card testCard;
    private Card testCard2;

    private Deck testDeck;
    private Folder testFolder;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        // Given
        testFolder = folderRepository.save(Folder.builder()
                .accountId(1L)
                .name("testFolder")
                .build());
        testCategory = categoryRepository.save(Category.builder()
                .name("testCategory")
                .build());
        testDeck = deckRepository.save(Deck.builder()
                .folder(testFolder)
                .category(testCategory)
                .name("testDeck")
                .description("testDescription")
                .build());
        testCard = cardRepository.save(Card.builder()
                .deck(testDeck)
                .name("testCard")
                .description("testDescription")
                .build());
        testCard2 = cardRepository.save(Card.builder()
                .deck(testDeck)
                .name("testCard2")
                .description("testDescription2")
                .build());
    }

    /**
     * 카드 저장 & 조회
     */
    @Test
    @DisplayName("Save And FindCard -> (성공)")
    void saveAndFindCard() {
        // When
        Card card = cardRepository.findByIdAndIsDeletedFalse(testCard.getId())
                .orElseThrow(() -> new AssertionError("Card not found"));

        // Then
        assertThat(card.getName()).isEqualTo("testCard");
    }

    /**
     * 존재하지 않는 카드 조회 -> (실패)
     */
    @Test
    @DisplayName("FindCard by Non-existent Id -> (실패)")
    void findCardByNonExistentId() {
        // Given
        Long nonExistentId = 999L;

        // When & Then
        assertThrows(AssertionError.class, () -> {
            cardRepository.findByIdAndIsDeletedFalse(nonExistentId)
                    .orElseThrow(() -> new AssertionError("Card not found"));
        });
    }

    /**
     * Soft Delete 후 데이터 유지 여부
     */
    /*
        TODO: SQLDelete 사용한 Soft Delete 코드 merge 후 추가 테스트 예정
    @Test
    @DisplayName("Soft Delete And NotFoundCard -> (성공)")
    void softDeleteCard(){
        // Given
        cardRepository.save(testCard);
        entityManager.flush();
        entityManager.clear();

        // When
        cardRepository.delete(testCard);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<Card> deletedCard = cardRepository.findByIdAndIsDeletedFalse(testCard.getId());
        assertThat(deletedCard).isEmpty();

        Long count = (Long) entityManager.createQuery(
                "SELECT COUNT(c) FROM card c where c.id = :id")
                .setParameter("id", 1L)
                .getSingleResult();

        assertThat(count).isEqualTo(1);
    }
    */

    /**
     * 카드 멀티 저장 & 조회
     */
    @Test
    @DisplayName("Multiple Save And FindCards -> (성공)")
    void saveAllAndFindAll() {
        // Given
        cardRepository.saveAll(List.of(testCard, testCard2));

        List<Long> cardIds = List.of(testCard.getId(), testCard2.getId());

        // When
        List<Card> cards = cardRepository.findAllById(cardIds);

        // Then
        assertThat(cards).hasSize(2);
    }

    /**
     * 존재하지 않는 카드 목록 조회 -> (실패)
     */
    @Test
    @DisplayName("FindCards by Non-existent Ids -> (실패)")
    void findCardsByNonExistentIds() {
        // Given
        List<Long> nonExistentIds = List.of(999L, 1000L);

        // When
        List<Card> cards = cardRepository.findAllById(nonExistentIds);

        // Then
        assertThat(cards).isEmpty();
    }
}
