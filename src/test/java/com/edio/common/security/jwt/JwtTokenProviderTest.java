package com.edio.common.security.jwt;

import com.edio.common.BaseTest;
import com.edio.common.security.CustomUserDetails;
import com.edio.common.security.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Mockito 확장 활성화
class JwtTokenProviderTest {

    @Mock
    private CustomUserDetailsService userDetailsService;

    private JwtTokenProvider jwtTokenProvider;

    private String secretKey;

    @BeforeEach
    public void setUp() throws InterruptedException {
        BaseTest.setUp();
        secretKey = System.getProperty("jwt.secret");
        jwtTokenProvider = new JwtTokenProvider(userDetailsService, secretKey);
    }

    @Test
    void testGetAuthentication() {
        // given
        Long accountId = 1L;
        String loginId = "629jyh7@gmail.com";
        Claims claims = Jwts.claims().setSubject(loginId);
        claims.put("auth", "ROLE_USER");

        String token = Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        // Mock CustomUserDetails
        CustomUserDetails userDetails = new CustomUserDetails(
                accountId,
                loginId,
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                Collections.emptyMap()
        );
        when(userDetailsService.loadUserByUsername(loginId)).thenReturn(userDetails);

        // when
        Authentication authentication = jwtTokenProvider.getAuthentication(token);

        // then
        assertNotNull(authentication); // Authentication 객체가 null이 아닌지 확인
        assertEquals(loginId, authentication.getName()); // 로그인 ID가 Claims에 설정된 값과 같은지 확인
        assertEquals("ROLE_USER", authentication.getAuthorities().iterator().next().getAuthority()); // 권한 확인

        // verify: UserDetailsService가 호출되었는지 확인
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
