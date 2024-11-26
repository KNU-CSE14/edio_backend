package com.edio.common.security;

import com.edio.common.security.jwt.JwtToken;
import com.edio.common.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Value("${redirect.url}")
    private String redirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        log.info("로그인 성공!");

        OAuth2AuthenticationToken auth = (OAuth2AuthenticationToken) authentication;
        JwtToken jwtToken = jwtTokenProvider.createToken(auth);

        String accessToken = jwtToken.getAccessToken();
        String refreshToken = jwtToken.getRefreshToken();

//        // 쿠키 생성 및 설정
        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true); // HTTP 환경이므로 false
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(3600);

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true); // HTTP 환경이므로 false
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(86400);

//        // 쿠키를 응답에 추가
//        response.addCookie(accessTokenCookie);
//        response.addCookie(refreshTokenCookie);

        response.addHeader("Set-Cookie",
                String.format("accessToken=%s; HttpOnly; Secure; Path=/; Max-Age=3600; SameSite=None", accessToken));
        response.addHeader("Set-Cookie",
                String.format("refreshToken=%s; HttpOnly; Secure; Path=/; Max-Age=86400; SameSite=None", refreshToken));

        log.info("jwt 성공!");
        response.sendRedirect(redirectUrl);
    }
}
