package com.edio.user.service;

import com.edio.common.exception.NotFoundException;
import com.edio.user.domain.enums.AccountLoginType;
import com.edio.user.domain.enums.AccountRole;
import com.edio.user.model.reponse.AccountResponse;
import com.edio.user.repository.AccountRepository;
import com.edio.user.domain.Accounts;
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
        Accounts account = accountRepository.findByLoginIdAndStatus(loginId, true)
                .orElseThrow(() -> new NotFoundException(Accounts.class, loginId));
        return AccountResponse.from(account);
    }

    /*
        Account 등록
     */
    @Override
    @Transactional
    public AccountResponse createAccount(Accounts account) {
        Accounts savedAccount = accountRepository.findByLoginIdAndStatus(account.getLoginId(), true)
                .orElseGet(() -> {
                    Accounts newAccount = Accounts.builder()
                            .loginId(account.getLoginId())
                            .loginType(AccountLoginType.GOOGLE) // 기본값을 사용하지 않고 명시적으로 설정
                            .roles(AccountRole.ROLE_USER) // 기본값을 사용하지 않고 명시적으로 설정
                            .build();
                    return accountRepository.save(newAccount);
                });
        return AccountResponse.from(savedAccount);
    }
}
