# Day 11 — Logging chuẩn 🔎

## Mục tiêu
Triển khai logging chuẩn với:
- **Dev/Test**: Log pattern có màu, dễ đọc cho developer
- **Prod**: Log JSON format, dễ ship về ELK/Loki/Datadog
- **Correlation ID**: Trace request xuyên suốt hệ thống
- **Environment control**: Điều khiển mức log qua ENV

## ✅ Đã triển khai

### 1. Dependency JSON Logging
```gradle
// backend/build.gradle
dependencies {
    // JSON logs for prod
    implementation 'net.logstash.logback:logstash-logback-encoder:7.4'
}
```

### 2. Correlation ID Filter
```java
// src/main/java/com/backend/backend/web/CorrelationIdFilter.java
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter implements Filter {
    public static final String HDR = "X-Correlation-Id";
    public static final String MDC_KEY = "corrId";
    public static final String MDC_PATH = "path";
    public static final String MDC_METHOD = "method";
    
    // Tự động sinh correlation ID nếu client không gửi
    // Lưu vào MDC để log pattern/JSON đều có trường này
}
```

### 3. Logback Configuration theo Profile
```xml
<!-- src/main/resources/logback-spring.xml -->
<configuration scan="true">
  <property name="APP_NAME" value="${spring.application.name:-springboot-mysql-docker}"/>
  <property name="LOG_LEVEL" value="${LOG_LEVEL:-INFO}"/>

  <!-- DEV / TEST: console pattern (human-readable) -->
  <springProfile name="dev,test">
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
      <encoder>
        <pattern>%d{HH:mm:ss.SSS} %clr(%-5level) [%thread] %clr(%logger{36}){cyan} - %msg
          | corrId=%X{corrId} | %X{method} %X{path}%n
        </pattern>
      </encoder>
    </appender>
  </springProfile>

  <!-- PROD: JSON console -->
  <springProfile name="prod">
    <appender name="JSON_CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
      <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
        <providers>
          <timestamp/>
          <pattern>
            <pattern>
              {
                "level": "%level",
                "logger": "%logger",
                "thread": "%thread",
                "message": "%message",
                "app": "${APP_NAME}",
                "corrId": "%mdc{corrId}",
                "method": "%mdc{method}",
                "path": "%mdc{path}"
              }
            </pattern>
          </pattern>
          <stackTrace/>
          <mdc/>
        </providers>
      </encoder>
    </appender>
  </springProfile>
</configuration>
```

### 4. Request Logging cho Dev/Test
```java
// src/main/java/com/backend/backend/web/RequestLoggingConfig.java
@Configuration
@Profile({"dev","test"})
public class RequestLoggingConfig {
    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        var f = new CommonsRequestLoggingFilter();
        f.setIncludeClientInfo(true);
        f.setIncludeQueryString(true);
        f.setIncludePayload(true);
        f.setMaxPayloadLength(1000);
        f.setIncludeHeaders(false);
        return f;
    }
}
```

### 5. Environment Configuration
```yaml
# docker-compose.yml
environment:
  SPRING_PROFILES_ACTIVE: ${SPRING_PROFILES_ACTIVE:-prod}
  LOG_LEVEL: ${LOG_LEVEL:-INFO}
```

```bash
# .env
LOG_LEVEL=INFO  # DEBUG, INFO, WARN, ERROR
```


## 🧪 Testing

### Test với Profile Dev (Pattern Logging)

```bash
# .env
SPRING_PROFILES_ACTIVE=dev
LOG_LEVEL=DEBUG
```

```bash
# Restart và test
make restart

# call test
curl -H "X-Correlation-Id: demo-123" http://localhost:8080/api/v1/products

# watch logs
make logs-tail
```

**Kết quả Dev:**
```
12:34:56.789 INFO  [http-nio-8080-exec-1] c.b.b.controller.ProductController - Create product
 | corrId=demo-123 | GET /api/v1/products
```

### Test với Profile Prod (JSON Logging)
```bash
# .env
SPRING_PROFILES_ACTIVE=prod
LOG_LEVEL=INFO
```

```bash
# Restart và test
make restart

# call test
curl -H "X-Correlation-Id: prod-test-456" http://localhost:8080/api/v1/products

# watch logs
make logs-correlation ID=prod-test-456
```

**Kết quả Prod:**
```json
{
  "@timestamp":"2025-09-06T07:51:14.543837796Z",
  "level":"INFO",
  "logger":"com.backend.backend.controller.ProductController",
  "thread":"http-nio-8080-exec-1",
  "message":"Create product",
  "app":"springboot-mysql-docker",
  "corrId":"prod-test-456",
  "method":"GET",
  "path":"/api/v1/products"
}
```

### Test Correlation ID
```bash
# Test với custom correlation ID
curl -H "X-Correlation-Id: custom-trace-789" http://localhost:8080/api/v1/products

# Test không có correlation ID (server tự sinh)
curl http://localhost:8080/api/v1/products

# Kiểm tra logs để thấy correlation ID
make logs-correlation
```

## 📊 Log Format Comparison

