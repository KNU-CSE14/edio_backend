package com.edio.user.exception;

import com.edio.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class AccountNotFoundException extends BaseException {
    public AccountNotFoundException(String loginId) {
        super(HttpStatus.NOT_FOUND, String.format("Not Found Account, loginId = %S", loginId));
    }
}
