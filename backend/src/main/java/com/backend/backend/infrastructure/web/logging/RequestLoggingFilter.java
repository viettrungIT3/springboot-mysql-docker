package com.backend.backend.infrastructure.web.logging;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

@Slf4j
public class RequestLoggingFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        Instant startTime = Instant.now();
        String method = httpRequest.getMethod();
        String uri = httpRequest.getRequestURI();
        String queryString = httpRequest.getQueryString();
        String userAgent = httpRequest.getHeader("User-Agent");
        String clientIp = getClientIpAddress(httpRequest);
        
        String fullUri = queryString != null ? uri + "?" + queryString : uri;
        
        log.info("Incoming request: {} {} from {} - User-Agent: {}", 
                method, fullUri, clientIp, userAgent);
        
        try {
            chain.doFilter(request, response);
        } finally {
            Instant endTime = Instant.now();
            Duration duration = Duration.between(startTime, endTime);
            int status = httpResponse.getStatus();
            
            log.info("Request completed: {} {} - Status: {} - Duration: {}ms", 
                    method, fullUri, status, duration.toMillis());
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
}
