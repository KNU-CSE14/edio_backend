package com.edio.common.security.jwt;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // permitAll()로 설정된 엔드포인트인지 확인하는 메서드
    private boolean isPermitAllEndpoint(String requestURI) {
        return  requestURI.startsWith("/api/account") ||
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
            logger.info("PermitAll endpoint, skipping JWT validation for URI: " + requestURI);
            chain.doFilter(request, response);
            logger.info("After chain.doFilter - Response Status: " + ((HttpServletResponse) response));
            return;
        }

        try {
            // Cookie에서 JWT 토큰 추출
            String accessToken = resolveAccessAndRefreshToken(httpRequest, "accessToken");

            // Access Token 유효성 검사
            if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
                // Access Token이 유효한 경우
                Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                // Access Token이 유효하지 않은 경우 Refresh Token 확인
                String refreshToken = resolveAccessAndRefreshToken(httpRequest, "refreshToken");

                if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
                    // Refresh Token이 유효한 경우 새로운 Access Token 및 Refresh Token 재생성
                    JwtToken newTokens = jwtTokenProvider.refreshAccessAndRefreshTokens(refreshToken);

                    // 새로운 Access Token 및 Refresh Token을 쿠키에 설정
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

                    // 응답에 쿠키 추가
                    httpResponse.addCookie(accessTokenCookie);
                    httpResponse.addCookie(refreshTokenCookie);

                    logger.info("Access Token 및 Refresh Token 재생성 완료 및 쿠키에 설정");

                    // 새로 생성된 Access Token으로 SecurityContext 설정
                    Authentication authentication = jwtTokenProvider.getAuthentication(newTokens.getAccessToken());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    // Refresh Token이 유효하지 않거나 없는 경우
                    httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Refresh token이 유효하지 않거나 없습니다. 다시 로그인해주세요.");
                    return;
                }
            }
        } catch (JwtException | IllegalArgumentException e) {
            logger.info(e.getMessage());
            SecurityContextHolder.clearContext();
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 토큰입니다.");
            return;
        }
        chain.doFilter(request, response);
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
