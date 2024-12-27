package com.edio.common.exception.base;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorMessages {
    TOKEN_EXPIRED("E401-001", "The token is invalid or expired."),
    ACCOUNT_NOT_FOUND("E401-002", "The associated account could not be found."),
    AUTHENTICATION_FAILED("E401-003", "Authentication failed. Please check your credentials or token."),

    UNPROCESSABLE_STATE_MAP("E422-001", "Failed to encode state map into JSON. Ensure the state map is serializable: %s");

    private final String code;
    private final String message;
}



