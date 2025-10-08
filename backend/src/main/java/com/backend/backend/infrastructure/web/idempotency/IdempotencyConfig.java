package com.backend.backend.infrastructure.web.idempotency;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class IdempotencyConfig {

    @Bean
    public FilterRegistrationBean<IdempotencyFilter> idempotencyFilter() {
        FilterRegistrationBean<IdempotencyFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new IdempotencyFilter());
        registrationBean.addUrlPatterns("/api/v1/orders/*");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 2);
        return registrationBean;
    }
}


