package com.edio.studywithcard.card.controller;

import com.edio.common.model.response.SwaggerCommonResponses;
import com.edio.common.security.CustomUserDetails;
import com.edio.studywithcard.card.model.request.CardBulkRequestWrapper;
import com.edio.studywithcard.card.model.request.CardsDeleteRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Card", description = "Card 관련 API")
@SecurityRequirement(name = "bearerAuth")
@SwaggerCommonResponses
public interface CardApiDoc {
    /**
     * @param cardBulkRequestWrapper (cardId, deckId, name, description, files) 등록 or 수정 객체
     */
    @Operation(summary = "Card 등록|수정", description = "Card를 등록하거나 수정합니다.")
    void upsertCards(CustomUserDetails userDetails, CardBulkRequestWrapper cardBulkRequestWrapper);

    /**
     * @param request (deckId, cardIds) 덱 ID, 삭제할 카드 ID 리스트
     */
    @Operation(summary = "Card 삭제", description = "Card를 삭제합니다.")
    void deleteCards(CustomUserDetails userDetails, CardsDeleteRequest request);
}
