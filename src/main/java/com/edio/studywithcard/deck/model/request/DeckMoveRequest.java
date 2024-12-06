package com.edio.studywithcard.deck.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DeckMoveRequest {
    private Long parentId; // 새로운 폴더 ID
}
