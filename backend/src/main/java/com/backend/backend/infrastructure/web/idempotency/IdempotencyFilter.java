package com.backend.backend.infrastructure.web.idempotency;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple in-memory Idempotency-Key filter for POST /api/v1/orders.
 * NOTE: For production, prefer a distributed store (Redis) with TTL.
 */
public class IdempotencyFilter implements Filter {
    private static final ConcurrentHashMap<String, Long> seenKeys = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        if ("POST".equalsIgnoreCase(req.getMethod()) && req.getRequestURI().startsWith("/api/v1/orders")) {
            String key = req.getHeader("Idempotency-Key");
            if (key == null || key.isBlank()) {
                res.setStatus(400);
                res.setContentType("application/json");
                res.getWriter().write("{\"error\":\"Missing Idempotency-Key header\"}");
                return;
            }

            Long existed = seenKeys.putIfAbsent(key, System.currentTimeMillis());
            if (existed != null) {
                res.setStatus(409);
                res.setContentType("application/json");
                res.getWriter().write("{\"error\":\"Duplicate Idempotency-Key\"}");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}


