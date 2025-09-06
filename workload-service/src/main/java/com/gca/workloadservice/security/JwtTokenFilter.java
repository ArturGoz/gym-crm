package com.gca.workloadservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

    private final AccessTokenService accessTokenService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = accessTokenService.extractAccessTokenFromRequest(request);
        log.debug("JWT filter triggered, token present: {}", token != null);

        if (token != null && accessTokenService.validateToken(token)) {
            setAuthenticationWithToken(token);
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthenticationWithToken(String token) {
        String username = accessTokenService.extractUsernameFromToken(token);
        log.debug("Authentication established for user={}", username);

        UserDetails userDetails = userDetailsService.buildUserFromToken(username);

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
