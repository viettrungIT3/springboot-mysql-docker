package com.backend.backend.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for CORS configuration validation and processing.
 */
@Slf4j
public class CorsUtils {
    
    /**
     * Validates and parses comma-separated origins string.
     * 
     * @param origins Comma-separated origins string
     * @return List of valid origins
     * @throws IllegalArgumentException if no valid origins found
     */
    public static List<String> validateAndParseOrigins(String origins) {
        if (!StringUtils.hasText(origins)) {
            log.warn("CORS origins is empty, using default localhost:3000");
            return Arrays.asList("http://localhost:3000");
        }
        
        List<String> originList = Arrays.stream(origins.split(","))
            .map(String::trim)
            .filter(StringUtils::hasText)
            .filter(CorsUtils::isValidOrigin)
            .collect(Collectors.toList());
        
        if (originList.isEmpty()) {
            log.error("No valid origins found in CORS configuration: {}", origins);
            throw new IllegalArgumentException("Invalid CORS configuration: no valid origins found");
        }
        
        log.info("CORS configured for {} origins: {}", originList.size(), originList);
        return originList;
    }
    
    /**
     * Validates if a given origin string is valid.
     * 
     * @param origin Origin string to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidOrigin(String origin) {
        if (!StringUtils.hasText(origin)) {
            return false;
        }
        
        try {
            URI uri = new URI(origin);
            String scheme = uri.getScheme();
            String host = uri.getHost();
            
            // Must have valid scheme and host
            if (scheme == null || host == null) {
                log.debug("Invalid origin format - missing scheme or host: {}", origin);
                return false;
            }
            
            // Only allow http and https schemes
            if (!"http".equals(scheme) && !"https".equals(scheme)) {
                log.debug("Invalid origin scheme - only http/https allowed: {}", origin);
                return false;
            }
            
            // Basic host validation (not empty, not just dots)
            if (host.trim().isEmpty() || host.matches("^\\.+$")) {
                log.debug("Invalid origin host: {}", origin);
                return false;
            }
            
            return true;
            
        } catch (URISyntaxException e) {
            log.debug("Invalid origin URI syntax: {} - {}", origin, e.getMessage());
            return false;
        }
    }
    
    /**
     * Checks if an origin is a localhost origin (for development).
     * 
     * @param origin Origin to check
     * @return true if localhost origin
     */
    public static boolean isLocalhostOrigin(String origin) {
        if (!StringUtils.hasText(origin)) {
            return false;
        }
        
        try {
            URI uri = new URI(origin);
            String host = uri.getHost();
            return host != null && (
                "localhost".equals(host) || 
                "127.0.0.1".equals(host) ||
                host.startsWith("192.168.") ||
                host.startsWith("10.") ||
                host.startsWith("172.")
            );
        } catch (URISyntaxException e) {
            return false;
        }
    }
    
    /**
     * Logs CORS configuration for debugging.
     * 
     * @param origins List of allowed origins
     * @param headers List of allowed headers
     * @param methods List of allowed methods
     * @param allowCredentials Whether credentials are allowed
     */
    public static void logCorsConfiguration(List<String> origins, List<String> headers, 
                                          List<String> methods, boolean allowCredentials) {
        if (log.isDebugEnabled()) {
            log.debug("CORS Configuration:");
            log.debug("  Origins: {}", origins);
            log.debug("  Headers: {}", headers);
            log.debug("  Methods: {}", methods);
            log.debug("  Allow Credentials: {}", allowCredentials);
        }
    }
}
