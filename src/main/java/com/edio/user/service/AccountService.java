package com.edio.user.service;

import com.edio.user.domain.Accounts;
import com.edio.user.model.reponse.AccountResponse;

import java.util.Optional;

public interface AccountService {
    // Account 조회
    AccountResponse findOneAccount(String loginId);
    // Account 등록
    AccountResponse createAccount(Accounts account);
}
