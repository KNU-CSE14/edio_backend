package com.edio.common.exception.base;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorMessages {
    BAD_REQUEST("E400-001", "Invalid Request"),

    TOKEN_EXPIRED("E401-001", "Token Invalid or Expired"),
    AUTHENTICATION_FAILED("E401-002", "Authentication Failed"),
    ACCOUNT_NOT_FOUND("E401-003", "Account Could Not Be Found"),

    INVALID_CSRF_TOKEN("E403-001", "Invalid CSRF Token"),

    NOT_FOUND_ENTITY("E404-001", "Entity Not Found"),

    CONFLICT("E409-001", "Conflict Occurred"),

    FILE_PROCESSING_UNSUPPORTED("E415-001", "Unsupported File Type"),

    FILE_PROCESSING_ERROR("E422-001", "File Processing Failed"),
    UNPROCESSABLE_STATE_MAP("E422-002", "Failed To Encode State Map"),

    INTERNAL_SERVER_ERROR("E500-001", "An Unexpected Error Occurred On The Server"),
    GENERAL_CREATION_FAILED("E500-002", "Create Account Failed");

    private final String code;
    private final String message;

    public String format(Object... args) {
        return String.format(this.message, args);
    }

}



