package com.edio.common.util;

import com.edio.user.domain.Account;
import com.edio.user.domain.Member;

import static com.edio.common.TestConstants.User.*;

public class TestUserUtil {

    private TestUserUtil() { }

    public static Member member() {
        return Member.builder()
                .id(MEMBER_ID)
                .email(EMAIL)
                .name(NAME)
                .givenName(GIVEN_NAME)
                .familyName(FAMILY_NAME)
                .profileUrl(PROFILE_URL)
                .build();
    }

    public static Account account(Member member) {
        return Account.builder()
                .id(ACCOUNT_ID)
                .loginId(EMAIL)
                .password(PASSWORD)
                .member(member)
                .loginType(LOGIN_TYPE)
                .roles(ROLE)
                .build();
    }
}
