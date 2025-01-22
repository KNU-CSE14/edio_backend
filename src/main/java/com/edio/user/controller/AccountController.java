package com.edio.user.controller;

import com.edio.common.model.response.SwaggerCommonResponses;
import com.edio.common.security.CustomUserDetails;
import com.edio.user.model.response.AccountResponse;
import com.edio.user.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Accounts", description = "Accounts 관련 API")
@SecurityRequirement(name = "bearerAuth")
@SwaggerCommonResponses
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/account")
    @Operation(summary = "Account 정보 조회", description = "Account 정보를 조회합니다.")
    public AccountResponse getAccount(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return accountService.findOneAccount(userDetails.getAccountId());
    }
}
