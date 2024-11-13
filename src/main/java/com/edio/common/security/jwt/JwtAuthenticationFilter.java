package com.edio.common.security.jwt;

import com.edio.common.exception.NotFoundException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    // permitAll()로 설정된 엔드포인트인지 확인하는 메서드
    private boolean isPermitAllEndpoint(String requestURI) {
        return requestURI.startsWith("/api/account") ||
                requestURI.startsWith("/api/auth") ||
                requestURI.startsWith("/swagger-ui") ||
                requestURI.startsWith("/v3/api-docs");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestURI = httpRequest.getRequestURI();

        // permitAll()로 설정된 엔드포인트에 대해서는 JWT 검증을 수행하지 않고 다음 필터로 요청을 전달
        if (isPermitAllEndpoint(requestURI)) {
            handlePermitAllEndpoint(chain, request, response, requestURI);
            return;
        }

        try {
            String accessToken = resolveAccessAndRefreshToken(httpRequest, "accessToken");
            if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
                setAuthentication(accessToken);
            } else {
                handleRefreshToken(httpRequest, httpResponse);
            }
        } catch (JwtException | IllegalArgumentException e) {
            handleInvalidToken(httpResponse, e);
            return;
        } catch (NotFoundException e) {
            handleAccountNotFound(httpResponse, e);
            return;
        }

        chain.doFilter(request, response);
    }

    // permitAll() 엔드포인트 처리
    private void handlePermitAllEndpoint(FilterChain chain, ServletRequest request, ServletResponse response, String requestURI) throws IOException, ServletException {
        logger.info("PermitAll endpoint, skipping JWT validation for URI: " + requestURI);
        chain.doFilter(request, response);
        logger.info("After chain.doFilter - Response Status: " + ((HttpServletResponse) response));
    }

    // Access Token 유효성 검사 후 인증 설정
    private void setAuthentication(String accessToken) {
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // Refresh Token 처리 및 새로운 토큰 재생성
    private void handleRefreshToken(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
        String refreshToken = resolveAccessAndRefreshToken(httpRequest, "refreshToken");

        if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
            JwtToken newTokens = jwtTokenProvider.refreshAccessAndRefreshTokens(refreshToken);
            setCookies(httpResponse, newTokens);
            setAuthentication(newTokens.getAccessToken());
            logger.info("Access Token 및 Refresh Token 재생성 완료 및 쿠키에 설정");
        } else {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Refresh token이 유효하지 않거나 없습니다. 다시 로그인해주세요.");
        }
    }

    // 쿠키 설정
    private void setCookies(HttpServletResponse httpResponse, JwtToken newTokens) {
        Cookie accessTokenCookie = new Cookie("accessToken", newTokens.getAccessToken());
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(false); // HTTPS 환경에서는 true로 설정
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(3600); // Access Token 유효 시간 설정 (1시간)

        Cookie refreshTokenCookie = new Cookie("refreshToken", newTokens.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false); // HTTPS 환경에서는 true로 설정
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(86400); // Refresh Token 유효 시간 설정 (1일)

        httpResponse.addCookie(accessTokenCookie);
        httpResponse.addCookie(refreshTokenCookie);
    }

    // 유효하지 않은 토큰 처리
    private void handleInvalidToken(HttpServletResponse httpResponse, Exception e) throws IOException {
        logger.info(e.getMessage());
        SecurityContextHolder.clearContext();
        httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 토큰입니다.");
    }

    // 계정이 없을 때 처리
    private void handleAccountNotFound(HttpServletResponse httpResponse, NotFoundException e) throws IOException {
        logger.info(e.getMessage());
        SecurityContextHolder.clearContext();
        httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "계정을 찾을 수 없습니다. 다시 로그인해주세요.");
    }

    // AccessToken & RefreshToken 추출
    private String resolveAccessAndRefreshToken(HttpServletRequest request, String tokenType) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (tokenType.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
