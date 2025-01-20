package com.edio.common.exception.custom;

import com.edio.common.exception.base.ErrorMessages;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        try {
            if (authException instanceof JwtAuthenticationException) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ErrorMessages.TOKEN_EXPIRED.getMessage());
            } else if (authException instanceof AccountNotFoundException) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ErrorMessages.ACCOUNT_NOT_FOUND.getMessage());
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ErrorMessages.AUTHENTICATION_FAILED.getMessage());
            }
        } catch (IOException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ErrorMessages.INTERNAL_SERVER_ERROR.getMessage());
        }
    }
}
