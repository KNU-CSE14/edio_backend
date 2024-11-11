package com.edio.common.security;

import com.edio.common.exception.NotFoundException;
import com.edio.user.domain.Account;
import com.edio.user.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    public CustomUserDetailsService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        Account account = accountRepository.findByLoginIdAndIsDeleted(loginId, false)
//                .orElseThrow(() -> new UsernameNotFoundException("Account not found: " + loginId));
                .orElseThrow(() -> new NotFoundException(Account.class, loginId));

        // 권한이 단일한 경우 처리 (ROLE_USER와 같은 하나의 역할을 가정)
        GrantedAuthority authority = new SimpleGrantedAuthority(account.getRoles().name());
        Collection<GrantedAuthority> authorities = Collections.singletonList(authority);

        return new org.springframework.security.core.userdetails.User(
                account.getLoginId(),
                account.getPassword(),  // 비밀번호
                true,                   // 활성 계정인지 여부
                true,                   // 계정 만료 여부
                true,                   // 자격 증명 만료 여부
                true,                   // 계정 잠금 여부
                authorities             // 권한 리스트
        );
    }
}
