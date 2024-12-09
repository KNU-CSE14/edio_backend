package com.edio.studywithcard.folder.model.response;

import com.edio.studywithcard.deck.domain.Deck;
import com.edio.studywithcard.folder.domain.Folder;

import java.util.List;

public record FolderWithDeckResponse(
        Long id,
        String name,
        List<SubFolder> subFolders,
        List<SubDeck> decks
) {
    public static FolderWithDeckResponse from(Folder folder) {
        List<SubFolder> subFolderResponses = folder.getChildrenFolders().stream()
                .filter(f -> !f.isDeleted())
                .map(SubFolder::from)
                .toList();

        List<SubDeck> deckResponses = folder.getDecks().stream()
                .filter(d -> !d.isDeleted())
                .map(SubDeck::from)
                .toList();

        return new FolderWithDeckResponse(folder.getId(), folder.getName(), subFolderResponses, deckResponses);
    }

    private record SubFolder(
            Long id,
            String name
    ) {
        public static SubFolder from(Folder folder) {
            return new SubFolder(folder.getId(), folder.getName());
        }
    }

    private record SubDeck(
            Long id,
            String name,
            String description,
            boolean isShared,
            boolean isFavorite
    ) {
        public static SubDeck from(Deck deck) {
            return new SubDeck(deck.getId(), deck.getName(), deck.getDescription(), deck.isShared(), deck.isFavorite());
        }
    }
}
