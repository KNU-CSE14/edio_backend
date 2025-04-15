package com.edio.common.security.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CookieConstants {
    public static final String COOKIE_ACCESS_TOKEN = "accessToken=%s; HttpOnly; Secure; Path=/; Max-Age=3600; SameSite=None; Domain=ec2-3-38-251-128.ap-northeast-2.compute.amazonaws.com";
    public static final String COOKIE_REFRESH_TOKEN = "refreshToken=%s; HttpOnly; Secure; Path=/; Max-Age=86400; SameSite=None; Domain=ec2-3-38-251-128.ap-northeast-2.compute.amazonaws.com";
}
