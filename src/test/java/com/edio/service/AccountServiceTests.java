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

    @Test
    public void createAccount_whenAccountDoesNotExist_createsNewAccount() {
        // given
        AccountCreateRequest accountRequest = new AccountCreateRequest();
        accountRequest.setLoginId("testUser@gmail.com");
        accountRequest.setMemberId(1L); // 요청값 설정

        // Mock Member 생성
        Member mockMember = Member.builder()
                .email("testUser@gmail.com")
                .name("Hong Gildong")
                .givenName("Hong")
                .familyName("Gildong")
                .profileUrl("http://example.com/profile.jpg")
                .build();
        ReflectionTestUtils.setField(mockMember, "id", accountRequest.getMemberId());

        // Mock Account 생성
        Account mockAccount = Account.builder()
                .loginId(accountRequest.getLoginId())
                .member(mockMember)
                .loginType(AccountLoginType.GOOGLE) // 기본값 가정
                .roles(AccountRole.ROLE_USER)          // 기본값 가정
                .build();

        // Mock Repository 동작 설정
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
        AccountCreateRequest existingAccount = new AccountCreateRequest();
        existingAccount.setLoginId("testUser");
        existingAccount.setMemberId(1L);

        // Mock Member 생성
        Member mockMember = Member.builder()
                .email("testUser@gmail.com")
                .name("Hong Gildong")
                .givenName("Hong")
                .familyName("Gildong")
                .profileUrl("http://example.com/profile.jpg")
                .build();
        ReflectionTestUtils.setField(mockMember, "id", existingAccount.getMemberId());

        // Mock Account 생성
        Account mockAccount = Account.builder()
                .loginId(existingAccount.getLoginId())
                .member(mockMember)
                .loginType(AccountLoginType.GOOGLE) // 기본값 가정
                .roles(AccountRole.ROLE_USER)          // 기본값 가정
                .build();

        // Mock Repository 동작 설정
        when(memberRepository.findById(existingAccount.getMemberId()))
                .thenReturn(Optional.of(mockMember)); // memberId에 대한 Member 반환
        when(accountRepository.save(any(Account.class)))
                .thenThrow(new ConflictException(Account.class, mockAccount.getLoginId()));

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
        account.setMemberId(1L);

        // Mock Member 생성
        Member mockMember = Member.builder()
                .email("testUser@gmail.com")
                .name("Hong Gildong")
                .givenName("Hong")
                .familyName("Gildong")
                .profileUrl("http://example.com/profile.jpg")
                .build();
        ReflectionTestUtils.setField(mockMember, "id", account.getMemberId());

        // Mock Repository 동작 설정
        when(memberRepository.findById(account.getMemberId()))
                .thenReturn(Optional.of(mockMember)); // memberId에 대한 Member 반환

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
        account.setMemberId(1L);

        // Mock Member 생성
        Member mockMember = Member.builder()
                .email("testUser@gmail.com")
                .name("Hong Gildong")
                .givenName("Hong")
                .familyName("Gildong")
                .profileUrl("http://example.com/profile.jpg")
                .build();
        ReflectionTestUtils.setField(mockMember, "id", account.getMemberId());

        // Mock Repository 동작 설정
        when(memberRepository.findById(account.getMemberId()))
                .thenReturn(Optional.of(mockMember)); // memberId에 대한 Member 반환
        when(accountRepository.save(any(Account.class))).thenThrow(new RuntimeException("Database error"));

        // when, then
        assertThatThrownBy(() -> accountService.createAccount(account))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Database error");
    }
}
