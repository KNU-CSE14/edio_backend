package com.edio.studywithcard.deck.controller;

import com.edio.common.model.response.SwaggerCommonResponses;
import com.edio.studywithcard.deck.model.request.DeckCreateRequest;
import com.edio.studywithcard.deck.model.request.DeckMoveRequest;
import com.edio.studywithcard.deck.model.response.DeckResponse;
import com.edio.studywithcard.deck.service.DeckService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Deck", description = "Deck 관련 API")
@SecurityRequirement(name = "bearerAuth")
@SwaggerCommonResponses
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DeckController {

    private final DeckService deckService;

    @PostMapping(value = "/deck", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Deck 등록", description = "Deck을 등록합니다.")
    public DeckResponse createDeck(@RequestPart DeckCreateRequest request,
                                   @RequestPart(value = "file", required = false) MultipartFile file) {
        log.info("Request part (JSON): {}", request);
        return deckService.createDeck(request, file);
    }

    @PatchMapping("/deck/{id}/position")
    @Operation(summary = "Deck 이동", description = "Deck을 이동합니다.")
    public void moveDeck(@PathVariable Long id, @RequestBody DeckMoveRequest deckMoveRequest) {
        deckService.moveDeck(id, deckMoveRequest.parentId());
    }
}
