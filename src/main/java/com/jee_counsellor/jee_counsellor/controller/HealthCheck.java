package com.jee_counsellor.jee_counsellor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Health Check", description = "Application health and status endpoints")
@RestController
public class HealthCheck {

    @Operation(summary = "Check application health", description = "Returns a simple string verifying that the server is alive and running.")
    @GetMapping("/health-check")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Application-alive");
    }
}
