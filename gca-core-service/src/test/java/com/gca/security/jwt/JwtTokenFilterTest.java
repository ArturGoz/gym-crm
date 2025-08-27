package com.gca.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtTokenFilterTest {

    @Mock
    private AccessTokenService accessTokenService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtTokenFilter filter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_validToken_setsAuthentication() throws ServletException, IOException {
        String token = "valid.jwt.token";
        String expectedUsername = "testuser";
        Cookie jwtCookie = new Cookie("JWT", token);
        UserDetails userDetails = new User(expectedUsername, "password", List.of());

        when(request.getCookies()).thenReturn(new Cookie[]{jwtCookie});
        when(accessTokenService.validateToken(token)).thenReturn(true);
        when(accessTokenService.getUsername(token)).thenReturn(expectedUsername);
        when(userDetailsService.loadUserByUsername(expectedUsername)).thenReturn(userDetails);

        filter.doFilterInternal(request, response, filterChain);

        Authentication actual = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(actual);
        assertTrue(actual.isAuthenticated());
        assertEquals(expectedUsername, actual.getName());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_invalidToken_doesNotSetAuthentication() throws ServletException, IOException {
        String token = "invalid.token";
        Cookie jwtCookie = new Cookie("JWT", token);

        when(request.getCookies()).thenReturn(new Cookie[]{jwtCookie});
        when(accessTokenService.validateToken(token)).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        Authentication actual = SecurityContextHolder.getContext().getAuthentication();
        assertNull(actual);
        verify(filterChain).doFilter(request, response);
        verify(accessTokenService, never()).getUsername(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    void doFilterInternal_noCookie_doesNotSetAuthentication() throws ServletException, IOException {
        when(request.getCookies()).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        Authentication actual = SecurityContextHolder.getContext().getAuthentication();
        assertNull(actual);
        verify(filterChain).doFilter(request, response);
        verify(accessTokenService, never()).validateToken(anyString());
    }
}