package com.edio.user.service;

import com.edio.user.domain.Accounts;
import com.edio.user.model.request.AccountRequest;
import com.edio.user.model.response.AccountResponse;

public interface AccountService {
    // Account 조회
    AccountResponse findOneAccount(String loginId);
    // Account 등록
    AccountResponse createAccount(AccountRequest accountRequest);
}
