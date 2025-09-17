package com.backend.backend.config;

import com.backend.backend.infrastructure.config.security.RateLimitFilter;
import com.backend.backend.util.CorsUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final CorsProperties corsProperties;
    private final RateLimitFilter rateLimitFilter;

    @Value("${CORS_ALLOWED_ORIGINS:http://localhost:3000}")
    private String corsAllowedOrigins;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**")
                        .permitAll()
                        .anyRequest().permitAll())
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        try {
            // Validate and parse origins
            List<String> allowedOrigins = CorsUtils.validateAndParseOrigins(corsAllowedOrigins);
            
            // Create different CORS configurations for different endpoint types
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            
            // Public endpoints - more permissive (products, health checks)
            CorsConfiguration publicConfig = createPublicCorsConfig(allowedOrigins);
            source.registerCorsConfiguration("/api/v1/products/**", publicConfig);
            source.registerCorsConfiguration("/actuator/health", publicConfig);
            source.registerCorsConfiguration("/v3/api-docs/**", publicConfig);
            source.registerCorsConfiguration("/swagger-ui/**", publicConfig);
            
            // Auth endpoints - strict configuration
            CorsConfiguration authConfig = createAuthCorsConfig(allowedOrigins);
            source.registerCorsConfiguration("/auth/**", authConfig);
            
            // Private API endpoints - standard configuration
            CorsConfiguration apiConfig = createApiCorsConfig(allowedOrigins);
            source.registerCorsConfiguration("/api/**", apiConfig);
            
            // Admin endpoints - very strict configuration
            CorsConfiguration adminConfig = createAdminCorsConfig(allowedOrigins);
            source.registerCorsConfiguration("/api/v1/admin/**", adminConfig);
            
            log.info("CORS configuration initialized successfully");
            return source;
            
        } catch (Exception e) {
            log.error("Failed to initialize CORS configuration", e);
            throw new IllegalStateException("CORS configuration failed", e);
        }
    }
    
    /**
     * Creates CORS configuration for public endpoints (products, health checks).
     */
    private CorsConfiguration createPublicCorsConfig(List<String> allowedOrigins) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(allowedOrigins);
        config.setAllowedMethods(corsProperties.getAllowedMethods());
        config.setAllowedHeaders(corsProperties.getAllowedHeaders());
        config.setAllowCredentials(corsProperties.isAllowCredentials());
        config.setExposedHeaders(corsProperties.getExposedHeaders());
        config.setMaxAge(corsProperties.getMaxAge());
        
        if (corsProperties.isEnableLogging()) {
            CorsUtils.logCorsConfiguration(allowedOrigins, config.getAllowedHeaders(), 
                config.getAllowedMethods(), config.getAllowCredentials());
        }
        
        return config;
    }
    
    /**
     * Creates CORS configuration for authentication endpoints.
     */
    private CorsConfiguration createAuthCorsConfig(List<String> allowedOrigins) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(allowedOrigins);
        config.setAllowedMethods(Arrays.asList("POST", "OPTIONS")); // Only POST for auth
        config.setAllowedHeaders(Arrays.asList(
            "Authorization", 
            "Content-Type", 
            "Accept",
            "X-Requested-With",
            "Origin"
        ));
        config.setAllowCredentials(true); // Required for auth
        config.setExposedHeaders(Arrays.asList("Authorization"));
        config.setMaxAge(1800L); // 30 minutes for auth
        
        return config;
    }
    
    /**
     * Creates CORS configuration for general API endpoints.
     */
    private CorsConfiguration createApiCorsConfig(List<String> allowedOrigins) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(allowedOrigins);
        config.setAllowedMethods(corsProperties.getAllowedMethods());
        config.setAllowedHeaders(corsProperties.getAllowedHeaders());
        config.setAllowCredentials(corsProperties.isAllowCredentials());
        config.setExposedHeaders(corsProperties.getExposedHeaders());
        config.setMaxAge(corsProperties.getMaxAge());
        
        return config;
    }
    
    /**
     * Creates CORS configuration for admin endpoints (most restrictive).
     */
    private CorsConfiguration createAdminCorsConfig(List<String> allowedOrigins) {
        CorsConfiguration config = new CorsConfiguration();
        
        // Only allow specific origins for admin (no wildcards)
        config.setAllowedOrigins(allowedOrigins.stream()
            .filter(origin -> !origin.contains("*"))
            .collect(java.util.stream.Collectors.toList()));
        
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList(
            "Authorization", 
            "Content-Type", 
            "Accept",
            "X-Requested-With"
        ));
        config.setAllowCredentials(true);
        config.setExposedHeaders(Arrays.asList("Authorization"));
        config.setMaxAge(900L); // 15 minutes for admin
        
        return config;
    }
}
