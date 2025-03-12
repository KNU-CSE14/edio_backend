package com.edio.user.service;

import com.edio.common.exception.base.ErrorMessages;
import com.edio.user.domain.Account;
import com.edio.user.model.response.AccountResponse;
import com.edio.user.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    /*
        Account 조회(active)
     */
    @Transactional(readOnly = true)
    @Override
    public AccountResponse findOneAccount(Long accountId) {
        Account account = accountRepository.findByIdAndIsDeletedFalse(accountId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.NOT_FOUND_ENTITY.format(Account.class.getSimpleName(), accountId)));
        return AccountResponse.from(account);
    }
}
