package com.edio.common.security.jwt;

import com.edio.common.security.CustomUserDetails;
import com.edio.common.security.CustomUserDetailsService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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

    // application.properties에서 secret 값 가져와서 key에 저장
    public JwtTokenProvider(CustomUserDetailsService userDetailsService, @Value("${jwt.secret}") String secretKey) {
        this.userDetailsService = userDetailsService;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public JwtToken createToken(OAuth2AuthenticationToken authentication) {
        // OAuth2AuthenticationToken에서 권한 정보 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String loginId = customUserDetails.getUsername(); // 사용자 ID
        Long accountId = customUserDetails.getAccountId();

        // Access Token 생성 (1시간 유효)
        long now = (new Date()).getTime();
        Date accessTokenExpiresIn = new Date(now + 3600000); // 1시간
        String accessToken = Jwts.builder()
                .setSubject(loginId) // 사용자 이름 설정
                .claim("auth", authorities) // 권한 정보 설정
                .claim("accountId", accountId)
                .setExpiration(accessTokenExpiresIn) // 만료 시간 설정
                .signWith(key, SignatureAlgorithm.HS256) // 서명 알고리즘 및 비밀 키 사용
                .compact();

        // Refresh Token 생성 (1일 유효)
        String refreshToken = Jwts.builder()
                .setSubject(loginId)
                .claim("auth", authorities)
                .claim("accountId", accountId)
                .setExpiration(new Date(now + 86400000)) // 1일 만료
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // JWT 토큰 정보를 담은 JwtToken 객체 생성 및 반환
        return JwtToken.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // Jwt 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken) {
        // Jwt 토큰 복호화
        Claims claims = parseClaims(accessToken);

        String loginId = claims.getSubject();

        // UserDetails에서 CustomUserDetails 반환
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(loginId);

        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }

    // 토큰 정보를 검증하는 메서드
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
            return false;  // 만료된 토큰인 경우
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

    // 토큰 복호화
    public Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
//            return e.getClaims();
            throw e;
        }
    }

    public JwtToken refreshAccessAndRefreshTokens(String refreshToken) {
        // Refresh Token 검증
        if (validateToken(refreshToken)) {
            Claims claims = parseClaims(refreshToken);

            // 기존 클레임에서 사용자 정보 가져오기
            String authorities = claims.get("auth", String.class);

            // SecurityContext에서 현재 사용자 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new RuntimeException("User is not authenticated");
            }
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String loginId = userDetails.getUsername(); // loginId
            Long accountId = userDetails.getAccountId();

            long now = (new Date()).getTime();
            // 새로운 Access Token 생성 (1시간 유효)
            Date accessTokenExpiresIn = new Date(now + 3600000); // 1시간 유효
            String newAccessToken = Jwts.builder()
                    .setSubject(loginId)
                    .claim("auth", authorities) // 권한 정보 설정
                    .claim("accountId", accountId) // loginId 정보 추가
                    .setExpiration(accessTokenExpiresIn) // 만료 시간 설정
                    .signWith(key, SignatureAlgorithm.HS256) // 서명 알고리즘 및 비밀 키 사용
                    .compact();

            // 새로운 Refresh Token 생성 (1일 유효)
            Date refreshTokenExpiresIn = new Date(now + 86400000); // 1일 유효
            String newRefreshToken = Jwts.builder()
                    .setSubject(loginId) // 사용자 이름 설정
                    .claim("auth", authorities) // 권한 정보 설정
                    .claim("accountId", accountId) // loginId 정보 추가
                    .setExpiration(refreshTokenExpiresIn) // 만료 시간 설정
                    .signWith(key, SignatureAlgorithm.HS256) // 서명 알고리즘 및 비밀 키 사용
                    .compact();

            // 새로운 Access Token 및 Refresh Token을 포함한 JwtToken 객체 반환
            return JwtToken.builder()
                    .grantType("Bearer")
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .build();
        }
        throw new RuntimeException("Invalid refresh token");
    }
}
