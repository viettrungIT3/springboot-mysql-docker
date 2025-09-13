package com.backend.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for testing rate limiting functionality.
 * Provides endpoints to test different rate limit configurations.
 */
@RestController
@RequestMapping("/api/v1/rate-limit-test")
public class RateLimitTestController {

    /**
     * Test endpoint for public rate limiting (100 requests/minute)
     */
    @GetMapping("/public")
    public ResponseEntity<Map<String, Object>> testPublicRateLimit() {
        Map<String, Object> response = new HashMap<>();
        response.put("endpoint", "public");
        response.put("message", "Public rate limit test successful");
        response.put("timestamp", Instant.now().toString());
        response.put("rateLimit", "100 requests per minute");
        return ResponseEntity.ok(response);
    }

    /**
     * Test endpoint for API rate limiting (200 requests/minute)
     */
    @GetMapping("/api")
    public ResponseEntity<Map<String, Object>> testApiRateLimit() {
        Map<String, Object> response = new HashMap<>();
        response.put("endpoint", "api");
        response.put("message", "API rate limit test successful");
        response.put("timestamp", Instant.now().toString());
        response.put("rateLimit", "200 requests per minute");
        return ResponseEntity.ok(response);
    }

    /**
     * Test endpoint for auth rate limiting (10 requests/minute)
     */
    @GetMapping("/auth")
    public ResponseEntity<Map<String, Object>> testAuthRateLimit() {
        Map<String, Object> response = new HashMap<>();
        response.put("endpoint", "auth");
        response.put("message", "Auth rate limit test successful");
        response.put("timestamp", Instant.now().toString());
        response.put("rateLimit", "10 requests per minute");
        return ResponseEntity.ok(response);
    }

    /**
     * Test endpoint with no rate limiting
     */
    @GetMapping("/unlimited")
    public ResponseEntity<Map<String, Object>> testUnlimited() {
        Map<String, Object> response = new HashMap<>();
        response.put("endpoint", "unlimited");
        response.put("message", "Unlimited endpoint test successful");
        response.put("timestamp", Instant.now().toString());
        response.put("rateLimit", "unlimited");
        return ResponseEntity.ok(response);
    }
}
