package com.edio.common.security.jwt;

import com.edio.common.security.CustomUserDetailsService;
import com.edio.user.service.AccountService;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtTokenProviderTest {

    @Mock
    private CustomUserDetailsService userDetailsService;

    private JwtTokenProvider jwtTokenProvider;

    private AccountService accountService;

    private String secretKey;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // .env 파일에서 JWT_SECRET 값 로드하여 시스템 속성 설정
        Dotenv dotenv = Dotenv.load();
        System.setProperty("jwt.secret", dotenv.get("JWT_SECRET"));
        secretKey = System.getProperty("jwt.secret");

        jwtTokenProvider = new JwtTokenProvider(userDetailsService, accountService, secretKey);
    }

    @Test
    void testGetAuthentication() {
        // given
        String loginId = "629jyh7@gmail.com";
        Claims claims = Jwts.claims().setSubject(loginId);
        claims.put("auth", "ROLE_USER");
        claims.put("accountId", 1L);

        String token = Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                loginId,
                "oauth_password",
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
        );
        when(userDetailsService.loadUserByUsername(loginId)).thenReturn(userDetails);

        // when
        Authentication authentication = jwtTokenProvider.getAuthentication(token);

        // then
        assertNotNull(authentication);
        assertEquals(loginId, authentication.getName());
        assertEquals("ROLE_USER", authentication.getAuthorities().iterator().next().getAuthority());

        verify(userDetailsService, times(1)).loadUserByUsername(loginId);
    }

    @Test
    void testGetAuthenticationWithInvalidToken() {
        // given
        String invalidToken = "invalid.token.value";

        // when & then
        assertThrows(io.jsonwebtoken.MalformedJwtException.class,
                () -> jwtTokenProvider.getAuthentication(invalidToken));
    }

    @Test
    void testGetAuthenticationWithExpiredToken() {
        // given
        Claims claims = Jwts.claims().setSubject("expired-user@gmail.com");
        claims.put("auth", "ROLE_USER"); // 필요한 키 추가
        claims.put("accountId", 1L);    // accountId와 같은 값도 명시적으로 추가
        claims.setExpiration(new Date(System.currentTimeMillis() - 60000)); // 만료된 토큰

        String expiredToken = Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        // when & then
        assertThrows(io.jsonwebtoken.ExpiredJwtException.class,
                () -> jwtTokenProvider.getAuthentication(expiredToken));
    }
}
