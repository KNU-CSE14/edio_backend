package com.edio.studywithcard.deck.repository;

import com.edio.studywithcard.deck.domain.Deck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DeckRepository extends JpaRepository<Deck, Long> {
    Optional<Deck> findByIdAndIsDeletedFalse(Long id);

    // 소유권 검증을 위한 JPQL
    @Query("SELECT f.accountId FROM Deck d JOIN d.folder f WHERE d.id = :deckId")
    Long findAccountIdByDeckId(@Param("deckId") Long deckId);
}
