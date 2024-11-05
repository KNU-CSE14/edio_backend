package com.edio.user.service;

import com.edio.common.exception.BaseException;
import com.edio.user.domain.Members;
import com.edio.user.exception.AccountNotFoundException;
import com.edio.user.model.reponse.AccountResponse;
import com.edio.user.repository.AccountRepository;
import com.edio.user.domain.Accounts;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
                .orElseThrow(() -> new AccountNotFoundException(loginId));
        return AccountResponse.from(account);
    }

    /*
        Account 등록
     */
    @Override
    public AccountResponse createAccount(Accounts account) {
        Accounts savedAccount = accountRepository.findByLoginIdAndStatus(account.getLoginId(), "active")
                .orElseGet(() -> {
                    // 계정 생성
                    Accounts newAccount = new Accounts();
                    newAccount.setLoginId(account.getLoginId());
                    newAccount.setPassword(null);
                    newAccount.setDeletedAt(null);
                    newAccount.setStatus("active");
                    newAccount.setLoginType("google");
                    newAccount.setRoles("ROLE_USER");
                    return accountRepository.save(newAccount);
                });
        return AccountResponse.from(savedAccount);
    }
}
