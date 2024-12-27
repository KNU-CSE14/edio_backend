package com.edio.studywithcard.deck.model.response;

import com.edio.studywithcard.attachment.model.response.AttachmentResponse;
import com.edio.studywithcard.card.model.response.CardResponse;
import com.edio.studywithcard.deck.domain.Deck;

import java.util.List;
import java.util.stream.Collectors;

public record DeckResponse(
        Long id,
        Long folderId,
        Long categoryId,
        boolean isFavorite,
        String name,
        String description,
        boolean isShared,
        List<AttachmentResponse> attachments,
        List<CardResponse> cards
) {
    public static DeckResponse from(Deck deck) {
        return new DeckResponse(
                deck.getId(),
                deck.getFolder().getId(),
                deck.getCategory().getId(),
                deck.isFavorite(),
                deck.getName(),
                deck.getDescription(),
                deck.isShared(),
                deck.getAttachmentDeckTargets()
                        .stream()
                        .filter(target -> !target.getAttachment().isDeleted())
                        .map(target -> AttachmentResponse.from(target.getAttachment()))
                        .collect(Collectors.toList()),
                deck.getCards()
                        .stream()
                        .filter(target -> !target.isDeleted())
                        .map(CardResponse::from)
                        .collect(Collectors.toList())
        );
    }
}
