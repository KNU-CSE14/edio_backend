package com.edio.user.model.request;

import com.edio.user.domain.enums.AccountLoginType;
import com.edio.user.domain.enums.AccountRole;

public record AccountCreateRequest(
        String loginId,
        Long memberId,
        AccountLoginType loginType,
        AccountRole role
) {
}