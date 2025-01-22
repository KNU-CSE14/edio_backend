package com.edio.service;

import com.edio.user.domain.Account;
import com.edio.user.domain.Member;
import com.edio.user.domain.enums.AccountLoginType;
import com.edio.user.domain.enums.AccountRole;
import com.edio.user.model.response.AccountResponse;
import com.edio.user.repository.AccountRepository;
import com.edio.user.service.AccountServiceImpl;
import jakarta.persistence.EntityNotFoundException;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTests {

    /*
        FIXME: 현재 AccountService에 findOneAccount만 테스트 하고 있기 때문에, Respository 테스트도 추가 필요 
     */

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Account mockAccount;
    private Member mockMember;

    @BeforeEach
    public void setUp() {
        mockMember = Member.builder()
                .email("testUser@gmail.com")
                .name("Hong Gildong")
                .givenName("gildong")
                .familyName("Hong")
                .profileUrl("http://example.com/profile.jpg")
                .build();
        ReflectionTestUtils.setField(mockMember, "id", 1L);

        mockAccount = Account.builder()
                .loginId("testUser@gmail.com")
                .member(mockMember)
                .isDeleted(false)
                .loginType(AccountLoginType.GOOGLE)
                .roles(AccountRole.ROLE_USER)
                .build();
        ReflectionTestUtils.setField(mockAccount, "id", 1L);
    }

    @Test
    public void findOneAccount_whenAccountExists_returnsAccountResponse() {
        assertThat(mockAccount.getMember()).isNotNull();

        // given
        when(accountRepository.findByIdAndIsDeleted(1L, false))
                .thenReturn(Optional.of(mockAccount));

        // when
        AccountResponse response = accountService.findOneAccount(1L);

        // then
        assertThat(response).isNotNull();
        assertThat(response.loginId()).isEqualTo(mockAccount.getLoginId());
    }

    @Test
    public void findOneAccount_whenAccountDoesNotExist_throwsEntityNotFoundException() {
        // given
        when(accountRepository.findByIdAndIsDeleted(1L, false))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> accountService.findOneAccount(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Account");
    }
}
