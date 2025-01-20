package com.edio.common.security.constants;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CookieConstants {
    public static final String COOKIE_ACCESS_TOKEN = "accessToken=%s; HttpOnly; Secure; Path=/; Max-Age=3600; SameSite=None";
    public static final String COOKIE_REFRESH_TOKEN = "refreshToken=%s; HttpOnly; Secure; Path=/; Max-Age=86400; SameSite=None";
}
