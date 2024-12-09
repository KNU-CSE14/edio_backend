package com.edio.studywithcard.deck.model.request;

public record DeckCreateRequest(
        Long folderId,
        Long categoryId,
        String name,
        String description,
        boolean isShared
) {
}
