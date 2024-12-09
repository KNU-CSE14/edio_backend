package com.edio.studywithcard.deck.model.response;

import com.edio.studywithcard.deck.domain.Deck;

public record DeckResponse(
        Long id,
        Long folderId,
        Long categoryId,
        boolean isFavorite,
        String name,
        String description,
        boolean isShared
) {
    public static DeckResponse from(Deck deck) {
        return new DeckResponse(
                deck.getId(),
                deck.getFolder().getId(),
                deck.getCategory().getId(),
                deck.isFavorite(),
                deck.getName(),
                deck.getDescription(),
                deck.isShared()
        );
    }
}
