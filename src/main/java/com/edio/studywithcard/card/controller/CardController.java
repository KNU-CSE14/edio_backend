package com.edio.studywithcard.card.controller;

import com.edio.common.model.response.SwaggerCommonResponses;
import com.edio.studywithcard.card.model.request.CardCreateRequest;
import com.edio.studywithcard.card.model.request.CardDeleteRequest;
import com.edio.studywithcard.card.model.request.CardUpdateRequest;
import com.edio.studywithcard.card.model.response.CardResponse;
import com.edio.studywithcard.card.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Card", description = "Card 관련 API")
@SecurityRequirement(name = "bearerAuth")
@SwaggerCommonResponses
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    /**
     * @param request (deckId, name, description) 등록할 Card 객체
     * @param files
     * @return 등록한 Card의 상세 정보
     */
    @PostMapping(value = "/card", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Card 등록", description = "Card를 등록합니다.")
    public CardResponse createCard(@RequestPart CardCreateRequest request,
                                   @RequestPart(value = "file", required = false) MultipartFile[] files) {
        return cardService.createCard(request, files);
    }

    /**
     * @param request (id, name, description) 수정할 Deck 객체
     * @param files
     */
    @PatchMapping(value = "/card", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Card 수정", description = "Card를 수정합니다.")
    public void updateCard(@RequestPart CardUpdateRequest request,
                           @RequestPart(value = "file", required = false) MultipartFile[] files) {
        cardService.updateCard(request, files);
    }

    /**
     * @param request (id) 삭제할 Card의 ID
     */
    @DeleteMapping("/card")
    @Operation(summary = "Card 삭제", description = "Card를 삭제합니다.")
    public void deleteCard(@RequestBody CardDeleteRequest request) {
        cardService.deleteCard(request);
    }
}
