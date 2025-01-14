package com.edio.studywithcard.card.controller;

import com.edio.common.model.response.SwaggerCommonResponses;
import com.edio.studywithcard.card.model.request.CardCreateRequest;
import com.edio.studywithcard.card.model.request.CardDeleteRequest;
import com.edio.studywithcard.card.model.request.CardUpdateRequest;
import com.edio.studywithcard.card.model.response.CardResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Card", description = "Card 관련 API")
@SecurityRequirement(name = "bearerAuth")
@SwaggerCommonResponses
public interface CardApiDoc {
    /**
     * @param request (deckId, name, description) 등록할 Card 객체
     * @param files
     * @return 등록한 Card의 상세 정보
     */
    @Operation(summary = "Card 등록", description = "Card를 등록합니다.")
    CardResponse createCard(CardCreateRequest request, MultipartFile[] files);

    /**
     * @param request (id, name, description) 수정할 Deck 객체
     * @param files
     */
    @Operation(summary = "Card 수정", description = "Card를 수정합니다.")
    void updateCard(CardUpdateRequest request, MultipartFile[] files);

    /**
     * @param request (id) 삭제할 Card의 ID
     */
    @Operation(summary = "Card 삭제", description = "Card를 삭제합니다.")
    void deleteCard(CardDeleteRequest request);
}
