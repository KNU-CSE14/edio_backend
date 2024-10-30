package com.edio.common.security;

import com.edio.common.security.jwt.JwtAuthenticationFilter;
import com.edio.common.security.jwt.JwtToken;
import com.edio.common.security.jwt.JwtTokenProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtTokenProvider jwtTokenProvider;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, JwtTokenProvider jwtTokenProvider) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(AbstractHttpConfigurer::disable) // Json을 통한 로그인 진행으로 refresh 토큰 만료 전까지 토큰 인증
                .formLogin(AbstractHttpConfigurer::disable) // Json을 통한 로그인 진행으로 refresh 토큰 만료 전까지 토큰 인증
//              .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정 활성화
			    .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(
                                "/api/auth/refresh",
                                "/oauth2/authorization/google",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                                ).permitAll()
                        .requestMatchers("/api/card/**").hasRole("USER")
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler((request, response, authentication) -> {
                            System.out.println("로그인 성공!");
                            OAuth2AuthenticationToken auth = (OAuth2AuthenticationToken) authentication;
                            JwtToken jwtToken = jwtTokenProvider.createToken(auth);

                            String accessToken = jwtToken.getAccessToken();
                            String refreshToken = jwtToken.getRefreshToken();

                            // 쿠키 생성 및 설정
                            Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
                            accessTokenCookie.setHttpOnly(true); // JavaScript로 접근 불가하게 설정
                            accessTokenCookie.setSecure(false); // HTTP에서도 쿠키 전송 가능하게 설정
                            accessTokenCookie.setPath("/"); // 쿠키의 경로 설정
                            accessTokenCookie.setMaxAge(3600); // 쿠키 유효 기간 (1시간)

                            Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
                            refreshTokenCookie.setHttpOnly(true);
                            refreshTokenCookie.setSecure(false);
                            refreshTokenCookie.setPath("/");
                            refreshTokenCookie.setMaxAge(86400); // 쿠키 유효 기간 (1일)

                            response.addCookie(accessTokenCookie);
                            response.addCookie(refreshTokenCookie);

                            System.out.println("jwt 성공!");
//                          response.sendRedirect("http://localhost:3000/oauth2/redirect");
                        })
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((request, response, authException) -> {
                            // 인증되지 않은 요청에 대해 401 응답
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                        })
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // OAuth 2.0 인증 흐름에서 액세스 토큰을 처리하는 클라이언트
    @Bean
    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
        return new DefaultAuthorizationCodeTokenResponseClient();
    }

    // Cors를 활성화하면 코드 적용
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        43.203.169.54:8080
//        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
//        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//        configuration.setAllowedHeaders(Arrays.asList("*"));
//        configuration.setAllowCredentials(true); // 쿠키를 포함한 요청 허용
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
}
