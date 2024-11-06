package com.edio.user;

import com.edio.user.domain.Accounts;
import com.edio.user.model.reponse.AccountResponse;
import com.edio.user.repository.AccountRepository;
import com.edio.user.service.AccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.AuditorAware;
import java.util.Optional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserControllerUnitTests {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AuditorAware<String> auditorAware;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    public void createAccount_whenAccountDoesNotExist_createsNewAccount() {
        // given
        Accounts account = new Accounts();
        account.setLoginId("testUser");

        // when
        when(accountRepository.findByLoginIdAndStatus(account.getLoginId(), true)).thenReturn(Optional.empty());
        when(accountRepository.save(any(Accounts.class))).thenAnswer(invocation -> {
            return invocation.getArgument(0);
        });

        AccountResponse response = accountService.createAccount(account);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getLoginId()).isEqualTo("testUser");
    }

    // 계정이 이미 존재할 때 기존 계정을 반환
    @Test
    public void createAccount_whenAccountExists_returnsExistingAccount() {
        // given
        Accounts existingAccount = Accounts.builder()
                .loginId("testUser")
                .build();

        when(accountRepository.findByLoginIdAndStatus(existingAccount.getLoginId(), true)).thenReturn(Optional.of(existingAccount));

        // when
        AccountResponse response = accountService.createAccount(existingAccount);

        // then
        assertThat(response).isNotNull();
        assertThat(response.loginId()).isEqualTo("testUser");
    }

    // 잘못된 데이터로 요청이 들어온 경우 예외 발생
    @Test
    public void createAccount_whenLoginIdIsNull_throwsException() {
        // given
        Accounts account = Accounts.builder()
                .loginId(null)
                .build();

        // when, then
        assertThatThrownBy(() -> accountService.createAccount(account))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("null");
    }

    // 저장 실패 시 예외를 처리
    @Test
    public void createAccount_whenSaveFails_throwsException() {
        // given
        Accounts account = Accounts.builder()
                .loginId("testUser")
                .build();

        when(accountRepository.save(any(Accounts.class))).thenThrow(new RuntimeException("Database error"));

        // when, then
        assertThatThrownBy(() -> accountService.createAccount(account))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Database error");
    }
}
