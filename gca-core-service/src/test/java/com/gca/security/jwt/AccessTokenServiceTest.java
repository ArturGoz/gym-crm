package com.gca.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.AssertionsKt.assertNotNull;

@ExtendWith(MockitoExtension.class)
class AccessTokenServiceTest {

    private AccessTokenService service;
    private String secret;
    private Long accessDuration;

    @BeforeEach
    void setUp() {
        service = new AccessTokenService();
        secret = "ThisIsASecretKeyForJwtThatIsAtLeast32Bytes!";
        accessDuration = 3600_000L;

        ReflectionTestUtils.setField(service, "secretKey", secret);
        ReflectionTestUtils.setField(service, "jwtAccessDuration", accessDuration);
    }

    @Test
    void createAccessToken_shouldReturnValidJwt() {
        String expected = "testUser";

        String actual = service.createAccessToken(expected);

        assertNotNull(actual);
        assertEquals(expected, parseUsername(actual));
    }

    @Test
    void getUsername_shouldExtractCorrectUsernameFromToken() {
        String expected = "yurii_donets";
        String actual = buildToken(expected, accessDuration);

        assertEquals(expected, service.getUsername(actual));
    }

    @Test
    void validateToken_shouldReturnTrueForValidToken() {
        String actual = buildToken("validUser", accessDuration);
        assertTrue(service.validateToken(actual));
    }

    @Test
    void validateToken_shouldReturnFalseForInvalidToken() {
        assertFalse(service.validateToken("invalid.token.value"));
    }

    @Test
    void validateToken_shouldReturnFalseForExpiredToken() {
        String actual = buildToken("expiredUser", -5_000L);
        assertFalse(service.validateToken(actual));
    }

    private String buildToken(String username, long durationMillis) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + durationMillis))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    private String parseUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
