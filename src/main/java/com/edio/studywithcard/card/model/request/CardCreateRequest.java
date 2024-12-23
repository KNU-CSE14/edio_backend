package com.edio.studywithcard.card.model.request;

public record CardCreateRequest(
        Long deckId,
        String name,
        String description
) {
}
