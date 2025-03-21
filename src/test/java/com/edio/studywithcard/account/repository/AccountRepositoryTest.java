package com.edio.studywithcard.account.repository;

import com.edio.common.config.JpaConfig;
import com.edio.user.domain.Account;
import com.edio.user.domain.Member;
import com.edio.user.domain.enums.AccountLoginType;
import com.edio.user.domain.enums.AccountRole;
import com.edio.user.repository.AccountRepository;
import com.edio.user.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(JpaConfig.class)
@ActiveProfiles("h2")
public class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MemberRepository memberRepository;

    private static final String email = "testUser@gmail.com";
    private static final String name = "Hong Gildong";
    private static final String givenName = "gildong";
    private static final String familyName = "Hong";
    private static final String profileUrl = "http://example.com/profile.jpg";
    private static final String nonExistentLoginId = "nonexistent@gmail.com";
    private static final Long nonExistentId = 999L;

    private Member testMember;
    private Account testAccount;

    @BeforeEach
    void setUp() {
        testMember = memberRepository.save(Member.builder()
                .email(email)
                .name(name)
                .givenName(givenName)
                .familyName(familyName)
                .profileUrl(profileUrl)
                .build());
        testAccount = accountRepository.save(Account.builder()
                .loginId(email)
                .member(testMember)
                .loginType(AccountLoginType.GOOGLE)
                .roles(AccountRole.ROLE_USER)
                .build());
    }

    @Test
    @DisplayName("로그인 ID로 조회 -> (성공)")
    void saveAndFindByLoginId() {
        // When
        Account account = accountRepository.findByLoginIdAndIsDeletedFalse(testAccount.getLoginId())
                .orElseThrow();

        // Then
        assertThat(account.getLoginId()).isEqualTo(testAccount.getLoginId());
        assertThat(account.isDeleted()).isFalse();
        assertThat(account.getLoginType()).isEqualTo(AccountLoginType.GOOGLE);
        assertThat(account.getRoles()).isEqualTo(AccountRole.ROLE_USER);
    }

    @Test
    @DisplayName("존재하지 않는 로그인 ID로 계정 조회 -> (실패)")
    void findAccountByNonExistentLoginId() {
        // When & Then
        assertThatThrownBy(() ->
                accountRepository.findByLoginIdAndIsDeletedFalse(nonExistentLoginId)
                        .orElseThrow()
        ).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("ID로 조회 -> (성공)")
    void saveAndFindById() {
        // When
        Account account = accountRepository.findByIdAndIsDeletedFalse(testAccount.getId())
                .orElseThrow();

        // Then
        assertThat(account.getLoginId()).isEqualTo(testAccount.getLoginId());
        assertThat(account.isDeleted()).isFalse();
        assertThat(account.getLoginType()).isEqualTo(AccountLoginType.GOOGLE);
        assertThat(account.getRoles()).isEqualTo(AccountRole.ROLE_USER);
    }

    @Test
    @DisplayName("존재하지 않는 ID로 계정 조회 -> (실패)")
    void findAccountByNonExistentId() {
        // When & Then
        assertThatThrownBy(() ->
                accountRepository.findByIdAndIsDeletedFalse(nonExistentId)
                        .orElseThrow()
        ).isInstanceOf(NoSuchElementException.class);
    }

     /*
        TODO: SQLDelete 사용한 Soft Delete 코드 merge 후 추가 테스트 예정 (계정 삭제 API 추가 필요)
    @Test
    @DisplayName("Soft Delete And NotFoundAccount -> (성공)")
    void softDeleteAccount(){
        // Given
        memberRepository.save(testMember);
        accountRepository.save(testAccount);
        entityManager.flush();
        entityManager.clear();

        // When
        accountRepository.delete(testAccount);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<Account> deletedAccount = accountRepository.findByIdAndIsDeletedFalse(testAccount.getId());
        assertThat(deletedAccount).isEmpty();

        Long count = (Long) entityManager.createQuery(
                "SELECT COUNT(a) FROM account a where a.id = :id")
                .setParameter("id", 1L)
                .getSingleResult();

        assertThat(count).isEqualTo(1);
    }
    */
}
