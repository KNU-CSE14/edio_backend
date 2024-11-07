package com.edio.user.controller;

import com.edio.common.model.response.SwaggerCommonResponses;
import com.edio.user.model.request.AccountRequest;
import com.edio.user.model.response.AccountResponse;
import com.edio.user.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Accounts", description = "Accounts 관련 API")
@SecurityRequirement(name = "bearerAuth")
@SwaggerCommonResponses
@RestController
@RequestMapping("/api")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/account")
    @Operation(summary = "Account 정보 조회", description = "Account 정보를 조회합니다.")
    public AccountResponse getAccount(@Parameter(required = true, description = "사용자 아이디") String loginId) {
        return accountService.findOneAccount(loginId);
    }

    @PostMapping("/account")
    @Operation(summary = "Account 등록", description = "Account를 등록합니다.")
    public AccountResponse createAccount(@RequestBody AccountRequest accountRequest) {
        return accountService.createAccount(accountRequest);
    }

}
