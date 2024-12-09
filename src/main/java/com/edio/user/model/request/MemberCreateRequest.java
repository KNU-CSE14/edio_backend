package com.edio.user.model.request;

public record MemberCreateRequest(
        String email,
        String name,
        String givenName,
        String familyName,
        String profileUrl
) {
}