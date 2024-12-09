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
import org.springframework.web.bind.annotation.*;

@Tag(name = "Deck", description = "Deck 관련 API")
@SecurityRequirement(name = "bearerAuth")
@SwaggerCommonResponses
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DeckController {

    private final DeckService deckService;

    @PostMapping("/deck")
    @Operation(summary = "Deck 등록", description = "Deck을 등록합니다.")
    public DeckResponse createDeck(@RequestBody DeckCreateRequest request) {
        return deckService.createDeck(request);
    }

    @PatchMapping("/deck/{id}/position")
    @Operation(summary = "Deck 이동", description = "Deck을 이동합니다.")
    public void moveDeck(@PathVariable Long id, @RequestBody DeckMoveRequest deckMoveRequest) {
        deckService.moveDeck(id, deckMoveRequest.parentId());
    }
}
