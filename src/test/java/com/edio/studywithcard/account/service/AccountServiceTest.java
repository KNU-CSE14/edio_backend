package com.edio.studywithcard.account.service;

import com.edio.user.domain.Account;
import com.edio.user.domain.Member;
import com.edio.user.model.response.AccountResponse;
import com.edio.user.repository.AccountRepository;
import com.edio.user.service.AccountServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static com.edio.common.TestConstants.User.ACCOUNT_ID;
import static com.edio.common.util.TestUserUtil.createAccount;
import static com.edio.common.util.TestUserUtil.createMember;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Account mockAccount;
    private Member mockMember;

    @BeforeEach
    public void setUp() {
        mockMember = createMember();
        mockAccount = createAccount(mockMember);
    }

    @Test
    @DisplayName("계정 ID로 계정 조회 -> (성공)")
    public void 계정_ID_계정_조회() {
        // Given
        assertThat(mockAccount.getMember()).isNotNull();
        when(accountRepository.findByIdAndIsDeletedFalse(ACCOUNT_ID)).thenReturn(Optional.of(mockAccount));

        // When
        AccountResponse response = accountService.findOneAccount(ACCOUNT_ID);

        // Then
        verify(accountRepository, times(1)).findByIdAndIsDeletedFalse(ACCOUNT_ID);
        assertThat(response).isNotNull();
        assertThat(response.loginId()).isEqualTo(mockAccount.getLoginId());
    }

    @Test
    @DisplayName("존재하지 않는 ID로 계정 조회 -> (실패)")
    public void 존재하지_않는_ID_계정_조회() {
        // When & Then
        Assertions.assertThatThrownBy(() ->
                accountService.findOneAccount(ACCOUNT_ID)
        ).isInstanceOf(NoSuchElementException.class);
    }
}
