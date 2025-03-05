package com.edio.studywithcard.deck.model.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Deck 수정 요청 정보", example = """
        {
          "id": 1,
          "categoryId": 1,
          "folderId": 1,
          "name": "Deck Update Name",
          "description": "Deck Update Description",
          "isFavorite" : false
        }
        """)
public record DeckUpdateRequest(
        Long id,
        Long categoryId,
        Long folderId,
        String name,
        String description,
        Boolean isFavorite
) {
}
