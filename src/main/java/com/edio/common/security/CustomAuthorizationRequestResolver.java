package com.edio.common.security;

import com.edio.common.exception.UnprocessableException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class CustomAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final OAuth2AuthorizationRequestResolver defaultResolver;

    private final ObjectMapper objectMapper;

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest authorizationRequest = defaultResolver.resolve(request);
        return customizeAuthorizationRequest(authorizationRequest, request);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest authorizationRequest = defaultResolver.resolve(request, clientRegistrationId);
        return customizeAuthorizationRequest(authorizationRequest, request);
    }

    private OAuth2AuthorizationRequest customizeAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request) {
        if (authorizationRequest == null) {
            return null;
        }

        // CSRF 토큰 생성 및 프론트에서 전달된 state 값 가져오기
        String csrfToken = UUID.randomUUID().toString(); // CSRF 방지용 랜덤 값 생성
        String frontendState = request.getParameter("state"); // 프론트에서 전달된 state

        // state 값을 JSON 형태로 생성 (CSRF 토큰 + 리다이렉트 URI)
        Map<String, String> stateMap = new HashMap<>();
        stateMap.put("csrfToken", csrfToken);
        stateMap.put("redirectUri", frontendState); // 프론트에서 전달된 state를 redirectUri로 처리

        String encodedState = null;
        try {
            String stateJson = objectMapper.writeValueAsString(stateMap); // JSON 변환
            encodedState = Base64.getEncoder().encodeToString(stateJson.getBytes(StandardCharsets.UTF_8)); // Base64 인코딩
        } catch (Exception e) {
            throw new UnprocessableException(CustomAuthorizationRequestResolver.class, stateMap);
        }

        return OAuth2AuthorizationRequest.from(authorizationRequest)
                .state(encodedState) // state에 인코딩된 값 설정
                .build();
    }
}
