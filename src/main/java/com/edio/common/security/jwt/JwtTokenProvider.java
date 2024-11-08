package com.edio.common.security.jwt;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenProvider {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Key key;

    // application.properties에서 secret 값 가져와서 key에 저장
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public JwtToken createToken(OAuth2AuthenticationToken authentication) {
        // OAuth2AuthenticationToken에서 권한 정보 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();

        // AccessToken에 loginId를 넣어주기 위해 아이디 가져오기
        String loginId = String.valueOf(authentication.getPrincipal().getAttributes().get("email"));

        // Access Token 생성 (1시간 유효)
        Date accessTokenExpiresIn = new Date(now + 3600000); // 1시간
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName()) // 사용자 이름 설정
                .claim("auth", authorities) // 권한 정보 설정
                .claim("loginId", loginId) // loginId 정보 추가
                .setExpiration(accessTokenExpiresIn) // 만료 시간 설정
                .signWith(key, SignatureAlgorithm.HS256) // 서명 알고리즘 및 비밀 키 사용
                .compact();

        // Refresh Token 생성 (1일 유효)
        String refreshToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities)
                .claim("loginId", loginId) // loginId 정보 추가
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

        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
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
            logger.info("Expired JWT Token", e);
            return false;  // 만료된 토큰인 경우
        } catch (SecurityException | MalformedJwtException e) {
            logger.info("Invalid JWT Token", e);
        } catch (UnsupportedJwtException e) {
            logger.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            logger.info("JWT claims string is empty.", e);
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
            return e.getClaims();
        }
    }

    public JwtToken refreshAccessAndRefreshTokens(String refreshToken) {
        // Refresh Token 검증
        if (validateToken(refreshToken)) {
            Claims claims = parseClaims(refreshToken);

            // 기존 클레임에서 사용자 정보 가져오기
            String username = claims.getSubject();
            String authorities = claims.get("auth", String.class);
            String loginId = claims.get("loginId", String.class);  // loginId를 클레임에서 가져옴

            // 현재 시간
            long now = (new Date()).getTime();

            // 새로운 Access Token 생성 (1시간 유효)
            Date accessTokenExpiresIn = new Date(now + 3600000); // 1시간 유효
            String newAccessToken = Jwts.builder()
                    .setSubject(username) // 사용자 이름 설정
                    .claim("auth", authorities) // 권한 정보 설정
                    .claim("loginId", loginId) // loginId 정보 추가
                    .setExpiration(accessTokenExpiresIn) // 만료 시간 설정
                    .signWith(key, SignatureAlgorithm.HS256) // 서명 알고리즘 및 비밀 키 사용
                    .compact();

            // 새로운 Refresh Token 생성 (1일 유효)
            Date refreshTokenExpiresIn = new Date(now + 86400000); // 1일 유효
            String newRefreshToken = Jwts.builder()
                    .setSubject(username) // 사용자 이름 설정
                    .claim("auth", authorities) // 권한 정보 설정
                    .claim("loginId", loginId) // loginId 정보 추가
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
