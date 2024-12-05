package com.edio.studywithcard.deck.model.request;

import lombok.Data;

@Data
public class DeckCreateRequest {
    private Long folderId;
    private Long categoryId;
    private String name;
    private String description;
    private String deckType;
}
