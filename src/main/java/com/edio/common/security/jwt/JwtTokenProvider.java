package com.edio.common.security.jwt;

import com.edio.common.exception.base.ErrorMessages;
import com.edio.common.properties.JwtProperties;
import com.edio.common.security.CustomUserDetails;
import com.edio.common.security.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private final CustomUserDetailsService userDetailsService;

    private final Key key;

    private final JwtProperties jwtProperties;

    private static final String ACCOUNT_ID = "accountId";
    private static final String CLAIM_AUTHORITY = "auth"; // 권한 정보를 저장하는 claim 키
    private static final long ONE_HOUR_IN_MILLISECONDS = 3_600_000L; // 1시간
    private static final long ONE_DAY_IN_MILLISECONDS = 86_400_000L; // 1일

    // application.properties에서 secret 값 가져와서 key에 저장
    public JwtTokenProvider(CustomUserDetailsService userDetailsService, JwtProperties jwtProperties) {
        this.userDetailsService = userDetailsService;
        this.jwtProperties = jwtProperties;
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // 토큰 생성
    public JwtToken createToken(OAuth2AuthenticationToken authentication) {
        // OAuth2AuthenticationToken에서 권한 정보 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String loginId = customUserDetails.getUsername(); // 사용자 ID
        Long accountId = customUserDetails.getAccountId();

        String accessToken = generateToken(loginId, authorities, accountId, ONE_HOUR_IN_MILLISECONDS);
        String refreshToken = generateToken(loginId, authorities, accountId, ONE_DAY_IN_MILLISECONDS);

        // JWT 토큰 정보를 담은 JwtToken 객체 생성 및 반환
        return JwtToken.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // Jwt 토큰을 복호화하여 사용자 정보를 가져오는 메서드
    public Authentication getAuthentication(String accessToken) {
        try {
            Claims claims = parseClaims(accessToken);
            String loginId = claims.getSubject();

            // 필수 클레임이 존재하는지 확인
            String authClaim = claims.get(CLAIM_AUTHORITY, String.class);

            if (authClaim == null || authClaim.trim().isEmpty()) {
                throw new InsufficientAuthenticationException(ErrorMessages.TOKEN_INVALID.getMessage());
            }

            // UserDetails에서 CustomUserDetails 반환
            CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(loginId);

            Collection<? extends GrantedAuthority> authorities = Arrays.stream(authClaim.split(","))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
        } catch (JwtException e) {
            throw new InsufficientAuthenticationException(ErrorMessages.TOKEN_INVALID.getMessage(), e);
        }
    }


    // 토큰 정보 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            log.error(e.getMessage());
            return false;
        }
    }

    // 토큰 복호화
    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 토큰 생성 로직 분리
    private String generateToken(String subject, String authorities, Long accountId, long expirationTime) {
        return Jwts.builder()
                .setSubject(subject)
                .claim(CLAIM_AUTHORITY, authorities)
                .claim(ACCOUNT_ID, accountId)
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // RefreshToken이 유효하면 토큰 재생성
    public JwtToken refreshAccessAndRefreshTokens(String refreshToken) {
        try {
            if (!validateToken(refreshToken)) {
                throw new InsufficientAuthenticationException(ErrorMessages.TOKEN_INVALID.getMessage());
            }

            Claims claims = parseClaims(refreshToken); // 예외 발생 시 처리할 수 있도록 `try-catch` 내부에서 호출
            Authentication authentication = getAuthentication(refreshToken);

            if (authentication == null || !authentication.isAuthenticated()) {
                throw new InsufficientAuthenticationException(ErrorMessages.AUTHENTICATION_FAILED.getMessage());
            }

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String loginId = userDetails.getUsername();
            Long accountId = userDetails.getAccountId();
            String authorities = claims.get(CLAIM_AUTHORITY, String.class);

            // Access Token & Refresh Token 생성
            String newAccessToken = generateToken(loginId, authorities, accountId, ONE_HOUR_IN_MILLISECONDS); // 1시간
            String newRefreshToken = generateToken(loginId, authorities, accountId, ONE_DAY_IN_MILLISECONDS); // 1일

            return JwtToken.builder()
                    .grantType("Bearer")
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .build();
        } catch (JwtException e) { // parseClaims()에서 발생하는 예외 처리
            throw new InsufficientAuthenticationException(ErrorMessages.TOKEN_INVALID.getMessage(), e);
        }
    }
}
