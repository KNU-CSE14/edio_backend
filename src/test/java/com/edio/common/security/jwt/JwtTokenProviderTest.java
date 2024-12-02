package com.edio.common.security.jwt;

import com.edio.common.security.CustomUserDetailsService;
import com.edio.user.service.AccountService;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class JwtTokenProviderTest {

    @Mock
    private CustomUserDetailsService userDetailsService;

    private JwtTokenProvider jwtTokenProvider;

    private AccountService accountService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // .env 파일에서 JWT_SECRET 값 로드하여 시스템 속성 설정
        Dotenv dotenv = Dotenv.load();
        System.setProperty("jwt.secret", dotenv.get("JWT_SECRET"));
        String secretKey = System.getProperty("jwt.secret");

        jwtTokenProvider = new JwtTokenProvider(userDetailsService, accountService, secretKey);
    }

    @Test
    void testGetAuthentication() {
        // given
        String loginId = "629jyh7@gmail.com";
        String accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI2MjlqeWg3QGdtYWlsLmNvbSIsImF1dGgiOiJST0xFX1VTRVIiLCJhY2NvdW50SWQiOjEsImV4cCI6MTczMzEyMDIwOX0.lqXvronNieziBWCxZRiK8dVKwZrBd8-jOsF3S4ZveNY";
        Claims claims = Jwts.claims().setSubject(loginId);
        claims.put("auth", "ROLE_USER");

        // Mocking UserDetailsService
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                loginId,
                "oauth_password",
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
        );
        when(userDetailsService.loadUserByUsername(loginId)).thenReturn(userDetails);

        // when
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);

        // then
        assertNotNull(authentication);
        assertEquals(loginId, authentication.getName());
        assertEquals("ROLE_USER", authentication.getAuthorities().iterator().next().getAuthority());

        verify(userDetailsService, times(1)).loadUserByUsername(loginId);
    }
}