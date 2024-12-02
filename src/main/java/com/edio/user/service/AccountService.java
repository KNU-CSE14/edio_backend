package com.edio.user.service;

import com.edio.user.model.request.AccountCreateRequest;
import com.edio.user.model.response.AccountResponse;

public interface AccountService {
    // AccountId 조회
    Long getAccountIdByLoginId(String loginId);

    // Account 조회
    AccountResponse findOneAccount(Long accountId);

    // Account 등록
    AccountResponse createAccount(AccountCreateRequest accountCreateRequest);

    // RootFolderId 등록
    void updateRootFolderId(Long accountId, Long rootFolderId);
}
