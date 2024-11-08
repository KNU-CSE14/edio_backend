package com.edio.service;

import com.edio.common.exception.ConflictException;
import com.edio.user.domain.Accounts;
import com.edio.user.model.request.AccountCreateRequest;
import com.edio.user.model.response.AccountResponse;
import com.edio.user.repository.AccountRepository;
import com.edio.user.service.AccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTests {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    public void createAccount_whenAccountDoesNotExist_createsNewAccount() {
        // given
        AccountCreateRequest account = new AccountCreateRequest();
        account.setLoginId("testUser");

        // when
        when(accountRepository.save(any(Accounts.class))).thenAnswer(invocation -> {
            return invocation.getArgument(0);
        });

        AccountResponse response = accountService.createAccount(account);

        // then
        assertThat(response).isNotNull();
        assertThat(response.loginId()).isEqualTo("testUser");
    }

    @Test
    public void createAccount_whenAccountExists_throwsConflictException() {
        // given
        AccountCreateRequest existingAccount = new AccountCreateRequest();
        existingAccount.setLoginId("testUser");

        // save 호출 시 DataIntegrityViolationException 발생하도록 설정
        when(accountRepository.save(any(Accounts.class))).thenThrow(new ConflictException(Accounts.class, existingAccount.getLoginId()));

        // when & then: ConflictException 발생을 기대함
        assertThatThrownBy(() -> accountService.createAccount(existingAccount))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("testUser");
    }

    // 잘못된 데이터로 요청이 들어온 경우 예외 발생
    @Test
    public void createAccount_whenLoginIdIsNull_throwsException() {
        // given
        AccountCreateRequest account = new AccountCreateRequest();
        account.setLoginId(null);

        // when, then
        assertThatThrownBy(() -> accountService.createAccount(account))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("null");
    }

    // 저장 실패 시 예외를 처리
    @Test
    public void createAccount_whenSaveFails_throwsException() {
        // given
        AccountCreateRequest account = new AccountCreateRequest();
        account.setLoginId("testUser");

        when(accountRepository.save(any(Accounts.class))).thenThrow(new RuntimeException("Database error"));

        // when, then
        assertThatThrownBy(() -> accountService.createAccount(account))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Database error");
    }
}
