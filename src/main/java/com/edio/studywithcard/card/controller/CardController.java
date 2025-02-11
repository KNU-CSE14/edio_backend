package com.edio.studywithcard.card.controller;

import com.edio.studywithcard.card.model.request.CardBulkRequestWrapper;
import com.edio.studywithcard.card.model.request.CardCreateRequest;
import com.edio.studywithcard.card.model.request.CardDeleteRequest;
import com.edio.studywithcard.card.model.request.CardUpdateRequest;
import com.edio.studywithcard.card.model.response.CardResponse;
import com.edio.studywithcard.card.service.CardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CardController implements CardApiDoc {

    private final CardService cardService;

    @PostMapping(value = "/card", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Override
    public CardResponse createCard(@RequestPart CardCreateRequest request,
                                   @RequestPart(value = "file", required = false) MultipartFile[] files) {
        return cardService.createCard(request, files);
    }

    @PatchMapping(value = "/card", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Override
    public void updateCard(@RequestPart CardUpdateRequest request,
                           @RequestPart(value = "file", required = false) MultipartFile[] files) {
        cardService.updateCard(request, files);
    }

    @DeleteMapping("/card")
    @Override
    public void deleteCard(@RequestBody CardDeleteRequest request) {
        cardService.deleteCard(request);
    }

    /*
        다중 처리를 위한 테스트 API
     */
    @PostMapping(value = "/cards", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Override
    public void createOrUpdateCards(@ModelAttribute CardBulkRequestWrapper cardBulkRequestWrapper) {
        cardService.createOrUpdateCard(cardBulkRequestWrapper);
    }
}
