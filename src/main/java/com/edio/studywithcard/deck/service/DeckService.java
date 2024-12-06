package com.edio.studywithcard.deck.service;

import com.edio.studywithcard.deck.model.request.DeckCreateRequest;
import com.edio.studywithcard.deck.model.response.DeckResponse;

public interface DeckService {
    // 덱 생성
    DeckResponse createDeck(DeckCreateRequest request);

    // 덱 이동
    void moveDeck(Long deckId, Long newFolderId);
}
