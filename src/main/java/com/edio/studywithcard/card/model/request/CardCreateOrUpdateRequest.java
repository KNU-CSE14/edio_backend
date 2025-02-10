package com.edio.studywithcard.card.model.request;

public record CardCreateOrUpdateRequest(
        Long deckId,
        Long cardId,
        String name,
        String description
) {
}
