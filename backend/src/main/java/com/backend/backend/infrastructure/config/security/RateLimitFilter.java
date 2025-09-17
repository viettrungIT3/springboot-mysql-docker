package com.backend.backend.infrastructure.config.security;

// Temporarily disabled Bucket4j imports for compilation
// import com.github.bucket4j.Bucket;
// import com.github.bucket4j.Bucket4j;
// import com.github.bucket4j.Refill;
// import com.github.bucket4j.Bandwidth;
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class RateLimitFilter implements Filter {
    
    // Temporarily disabled for compilation
    // private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    
    // Rate limit: 100 requests per minute per IP
    private static final int REQUEST_LIMIT = 100;
    private static final Duration TIME_WINDOW = Duration.ofMinutes(1);
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // String clientIp = getClientIpAddress(httpRequest);
        // Temporarily disable bucket4j functionality
        // Bucket bucket = getBucket(clientIp);
        
        // Temporarily allow all requests
        chain.doFilter(request, response);
    }
    
    // Temporarily disabled for compilation
    /*
    private Bucket getBucket(String key) {
        return buckets.computeIfAbsent(key, k -> {
            Bandwidth limit = Bandwidth.classic(REQUEST_LIMIT, Refill.intervally(REQUEST_LIMIT, TIME_WINDOW));
            return Bucket4j.builder()
                    .addLimit(limit)
                    .build();
        });
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
    */
}
