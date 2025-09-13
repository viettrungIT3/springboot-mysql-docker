package com.backend.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for Rate Limiting settings.
 * Reads from environment variables (from .env file)
 */
@Getter
@Setter
@Component
public class RateLimitProperties {
    
    /**
     * Whether rate limiting is enabled.
     * Environment variable: RATE_LIMIT_ENABLED
     * Default: true
     */
    @Value("${RATE_LIMIT_ENABLED:true}")
    private boolean enabled;
    
    /**
     * Number of requests allowed per time window for public endpoints.
     * Environment variable: RATE_LIMIT_PUBLIC_REQUESTS
     * Default: 100 requests per minute
     */
    @Value("${RATE_LIMIT_PUBLIC_REQUESTS:100}")
    private int publicRequests;
    
    /**
     * Time window in seconds for public endpoints.
     * Environment variable: RATE_LIMIT_PUBLIC_WINDOW_SECONDS
     * Default: 60 seconds (1 minute)
     */
    @Value("${RATE_LIMIT_PUBLIC_WINDOW_SECONDS:60}")
    private int publicWindowSeconds;
    
    /**
     * Number of requests allowed per time window for API endpoints.
     * Environment variable: RATE_LIMIT_API_REQUESTS
     * Default: 200 requests per minute
     */
    @Value("${RATE_LIMIT_API_REQUESTS:200}")
    private int apiRequests;
    
    /**
     * Time window in seconds for API endpoints.
     * Environment variable: RATE_LIMIT_API_WINDOW_SECONDS
     * Default: 60 seconds (1 minute)
     */
    @Value("${RATE_LIMIT_API_WINDOW_SECONDS:60}")
    private int apiWindowSeconds;
    
    /**
     * Number of requests allowed per time window for auth endpoints.
     * Environment variable: RATE_LIMIT_AUTH_REQUESTS
     * Default: 10 requests per minute (more restrictive for security)
     */
    @Value("${RATE_LIMIT_AUTH_REQUESTS:10}")
    private int authRequests;
    
    /**
     * Time window in seconds for auth endpoints.
     * Environment variable: RATE_LIMIT_AUTH_WINDOW_SECONDS
     * Default: 60 seconds (1 minute)
     */
    @Value("${RATE_LIMIT_AUTH_WINDOW_SECONDS:60}")
    private int authWindowSeconds;
    
    /**
     * Whether to enable rate limit logging for debugging.
     * Environment variable: RATE_LIMIT_ENABLE_LOGGING
     * Default: false
     */
    @Value("${RATE_LIMIT_ENABLE_LOGGING:false}")
    private boolean enableLogging;
    
    /**
     * Custom message for rate limit exceeded.
     * Environment variable: RATE_LIMIT_MESSAGE
     * Default: Rate limit exceeded
     */
    @Value("${RATE_LIMIT_MESSAGE:Rate limit exceeded. Please try again later.}")
    private String message;
}
