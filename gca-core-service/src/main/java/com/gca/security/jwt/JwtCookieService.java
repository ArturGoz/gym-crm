package com.gca.security.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;

@Component
public class JwtCookieService {

    private static final String TOKEN_ACCESS_COOKIE_NAME = "JWT";
    private static final String TOKEN_REFRESH_COOKIE_NAME = "REFRESH_TOKEN";

    @Value("${jwt.access.duration}")
    private Long jwtAccessDuration;

    @Value("${jwt.refresh.duration}")
    private Long jwtRefreshDuration;

    public ResponseCookie createAccessTokenCookie(String accessToken) {
        return buildCookie(TOKEN_ACCESS_COOKIE_NAME, accessToken, jwtAccessDuration);
    }

    public ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return buildCookie(TOKEN_REFRESH_COOKIE_NAME, refreshToken, jwtRefreshDuration);
    }

    public ResponseCookie createCleanJwtCookie() {
        return buildCookie(TOKEN_ACCESS_COOKIE_NAME, "", 0L);
    }

    public ResponseCookie createCleanRefreshTokenCookie() {
        return buildCookie(TOKEN_REFRESH_COOKIE_NAME, "", 0L);
    }

    public String extractRefreshTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return null;
        }

        return Arrays.stream(cookies)
                .filter(cookie -> TOKEN_REFRESH_COOKIE_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    private ResponseCookie buildCookie(String name, String value, Long durationMillis) {
        return ResponseCookie.from(name, value)
                .maxAge(Duration.ofMillis(durationMillis))
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .build();
    }
}