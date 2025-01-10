package com.edio.common.security;

import com.edio.common.exception.custom.NotFoundException;
import com.edio.user.domain.Account;
import com.edio.user.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        Account account = accountRepository.findByLoginIdAndIsDeleted(loginId, false)
                .orElseThrow(() -> new NotFoundException(Account.class, loginId));

        GrantedAuthority authority = new SimpleGrantedAuthority(account.getRoles().name());
        Collection<GrantedAuthority> authorities = Collections.singletonList(authority);

        return new CustomUserDetails(
                account.getId(),
                account.getRootFolderId(),
                account.getLoginId(),
                authorities,
                Collections.emptyMap()
        );
    }
}
