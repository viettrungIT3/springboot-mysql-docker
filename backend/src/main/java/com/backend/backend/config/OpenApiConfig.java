package com.backend.backend.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

        // Khai báo trước để Day 14 bật JWT dễ dàng
        private static final String JWT_SCHEME = "bearer-jwt";

        @Bean
        public OpenAPI apiSpec() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("Spring Boot + MySQL – Inventory API")
                                                .description("""
                                                                API mẫu cho dự án Spring Boot + MySQL + Docker.
                                                                Bao gồm Products, Customers, Orders, Suppliers, Stock Management...
                                                                Hỗ trợ pagination, validation, và exception handling.
                                                                """)
                                                .version("v1")
                                                .contact(new Contact()
                                                                .name("Harry Dev")
                                                                .url("https://github.com/viettrungIT3")
                                                                .email("viettrungcntt03@gmail.com")))
                                .servers(List.of(
                                                new Server().url("http://localhost:8080")
                                                                .description("Local Development"),
                                                new Server().url("https://api.example.com")
                                                                .description("Production (example)")))
                                .tags(List.of(
                                                new Tag().name("Products").description(
                                                                "Quản lý sản phẩm - CRUD operations, search, pagination"),
                                                new Tag().name("Orders").description(
                                                                "Quản lý đơn hàng - Tạo, cập nhật, theo dõi đơn hàng"),
                                                new Tag().name("Customers").description(
                                                                "Quản lý khách hàng - Thông tin cá nhân, lịch sử mua hàng"),
                                                new Tag().name("Suppliers")
                                                                .description("Quản lý nhà cung cấp - CRUD operations"),
                                                new Tag().name("Stock Entries")
                                                                .description("Quản lý nhập kho - Theo dõi inventory"),
                                                new Tag().name("Order Items").description(
                                                                "Chi tiết đơn hàng - Line items của orders"),
                                                new Tag().name("Administrators")
                                                                .description("Quản lý admin - User management")))
                                // Đặt chỗ security scheme cho JWT (Day 14 sẽ dùng)
                                .components(new Components().addSecuritySchemes(JWT_SCHEME,
                                                new SecurityScheme()
                                                                .name(JWT_SCHEME)
                                                                .type(SecurityScheme.Type.HTTP)
                                                                .scheme("bearer")
                                                                .bearerFormat("JWT")));
        }
}
