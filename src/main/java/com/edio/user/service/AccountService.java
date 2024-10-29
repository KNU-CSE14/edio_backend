package com.edio.user.service;

import com.edio.user.domain.Accounts;
import com.edio.user.model.reponse.AccountResponse;

import java.util.Optional;

public interface AccountService {
    //회원 조회
    AccountResponse findOneAccount(String loginId);
}
