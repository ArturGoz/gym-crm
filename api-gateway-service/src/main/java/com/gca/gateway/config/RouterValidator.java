package com.gca.gateway.config;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RouterValidator {

    private static final List<String> OPEN_ENDPOINTS = List.of(
            "/auth/",
            "/register",
            "/actuator/health/"
    );

    public boolean isSecured(ServerHttpRequest request) {
        return OPEN_ENDPOINTS.stream()
                .noneMatch(uri -> request.getURI().getPath().contains(uri));
    }
}
