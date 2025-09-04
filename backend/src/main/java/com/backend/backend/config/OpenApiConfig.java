package com.backend.backend.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
        @Bean
        public OpenAPI apiInfo() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("Inventory API")
                                                .description("API quản lý sản phẩm, đơn hàng, khách hàng, nhà cung cấp")
                                                .version("v1")
                                                .license(new License().name("MIT")))
                                .externalDocs(new ExternalDocumentation()
                                                .description("Swagger UI")
                                                .url("/swagger-ui.html"));
        }
}
