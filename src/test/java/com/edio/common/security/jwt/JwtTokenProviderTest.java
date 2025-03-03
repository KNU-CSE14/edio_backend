package com.edio.common.security.jwt;

import com.edio.common.properties.JwtProperties;
import com.edio.common.security.CustomUserDetails;
import com.edio.common.security.CustomUserDetailsService;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.security.Key;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Mockito 확장 활성화
class JwtTokenProviderTest {

    @Mock
    private CustomUserDetailsService userDetailsService;

    private JwtTokenProvider jwtTokenProvider;

    private JwtProperties jwtProperties;

    private String secretKey;

    @BeforeEach
    public void setUp() throws InterruptedException {
        // 시스템 환경변수 우선 조회
        secretKey = System.getenv("JWT_SECRET");
        // 없을 경우에만 .env 파일에서 로드
        if (secretKey == null || secretKey.isEmpty()) {
            Dotenv dotenv = Dotenv.load();
            secretKey = dotenv.get("JWT_SECRET");
        }
        jwtProperties = new JwtProperties(secretKey);
        jwtTokenProvider = new JwtTokenProvider(userDetailsService, jwtProperties);
    }

    /**
     * 토큰 생성 헬퍼 메서드
     *
     * @param subject    토큰의 subject (예: 로그인 ID)
     * @param auth       auth 값 (예: ROLE_USER), null인 경우 auth 클레임 없이 생성
     * @param expiration 토큰 만료 시간, null인 경우 만료 시간 미설정
     * @return 생성된 JWT 토큰 문자열
     */
    private String createToken(String subject, Object auth, Date expiration) {
        Claims claims = Jwts.claims().setSubject(subject);
        if (auth != null) {
            claims.put("auth", auth);
        }
        if (expiration != null) {
            claims.setExpiration(expiration);
        }

        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        Key key = Keys.hmacShaKeyFor(keyBytes);
        return Jwts.builder()
                .setClaims(claims)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Test
    void 정상_토큰_인증정보_반환() {
        // given
        Long accountId = 1L;
        Long rootFolderId = 1L;
        String loginId = "629jyh7@gmail.com";
        String token = createToken(loginId, "ROLE_USER", null);

        // Mock 사용자 정보 생성
        CustomUserDetails userDetails = new CustomUserDetails(
                accountId,
                rootFolderId,
                loginId,
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                Collections.emptyMap()
        );
        when(userDetailsService.loadUserByUsername(loginId)).thenReturn(userDetails);

        // when
        Authentication authentication = jwtTokenProvider.getAuthentication(token);

        // then
        assertNotNull(authentication, "Authentication 객체는 null이 아니어야 합니다.");
        assertEquals(loginId, authentication.getName(), "로그인 ID가 일치해야 합니다.");
        assertEquals("ROLE_USER", authentication.getAuthorities().iterator().next().getAuthority(), "권한은 ROLE_USER여야 합니다.");

        // verify: loadUserByUsername 메서드가 한 번 호출되었는지 검증
        verify(userDetailsService, times(1)).loadUserByUsername(loginId);
    }

    @Test
    void 잘못된_토큰_예외발생() {
        // given
        String invalidToken = "invalid.token.value";

        // when & then
        assertThrows(org.springframework.security.authentication.InsufficientAuthenticationException.class,
                () -> jwtTokenProvider.getAuthentication(invalidToken),
                "잘못된 토큰일 경우 InsufficientAuthenticationException 예외가 발생해야 합니다.");
    }

    @Test
    void 만료된_토큰_예외발생() {
        // given
        String subject = "expired-user@gmail.com";
        Date expiredDate = new Date(System.currentTimeMillis() - 60000); // 현재 시간보다 1분 이전
        String expiredToken = createToken(subject, "ROLE_USER", expiredDate);

        // when & then
        assertThrows(org.springframework.security.authentication.InsufficientAuthenticationException.class,
                () -> jwtTokenProvider.getAuthentication(expiredToken),
                "만료된 토큰일 경우 InsufficientAuthenticationException 예외가 발생해야 합니다.");
    }

    @Test
    void auth_클레임_없을때_예외발생() {
        // given
        String subject = "no-auth-user@gmail.com";
        // auth 클레임 없이 토큰 생성
        String tokenWithoutAuth = createToken(subject, null, null);

        // when & then
        assertThrows(org.springframework.security.authentication.InsufficientAuthenticationException.class,
                () -> jwtTokenProvider.getAuthentication(tokenWithoutAuth),
                "auth 클레임이 없으면 InsufficientAuthenticationException 예외가 발생해야 합니다.");
    }
}
