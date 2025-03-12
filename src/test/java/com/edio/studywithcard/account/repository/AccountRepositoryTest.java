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

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaConfig.class)
public class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MemberRepository memberRepository;

    @PersistenceContext
    private EntityManager entityManager;

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
        testAccount = Account.builder()
                .loginId("testUser@gmail.com")
                .member(testMember)
                .loginType(AccountLoginType.GOOGLE)
                .roles(AccountRole.ROLE_USER)
                .build();
    }

    /**
     * 1. 사용자 저장 & 조회 (findByLoginId)
     */
    @Test
    @DisplayName("Save And FindByLoginId -> (성공)")
    void saveAndFindByLoginId() {
        // Given
        accountRepository.save(testAccount);

        // When
        Optional<Account> findAccount = accountRepository.findByLoginIdAndIsDeletedFalse(testAccount.getLoginId());

        // Then
        assertThat(findAccount).isPresent();
        assertThat(findAccount.get().getLoginId()).isEqualTo("testUser@gmail.com");
        assertThat(findAccount.get().isDeleted()).isFalse();
        assertThat(findAccount.get().getLoginType()).isEqualTo(AccountLoginType.GOOGLE);
        assertThat(findAccount.get().getRoles()).isEqualTo(AccountRole.ROLE_USER);
    }

    /**
     * 2. 사용자 저장 & 조회 (findById)
     */
    @Test
    @DisplayName("Save And FindById -> (성공)")
    void saveAndFindById() {
        // Given
        accountRepository.save(testAccount);

        // When
        Optional<Account> findAccount = accountRepository.findByIdAndIsDeletedFalse(testAccount.getId());

        // Then
        assertThat(findAccount).isPresent();
        assertThat(findAccount.get().getLoginId()).isEqualTo("testUser@gmail.com");
        assertThat(findAccount.get().isDeleted()).isFalse();
        assertThat(findAccount.get().getLoginType()).isEqualTo(AccountLoginType.GOOGLE);
        assertThat(findAccount.get().getRoles()).isEqualTo(AccountRole.ROLE_USER);
    }

    /**
     * 3. Soft Delete 후 데이터 유지 여부
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
