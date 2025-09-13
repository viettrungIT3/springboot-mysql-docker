package com.backend.backend.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Rate limiting filter using Bucket4j for public endpoints.
 * Implements token bucket algorithm to limit requests per IP address.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Order(1) // Execute before security filter
public class RateLimitFilter implements Filter {
    
    private final RateLimitProperties rateLimitProperties;
    
    // Cache buckets per IP address
    private final ConcurrentMap<String, Bucket> buckets = new ConcurrentHashMap<>();
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        log.debug("RateLimitFilter: Processing request to {}", ((HttpServletRequest) request).getRequestURI());
        
        if (!rateLimitProperties.isEnabled()) {
            log.debug("RateLimitFilter: Rate limiting disabled, skipping");
            chain.doFilter(request, response);
            return;
        }
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String clientIp = getClientIpAddress(httpRequest);
        String requestPath = httpRequest.getRequestURI();
        
        // Determine rate limit based on endpoint type
        RateLimitConfig config = getRateLimitConfig(requestPath);
        
        log.debug("RateLimitFilter: Config for {}: {}", requestPath, config != null ? "FOUND" : "NOT FOUND");
        
        if (config != null) {
            log.debug("RateLimitFilter: Applying rate limit {} requests per {} seconds", config.requests, config.windowSeconds);
            Bucket bucket = getBucket(clientIp, config);
            
            if (bucket.tryConsume(1)) {
                // Request allowed
                log.debug("Rate limit: Request allowed for IP {} to {} (remaining: {})", 
                    clientIp, requestPath, bucket.getAvailableTokens());
                chain.doFilter(request, response);
            } else {
                // Rate limit exceeded
                log.warn("Rate limit exceeded for IP {} to {} (limit: {}/{}s)", 
                    clientIp, requestPath, config.requests, config.windowSeconds);
                
                httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                httpResponse.setContentType("application/json");
                httpResponse.getWriter().write(String.format(
                    "{\"error\":\"Too Many Requests\",\"message\":\"%s\",\"status\":429}",
                    rateLimitProperties.getMessage()
                ));
                return;
            }
        } else {
            // No rate limit for this endpoint
            log.debug("RateLimitFilter: No rate limit for {}", requestPath);
            chain.doFilter(request, response);
        }
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    private RateLimitConfig getRateLimitConfig(String requestPath) {
        // Public endpoints (products, health, docs)
        if (requestPath.startsWith("/api/v1/products") ||
            requestPath.startsWith("/actuator/health") ||
            requestPath.startsWith("/v3/api-docs") ||
            requestPath.startsWith("/swagger-ui")) {
            return new RateLimitConfig(
                rateLimitProperties.getPublicRequests(),
                rateLimitProperties.getPublicWindowSeconds()
            );
        }
        
        // Auth endpoints (more restrictive)
        if (requestPath.startsWith("/auth/") || requestPath.contains("/auth")) {
            return new RateLimitConfig(
                rateLimitProperties.getAuthRequests(),
                rateLimitProperties.getAuthWindowSeconds()
            );
        }
        
        // General API endpoints
        if (requestPath.startsWith("/api/")) {
            return new RateLimitConfig(
                rateLimitProperties.getApiRequests(),
                rateLimitProperties.getApiWindowSeconds()
            );
        }
        
        // No rate limit for other endpoints
        return null;
    }
    
    private Bucket getBucket(String clientIp, RateLimitConfig config) {
        // Create unique key combining IP and endpoint type (requests + windowSeconds)
        String bucketKey = clientIp + ":" + config.requests + ":" + config.windowSeconds;
        
        return buckets.computeIfAbsent(bucketKey, key -> {
            Refill refill = Refill.intervally(config.requests, Duration.ofSeconds(config.windowSeconds));
            Bandwidth limit = Bandwidth.classic(config.requests, refill);
            return Bucket4j.builder().addLimit(limit).build();
        });
    }
    
    /**
     * Internal class to hold rate limit configuration
     */
    private static class RateLimitConfig {
        final int requests;
        final int windowSeconds;
        
        RateLimitConfig(int requests, int windowSeconds) {
            this.requests = requests;
            this.windowSeconds = windowSeconds;
        }
    }
}
