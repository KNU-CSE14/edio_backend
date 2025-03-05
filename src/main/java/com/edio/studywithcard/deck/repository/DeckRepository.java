package com.edio.studywithcard.deck.repository;

import com.edio.studywithcard.deck.domain.Deck;
import com.edio.studywithcard.folder.domain.Folder;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeckRepository extends JpaRepository<Deck, Long> {
    /**
     * Deck 조회
     *
     * @param id
     * @return
     */
    @EntityGraph(attributePaths = {"attachmentDeckTargets", "cards"})
    Optional<Deck> findByIdAndIsDeletedFalse(Long id);
}
