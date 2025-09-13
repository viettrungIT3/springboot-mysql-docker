package com.backend.backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Test controller for CORS functionality.
 * This controller provides endpoints to test CORS configuration.
 */
@RestController
@RequestMapping("/api/v1/cors-test")
@Slf4j
public class CorsTestController {
    
    /**
     * Simple GET endpoint to test CORS.
     * 
     * @param request HTTP request
     * @return CORS test response
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> testCorsGet(HttpServletRequest request) {
        log.info("CORS GET test - Origin: {}, User-Agent: {}", 
            request.getHeader("Origin"), 
            request.getHeader("User-Agent"));
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "CORS GET test successful");
        response.put("timestamp", LocalDateTime.now());
        response.put("origin", request.getHeader("Origin"));
        response.put("method", "GET");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * POST endpoint to test CORS with request body.
     * 
     * @param request HTTP request
     * @param body Request body
     * @return CORS test response
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> testCorsPost(
            HttpServletRequest request, 
            @RequestBody(required = false) Map<String, Object> body) {
        
        log.info("CORS POST test - Origin: {}, Content-Type: {}", 
            request.getHeader("Origin"), 
            request.getHeader("Content-Type"));
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "CORS POST test successful");
        response.put("timestamp", LocalDateTime.now());
        response.put("origin", request.getHeader("Origin"));
        response.put("method", "POST");
        response.put("receivedBody", body);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * OPTIONS endpoint to test CORS preflight.
     * 
     * @param request HTTP request
     * @return CORS preflight response
     */
    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Map<String, Object>> testCorsOptions(HttpServletRequest request) {
        log.info("CORS OPTIONS test - Origin: {}, Access-Control-Request-Method: {}", 
            request.getHeader("Origin"), 
            request.getHeader("Access-Control-Request-Method"));
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "CORS OPTIONS preflight successful");
        response.put("timestamp", LocalDateTime.now());
        response.put("origin", request.getHeader("Origin"));
        response.put("method", "OPTIONS");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Admin-only endpoint to test strict CORS configuration.
     * 
     * @param request HTTP request
     * @return Admin CORS test response
     */
    @GetMapping("/admin")
    public ResponseEntity<Map<String, Object>> testCorsAdmin(HttpServletRequest request) {
        log.info("CORS Admin test - Origin: {}, Authorization: {}", 
            request.getHeader("Origin"), 
            request.getHeader("Authorization") != null ? "Present" : "Missing");
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "CORS Admin test successful");
        response.put("timestamp", LocalDateTime.now());
        response.put("origin", request.getHeader("Origin"));
        response.put("method", "GET");
        response.put("endpoint", "admin");
        
        return ResponseEntity.ok(response);
    }
}
