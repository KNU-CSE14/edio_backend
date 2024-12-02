package com.edio.user.repository;

import com.edio.user.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByLoginIdAndIsDeleted(String loginId, boolean isDeleted);

    Optional<Account> findByIdAndIsDeleted(Long accountId, boolean isDeleted);
}
