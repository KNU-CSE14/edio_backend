package com.edio.studywithcard.folder.model.response;

import com.edio.studywithcard.deck.domain.Deck;
import com.edio.studywithcard.folder.domain.Folder;

import java.util.List;
import java.util.stream.Collectors;

public record FolderAllResponse(
        Long id,
        String name,
        List<FolderAllResponse> subFolders,
        List<SubDeck> decks
) {
    public static FolderAllResponse from(Folder folder) {
        return new FolderAllResponse(
                folder.getId(),
                folder.getName(),
                folder.getChildrenFolders().stream()
                        .filter(f -> !f.isDeleted())
                        .map(FolderAllResponse::from)
                        .collect(Collectors.toList()),
                folder.getDecks().stream()
                        .filter(f -> !f.isDeleted())
                        .map(SubDeck::from)
                        .collect(Collectors.toList())
        );
    }

    private record SubDeck(
            Long id,
            String name,
            String description,
            boolean isShared,
            boolean isFavorite
    ) {
        private static FolderAllResponse.SubDeck from(Deck deck) {
            return new FolderAllResponse.SubDeck(deck.getId(), deck.getName(), deck.getDescription(), deck.isShared(), deck.isFavorite());
        }
    }
}
