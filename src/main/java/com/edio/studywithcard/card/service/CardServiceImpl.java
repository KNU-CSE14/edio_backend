package com.edio.studywithcard.card.service;

import com.edio.studywithcard.card.domain.Card;
import com.edio.studywithcard.card.repository.CardRepository;
import org.springframework.stereotype.Service;

@Service
public class CardServiceImpl implements CardService{

    private final CardRepository cardRepository;

    public CardServiceImpl(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }
}
