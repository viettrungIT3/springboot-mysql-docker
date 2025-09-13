package com.backend.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration properties for CORS settings.
 * Reads from environment variables (from .env file)
 */
@Getter
@Setter
@Component
public class CorsProperties {
    
    /**
     * Comma-separated list of allowed origins.
     * Environment variable: CORS_ALLOWED_ORIGINS
     * Default: http://localhost:3000 for development
     */
    @Value("${CORS_ALLOWED_ORIGINS:http://localhost:3000}")
    private String allowedOrigins;
    
    /**
     * Comma-separated list of allowed headers.
     * Environment variable: CORS_ALLOWED_HEADERS
     * Default: Common headers needed for API communication
     */
    @Value("${CORS_ALLOWED_HEADERS:Authorization,Content-Type,Accept,X-Requested-With,Origin}")
    private String allowedHeadersStr;
    
    /**
     * Comma-separated list of allowed HTTP methods.
     * Environment variable: CORS_ALLOWED_METHODS
     * Default: Standard REST methods
     */
    @Value("${CORS_ALLOWED_METHODS:GET,POST,PUT,PATCH,DELETE,OPTIONS}")
    private String allowedMethodsStr;
    
    /**
     * Whether to allow credentials (cookies, authorization headers).
     * Environment variable: CORS_ALLOW_CREDENTIALS
     * Default: true for API communication
     */
    @Value("${CORS_ALLOW_CREDENTIALS:true}")
    private boolean allowCredentials;
    
    /**
     * Comma-separated list of headers to expose to the client.
     * Environment variable: CORS_EXPOSED_HEADERS
     * Default: Authorization header for token access
     */
    @Value("${CORS_EXPOSED_HEADERS:Authorization}")
    private String exposedHeadersStr;
    
    /**
     * Maximum age (in seconds) for preflight requests.
     * Environment variable: CORS_MAX_AGE
     * Default: 1 hour (3600 seconds)
     */
    @Value("${CORS_MAX_AGE:3600}")
    private long maxAge;
    
    /**
     * Whether to enable CORS logging for debugging.
     * Environment variable: CORS_ENABLE_LOGGING
     * Default: false in production
     */
    @Value("${CORS_ENABLE_LOGGING:false}")
    private boolean enableLogging;
    
    // Helper methods to convert strings to lists
    public List<String> getAllowedHeaders() {
        return Arrays.asList(allowedHeadersStr.split(","));
    }
    
    public List<String> getAllowedMethods() {
        return Arrays.asList(allowedMethodsStr.split(","));
    }
    
    public List<String> getExposedHeaders() {
        return Arrays.asList(exposedHeadersStr.split(","));
    }
}