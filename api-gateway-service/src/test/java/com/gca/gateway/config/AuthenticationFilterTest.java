package com.gca.gateway.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationFilterTest {

    @InjectMocks
    private AuthenticationFilter sut;

    @Mock
    private AccessTokenService accessTokenService;

    @Mock
    private RouterValidator routerValidator;

    @Mock
    private GatewayFilterChain filterChain;

    @Test
    void unsecuredRequest_shouldPassThrough() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/public").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
        when(routerValidator.isSecured(request)).thenReturn(false);

        sut.filter(exchange, filterChain).block();

        verify(filterChain).filter(exchange);
        assertThat(exchange.getResponse().getStatusCode()).isNull();
    }

    @Test
    void securedRequest_withInvalidToken_shouldReturnUnauthorized() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/secure").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        when(routerValidator.isSecured(request)).thenReturn(true);
        when(accessTokenService.extractAccessTokenFromRequest(request)).thenReturn("invalid-token");
        when(accessTokenService.validateToken("invalid-token")).thenReturn(false);

        sut.filter(exchange, filterChain).block();

        verify(filterChain, never()).filter(exchange);
        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void securedRequest_withValidToken_shouldPassThrough() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/secure").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
        when(routerValidator.isSecured(request)).thenReturn(true);
        when(accessTokenService.extractAccessTokenFromRequest(request)).thenReturn("valid-token");
        when(accessTokenService.validateToken("valid-token")).thenReturn(true);

        sut.filter(exchange, filterChain).block();

        verify(filterChain).filter(exchange);
        assertThat(exchange.getResponse().getStatusCode()).isNull();
    }
}