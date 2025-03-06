package com.edio.studywithcard.deck.repository;

import com.edio.studywithcard.deck.domain.Deck;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeckRepository extends JpaRepository<Deck, Long> {
    /**
     * Deck 조회
     *
     * @param id
     * @return
     */
    Optional<Deck> findByIdAndIsDeletedFalse(Long id);
}
