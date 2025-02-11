package com.edio.studywithcard.card.controller;

import com.edio.studywithcard.card.model.request.CardBulkRequestWrapper;
import com.edio.studywithcard.card.service.CardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CardController implements CardApiDoc {

    private final CardService cardService;

    @PostMapping(value = "/cards", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Override
    public void createOrUpdateCards(@ModelAttribute CardBulkRequestWrapper cardBulkRequestWrapper) {
        cardService.createOrUpdateCards(cardBulkRequestWrapper);
    }

    @DeleteMapping("/cards")
    @Override
    public void deleteCards(@RequestBody List<Long> request) {
        cardService.deleteCards(request);
    }
}
