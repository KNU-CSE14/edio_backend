package com.edio.service;

import com.edio.common.exception.ConflictException;
import com.edio.user.domain.Account;
import com.edio.user.domain.Member;
import com.edio.user.domain.enums.AccountLoginType;
import com.edio.user.domain.enums.AccountRole;
import com.edio.user.model.request.AccountCreateRequest;
import com.edio.user.model.response.AccountResponse;
import com.edio.user.repository.AccountRepository;
import com.edio.user.repository.MemberRepository;
import com.edio.user.service.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTests {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    private AccountCreateRequest accountRequest;
    private Member mockMember;
    private Account mockAccount;

    @BeforeEach
    public void setUp() {
        // 공통 테스트 데이터 초기화
        accountRequest = new AccountCreateRequest();
        accountRequest.setLoginId("testUser@gmail.com");
        accountRequest.setMemberId(1L);

        mockMember = Member.builder()
                .email("testUser@gmail.com")
                .name("Hong Gildong")
                .givenName("Hong")
                .familyName("Gildong")
                .profileUrl("http://example.com/profile.jpg")
                .build();
        ReflectionTestUtils.setField(mockMember, "id", accountRequest.getMemberId());

        mockAccount = Account.builder()
                .loginId(accountRequest.getLoginId())
                .member(mockMember)
                .loginType(AccountLoginType.GOOGLE) // 기본값 가정
                .roles(AccountRole.ROLE_USER)          // 기본값 가정
                .build();
    }

    @Test
    public void createAccount_whenAccountDoesNotExist_createsNewAccount() {
        // given
        when(memberRepository.findById(accountRequest.getMemberId()))
                .thenReturn(Optional.of(mockMember));
        when(accountRepository.save(any(Account.class)))
                .thenReturn(mockAccount);

        // when
        AccountResponse response = accountService.createAccount(accountRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.memberResponse().email()).isEqualTo(mockMember.getEmail());
    }

    @Test
    public void createAccount_whenAccountExists_throwsConflictException() {
        // given
        when(memberRepository.findById(accountRequest.getMemberId()))
                .thenReturn(Optional.of(mockMember));
        when(accountRepository.save(any(Account.class)))
                .thenThrow(new ConflictException(Account.class, mockAccount.getLoginId()));

        // when & then
        assertThatThrownBy(() -> accountService.createAccount(accountRequest))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("testUser@gmail.com");
    }

    @Test
    public void createAccount_whenLoginIdIsNull_throwsException() {
        // given
        accountRequest.setLoginId(null);

        when(memberRepository.findById(accountRequest.getMemberId()))
                .thenReturn(Optional.of(mockMember));

        // when, then
        assertThatThrownBy(() -> accountService.createAccount(accountRequest))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("null");
    }

    @Test
    public void createAccount_whenSaveFails_throwsException() {
        // given
        when(memberRepository.findById(accountRequest.getMemberId()))
                .thenReturn(Optional.of(mockMember));
        when(accountRepository.save(any(Account.class)))
                .thenThrow(new RuntimeException("Database error"));

        // when, then
        assertThatThrownBy(() -> accountService.createAccount(accountRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Database error");
    }
}
