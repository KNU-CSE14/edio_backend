package com.edio.user.model.request;

public record AccountCreateRequest(
        String loginId,
        Long memberId
) {
}