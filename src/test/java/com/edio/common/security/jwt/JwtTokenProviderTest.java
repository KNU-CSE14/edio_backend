package com.edio.common.security.jwt;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.security.Key;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserDetailsService userDetailsService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // .env 파일에서 JWT_SECRET 값 로드하여 시스템 속성 설정
        Dotenv dotenv = Dotenv.load();
        System.setProperty("jwt.secret", dotenv.get("JWT_SECRET"));

        // System.getProperty를 통해 secretKey 값을 가져옴
        String secretKey = System.getProperty("jwt.secret");
        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalArgumentException("JWT_SECRET 환경 변수가 설정되지 않았습니다.");
        }

        // secretKey를 Base64로 디코딩하여 키 생성
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        // JwtTokenProvider 인스턴스 생성
        jwtTokenProvider = new JwtTokenProvider(userDetailsService, secretKey);
    }

    @Test
    public void testGetAuthentication() {
        // Given
        String testAccessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJZVU5IV0FOIEpFT05HIiwiYXV0aCI6IlJPTEVfVVNFUiIsImxvZ2luSWQiOiI2MjlqeWg3QGdtYWlsLmNvbSIsImV4cCI6MTczMTA1ODQzNH0.bJQQH1TJ15TEfvF-_caEAitS2FzUxBZxx-AL0VYBWZo";  // JWT 토큰 샘플

        // When
        Authentication authentication = jwtTokenProvider.getAuthentication(testAccessToken);

        // Then
        assertNotNull(authentication);
    }
}