| Aspect | Dev/Test | Production |
|--------|----------|------------|
| **Format** | Colored pattern | JSON |
| **Readability** | Human-friendly | Machine-friendly |
| **Correlation ID** | ✅ Visible | ✅ Structured |
| **Method/Path** | ✅ Visible | ✅ Structured |
| **Timestamp** | ✅ Human format | ✅ ISO 8601 |
| **Log Level** | ✅ Colored | ✅ Structured |
| **Stack Trace** | ✅ Readable | ✅ Structured |

## 🎯 Best Practices

### 1. Log Levels
- **ERROR**: Lỗi nghiêm trọng, cần xử lý ngay
- **WARN**: Cảnh báo, có thể ảnh hưởng đến hệ thống
- **INFO**: Thông tin business quan trọng (create order, payment, etc.)
- **DEBUG**: Chi tiết kỹ thuật, chỉ dùng khi debug

### 2. Correlation ID Usage
- **Frontend/Gateway**: Truyền `X-Correlation-Id` header
- **Backend**: Giữ nguyên correlation ID xuyên suốt request
- **Microservices**: Forward correlation ID giữa các service

### 3. Production Logging
- **JSON format**: Dễ ship về ELK/Loki/Datadog
- **Structured data**: Dễ query và analyze
- **No sensitive data**: Không log password, token, etc.

### 4. Development Logging
- **Colored output**: Dễ đọc khi development
- **Request logging**: Bật ở dev/test, tắt ở prod
- **Debug level**: Có thể bật khi cần debug

### 5. Performance Considerations
- **JSON encoding**: Có overhead nhỏ so với pattern logging
- **Correlation ID**: Minimal impact, chỉ set/get MDC
- **Request logging**: Chỉ bật ở dev/test, tắt ở prod
- **Log level**: DEBUG có thể ảnh hưởng performance

## 🔧 Configuration Commands

### Makefile Commands cho Logging
```bash
# 🎯 RECOMMENDED: Xem logs gần đây (20 dòng cuối)
make logs-tail

# 🔍 Xem logs với correlation ID cụ thể
make logs-correlation ID=demo-123

# 📋 Xem tất cả correlation IDs gần đây
make logs-correlation

# 📄 Xem logs backend (follow mode - nhiều thông tin)
make logs

# 📊 Xem logs tất cả services
make logs-all

# 🐚 Shell vào container backend
make sh-app

# 🔄 Restart để áp dụng config mới
make restart
```

**💡 Tips:**
- `make logs-tail` - Ngắn gọn, chỉ 20 dòng cuối
- `make logs-correlation` - Tìm correlation IDs trong logs
- `make logs-correlation ID=xxx` - Filter theo correlation ID cụ thể
- `make logs` - Follow mode, nhiều thông tin (Ctrl+C để dừng)

### Cách thao tác với .env file

```bash
# Chỉnh sửa trực tiếp file .env
LOG_LEVEL=DEBUG  # hoặc INFO, WARN, ERROR
SPRING_PROFILES_ACTIVE=dev  # hoặc prod, test
```

**Lưu ý:** File `.env.example` có comment chi tiết cho tất cả biến môi trường.

### Thay đổi Log Level
```bash
# .env
LOG_LEVEL=DEBUG  # hoặc INFO, WARN, ERROR
```

### Thay đổi Profile
```bash
# .env
SPRING_PROFILES_ACTIVE=dev   # hoặc prod, test
```

## 🔧 Troubleshooting

### Log không hiển thị correlation ID
```bash
# Kiểm tra correlation IDs gần đây
make logs-correlation

# Kiểm tra filter có được load không
make logs-tail | grep "CorrelationIdFilter"
```

### JSON logs không đúng format
```bash
# Kiểm tra profile
make logs-tail | grep "Active profiles"

# Kiểm tra logback config
make sh-app
# Trong container: cat /app/logback-spring.xml
```

### Log level không thay đổi
```bash
# Kiểm tra environment variable
make sh-app
# Trong container: env | grep LOG_LEVEL

# Restart container
make restart
```

## 🚀 Next Steps

1. **Log Aggregation**: Setup ELK hoặc Grafana Loki
2. **Alerting**: Cấu hình alert cho ERROR logs  
3. **Metrics**: Thêm Micrometer metrics
4. **Distributed Tracing**: Tích hợp với Zipkin/Jaeger
5. **Log Retention**: Cấu hình log rotation và cleanup
6. **Performance**: Monitor log performance impact
7. **Security**: Audit logging cho sensitive operations

## 📚 References

- [Logback Documentation](http://logback.qos.ch/documentation.html)
- [Logstash Logback Encoder](https://github.com/logstash/logstash-logback-encoder)
- [Spring Boot Logging](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.logging)
- [ELK Stack Guide](https://www.elastic.co/guide/en/elastic-stack/current/index.html)
- [Grafana Loki](https://grafana.com/docs/loki/latest/)

---

**Day 11 Complete!** 🎉  
Logging system đã sẵn sàng cho development và production với correlation ID tracking và structured JSON logging.
