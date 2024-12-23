package com.edio.studywithcard.card.repository;

import com.edio.studywithcard.card.domain.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findByIdAndIsDeletedFalse(Long id);
}
