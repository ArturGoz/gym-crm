package com.gca.workloadservice.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static io.jsonwebtoken.security.Keys.hmacShaKeyFor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AccessTokenServiceTest {

    private final static String SECRET_KEY = "my-super-secret-key-which-should-be-long-enough-12345";

    private AccessTokenService service;

    @BeforeEach
    void setUp() {
        service = new AccessTokenService();

        ReflectionTestUtils.setField(service, "secretKey", SECRET_KEY);
    }

    @Test
    void extractUsernameFromToken_shouldReturnSubject() {
        String username = "testUser";
        String token = generateToken(username, 10000);

        String actual = service.extractUsernameFromToken(token);

        assertThat(actual).isEqualTo(username);
    }

    @Test
    void validateToken_shouldReturnTrueForValidToken() {
        String token = generateToken("validUser", 10000);
        boolean actual = service.validateToken(token);
        assertThat(actual).isTrue();
    }

    @Test
    void validateToken_shouldReturnFalseForExpiredToken() {
        String token = generateToken("expiredUser", -1000);
        boolean actual = service.validateToken(token);
        assertThat(actual).isFalse();
    }

    @Test
    void extractAccessTokenFromRequest_shouldReturnTokenFromCookie() {
        String token = "cookieToken";
        Cookie cookie = new Cookie("JWT", token);
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        String actual = service.extractAccessTokenFromRequest(request);

        assertThat(actual).isEqualTo(token);
    }

    @Test
    void extractAccessTokenFromRequest_shouldReturnNullIfCookieMissing() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(null);

        String actual = service.extractAccessTokenFromRequest(request);

        assertThat(actual).isNull();
    }

    @Test
    void extractAccessTokenFromRequest_shouldReturnNullIfCookieValueBlank() {
        Cookie cookie = new Cookie("JWT", " ");
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        String actual = service.extractAccessTokenFromRequest(request);

        assertThat(actual).isNull();
    }

    private String generateToken(String username, long validityMillis) {
        SecretKey key = hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityMillis);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}