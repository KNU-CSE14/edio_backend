package com.edio.studywithcard.card.repository;

import com.edio.studywithcard.card.domain.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
}
