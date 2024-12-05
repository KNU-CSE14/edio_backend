package com.edio.studywithcard.folder.model.response;

import com.edio.studywithcard.deck.domain.Deck;
import com.edio.studywithcard.folder.domain.Folder;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FolderWithDeckResponse {
    private Long id;
    private String name;
    private List<SubFolder> subFolders;
    private List<SubDeck> decks;

    @Data
    @AllArgsConstructor
    public static class SubFolder {
        private Long id;
        private String name;

        public static SubFolder from(Folder folder) {
            return new SubFolder(folder.getId(), folder.getName());
        }
    }

    @Data
    @AllArgsConstructor
    public static class SubDeck {
        private Long id;
        private String name;
        private String description;
        private String deckType;
        private boolean isFavorite;

        public static SubDeck from(Deck deck) {
            return new SubDeck(deck.getId(), deck.getName(), deck.getDescription(), deck.getDeckType(), deck.isFavorite());
        }
    }

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
}
