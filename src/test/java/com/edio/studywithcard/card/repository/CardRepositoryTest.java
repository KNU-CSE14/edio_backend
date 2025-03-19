package com.edio.studywithcard.card.repository;

import com.edio.common.TestConstants;
import com.edio.common.config.JpaConfig;
import com.edio.studywithcard.card.domain.Card;
import com.edio.studywithcard.category.domain.Category;
import com.edio.studywithcard.category.repository.CategoryRepository;
import com.edio.studywithcard.deck.domain.Deck;
import com.edio.studywithcard.deck.repository.DeckRepository;
import com.edio.studywithcard.folder.domain.Folder;
import com.edio.studywithcard.folder.repository.FolderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
                .accountId(TestConstants.Account.ACCOUNT_ID)
                .name(TestConstants.Folder.FOLDER_NAME)
                .build());
        testCategory = categoryRepository.save(Category.builder()
                .name(TestConstants.Category.CATEGORY_NAME)
                .build());
        testDeck = deckRepository.save(Deck.builder()
                .folder(testFolder)
                .category(testCategory)
                .name(TestConstants.Deck.DECK_NAME)
                .description(TestConstants.Deck.DECK_DESCRIPTION)
                .build());
        testCard = cardRepository.save(Card.builder()
                .deck(testDeck)
                .name(TestConstants.Card.CARD_NAMES.get(0))
                .description(TestConstants.Card.CARD_DESCRIPTIONS.get(0))
                .build());
        testCard2 = cardRepository.save(Card.builder()
                .deck(testDeck)
                .name(TestConstants.Card.CARD_NAMES.get(1))
                .description(TestConstants.Card.CARD_DESCRIPTIONS.get(1))
                .build());
    }

    @Test
    @DisplayName("카드 ID로 조회 -> (성공)")
    void saveAndFindCard() {
        // When
        Card card = cardRepository.findByIdAndIsDeletedFalse(testCard.getId())
                .orElseThrow();

        // Then
        assertThat(card.getName()).isEqualTo(testCard.getName());
    }

    @Test
    @DisplayName("존재하지 않는 카드 ID로 조회 -> (실패)")
    void findCardByNonExistentId() {
        // When & Then
        assertThatThrownBy(() ->
                cardRepository.findByIdAndIsDeletedFalse(TestConstants.NON_EXISTENT_ID)
                        .orElseThrow()
        ).isInstanceOf(NoSuchElementException.class);
    }

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

    @Test
    @DisplayName("여러 카드 리스트 저장 후 조회 -> (성공)")
    void saveAllAndFindAll() {
        // Given
        cardRepository.saveAll(List.of(testCard, testCard2));

        List<Long> cardIds = List.of(testCard.getId(), testCard2.getId());

        // When
        List<Card> cards = cardRepository.findAllById(cardIds);

        // Then
        assertThat(cards).hasSize(2);
    }

    @Test
    @DisplayName("존재하지 않는 ID 리스트로 카드 조회 -> (실패)")
    void findCardsByNonExistentIds() {
        // When
        List<Card> cards = cardRepository.findAllById(TestConstants.NON_EXISTENT_IDS);

        // Then
        assertThat(cards).isEmpty();
    }
}
