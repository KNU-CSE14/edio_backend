package com.edio.common.exception.base;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorMessages {
    BAD_REQUEST("E400-001", "Invalid Request: %s"),

    TOKEN_EXPIRED("E401-001", "Token Invalid or Expired: %s"),
    AUTHENTICATION_FAILED("E401-002", "Authentication Failed: %s"),
    ACCOUNT_NOT_FOUND("E401-003", "Account Could Not Be Found"),

    INVALID_CSRF_TOKEN("E403-001", "Invalid CSRF Token: %s"),

    NOT_FOUND_ENTITY("E404-001", "%s Not Found with ID: %s"),

    CONFLICT("E409-001", "Conflict Occurred: %s"),

    FILE_PROCESSING_UNSUPPORTED("E415-001", "Unsupported File Type: %s"),

    FILE_PROCESSING_ERROR("E422-001", "File Processing Failed: %s"),
    UNPROCESSABLE_STATE_MAP("E422-002", "Failed To Encode State Map: %s"),

    INTERNAL_SERVER_ERROR("E500-001", "An Unexpected Error Occurred On The Server");


    private final String code;
    private final String message;

    public String format(Object... args) {
        return String.format(this.message, args);
    }

}



