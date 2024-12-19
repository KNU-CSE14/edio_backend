package com.edio.studywithcard.deck.model.request;

public record DeckUpdateRequest(
        Long id,
        Long categoryId,
        String name,
        String description,
        Boolean isFavorite
) {
}
