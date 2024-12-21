package com.edio.studywithcard.card.controller;

import com.edio.common.model.response.SwaggerCommonResponses;
import com.edio.studywithcard.card.model.request.CardCreateRequest;
import com.edio.studywithcard.card.model.response.CardResponse;
import com.edio.studywithcard.card.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
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
}
