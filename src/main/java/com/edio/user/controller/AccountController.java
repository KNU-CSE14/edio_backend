package com.edio.user.controller;

import com.edio.common.security.CustomUserDetails;
import com.edio.user.model.response.AccountResponse;
import com.edio.user.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccountController implements AccountApiDoc {

    private final AccountService accountService;

    @GetMapping("/account")
    @Override
    public AccountResponse getAccount(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return accountService.findOneAccount(userDetails.getAccountId());
    }
}
