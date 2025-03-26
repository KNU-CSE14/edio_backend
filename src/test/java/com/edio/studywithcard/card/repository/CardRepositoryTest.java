package com.edio.studywithcard.card.repository;

import com.edio.common.config.JpaConfig;
import com.edio.studywithcard.card.domain.Card;
import com.edio.studywithcard.category.domain.Category;
import com.edio.studywithcard.category.repository.CategoryRepository;
import com.edio.studywithcard.deck.domain.Deck;
import com.edio.studywithcard.deck.repository.DeckRepository;
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

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.edio.common.TestConstants.Card.CARD_DESCRIPTIONS;
import static com.edio.common.TestConstants.Card.CARD_NAMES;
import static com.edio.common.TestConstants.Category.CATEGORY_NAME;
import static com.edio.common.TestConstants.Deck.DECK_DESCRIPTION;
import static com.edio.common.TestConstants.Deck.DECK_NAME;
import static com.edio.common.TestConstants.Folder.FOLDER_NAME;
import static com.edio.common.TestConstants.NON_EXISTENT_ID;
import static com.edio.common.TestConstants.NON_EXISTENT_IDS;
import static com.edio.common.TestConstants.User.ACCOUNT_ID;
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

    @PersistenceContext
    private EntityManager entityManager;

    private Card testCard;
    private Card testCard2;

    private Deck testDeck;
    private Folder testFolder;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        // Given
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
        testCard = cardRepository.save(Card.builder()
                .deck(testDeck)
                .name(CARD_NAMES.get(0))
                .description(CARD_DESCRIPTIONS.get(0))
                .build());
        testCard2 = cardRepository.save(Card.builder()
                .deck(testDeck)
                .name(CARD_NAMES.get(1))
                .description(CARD_DESCRIPTIONS.get(1))
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
                cardRepository.findByIdAndIsDeletedFalse(NON_EXISTENT_ID)
                        .orElseThrow()
        ).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("카드 Soft Delete 동작 확인 -> (성공)")
    void softDeleteCard() {
        // When
        cardRepository.delete(testCard);
        entityManager.flush(); // DB에 반영
        entityManager.clear(); // 1차 캐시 초기화

        Optional<Card> card;
        // Then
        card = cardRepository.findById(testCard.getId());
        assertThat(card).isPresent();
        assertThat(card.get().isDeleted()).isTrue();

        // isDeleted = false 조건으로 조회
        card = cardRepository.findByIdAndIsDeletedFalse(testCard.getId());
        assertThat(card).isEmpty();
    }

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
        List<Card> cards = cardRepository.findAllById(NON_EXISTENT_IDS);

        // Then
        assertThat(cards).isEmpty();
    }
}
