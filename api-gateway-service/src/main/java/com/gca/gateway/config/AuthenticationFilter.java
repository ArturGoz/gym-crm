package com.gca.gateway.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AuthenticationFilter implements GlobalFilter {

    private final AccessTokenService accessTokenService;
    private final RouterValidator routerValidator;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (!routerValidator.isSecured(request)) {
            return chain.filter(exchange);
        }

        if (isUnauthorized(request)) {
            return handleUnauthorized(exchange, request);
        }

        return chain.filter(exchange);
    }

    private boolean isUnauthorized(ServerHttpRequest request) {
        String token = accessTokenService.extractAccessTokenFromRequest(request);

        return token == null || !accessTokenService.validateToken(token);
    }

    private Mono<Void> handleUnauthorized(ServerWebExchange exchange, ServerHttpRequest request) {
        log.warn("Unauthorized request to path: {}", request.getURI());
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);

        return exchange.getResponse().setComplete();
    }
}
