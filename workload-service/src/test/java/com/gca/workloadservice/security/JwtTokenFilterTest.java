package com.gca.workloadservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtTokenFilterTest {

    @Mock
    private AccessTokenService accessTokenService;

    @Mock
    private CustomUserDetailsService userDetailsService;

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
    void doFilterInternal_shouldSetAuthenticationWhenTokenIsValid() throws ServletException, IOException {
        String token = "validToken";
        String username = "testUser";
        UserDetails userDetails = mock(UserDetails.class);

        when(userDetails.getAuthorities()).thenReturn(java.util.List.of());
        when(accessTokenService.extractAccessTokenFromRequest(request)).thenReturn(token);
        when(accessTokenService.validateToken(token)).thenReturn(true);
        when(accessTokenService.extractUsernameFromToken(token)).thenReturn(username);
        when(userDetailsService.buildUserFromToken(username)).thenReturn(userDetails);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(userDetailsService).buildUserFromToken(username);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isInstanceOf(UsernamePasswordAuthenticationToken.class);
        assertThat(authentication.getPrincipal()).isEqualTo(userDetails);
    }

    @Test
    void doFilterInternal_shouldNotSetAuthenticationWhenNoToken() throws ServletException, IOException {
        when(accessTokenService.extractAccessTokenFromRequest(request)).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_shouldNotSetAuthenticationWhenTokenInvalid() throws ServletException, IOException {
        String token = "invalidToken";
        when(accessTokenService.extractAccessTokenFromRequest(request)).thenReturn(token);
        when(accessTokenService.validateToken(token)).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(userDetailsService, never()).buildUserFromToken(anyString());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
