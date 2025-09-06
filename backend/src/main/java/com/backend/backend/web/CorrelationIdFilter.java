package com.backend.backend.web;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter implements Filter {

    public static final String HDR = "X-Correlation-Id";
    public static final String MDC_KEY = "corrId";
    public static final String MDC_PATH = "path";
    public static final String MDC_METHOD = "method";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        try {
            HttpServletRequest http = (HttpServletRequest) request;
            String id = http.getHeader(HDR);
            if (id == null || id.isBlank()) {
                id = UUID.randomUUID().toString().replace("-", "");
            }
            MDC.put(MDC_KEY, id);
            MDC.put(MDC_PATH, http.getRequestURI());
            MDC.put(MDC_METHOD, http.getMethod());

            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
