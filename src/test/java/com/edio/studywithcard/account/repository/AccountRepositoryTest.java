package com.edio.studywithcard.account.repository;

import com.edio.common.config.JpaConfig;
import com.edio.user.domain.Account;
import com.edio.user.domain.Member;
import com.edio.user.repository.AccountRepository;
import com.edio.user.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.NoSuchElementException;

import static com.edio.common.TestConstants.User.NON_EXISTENT_ID;
import static com.edio.common.TestConstants.User.NON_EXISTENT_LOGIN_ID;
import static com.edio.common.util.TestUserUtil.account;
import static com.edio.common.util.TestUserUtil.member;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(JpaConfig.class)
public class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member testMember;
    private Account testAccount;

    @BeforeEach
    void setUp() {
        testMember = memberRepository.save(member());
        testAccount = accountRepository.save(account(testMember));
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
        assertThat(account.getLoginType()).isEqualTo(testAccount.getLoginType());
        assertThat(account.getRoles()).isEqualTo(testAccount.getRoles());
    }

    @Test
    @DisplayName("존재하지 않는 로그인 ID로 계정 조회 -> (실패)")
    void findAccountByNonExistentLoginId() {
        // When & Then
        assertThatThrownBy(() ->
                accountRepository.findByLoginIdAndIsDeletedFalse(NON_EXISTENT_LOGIN_ID)
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
        assertThat(account.getLoginType()).isEqualTo(testAccount.getLoginType());
        assertThat(account.getRoles()).isEqualTo(testAccount.getRoles());
    }

    @Test
    @DisplayName("존재하지 않는 ID로 계정 조회 -> (실패)")
    void findAccountByNonExistentId() {
        // When & Then
        assertThatThrownBy(() ->
                accountRepository.findByIdAndIsDeletedFalse(NON_EXISTENT_ID)
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
