package com.edio.user.model.response;

import com.edio.user.domain.Members;

import java.time.LocalDateTime;

public record MemberResponse(
        Long id,
        Long accountId,
        String email,
        String name,
        String givenName,
        String familyName,
        String profileUrl,
        LocalDateTime createdAt,
        LocalDateTime updateAt
){
    public static MemberResponse from(Members member) {
        return new MemberResponse(
                member.getId(),
                member.getAccountId(),
                member.getEmail(),
                member.getName(),
                member.getGivenName(),
                member.getFamilyName(),
                member.getProfileUrl(),
                member.getCreatedAt(),
                member.getUpdatedAt()
        );
    }
}