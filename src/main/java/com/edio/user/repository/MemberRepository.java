package com.edio.user.repository;

import com.edio.user.domain.Members;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Members, Long> {
}
