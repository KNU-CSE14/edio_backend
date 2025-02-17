package com.edio.studywithcard.card.model.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "카드 삭제 요청 정보", example = """
        {
          "deckId": 1,
          "cardIds": [1, 2, 3]
        }
        """)
public record CardsDeleteRequest(Long deckId, List<Long> cardIds) {
}

