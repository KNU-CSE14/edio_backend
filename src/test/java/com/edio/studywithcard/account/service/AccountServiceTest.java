package com.edio.studywithcard.account.service;

import com.edio.common.TestConstants;
import com.edio.user.domain.Account;
import com.edio.user.domain.Member;
import com.edio.user.domain.enums.AccountLoginType;
import com.edio.user.domain.enums.AccountRole;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    private static final Long memberId = 1L;
    private static final Long accountId = 1L;
    private static final String email = "testUser@gmail.com";
    private static final String name = "Hong Gildong";
    private static final String givenName = "Gildong";
    private static final String familyName = "Hong";
    private static final String profileUrl = "http://example.com/profile.jpg";
    private static final AccountLoginType loginType = AccountLoginType.GOOGLE;
    private static final AccountRole role = AccountRole.ROLE_USER;

    private Account mockAccount;
    private Member mockMember;

    @BeforeEach
    public void setUp() {
        mockMember = Member.builder()
                .id(TestConstants.Account.MEMBER_ID)
                .email(TestConstants.Account.EMAIL)
                .name(TestConstants.Account.NAME)
                .givenName(TestConstants.Account.GIVEN_NAME)
                .familyName(TestConstants.Account.FAMILY_NAME)
                .profileUrl(TestConstants.Account.PROFILE_URL)
                .build();

        mockAccount = Account.builder()
                .id(TestConstants.Account.ACCOUNT_ID)
                .loginId(TestConstants.Account.EMAIL)
                .member(mockMember)
                .loginType(TestConstants.Account.LOGIN_TYPE)
                .roles(TestConstants.Account.ROLE)
                .build();
    }

    @Test
    @DisplayName("계정 ID로 계정 조회 -> (성공)")
    public void 계정_ID_계정_조회() {
        // Given
        assertThat(mockAccount.getMember()).isNotNull();
        when(accountRepository.findByIdAndIsDeletedFalse(TestConstants.Account.ACCOUNT_ID)).thenReturn(Optional.of(mockAccount));

        // When
        AccountResponse response = accountService.findOneAccount(TestConstants.Account.ACCOUNT_ID);

        // Then
        verify(accountRepository, times(1)).findByIdAndIsDeletedFalse(TestConstants.Account.ACCOUNT_ID);
        assertThat(response).isNotNull();
        assertThat(response.loginId()).isEqualTo(mockAccount.getLoginId());
    }

    @Test
    @DisplayName("존재하지 않는 ID로 계정 조회 -> (실패)")
    public void 존재하지_않는_ID_계정_조회() {
        // When & Then
        Assertions.assertThatThrownBy(() ->
                accountService.findOneAccount(TestConstants.Account.ACCOUNT_ID)
        ).isInstanceOf(NoSuchElementException.class);
    }
}
