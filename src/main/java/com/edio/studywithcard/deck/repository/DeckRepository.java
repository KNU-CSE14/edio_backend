package com.edio.studywithcard.deck.repository;

import com.edio.studywithcard.deck.domain.Deck;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeckRepository extends JpaRepository<Deck, Long> {
}
