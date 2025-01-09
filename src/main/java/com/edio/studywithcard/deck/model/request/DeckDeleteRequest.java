package com.edio.studywithcard.deck.model.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Deck 삭제 요청 정보", example = """
        {
          "id": 1
        }
        """)
public record DeckDeleteRequest(
        Long id
) {
}
