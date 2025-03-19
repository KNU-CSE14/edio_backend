package com.edio.studywithcard.deck.service;

import com.edio.common.TestConstants;
import com.edio.studywithcard.attachment.service.AttachmentService;
import com.edio.studywithcard.category.domain.Category;
import com.edio.studywithcard.category.repository.CategoryRepository;
import com.edio.studywithcard.deck.domain.Deck;
import com.edio.studywithcard.deck.model.request.DeckCreateRequest;
import com.edio.studywithcard.deck.model.request.DeckDeleteRequest;
import com.edio.studywithcard.deck.model.request.DeckUpdateRequest;
import com.edio.studywithcard.deck.model.response.DeckResponse;
import com.edio.studywithcard.deck.repository.DeckRepository;
import com.edio.studywithcard.folder.domain.Folder;
import com.edio.studywithcard.folder.repository.FolderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeckServiceTest {

    @Mock
    private DeckRepository deckRepository;

    @Mock
    private FolderRepository folderRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private AttachmentService attachmentService;

    @InjectMocks
    private DeckServiceImpl deckService;

    private Deck mockDeck;
    private Folder mockFolder;
    private Category mockCategory;
    private DeckCreateRequest deckCreateRequest;
    private DeckUpdateRequest deckUpdateRequest;
    private DeckDeleteRequest deckDeleteRequest;

    @BeforeEach
    void setUp() {
        mockFolder = Folder.builder().name(TestConstants.Folder.FOLDER_NAME).build();
        mockCategory = Category.builder().name(TestConstants.Category.CATEGORY_NAME).build();
        mockDeck = Deck.builder()
                .name(TestConstants.Deck.DECK_NAMES.get(0))
                .description(TestConstants.Deck.DECK_DESCRIPTIONS.get(0))
                .folder(mockFolder)
                .category(mockCategory)
                .isShared(TestConstants.Deck.IS_SHARED)
                .isFavorite(TestConstants.Deck.IS_FAVORITE)
                .build();
        deckCreateRequest = new DeckCreateRequest(
                TestConstants.Folder.FOLDER_ID,
                TestConstants.Category.CATEGORY_ID,
                TestConstants.Deck.DECK_NAMES.get(1),
                TestConstants.Deck.DECK_DESCRIPTIONS.get(1),
                TestConstants.Deck.IS_SHARED);
        deckUpdateRequest = new DeckUpdateRequest(
                TestConstants.Deck.DECK_ID,
                TestConstants.Category.CATEGORY_ID,
                null,
                TestConstants.Deck.DECK_NAMES.get(2),
                TestConstants.Deck.DECK_DESCRIPTIONS.get(2),
                !TestConstants.Deck.IS_FAVORITE);
        deckDeleteRequest = new DeckDeleteRequest(TestConstants.Deck.DECK_ID);
    }

    @Test
    @DisplayName("덱 ID 조회 -> (성공)")
    void 덱_ID_조회() {
        // Given
        when(deckRepository.findByIdAndIsDeletedFalse(TestConstants.Deck.DECK_ID)).thenReturn(Optional.of(mockDeck));

        // When
        DeckResponse response = deckService.getDeck(TestConstants.Deck.DECK_ID);

        // Then
        assertNotNull(response);
        assertEquals(mockDeck.getName(), response.name());
        verify(deckRepository, times(1)).findByIdAndIsDeletedFalse(1L);
    }

    @Test
    @DisplayName("존재하지 않는 덱 ID 조회 -> (실패)")
    void 존재하지_않는_덱_ID_조회() {
        // When & Then
        assertThatThrownBy(() ->
                deckService.getDeck(TestConstants.NON_EXISTENT_ID)
        ).isInstanceOf(NoSuchElementException.class);
    }

    // TODO: 덱 생성 시 이미지 첨부 파일 검증 필요
    @Test
    @DisplayName("덱 생성 및 검증 -> (성공)")
    void 덱_생성_검증() {
        // Given
        when(folderRepository.getReferenceById(1L)).thenReturn(mockFolder);
        when(categoryRepository.getReferenceById(1L)).thenReturn(mockCategory);
        when(deckRepository.save(any(Deck.class))).thenReturn(mockDeck);

        // When
        DeckResponse response = deckService.createDeck(deckCreateRequest, null);

        // Then
        assertNotNull(response);
        assertEquals(mockDeck.getName(), response.name());
        verify(folderRepository, times(1)).getReferenceById(1L);
        verify(categoryRepository, times(1)).getReferenceById(1L);
        verify(deckRepository, times(1)).save(any(Deck.class));
    }

    @Test
    @DisplayName("덱 업데이트 및 검증 -> (성공)")
    void 덱_업데이트_검증() {
        // Given
        when(deckRepository.findByIdAndIsDeletedFalse(TestConstants.Deck.DECK_ID)).thenReturn(Optional.of(mockDeck));

        // When
        deckService.updateDeck(deckUpdateRequest, null);

        // Then
        verify(deckRepository, times(1)).findByIdAndIsDeletedFalse(1L);
        verify(categoryRepository, times(1)).getReferenceById(1L);
        assertEquals(TestConstants.Deck.DECK_NAMES.get(2), mockDeck.getName());
        assertEquals(TestConstants.Deck.DECK_DESCRIPTIONS.get(2), mockDeck.getDescription());
        assertTrue(mockDeck.isFavorite());
    }

    @Test
    @DisplayName("덱 삭제 검증 -> (성공)")
    void 덱_삭제_검증() {
        // Given
        when(deckRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(mockDeck));

        // When
        deckService.deleteDeck(deckDeleteRequest);

        // Then
        verify(deckRepository, times(1)).findByIdAndIsDeletedFalse(1L);
        verify(deckRepository, times(1)).delete(mockDeck);
    }
}
