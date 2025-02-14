package com.edio.studywithcard.card.service;

import com.edio.studywithcard.card.model.request.CardBulkRequestWrapper;

import java.util.List;

public interface CardService {
    void createOrUpdateCards(Long accountId, CardBulkRequestWrapper cardBulkRequestWrapper);

    void deleteCards(List<Long> request);
}
