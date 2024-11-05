package com.edio.user.service;

import com.edio.common.exception.BaseException;
import com.edio.common.exception.NotFoundException;
import com.edio.user.exception.AccountNotFoundException;
import com.edio.user.model.reponse.AccountResponse;
import com.edio.user.repository.AccountRepository;
import com.edio.user.domain.Accounts;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountServiceImpl implements AccountService{

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /*
        Account 조회(active)
     */
    @Transactional(readOnly = true)
    @Override
    public AccountResponse findOneAccount(String loginId) {
        Accounts account = accountRepository.findByLoginIdAndStatus(loginId, "active")
                .orElseThrow(() -> new NotFoundException("account", loginId));
        return AccountResponse.from(account);
    }
}
