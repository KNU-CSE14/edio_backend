package com.edio.studywithcard.deck.model.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Deck 생성 요청 정보", example = """
        {
          "folderId": 1,
          "categoryId": 1,
          "name": "Deck Name",
          "description": "Deck Description",
          "isShared": false
        }
        """)
public record DeckCreateRequest(
        Long folderId,
        Long categoryId,
        String name,
        String description,
        boolean isShared
) {
}
