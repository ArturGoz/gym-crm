package com.gca.integration.web;

import com.gca.dto.trainer.TrainerWorkloadDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

import static com.gca.controller.ApiConstant.TRAINER_WORKLOAD_URL;
import static java.lang.String.format;

@Component
@RequiredArgsConstructor
@Slf4j
public class WorkloadConnector {

    private static final String TRANSACTION_ID_HEADER = "X-Transaction-Id";
    private static final String JWT_COOKIE_NAME = "JWT";

    private final RestTemplate restTemplate;

    @CircuitBreaker(name = "workloadService", fallbackMethod = "fallback")
    public ResponseEntity<Void> processTrainerWorkloadRequest(TrainerWorkloadDTO request) {
        HttpServletRequest httpRequest = getCurrentHttpRequest();
        HttpHeaders headers = buildHeaders(httpRequest);

        HttpEntity<TrainerWorkloadDTO> entity = new HttpEntity<>(request, headers);

        log.info("Calling workload service for trainer: {}", request.getTrainerUsername());
        return restTemplate.postForEntity(TRAINER_WORKLOAD_URL, entity, Void.class);
    }

    private ResponseEntity<Void> fallback(TrainerWorkloadDTO request, Throwable ex) {
        log.error("Workload service unavailable for trainer: {}, reason: {}",
                request.getTrainerUsername(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    private HttpHeaders buildHeaders(HttpServletRequest httpRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        addJwtCookieIfPresent(httpRequest, headers);
        addTransactionIdIfPresent(httpRequest, headers);

        return headers;
    }

    private void addJwtCookieIfPresent(HttpServletRequest httpRequest, HttpHeaders headers) {
        if (httpRequest.getCookies() == null) {
            return;
        }

        Arrays.stream(httpRequest.getCookies())
                .filter(cookie -> cookie.getName().equals(JWT_COOKIE_NAME))
                .findFirst()
                .ifPresent(jwtCookie ->
                        headers.add(HttpHeaders.COOKIE,
                                format("%s=%s", JWT_COOKIE_NAME, jwtCookie.getValue())));
    }

    private void addTransactionIdIfPresent(HttpServletRequest httpRequest, HttpHeaders headers) {
        String transactionId = httpRequest.getHeader(TRANSACTION_ID_HEADER);

        if (transactionId != null) {
            headers.set(TRANSACTION_ID_HEADER, transactionId);
        }
    }

    private HttpServletRequest getCurrentHttpRequest() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attrs == null) {
            throw new IllegalStateException("No current HTTP request available");
        }

        return attrs.getRequest();
    }
}

