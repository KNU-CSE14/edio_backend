package com.edio.common.exception.base;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorMessages {
    BAD_REQUEST("E400-001", "Invalid Request"), // IllegalArgumentException

    TOKEN_EXPIRED("E401-001", "Token Expired"), // BadCredentialsException
    TOKEN_INVALID("E401-002", "Token Invalid"),
    AUTHENTICATION_FAILED("E401-003", "Authentication Failed"), // AuthenticationException

    INVALID_CSRF_TOKEN("E403-001", "Invalid CSRF Token"), // AccessDeniedException
    FORBIDDEN_NOT_OWNER("E403-002", "Not Resource Owner"), // AccessDeniedException

    DATA_NOT_FOUND("E404-001", "Data Not Found"), // NoSuchElementException
    NOT_FOUND_ENTITY("E404-002", "%s Not Found with ID: %s"), // EntityNotFoundException

    CONFLICT("E409-001", "Conflict Occurred"), // IllegalStateException

    FILE_PROCESSING_UNSUPPORTED("E415-001", "Unsupported File Type"), // UnsupportedMediaTypeStatusException, UnsupportedOperationException

    FILE_PROCESSING_ERROR("E422-001", "File Processing Failed"), // IllegalStateException
    UNPROCESSABLE_STATE_MAP("E422-002", "Failed To Encode State Map"), // IllegalStateException

    INTERNAL_SERVER_ERROR("E500-001", "An Unexpected Error Occurred On The Server"), // RuntimeException
    GENERAL_CREATION_FAILED("E500-002", "Create Account Failed"); // OAuth2AuthenticationException

    private final String code;
    private final String message;

    public String format(Object... args) {
        return String.format(this.message, args);
    }

}



