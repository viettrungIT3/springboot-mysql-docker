package com.backend.backend.infrastructure.web.versioning;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class ApiVersionConfig {

    @Bean
    public FilterRegistrationBean<ApiVersionRedirectFilter> apiVersionRedirectFilter() {
        FilterRegistrationBean<ApiVersionRedirectFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ApiVersionRedirectFilter());
        registrationBean.addUrlPatterns("/api/*");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        return registrationBean;
    }
}


