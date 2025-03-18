package com.edio.studywithcard.deck.service;

import com.edio.common.exception.base.ErrorMessages;
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
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

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

    private Deck existingDeck;
    private Folder folder;
    private Category category;
    private DeckCreateRequest deckCreateRequest;
    private DeckUpdateRequest deckUpdateRequest;
    private DeckDeleteRequest deckDeleteRequest;

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
                .build();
        deckCreateRequest = new DeckCreateRequest(1L, 1L, "New Deck", "New Description", false);
        deckUpdateRequest = new DeckUpdateRequest(1L, 1L, null, "Updated Deck", "Updated Description", true);
        deckDeleteRequest = new DeckDeleteRequest(1L);
    }

    @Test
    void testGetDeck() {
        when(deckRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(existingDeck));

        DeckResponse response = deckService.getDeck(1L);

        assertNotNull(response);
        assertEquals(existingDeck.getName(), response.name());
        verify(deckRepository, times(1)).findByIdAndIsDeletedFalse(1L);
    }

    @Test
    void testGetDeck_NotFound() {
        when(deckRepository.findByIdAndIsDeletedFalse(1L))
                .thenThrow(new EntityNotFoundException(ErrorMessages.NOT_FOUND_ENTITY.format("Deck", 1L)));

        assertThrows(EntityNotFoundException.class, () -> deckService.getDeck(1L));
        verify(deckRepository, times(1)).findByIdAndIsDeletedFalse(1L);
    }

    @Test
    void testCreateDeck() {
        when(folderRepository.getReferenceById(1L)).thenReturn(folder);
        when(categoryRepository.getReferenceById(1L)).thenReturn(category);
        when(deckRepository.save(any(Deck.class))).thenReturn(existingDeck);

        DeckResponse response = deckService.createDeck(deckCreateRequest, null);

        assertNotNull(response);
        assertEquals(existingDeck.getName(), response.name());
        verify(folderRepository, times(1)).getReferenceById(1L);
        verify(categoryRepository, times(1)).getReferenceById(1L);
        verify(deckRepository, times(1)).save(any(Deck.class));
    }

    @Test
    void testCreateDeck_Conflict() {
        when(folderRepository.getReferenceById(1L)).thenReturn(folder);
        when(categoryRepository.getReferenceById(1L)).thenReturn(category);
        when(deckRepository.save(any(Deck.class))).thenThrow(DataIntegrityViolationException.class);

        assertThrows(RuntimeException.class, () -> deckService.createDeck(deckCreateRequest, null));
        verify(folderRepository, times(1)).getReferenceById(1L);
        verify(categoryRepository, times(1)).getReferenceById(1L);
        verify(deckRepository, times(1)).save(any(Deck.class));
    }

    @Test
    void testUpdateDeck() {
        when(deckRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(existingDeck));
        when(categoryRepository.getReferenceById(1L)).thenReturn(category);

        deckService.updateDeck(deckUpdateRequest, null);

        verify(deckRepository, times(1)).findByIdAndIsDeletedFalse(1L);
        verify(categoryRepository, times(1)).getReferenceById(1L);
        assertEquals("Updated Deck", existingDeck.getName());
        assertEquals("Updated Description", existingDeck.getDescription());
        assertTrue(existingDeck.isFavorite());
    }

    @Test
    void testDeleteDeck() {
        when(deckRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(existingDeck));

        deckService.deleteDeck(deckDeleteRequest);

        verify(deckRepository, times(1)).findByIdAndIsDeletedFalse(1L);
        verify(deckRepository, times(1)).delete(existingDeck);
    }
}
