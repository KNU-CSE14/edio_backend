package com.edio.user.controller;

import com.edio.common.model.response.SwaggerCommonResponses;
import com.edio.studywithcard.card.service.CardService;
import com.edio.user.model.reponse.AccountResponse;
import com.edio.user.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Accounts", description = "Accounts 관련 API")
@RestController
@RequestMapping("/api")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/account")
    @Operation(summary = "Account 정보 조회", description = "Account 정보를 조회합니다.")
    @SwaggerCommonResponses
    public AccountResponse getAccount(@Parameter(required = true, description = "사용자 아이디") String loginId){
        return accountService.findOneAccount(loginId);
    }


}
