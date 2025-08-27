package com.gca.security.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtCookieServiceTest {

    private static final Long JWT_ACCESS_DURATION = 900000L;
    private static final Long JWT_REFRESH_DURATION = 604800000L;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private JwtCookieService service;

    @Test
    void createAccessTokenCookie_shouldCreateValidCookie() {
        String expectedAccessToken = "access-token-123";
        ReflectionTestUtils.setField(service, "jwtAccessDuration", JWT_ACCESS_DURATION);

        ResponseCookie actual = service.createAccessTokenCookie(expectedAccessToken);

        assertEquals("JWT", actual.getName());
        assertEquals(expectedAccessToken, actual.getValue());
        assertEquals(Duration.ofMillis(JWT_ACCESS_DURATION), actual.getMaxAge());
        assertTrue(actual.isHttpOnly());
    }

    @Test
    void createRefreshTokenCookie_shouldCreateValidCookie() {
        String expectedRefreshToken = "refresh-token-456";
        ReflectionTestUtils.setField(service, "jwtRefreshDuration", JWT_REFRESH_DURATION);

        ResponseCookie actual = service.createRefreshTokenCookie(expectedRefreshToken);

        assertEquals("REFRESH_TOKEN", actual.getName());
        assertEquals(expectedRefreshToken, actual.getValue());
        assertEquals(Duration.ofMillis(JWT_REFRESH_DURATION), actual.getMaxAge());
    }

    @Test
    void createCleanJwtCookie_shouldCreateEmptyCookie() {
        ResponseCookie actual = service.createCleanJwtCookie();

        assertEquals("JWT", actual.getName());
        assertEquals("", actual.getValue());
        assertEquals(Duration.ofMillis(0L), actual.getMaxAge());
    }

    @Test
    void createCleanRefreshTokenCookie_shouldCreateEmptyCookie() {
        ResponseCookie actual = service.createCleanRefreshTokenCookie();

        assertEquals("REFRESH_TOKEN", actual.getName());
        assertEquals("", actual.getValue());
        assertEquals(Duration.ofMillis(0L), actual.getMaxAge());
    }

    @Test
    void extractRefreshTokenFromCookies_shouldReturnToken_whenExists() {
        String expectedToken = "valid-refresh-token";
        Cookie[] cookies = {
                new Cookie("JWT", "access-token"),
                new Cookie("REFRESH_TOKEN", expectedToken)
        };

        when(httpServletRequest.getCookies()).thenReturn(cookies);

        String actualToken = service.extractRefreshTokenFromCookies(httpServletRequest);

        assertEquals(expectedToken, actualToken);
    }

    @Test
    void extractRefreshTokenFromCookies_shouldReturnNull_whenNotExists() {
        Cookie[] cookies = {new Cookie("JWT", "access-token")};

        when(httpServletRequest.getCookies()).thenReturn(cookies);

        String actualToken = service.extractRefreshTokenFromCookies(httpServletRequest);

        assertNull(actualToken);
    }

    @Test
    void extractRefreshTokenFromCookies_shouldReturnNull_whenCookiesAreNull() {
        when(httpServletRequest.getCookies()).thenReturn(null);
        String actualToken = service.extractRefreshTokenFromCookies(httpServletRequest);
        assertNull(actualToken);
    }
}