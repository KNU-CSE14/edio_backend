package com.edio.studywithcard.account.repository;

import com.edio.common.config.JpaConfig;
import com.edio.user.domain.Account;
import com.edio.user.domain.Member;
import com.edio.user.domain.enums.AccountLoginType;
import com.edio.user.domain.enums.AccountRole;
import com.edio.user.repository.AccountRepository;
import com.edio.user.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        testMember = memberRepository.save(Member.builder()
                .email("testUser@gmail.com")
                .name("Hong Gildong")
                .givenName("gildong")
                .familyName("Hong")
                .profileUrl("http://example.com/profile.jpg")
                .build());
        testAccount = accountRepository.save(Account.builder()
                .loginId("testUser@gmail.com")
                .member(testMember)
                .loginType(AccountLoginType.GOOGLE)
                .roles(AccountRole.ROLE_USER)
                .build());
    }

    /**
     * 사용자 저장 & 조회 (findByLoginId)
     */
    @Test
    @DisplayName("Save And FindByLoginId -> (성공)")
    void saveAndFindByLoginId() {
        // When
        Account account = accountRepository.findByLoginIdAndIsDeletedFalse(testAccount.getLoginId())
                .orElseThrow(() -> new AssertionError("Account not found"));

        // Then
        assertThat(account.getLoginId()).isEqualTo("testUser@gmail.com");
        assertThat(account.isDeleted()).isFalse();
        assertThat(account.getLoginType()).isEqualTo(AccountLoginType.GOOGLE);
        assertThat(account.getRoles()).isEqualTo(AccountRole.ROLE_USER);
    }

    /**
     * 존재하지 않는 로그인 ID로 계정 조회 -> (실패)
     */
    @Test
    @DisplayName("FindAccount by Non-existent LoginId -> (실패)")
    void findAccountByNonExistentLoginId() {
        // Given
        String nonExistentLoginId = "nonexistent@gmail.com";

        // When & Then
        assertThrows(AssertionError.class, () -> {
            accountRepository.findByLoginIdAndIsDeletedFalse(nonExistentLoginId)
                    .orElseThrow(() -> new AssertionError("Account not found"));
        });
    }

    /**
     * 사용자 저장 & 조회 (findById)
     */
    @Test
    @DisplayName("Save And FindById -> (성공)")
    void saveAndFindById() {
        // When
        Account account = accountRepository.findByIdAndIsDeletedFalse(testAccount.getId())
                .orElseThrow(() -> new AssertionError("Account not found"));

        // Then
        assertThat(account.getLoginId()).isEqualTo("testUser@gmail.com");
        assertThat(account.isDeleted()).isFalse();
        assertThat(account.getLoginType()).isEqualTo(AccountLoginType.GOOGLE);
        assertThat(account.getRoles()).isEqualTo(AccountRole.ROLE_USER);
    }

    /**
     * 존재하지 않는 ID로 계정 조회 -> (실패)
     */
    @Test
    @DisplayName("FindAccount by Non-existent Id -> (실패)")
    void findAccountByNonExistentId() {
        // Given
        Long nonExistentId = 999L;

        // When & Then
        assertThrows(AssertionError.class, () -> {
            accountRepository.findByIdAndIsDeletedFalse(nonExistentId)
                    .orElseThrow(() -> new AssertionError("Account not found"));
        });
    }

    /**
     * Soft Delete 후 데이터 유지 여부
     */
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
