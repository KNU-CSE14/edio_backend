package com.edio.user.model.response;

import com.edio.user.domain.Account;
import com.edio.user.domain.enums.AccountRole;

public record AccountResponse(
        Long id,
        String loginId,
        Long rootFolderId,
        AccountRole roles,
        MemberResponse memberResponse

) {
    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getLoginId(),
                account.getRootFolderId(),
                account.getRoles(),
                MemberResponse.from(account.getMember())
        );
    }
}
