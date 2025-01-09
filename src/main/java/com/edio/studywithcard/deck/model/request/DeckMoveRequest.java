package com.edio.studywithcard.deck.model.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Deck 이동 요청 정보", example = """
        {
          "id": 1,
          "parentId": 2
        }
        """)
public record DeckMoveRequest(
        Long id,
        Long parentId
) {
}
