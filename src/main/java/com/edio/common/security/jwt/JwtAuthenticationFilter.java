package com.edio.common.security.jwt;

import com.edio.common.exception.base.ErrorMessages;
import com.edio.common.security.constants.CookieConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    // permitAll()로 설정된 엔드포인트인지 확인하는 메서드
    private boolean isPermitAllEndpoint(String requestURI) {
        return requestURI.startsWith("/swagger-ui") || requestURI.startsWith("/v3/api-docs");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestURI = httpRequest.getRequestURI();

        // permitAll()로 설정된 엔드포인트에 대해서는 JWT 검증을 수행하지 않고 다음 필터로 요청을 전달
        if (isPermitAllEndpoint(requestURI)) {
            handlePermitAllEndpoint(chain, request, response);
            return;
        }

        try{
            String accessToken = resolveAccessAndRefreshToken(httpRequest, "accessToken");
            if (accessToken != null) {
                boolean isValid = jwtTokenProvider.validateToken(accessToken);
                if (isValid) {
                    setAuthentication(accessToken);
                } else { // 토큰 검증 실패
                    throw new InsufficientAuthenticationException(ErrorMessages.TOKEN_EXPIRED.getMessage());
                }
            } else {
                try { // JwtTokenProvider에서 넘어온 예외를 처리하기 위한 'try-catch'
                    handleRefreshToken(httpRequest, httpResponse);
                    if (httpResponse.isCommitted()) {
                        return;
                    }
                } catch (InsufficientAuthenticationException e) {
                    throw new InsufficientAuthenticationException(ErrorMessages.TOKEN_EXPIRED.getMessage(), e);
                }
            }
            chain.doFilter(request, response);
        }catch(AuthenticationException e){
            SecurityContextHolder.clearContext();
            chain.doFilter(request, response); // ExceptionTranslationFilter로 전달하여 EntryPoint로 401 응답
        }
    }

    // permitAll() 엔드포인트 처리
    private void handlePermitAllEndpoint(FilterChain chain, ServletRequest request, ServletResponse response) throws IOException, ServletException {
        chain.doFilter(request, response);
    }

    // Access Token 유효성 검사 후 인증 설정
    private void setAuthentication(String accessToken) {
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // Refresh Token 처리 및 새로운 토큰 재생성
    private void handleRefreshToken(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
        try {
            String refreshToken = resolveAccessAndRefreshToken(httpRequest, "refreshToken");

            if (refreshToken == null) {
                throw new InsufficientAuthenticationException(ErrorMessages.TOKEN_EXPIRED.getMessage());
            }

            boolean isValid = jwtTokenProvider.validateToken(refreshToken);
            if (isValid) {
                JwtToken newTokens = jwtTokenProvider.refreshAccessAndRefreshTokens(refreshToken);
                setCookies(httpResponse, newTokens);
                setAuthentication(newTokens.getAccessToken());
                log.info("Access Token 및 Refresh Token 재생성 완료 및 쿠키에 설정");
            } else {
                throw new InsufficientAuthenticationException(ErrorMessages.TOKEN_EXPIRED.getMessage());
            }
        } catch (AuthenticationException e) {
            SecurityContextHolder.clearContext();
            throw e;
        }
    }

    // 쿠키 설정
    private void setCookies(HttpServletResponse httpResponse, JwtToken newTokens) {
        String accessTokenCookie = String.format(
                CookieConstants.COOKIE_ACCESS_TOKEN,
                newTokens.getAccessToken()
        );
        String refreshTokenCookie = String.format(
                CookieConstants.COOKIE_REFRESH_TOKEN,
                newTokens.getRefreshToken()
        );

        // HttpHeaders를 활용하여 쿠키 헤더 추가
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie);
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie);
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
