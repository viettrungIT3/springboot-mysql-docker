package com.backend.backend.infrastructure.web.versioning;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Adds deprecation headers and forwards legacy /api/* requests to /api/v1/*.
 */
public class ApiVersionRedirectFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String uri = httpRequest.getRequestURI();

        // Only handle legacy paths that start with /api/ but not with /api/v1/
        if (uri.startsWith("/api/") && !uri.startsWith("/api/v1/")) {
            // Add deprecation headers
            httpResponse.setHeader("Deprecation", "true");
            httpResponse.setHeader("Sunset", "2026-12-31");
            httpResponse.setHeader("Link", "</api/v1>; rel=successor-version");
            httpResponse.setHeader("X-API-Deprecated", "Use /api/v1 instead of /api");

            String forwarded = "/api/v1/" + uri.substring("/api/".length());
            RequestDispatcher dispatcher = request.getRequestDispatcher(forwarded);
            dispatcher.forward(request, response);
            return;
        }

        chain.doFilter(request, response);
    }
}


