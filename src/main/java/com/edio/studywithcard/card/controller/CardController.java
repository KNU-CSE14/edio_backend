package com.edio.studywithcard.card.controller;

import com.edio.common.model.response.SwaggerCommonResponses;
import com.edio.studywithcard.card.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Tag(name = "Card", description = "Card 관련 API")
@RestController
@RequestMapping("/api")
public class CardController {

    @Autowired
    private CardService cardService;

    @GetMapping("/card/{cardId}")
    @Operation(summary = "Card ID 조회", description = "카드 ID를 조회합니다.")
//  Header Token 설정
//  security = @SecurityRequirement(name = "bearerAuth"))
    @SwaggerCommonResponses //Swagger 공통 응답 어노테이션
    public Long getCard(@Parameter(required = true, description = "Card ID") @PathVariable long cardId){
        return cardId;
    }

    @GetMapping("/card/select")
    @Operation(summary = "Card ID 조회", description = "카드 ID를 조회합니다.")
//  Header Token 설정
//  security = @SecurityRequirement(name = "bearerAuth"))
    @SwaggerCommonResponses //Swagger 공통 응답 어노테이션
    public List<String> getCarTest(){
        return cardService.getCardTest();
    }



    @Autowired
    private DataSource dataSource;

    @GetMapping("/health/db")
    public ResponseEntity<String> checkDatabaseConnection() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection != null && !connection.isClosed()) {
                return ResponseEntity.ok("Database connection is OK");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database connection is not established");
            }
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to connect to database: " + e.getMessage());
        }
    }
}
