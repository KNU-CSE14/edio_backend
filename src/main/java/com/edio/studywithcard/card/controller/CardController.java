package com.edio.studywithcard.card.controller;

import com.edio.common.model.response.SwaggerCommonResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Card", description = "Card 관련 API")
@RestController
@RequestMapping("/api")
public class CardController {

    @GetMapping("/card/{cardId}")
    @Operation(summary = "Card ID 조회", description = "카드 ID를 조회합니다.")
//  Header Token 설정
//  security = @SecurityRequirement(name = "bearerAuth"))
    @SwaggerCommonResponses //Swagger 공통 응답 어노테이션
    public Long getCard(@Parameter(required = true, description = "Card ID") @PathVariable long cardId){
        return cardId;
    }
}
