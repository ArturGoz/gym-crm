package com.gca.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @RequestMapping("/fallback/workload")
    public ResponseEntity<String> workloadFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Workload Service is temporarily unavailable. Please try again later.");
    }

    @RequestMapping("/fallback/core")
    public ResponseEntity<String> coreFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Core Service is temporarily unavailable. Please try again later.");
    }
}

