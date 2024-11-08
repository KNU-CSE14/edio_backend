package com.edio.user.repository;

import com.edio.user.domain.Accounts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Accounts, Long> {
    Optional<Accounts> findByLoginIdAndIsDeleted(String loginId, boolean isDeleted);
}
