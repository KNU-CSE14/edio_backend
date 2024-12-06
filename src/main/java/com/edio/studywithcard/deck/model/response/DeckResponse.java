package com.edio.studywithcard.deck.model.response;

import com.edio.studywithcard.deck.domain.Deck;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeckResponse {
    private Long id;
    private Long folderId;
    private Long categoryId;
    private boolean isFavorite;
    private String name;
    private String description;
    private String deckType;


    public static DeckResponse from(Deck deck) {
        return new DeckResponse(deck.getId(),
                deck.getFolder().getId(),
                deck.getCategory().getId(),
                deck.isFavorite(),
                deck.getName(),
                deck.getDescription(),
                deck.getDeckType());
    }
}
