package com.edio.studywithcard.deck.controller;

import com.edio.studywithcard.deck.model.request.DeckCreateRequest;
import com.edio.studywithcard.deck.model.request.DeckDeleteRequest;
import com.edio.studywithcard.deck.model.request.DeckMoveRequest;
import com.edio.studywithcard.deck.model.request.DeckUpdateRequest;
import com.edio.studywithcard.deck.model.response.DeckResponse;
import com.edio.studywithcard.deck.service.DeckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DeckController implements DeckApiDoc {

    private final DeckService deckService;

    @GetMapping("/deck")
    @Override
    public DeckResponse getDeck(@RequestParam(value = "id") Long id) {
        return deckService.getDeck(id);
    }

    @PostMapping(value = "/deck", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Override
    public DeckResponse createDeck(@RequestPart(value = "request") DeckCreateRequest request,
                                   @RequestPart(value = "file", required = false) MultipartFile file) {
        return deckService.createDeck(request, file);
    }

    @PatchMapping(value = "/deck", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Override
    public void updateDeck(@RequestPart(value = "request") DeckUpdateRequest request,
                           @RequestPart(value = "file", required = false) MultipartFile file) {
        deckService.updateDeck(request, file);
    }

    @PatchMapping("/deck/position")
    @Override
    public void moveDeck(@RequestBody DeckMoveRequest request) {
        deckService.moveDeck(request);
    }

    @DeleteMapping("/deck")
    @Override
    public void deleteDeck(@RequestBody DeckDeleteRequest request) {
        deckService.deleteDeck(request);
    }
}
