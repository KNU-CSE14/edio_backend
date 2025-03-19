package com.edio.common.security.jwt;

import com.edio.common.TestConstants;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.security.Key;
import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

    // ==================== 헬퍼 메서드 ====================
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
    @DisplayName("정상 토큰 인증 정보 반환 -> (성공)")
    void 정상_토큰_인증정보_반환() {
        // Given
        String token = createToken(TestConstants.Account.EMAIL, TestConstants.Account.ROLE, null);

        // Mock 사용자 정보 생성
        CustomUserDetails userDetails = new CustomUserDetails(
                TestConstants.Account.ACCOUNT_ID,
                TestConstants.Folder.ROOT_FOLDER_ID,
                TestConstants.Account.EMAIL,
                Collections.singleton(new SimpleGrantedAuthority(String.valueOf(TestConstants.Account.ROLE))),
                Collections.emptyMap()
        );

        // When
        when(userDetailsService.loadUserByUsername(TestConstants.Account.EMAIL)).thenReturn(userDetails);

        Authentication authentication = jwtTokenProvider.getAuthentication(token);

        // Then
        assertNotNull(authentication);
        assertEquals(TestConstants.Account.EMAIL, authentication.getName());
        assertEquals(String.valueOf(TestConstants.Account.ROLE), authentication.getAuthorities().iterator().next().getAuthority());
        verify(userDetailsService, times(1)).loadUserByUsername(TestConstants.Account.EMAIL);
    }

    @Test
    @DisplayName("잘못된 토큰으로 예외 발생 -> (실패)")
    void 잘못된_토큰_예외발생() {
        // When & Then
        assertThatThrownBy(() ->
                jwtTokenProvider.getAuthentication(TestConstants.Account.INVALID_TOKEN)
        ).isInstanceOf(InsufficientAuthenticationException.class);
    }

    @Test
    @DisplayName("만료된 토큰으로 예외 발생 -> (실패)")
    void 만료된_토큰_예외발생() {
        // Given
        Date expiredDate = new Date(System.currentTimeMillis() - 60000); // 현재 시간보다 1분 이전
        String expiredToken = createToken(TestConstants.Account.EMAIL, TestConstants.Account.ROLE, expiredDate);

        // When & Then
        assertThatThrownBy(() ->
                jwtTokenProvider.getAuthentication(expiredToken)
        ).isInstanceOf(InsufficientAuthenticationException.class);
    }

    @Test
    @DisplayName("Auth 클레임이 없을 경우 예외 발생 -> (실패)")
    void auth_클레임_없을때_예외발생() {
        // Given
        String tokenWithoutAuth = createToken(TestConstants.Account.EMAIL, null, null);

        // When & Then
        assertThatThrownBy(() ->
                jwtTokenProvider.getAuthentication(tokenWithoutAuth)
        ).isInstanceOf(InsufficientAuthenticationException.class);
    }
}
