package com.edio.studywithcard.card.model.request;

public record CardUpdateRequest(
        Long id,
        String name,
        String description
) {
}
