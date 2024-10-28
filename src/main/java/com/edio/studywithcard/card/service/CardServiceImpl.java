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


    /*
        DB 호출 테스트
     */
    @Override
    public List<String> getCardTest() {
        return cardRepository.findAll().stream()
                .map(Card::getTitle)  // title 필드가 있다고 가정
                .collect(Collectors.toList());
    }
}
