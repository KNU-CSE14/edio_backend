package com.edio.user.service;

import com.edio.user.model.response.AccountResponse;

public interface AccountService {
    // Account 조회
    AccountResponse findOneAccount(Long accountId);
}
