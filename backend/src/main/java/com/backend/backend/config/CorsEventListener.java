package com.backend.backend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * CORS monitoring component for logging and security analysis.
 * Note: Spring Boot 3.x doesn't have built-in CORS events, 
 * so we'll use a different approach for monitoring.
 */
@Component
@Slf4j
public class CorsEventListener {
    
    /**
     * Logs CORS-related information for monitoring purposes.
     * This can be called from other components when CORS issues occur.
     * 
     * @param origin Request origin
     * @param method HTTP method
     * @param requestURI Request URI
     * @param userAgent User agent header
     * @param rejected Whether the request was rejected
     */
    public void logCorsRequest(String origin, String method, String requestURI, 
                              String userAgent, boolean rejected) {
        if (rejected) {
            log.warn("CORS request rejected - Origin: {}, Method: {}, RequestURI: {}, User-Agent: {}", 
                origin, method, requestURI, userAgent);
        } else if (log.isDebugEnabled()) {
            log.debug("CORS request allowed - Origin: {}, Method: {}, RequestURI: {}", 
                origin, method, requestURI);
        }
    }
    
    /**
     * Logs CORS preflight requests for monitoring.
     * 
     * @param origin Request origin
     * @param method Requested method
     * @param headers Requested headers
     * @param requestURI Request URI
     */
    public void logCorsPreflight(String origin, String method, String headers, String requestURI) {
        if (log.isDebugEnabled()) {
            log.debug("CORS preflight request - Origin: {}, Method: {}, Headers: {}, RequestURI: {}", 
                origin, method, headers, requestURI);
        }
    }
}
