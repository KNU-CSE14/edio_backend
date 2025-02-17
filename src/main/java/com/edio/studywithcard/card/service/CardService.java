package com.edio.studywithcard.card.service;

import com.edio.studywithcard.card.model.request.CardBulkRequestWrapper;

import java.util.List;

public interface CardService {
    void upsert(Long accountId, CardBulkRequestWrapper cardBulkRequestWrapper);

    void deleteCards(Long accountId, Long deckId, List<Long> cardIds);
}
