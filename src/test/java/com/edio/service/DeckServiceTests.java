package com.edio.service;

import com.edio.common.exception.ConflictException;
import com.edio.common.exception.NotFoundException;
import com.edio.studywithcard.category.domain.Category;
import com.edio.studywithcard.category.repository.CategoryRepository;
import com.edio.studywithcard.deck.domain.Deck;
import com.edio.studywithcard.deck.model.request.DeckCreateRequest;
import com.edio.studywithcard.deck.model.request.DeckUpdateRequest;
import com.edio.studywithcard.deck.model.response.DeckResponse;
import com.edio.studywithcard.deck.repository.DeckRepository;
import com.edio.studywithcard.deck.service.DeckServiceImpl;
import com.edio.studywithcard.folder.domain.Folder;
import com.edio.studywithcard.folder.repository.FolderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeckServiceTests {

    @Mock
    private DeckRepository deckRepository;

    @Mock
    private FolderRepository folderRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private DeckServiceImpl deckService;

    private Deck existingDeck;
    private Folder folder;
    private Category category;
    private DeckCreateRequest deckCreateRequest;
    private DeckUpdateRequest deckUpdateRequest;

    @BeforeEach
    void setUp() {
        folder = Folder.builder().name("Test Folder").build();
        category = Category.builder().name("Test Category").build();
        existingDeck = Deck.builder()
                .name("Test Deck")
                .description("Test Description")
                .folder(folder)
                .category(category)
                .isShared(false)
                .isFavorite(false)
                .isDeleted(false)
                .build();
        deckCreateRequest = new DeckCreateRequest(1L, 1L, "New Deck", "New Description", false);
        deckUpdateRequest = new DeckUpdateRequest(1L, "Updated Deck", "Updated Description", true);
    }

    @Test
    void testGetDeck() {
        when(deckRepository.findByIdAndIsDeleted(1L, false)).thenReturn(Optional.of(existingDeck));

        DeckResponse response = deckService.getDeck(1L);

        assertNotNull(response);
        assertEquals(existingDeck.getName(), response.name());
        verify(deckRepository, times(1)).findByIdAndIsDeleted(1L, false);
    }

    @Test
    void testGetDeck_NotFound() {
        when(deckRepository.findByIdAndIsDeleted(1L, false)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> deckService.getDeck(1L));
        verify(deckRepository, times(1)).findByIdAndIsDeleted(1L, false);
    }

    @Test
    void testCreateDeck() {
        when(folderRepository.findById(1L)).thenReturn(Optional.of(folder));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(deckRepository.save(any(Deck.class))).thenReturn(existingDeck);

        DeckResponse response = deckService.createDeck(deckCreateRequest);

        assertNotNull(response);
        assertEquals(existingDeck.getName(), response.name());
        verify(folderRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).findById(1L);
        verify(deckRepository, times(1)).save(any(Deck.class));
    }

    @Test
    void testCreateDeck_Conflict() {
        when(folderRepository.findById(1L)).thenReturn(Optional.of(folder));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(deckRepository.save(any(Deck.class))).thenThrow(DataIntegrityViolationException.class);

        assertThrows(ConflictException.class, () -> deckService.createDeck(deckCreateRequest));
        verify(folderRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).findById(1L);
        verify(deckRepository, times(1)).save(any(Deck.class));
    }

    @Test
    void testUpdateDeck() {
        when(deckRepository.findByIdAndIsDeleted(1L, false)).thenReturn(Optional.of(existingDeck));
        when(categoryRepository.getReferenceById(1L)).thenReturn(category);

        deckService.updateDeck(1L, deckUpdateRequest);

        verify(deckRepository, times(1)).findByIdAndIsDeleted(1L, false);
        verify(categoryRepository, times(1)).getReferenceById(1L);
        assertEquals("Updated Deck", existingDeck.getName());
        assertEquals("Updated Description", existingDeck.getDescription());
        assertTrue(existingDeck.isFavorite());
    }

    @Test
    void testDeleteDeck() {
        when(deckRepository.findByIdAndIsDeleted(1L, false)).thenReturn(Optional.of(existingDeck));

        deckService.deleteDeck(1L);

        verify(deckRepository, times(1)).findByIdAndIsDeleted(1L, false);
        assertTrue(existingDeck.isDeleted());
    }
}