package com.edio.user.model.response;

import com.edio.user.domain.Accounts;
import com.edio.user.domain.enums.AccountLoginType;
import com.edio.user.domain.enums.AccountRole;

import java.time.LocalDateTime;

public record AccountResponse(
    Long id,
    String loginId,
    String password,
    LocalDateTime createdAt,
    LocalDateTime updateAt,
    boolean isDeleted,
    AccountLoginType loginType,
    AccountRole roles
){
    public static AccountResponse from(Accounts account) {
        return new AccountResponse(
                account.getId(),
                account.getLoginId(),
                account.getPassword(),
                account.getCreatedAt(),
                account.getUpdatedAt(),
                account.isDeleted(),
                account.getLoginType(),
                account.getRoles()
        );
    }
}
