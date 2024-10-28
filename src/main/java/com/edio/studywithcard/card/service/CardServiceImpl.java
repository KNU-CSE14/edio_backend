package com.edio.studywithcard.card.service;

import com.edio.studywithcard.card.domain.Card;
import com.edio.studywithcard.card.repository.CardRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardServiceImpl implements CardService{

    private final CardRepository cardRepository;

    public CardServiceImpl(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }


    @Override
    public List<String> getCardTest() {
        // Card 엔티티에서 title만 추출하여 반환
        return cardRepository.findAll().stream()
                .map(Card::getTitle)  // title 필드가 있다고 가정
                .collect(Collectors.toList());
    }
}
