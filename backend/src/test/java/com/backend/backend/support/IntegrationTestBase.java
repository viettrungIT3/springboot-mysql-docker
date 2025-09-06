package com.backend.backend.support;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")   // tái dùng cấu hình test (logging, v.v.)
@TestInstance(Lifecycle.PER_CLASS)
public abstract class IntegrationTestBase {

    private static final Properties testConfig = loadTestConfig();

    @Container
    @SuppressWarnings("resource") // Testcontainers tự động quản lý lifecycle của container
    static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName(getTestProperty("test.db.name", "testdb"))
            .withUsername(getTestProperty("test.db.user", "testuser"))
            .withPassword(getTestProperty("test.db.password", "testpass"));

    private static Properties loadTestConfig() {
        Properties props = new Properties();
        try (InputStream is = IntegrationTestBase.class.getClassLoader()
                .getResourceAsStream("test-config.properties")) {
            if (is != null) {
                props.load(is);
            }
        } catch (IOException e) {
            // Ignore - sẽ dùng defaults
        }
        return props;
    }

    private static String getTestProperty(String key, String defaultValue) {
        // Ưu tiên environment variables, sau đó properties file, cuối cùng là default
        return System.getenv().getOrDefault(
                key.toUpperCase().replace(".", "_"),
                testConfig.getProperty(key, defaultValue)
        );
    }

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");

        // Tạm thời cho test tự tạo schema; Day 9 sẽ chuyển sang Flyway.
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update"); // hoặc "create-drop" nếu muốn sạch mỗi lần
        registry.add("spring.jpa.show-sql", () -> "false");
    }
}
