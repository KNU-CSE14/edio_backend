package com.edio.user.repository;

import com.edio.user.domain.Accounts;
import com.edio.user.domain.Members;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Members, Long> {
    Optional<Members> findByAccountId(long accountId);
}
