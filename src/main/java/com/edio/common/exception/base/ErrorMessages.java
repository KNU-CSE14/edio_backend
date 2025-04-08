package com.edio.common.exception.base;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @see java.lang.IllegalArgumentException
 * @see org.springframework.security.authentication.BadCredentialsException
 * @see org.springframework.security.core.AuthenticationException
 * @see org.springframework.security.access.AccessDeniedException
 * @see java.util.NoSuchElementException
 * @see jakarta.persistence.EntityNotFoundException
 * @see java.lang.IllegalStateException
 * @see org.springframework.web.HttpMediaTypeNotSupportedException
 * @see org.springframework.security.oauth2.core.OAuth2AuthenticationException
 */
@AllArgsConstructor
@Getter
public enum ErrorMessages {

    /**
     * Invalid Request
     *
     * @see java.lang.IllegalArgumentException
     */
    BAD_REQUEST("E400-001", "Invalid Request"),

    /**
     * Token Expired
     *
     * @see org.springframework.security.authentication.BadCredentialsException
     */
    TOKEN_EXPIRED("E401-001", "Token Expired"),

    /**
     * Token Invalid
     */
    TOKEN_INVALID("E401-002", "Token Invalid"),

    /**
     * Authentication Failed
     *
     * @see org.springframework.security.core.AuthenticationException
     */
    AUTHENTICATION_FAILED("E401-003", "Authentication Failed"),

    /**
     * Invalid CSRF Token
     *
     * @see org.springframework.security.access.AccessDeniedException
     */
    INVALID_CSRF_TOKEN("E403-001", "Invalid CSRF Token"),

    /**
     * Not Resource Owner
     *
     * @see org.springframework.security.access.AccessDeniedException
     */
    FORBIDDEN_NOT_OWNER("E403-002", "Not Resource Owner"),

    /**
     * Data Not Found
     *
     * @see java.util.NoSuchElementException
     */
    DATA_NOT_FOUND("E404-001", "Data Not Found"),

    /**
     * Entity Not Found by ID
     *
     * @see jakarta.persistence.EntityNotFoundException
     */
    NOT_FOUND_ENTITY("E404-002", "%s Not Found with ID: %s"),

    /**
     * Conflict Occurred
     *
     * @see java.lang.IllegalStateException
     */
    CONFLICT("E409-001", "Conflict Occurred"),

    /**
     * Unsupported File Type
     *
     * @see org.springframework.web.HttpMediaTypeNotSupportedException
     * @see java.lang.UnsupportedOperationException
     */
    FILE_PROCESSING_UNSUPPORTED("E415-001", "Unsupported File Type"),

    /**
     * File Processing Failed
     *
     * @see java.lang.IllegalStateException
     */
    FILE_PROCESSING_ERROR("E422-001", "File Processing Failed"),

    /**
     * Failed To Encode State Map
     *
     * @see java.lang.IllegalStateException
     */
    UNPROCESSABLE_STATE_MAP("E422-002", "Failed To Encode State Map"),

    /**
     * An Unexpected Error Occurred On The Server
     *
     * @see java.lang.RuntimeException
     */
    INTERNAL_SERVER_ERROR("E500-001", "An Unexpected Error Occurred On The Server"),

    /**
     * Create Account Failed
     *
     * @see org.springframework.security.oauth2.core.OAuth2AuthenticationException
     */
    GENERAL_CREATION_FAILED("E500-002", "Create Account Failed");

    private final String code;
    private final String message;

    public String format(Object... args) {
        return String.format(this.message, args);
    }
}
