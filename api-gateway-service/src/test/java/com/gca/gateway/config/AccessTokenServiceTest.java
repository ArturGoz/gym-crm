package com.gca.gateway.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpCookie;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static io.jsonwebtoken.security.Keys.hmacShaKeyFor;
import static org.assertj.core.api.Assertions.assertThat;

class AccessTokenServiceTest {

    private final String secretKey = "test-secret-key-test-secret-key-1234";

    private AccessTokenService sut;

    @BeforeEach
    void setUp() {
        sut = new AccessTokenService();
        ReflectionTestUtils.setField(sut, "secretKey", secretKey);
    }

    @Test
    void validateToken_withValidToken_returnsTrue() {
        String token = generateValidToken();
        boolean result = sut.validateToken(token);
        assertThat(result).isTrue();
    }

    @Test
    void validateToken_withInvalidToken_returnsFalse() {
        boolean result = sut.validateToken("invalid-token");
        assertThat(result).isFalse();
    }

    @Test
    void extractAccessTokenFromRequest_withValidCookie_returnsToken() {
        String token = "sample-token";

        MockServerHttpRequest request = MockServerHttpRequest.get("/")
                .cookie(new HttpCookie("JWT", token))
                .build();

        String actual = sut.extractAccessTokenFromRequest(request);

        assertThat(actual).isEqualTo(token);
    }

    @Test
    void extractAccessTokenFromRequest_withMissingCookie_returnsNull() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/").build();
        String extracted = sut.extractAccessTokenFromRequest(request);
        assertThat(extracted).isNull();
    }

    @Test
    void extractAccessTokenFromRequest_withBlankCookie_returnsNull() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/")
                .cookie(new HttpCookie("JWT", ""))
                .build();

        String actual = sut.extractAccessTokenFromRequest(request);

        assertThat(actual).isNull();
    }

    private String generateValidToken() {
        SecretKey key = hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setSubject("user123")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}