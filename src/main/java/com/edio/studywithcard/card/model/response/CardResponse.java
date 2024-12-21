package com.edio.studywithcard.card.model.response;

import com.edio.studywithcard.attachment.model.response.AttachmentResponse;
import com.edio.studywithcard.card.domain.Card;

import java.util.List;
import java.util.stream.Collectors;

public record CardResponse(
        Long id,
        Long deckId,
        String name,
        String description,
        List<AttachmentResponse> attachments
) {
    public static CardResponse from(Card card) {
        return new CardResponse(
                card.getId(),
                card.getDeck().getId(),
                card.getName(),
                card.getDescription(),
                card.getAttachmentCardTargets()
                        .stream()
                        .filter(target -> !target.getAttachment().isDeleted())
                        .map(target -> AttachmentResponse.from(target.getAttachment()))
                        .collect(Collectors.toList())
        );
    }
}
