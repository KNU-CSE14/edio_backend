package com.edio.service.unit;

import com.edio.user.domain.Accounts;
import com.edio.user.model.reponse.AccountResponse;
import com.edio.user.repository.AccountRepository;
import com.edio.user.service.AccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
        Accounts account = Accounts.builder()
                .loginId("testUser")
                .build();

        // when
        when(accountRepository.findByLoginIdAndStatus(account.getLoginId(), "active")).thenReturn(Optional.empty());
        when(accountRepository.save(any(Accounts.class))).thenAnswer(invocation -> {
            return invocation.getArgument(0);
        });

        AccountResponse response = accountService.createAccount(account);

        // then
        assertThat(response).isNotNull();
        assertThat(response.loginId()).isEqualTo("testUser");
    }
}
