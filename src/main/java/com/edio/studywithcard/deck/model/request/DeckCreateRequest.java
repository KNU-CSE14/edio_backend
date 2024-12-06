package com.edio.studywithcard.deck.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DeckCreateRequest {
    private Long folderId;
    private Long categoryId;
    private String name;
    private String description;
    private String deckType;
}
