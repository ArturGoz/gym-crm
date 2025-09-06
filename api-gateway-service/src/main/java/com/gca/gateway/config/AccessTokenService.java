package com.gca.gateway.config;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AccessTokenService {
    private static final String TOKEN_ACCESS_COOKIE_NAME = "JWT";

    @Value("${jwt.secret}")
    private String secretKey;

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String extractAccessTokenFromRequest(ServerHttpRequest request) {
        return getCookieValue(request);
    }

    private String getCookieValue(ServerHttpRequest request) {
        return extractCookie(request)
                .map(HttpCookie::getValue)
                .filter(value -> !value.isBlank())
                .orElse(null);
    }

    private Optional<HttpCookie> extractCookie(ServerHttpRequest request) {
        return Optional.ofNullable(request.getCookies().getFirst(AccessTokenService.TOKEN_ACCESS_COOKIE_NAME));
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}
