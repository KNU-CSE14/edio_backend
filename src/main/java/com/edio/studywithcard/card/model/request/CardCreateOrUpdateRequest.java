package com.edio.studywithcard.card.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardCreateOrUpdateRequest {
    private Long deckId;
    private Long cardId;
    private String name;
    private String description;
}
