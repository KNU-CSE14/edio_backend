package com.edio.studywithcard.deck.model.response;

import com.edio.studywithcard.attachment.model.response.AttachmentResponse;
import com.edio.studywithcard.deck.domain.Deck;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public record DeckResponse(
        Long id,
        Long folderId,
        Long categoryId,
        boolean isFavorite,
        String name,
        String description,
        boolean isShared,
        List<AttachmentResponse> attachments
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
                Optional.ofNullable(deck.getAttachmentDeckTargets())
                        .orElse(Collections.emptyList()) // Null 처리
                        .stream()
                        .map(target -> AttachmentResponse.from(target.getAttachment()))
                        .collect(Collectors.toList())
        );
    }
}
