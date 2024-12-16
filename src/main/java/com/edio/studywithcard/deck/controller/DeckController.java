package com.edio.studywithcard.deck.controller;

import com.edio.common.model.response.SwaggerCommonResponses;
import com.edio.studywithcard.deck.model.request.DeckCreateRequest;
import com.edio.studywithcard.deck.model.request.DeckMoveRequest;
import com.edio.studywithcard.deck.model.request.DeckUpdateRequest;
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

    /**
     * @param id 조회할 Deck의 ID
     * @return 조회한 Deck의 상세 정보
     */
    @GetMapping("/deck")
    @Operation(summary = "Deck 조회", description = "Deck을 조회합니다.")
    public DeckResponse getDeck(@RequestParam Long id) {
        return deckService.getDeck(id);
    }

    /**
     * @param request (folderId, categoryId, name, description, isShared) 등록할 Deck 객체
     * @return 등록한 Deck의 상세 정보
     */
    @PostMapping(value = "/deck", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Deck 등록", description = "Deck을 등록합니다.")
    public DeckResponse createDeck(@RequestPart DeckCreateRequest request,
                                   @RequestPart(value = "file", required = false) MultipartFile file) {
        return deckService.createDeck(request, file);
    }

    /**
     * @param id                수정할 Deck의 ID
     * @param deckUpdateRequest (categoryId, name, description) 수정할 Deck 객체
     */
    @PatchMapping("/deck/{id}")
    @Operation(summary = "Deck 수정", description = "Deck을 수정합니다.")
    public void updateDeck(@PathVariable Long id, @RequestBody DeckUpdateRequest deckUpdateRequest) {
        deckService.updateDeck(id, deckUpdateRequest);
    }

    /**
     * @param id              수정할 Deck의 ID
     * @param deckMoveRequest (parentId) 이동할 Deck의 부모 폴더 ID
     */
    @PatchMapping("/deck/{id}/position")
    @Operation(summary = "Deck 이동", description = "Deck을 이동합니다.")
    public void moveDeck(@PathVariable Long id, @RequestBody DeckMoveRequest deckMoveRequest) {
        deckService.moveDeck(id, deckMoveRequest.parentId());
    }

    /**
     * @param id 삭제할 Deck의 ID
     */
    @DeleteMapping("/deck/{id}")
    @Operation(summary = "Deck 삭제", description = "Deck을 삭제합니다.")
    public void deleteDeck(@PathVariable Long id) {
        deckService.deleteDeck(id);
    }
}
