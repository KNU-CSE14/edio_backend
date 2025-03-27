package com.edio.studywithcard.deck.controller;

import com.edio.common.model.response.SwaggerCommonResponses;
import com.edio.studywithcard.deck.model.request.DeckCreateRequest;
import com.edio.studywithcard.deck.model.request.DeckMoveRequest;
import com.edio.studywithcard.deck.model.request.DeckUpdateRequest;
import com.edio.studywithcard.deck.model.response.DeckResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Deck", description = "Deck 관련 API")
@SecurityRequirement(name = "bearerAuth")
@SwaggerCommonResponses
public interface DeckApiDoc {
    /**
     * @param id 조회할 Deck의 ID
     * @return 조회한 Deck의 상세 정보
     */
    @Operation(summary = "Deck 조회", description = "Deck을 조회합니다.")
    DeckResponse getDeck(Long id);

    /**
     * @param request (folderId, categoryId, name, description, isShared) 등록할 Deck 객체
     * @param file
     * @return 등록한 Deck의 상세 정보
     */
    @Operation(summary = "Deck 등록", description = "Deck을 등록합니다.",
            requestBody = @RequestBody(content = @Content(encoding = @Encoding(name = "request", contentType = MediaType.APPLICATION_JSON_VALUE))))
    DeckResponse createDeck(DeckCreateRequest request, MultipartFile file);

    /**
     * @param request (id, categoryId, name, description) 수정할 Deck 객체
     * @param file
     */
    @Operation(summary = "Deck 수정", description = "Deck을 수정합니다.",
            requestBody = @RequestBody(content = @Content(encoding = @Encoding(name = "request", contentType = MediaType.APPLICATION_JSON_VALUE))))
    void updateDeck(DeckUpdateRequest request, MultipartFile file);

    /**
     * @param request (id, parentId) 이동할 Deck의 부모 폴더 ID
     */
    @Operation(summary = "Deck 이동", description = "Deck을 이동합니다.")
    void moveDeck(DeckMoveRequest request);

    /**
     * @param id 삭제할 Deck의 ID
     */
    @Operation(summary = "Deck 삭제", description = "Deck을 삭제합니다.")
    void deleteDeck(Long id);
}
