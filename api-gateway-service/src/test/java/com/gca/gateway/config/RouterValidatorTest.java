package com.gca.gateway.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;

import static org.assertj.core.api.Assertions.assertThat;

class RouterValidatorTest {

    private RouterValidator sut;

    @BeforeEach
    void setUp() {
        sut = new RouterValidator();
    }

    @Test
    void isSecured_shouldReturnFalse_forOpenEndpoints() {
        ServerHttpRequest request1 = MockServerHttpRequest.get("/auth/login").build();
        ServerHttpRequest request2 = MockServerHttpRequest.get("/register").build();

        assertThat(sut.isSecured(request1)).isFalse();
        assertThat(sut.isSecured(request2)).isFalse();
    }

    @Test
    void isSecured_shouldReturnTrue_forSecuredEndpoints() {
        ServerHttpRequest request1 = MockServerHttpRequest.get("/secure/data").build();
        ServerHttpRequest request2 = MockServerHttpRequest.get("/api/users").build();

        assertThat(sut.isSecured(request1)).isTrue();
        assertThat(sut.isSecured(request2)).isTrue();
    }

    @Test
    void isSecured_shouldReturnTrue_forPartiallyMatchingPaths() {
        ServerHttpRequest request = MockServerHttpRequest.get("/authenticated").build();

        assertThat(sut.isSecured(request)).isTrue();
    }
}