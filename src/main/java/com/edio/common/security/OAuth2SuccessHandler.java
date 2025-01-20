package com.edio.common.security;

import com.edio.common.exception.base.ErrorMessages;
import com.edio.common.security.constants.CookieConstants;
import com.edio.common.security.jwt.JwtToken;
import com.edio.common.security.jwt.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        log.info("로그인 성공!");

        OAuth2AuthenticationToken auth = (OAuth2AuthenticationToken) authentication;
        JwtToken jwtToken = jwtTokenProvider.createToken(auth);

        String accessToken = jwtToken.getAccessToken();
        String refreshToken = jwtToken.getRefreshToken();

        // 쿠키 생성 및 설정
        String accessTokenCookie = String.format(CookieConstants.COOKIE_ACCESS_TOKEN, accessToken);
        String refreshTokenCookie = String.format(CookieConstants.COOKIE_REFRESH_TOKEN, refreshToken);

        log.info(accessTokenCookie);
        log.info(refreshTokenCookie);

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie);
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie);

        String encodedState = request.getParameter("state"); // OAuth Provider에서 반환된 state 값

        // state 디코딩 및 JSON 복원
        String decodedState = new String(Base64.getDecoder().decode(encodedState), StandardCharsets.UTF_8);
        Map<String, String> stateMap = objectMapper.readValue(decodedState, Map.class);

        // CSRF 토큰 검증
        String csrfToken = stateMap.get("csrfToken");
        if (!isValidCsrfToken(csrfToken)) {
            throw new AccessDeniedException(ErrorMessages.INVALID_CSRF_TOKEN.getMessage());
        }

        // 복원된 redirectUri로 리다이렉트
        String redirectUri = stateMap.get("redirectUri");
        if (redirectUri == null || redirectUri.isEmpty()) {
            redirectUri = "/"; // 기본값 설정
        }
        log.info("jwt 성공!");
        response.sendRedirect(redirectUri);
    }

    private boolean isValidCsrfToken(String csrfToken) {
        // 실제 CSRF 토큰 검증 로직 (세션 또는 DB를 참조하여 검증)
        return true; // 항상 유효하다고 가정
    }
}
