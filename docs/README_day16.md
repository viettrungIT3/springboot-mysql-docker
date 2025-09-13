# Day 16 — CORS & Rate Limiting Configuration 🌐🚦

## Mục tiêu

1. **CORS Configuration**: Cấu hình CORS an toàn và linh hoạt sử dụng environment variables từ file `.env`, cho phép frontend gọi API backend từ domain khác với bảo mật cao.

2. **Rate Limiting**: Implement Bucket4j rate limiting cho public endpoints với tiêu chí 429 khi vượt limit và cấu hình qua environment variables.

## Tổng quan

Trong ngày này, chúng ta đã hoàn thành hai tính năng quan trọng:

1. **CORS Configuration**: Refactor toàn bộ cấu hình CORS từ các file `application-*.yml` sang sử dụng environment variables trong file `.env`. Điều này mang lại nhiều lợi ích về bảo mật, linh hoạt và dễ quản lý.

2. **Rate Limiting**: Implement Bucket4j rate limiting với bucket isolation cho các loại endpoints khác nhau, đảm bảo API được bảo vệ khỏi abuse và overload.

## Kiến trúc CORS mới

### 🔧 Components chính

1. **CorsProperties**: Component đọc cấu hình từ environment variables
2. **SecurityConfig**: Tích hợp CORS vào Spring Security với phân quyền theo endpoint
3. **CorsUtils**: Utility class cho validation và logging
4. **CorsEventListener**: Monitoring CORS requests
5. **CorsTestController**: Endpoints để test CORS

### 📁 File structure

```
backend/src/main/java/com/backend/backend/
├── config/
│   ├── CorsProperties.java          # Đọc từ .env
│   ├── SecurityConfig.java         # CORS integration
│   └── CorsEventListener.java      # Monitoring
├── util/
│   └── CorsUtils.java              # Validation & logging
└── controller/
    └── CorsTestController.java     # Test endpoints
```

## Cấu hình Environment Variables

### 📝 File .env

```bash
# ------------------ CORS Configuration ------------------
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5173,http://127.0.0.1:3000,http://127.0.0.1:5173
CORS_ALLOWED_HEADERS=Authorization,Content-Type,Accept,X-Requested-With,Origin
CORS_ALLOWED_METHODS=GET,POST,PUT,PATCH,DELETE,OPTIONS
CORS_ALLOWED_CREDENTIALS=true
CORS_EXPOSED_HEADERS=Authorization
CORS_MAX_AGE=3600
CORS_ENABLE_LOGGING=true
```

### 🔧 CorsProperties.java

```java
@Component
public class CorsProperties {
    
    @Value("${CORS_ALLOWED_ORIGINS:http://localhost:3000}")
    private String allowedOrigins;
    
    @Value("${CORS_ALLOWED_HEADERS:Authorization,Content-Type,Accept,X-Requested-With,Origin}")
    private String allowedHeadersStr;
    
    @Value("${CORS_ALLOWED_METHODS:GET,POST,PUT,PATCH,DELETE,OPTIONS}")
    private String allowedMethodsStr;
    
    @Value("${CORS_ALLOW_CREDENTIALS:true}")
    private boolean allowCredentials;
    
    @Value("${CORS_EXPOSED_HEADERS:Authorization}")
    private String exposedHeadersStr;
    
    @Value("${CORS_MAX_AGE:3600}")
    private long maxAge;
    
    @Value("${CORS_ENABLE_LOGGING:false}")
    private boolean enableLogging;
    
    // Helper methods để convert strings thành lists
    public List<String> getAllowedHeaders() {
        return Arrays.asList(allowedHeadersStr.split(","));
    }
    
    public List<String> getAllowedMethods() {
        return Arrays.asList(allowedMethodsStr.split(","));
    }
    
    public List<String> getExposedHeaders() {
        return Arrays.asList(exposedHeadersStr.split(","));
    }
}
```

## Cấu hình CORS theo Endpoint

### 🛡️ SecurityConfig.java

```java
@Configuration
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
    
    private final CorsProperties corsProperties;
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        try {
            List<String> origins = CorsUtils.validateAndParseOrigins(corsProperties.getAllowedOrigins());
            
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            
            // Public endpoints (products, health, docs)
            source.registerCorsConfiguration("/api/v1/products/**", createPublicCorsConfig(origins));
            source.registerCorsConfiguration("/actuator/health", createPublicCorsConfig(origins));
            source.registerCorsConfiguration("/v3/api-docs/**", createPublicCorsConfig(origins));
            source.registerCorsConfiguration("/swagger-ui/**", createPublicCorsConfig(origins));
            
            // Auth endpoints
            source.registerCorsConfiguration("/auth/**", createAuthCorsConfig(origins));
            
            // General API endpoints
            source.registerCorsConfiguration("/api/**", createApiCorsConfig(origins));
            
            // Admin endpoints (restrictive)
            source.registerCorsConfiguration("/api/v1/admin/**", createAdminCorsConfig(origins));
            
            return source;
        } catch (Exception e) {
            log.error("Failed to initialize CORS configuration", e);
            throw new IllegalStateException("CORS configuration failed", e);
        }
    }
    
    private CorsConfiguration createPublicCorsConfig(List<String> origins) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(origins);
        config.setAllowedHeaders(corsProperties.getAllowedHeaders());
        config.setAllowedMethods(corsProperties.getAllowedMethods());
        config.setAllowCredentials(corsProperties.isAllowCredentials());
        config.setExposedHeaders(corsProperties.getExposedHeaders());
        config.setMaxAge(corsProperties.getMaxAge());
        return config;
    }
    
    // ... các method khác tương tự
}
```

## Validation và Logging

### 🔍 CorsUtils.java

```java
@Slf4j
public class CorsUtils {

    public static List<String> validateAndParseOrigins(String origins) {
        if (origins == null || origins.trim().isEmpty()) {
            log.warn("CORS_ALLOWED_ORIGINS is empty, using default 'http://localhost:3000'");
            return Arrays.asList("http://localhost:3000");
        }

        List<String> originList = Arrays.stream(origins.split(","))
            .map(String::trim)
            .filter(CorsUtils::isValidOrigin)
            .collect(Collectors.toList());

        if (originList.isEmpty()) {
            log.error("No valid origins found in CORS_ALLOWED_ORIGINS: {}", origins);
            throw new IllegalStateException("Invalid CORS configuration: No valid origins found.");
        }

        log.info("CORS configured for origins: {}", originList);
        return originList;
    }

    private static boolean isValidOrigin(String origin) {
        try {
            URI uri = new URI(origin);
            boolean isValid = uri.getScheme() != null &&
                             (uri.getScheme().equals("http") || uri.getScheme().equals("https")) &&
                             uri.getHost() != null;
            if (!isValid) {
                log.warn("Invalid origin format or scheme: {}", origin);
            }
            return isValid;
        } catch (URISyntaxException e) {
            log.warn("Invalid origin format: {}", origin);
            return false;
        }
    }
}
```

## Testing CORS

### 🧪 CorsTestController.java

```java
@RestController
@RequestMapping("/api/v1/cors-test")
public class CorsTestController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> testCors(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("method", request.getMethod());
        response.put("origin", request.getHeader("Origin"));
        response.put("message", "CORS GET test successful");
        response.put("timestamp", Instant.now().toString());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/public")
    public ResponseEntity<String> testPublicCors() {
        return ResponseEntity.ok("Public CORS test successful!");
    }

    @GetMapping("/admin")
    public ResponseEntity<String> testAdminCors() {
        return ResponseEntity.ok("Admin CORS test successful!");
    }
}
```

## Cấu hình theo môi trường

### 🚀 Development (.env)

```bash
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5173,http://127.0.0.1:3000,http://127.0.0.1:5173
CORS_ENABLE_LOGGING=true
CORS_MAX_AGE=3600
```

### 🏭 Production (.env)

```bash
CORS_ALLOWED_ORIGINS=https://my-frontend.com,https://www.my-frontend.com
CORS_ENABLE_LOGGING=false
CORS_MAX_AGE=1800
CORS_ALLOWED_HEADERS=Authorization,Content-Type,Accept,X-Requested-With
```

### 🧪 Testing (.env)

```bash
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5173
CORS_ENABLE_LOGGING=true
CORS_MAX_AGE=1800
```

## Testing và Validation

### 🔬 Test với curl

```bash
# Test origin được phép
curl -i http://localhost:8080/api/v1/cors-test -H "Origin: http://localhost:3000"

# Test origin không được phép
curl -i http://localhost:8080/api/v1/cors-test -H "Origin: http://malicious-site.com"

# Test preflight request
curl -i -X OPTIONS http://localhost:8080/api/v1/cors-test \
  -H "Origin: http://localhost:3000" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Authorization,Content-Type"
```

### 🌐 Test với browser

```javascript
// Trong browser console
fetch("http://localhost:8080/api/v1/cors-test", {
  method: "GET",
  headers: { 
    "Origin": "http://localhost:3000",
    "Authorization": "Bearer your-token"
  }
})
.then(r => r.json())
.then(console.log)
.catch(console.error);
```

## Lợi ích của kiến trúc mới

### ✅ Bảo mật

- **Không hardcode**: Origins không được hardcode trong code
- **Validation**: Tự động validate format của origins
- **Phân quyền**: CORS khác nhau cho các endpoint khác nhau
- **Logging**: Monitor và log CORS requests

### ✅ Linh hoạt

- **Environment-based**: Cấu hình khác nhau cho dev/prod/test
- **Dynamic**: Thay đổi cấu hình mà không cần rebuild
- **Centralized**: Tất cả cấu hình ở một nơi (.env)

### ✅ Dễ quản lý

- **Template**: .env.example làm template
- **Documentation**: Hướng dẫn chi tiết
- **Testing**: Endpoints chuyên dụng để test

## Best Practices

### 🔒 Bảo mật

1. **Không sử dụng wildcard (*)** trong production
2. **Whitelist cụ thể** các domain được phép
3. **Rotate secrets** định kỳ
4. **Tắt logging** trong production

### 🚀 Performance

1. **Cache preflight** với max-age phù hợp
2. **Minimize headers** chỉ expose cần thiết
3. **Optimize origins** không thêm domain không cần

### 🛠️ Development

1. **Enable logging** trong development
2. **Test endpoints** để verify CORS
3. **Document changes** khi thay đổi cấu hình

## Troubleshooting

### ❌ Lỗi thường gặp

1. **"Invalid CORS request"**: Kiểm tra origin có trong whitelist không
2. **"No valid origins found"**: Kiểm tra format của CORS_ALLOWED_ORIGINS
3. **"CORS configuration failed"**: Kiểm tra logs để xem lỗi cụ thể

### 🔍 Debug steps

1. **Kiểm tra .env**: `cat .env | grep CORS`
2. **Kiểm tra logs**: `docker logs backend | grep -i cors`
3. **Test endpoint**: `curl -i http://localhost:8080/api/v1/cors-test -H "Origin: http://localhost:3000"`

## Kết luận

## 🚦 Rate Limiting Implementation

### Kiến trúc Rate Limiting

#### Components chính

1. **RateLimitProperties**: Component đọc cấu hình rate limiting từ environment variables
2. **RateLimitFilter**: Servlet Filter implement Bucket4j rate limiting
3. **RateLimitTestController**: Endpoints để test rate limiting
4. **Bucket Isolation**: Mỗi endpoint type có bucket riêng biệt

#### Cấu hình Rate Limiting

```bash
# Rate Limiting Configuration
RATE_LIMIT_ENABLED=true
RATE_LIMIT_PUBLIC_REQUESTS=100
RATE_LIMIT_PUBLIC_WINDOW_SECONDS=60
RATE_LIMIT_API_REQUESTS=200
RATE_LIMIT_API_WINDOW_SECONDS=60
RATE_LIMIT_AUTH_REQUESTS=10
RATE_LIMIT_AUTH_WINDOW_SECONDS=60
RATE_LIMIT_ENABLE_LOGGING=true
RATE_LIMIT_MESSAGE=Rate limit exceeded. Please try again later.
```

#### Endpoint Types và Rate Limits

| Endpoint Type | Rate Limit | Window | Mô tả |
|---------------|------------|--------|-------|
| **Public** | 100 requests | 60 seconds | `/api/v1/products`, `/actuator/health`, `/v3/api-docs`, `/swagger-ui` |
| **API** | 200 requests | 60 seconds | `/api/**` (general API endpoints) |
| **Auth** | 10 requests | 60 seconds | `/auth/**`, endpoints chứa "auth" |

#### Bucket Isolation

Mỗi endpoint type có bucket riêng biệt dựa trên:
- **IP Address**: `clientIp`
- **Endpoint Type**: `requests:windowSeconds`

Ví dụ bucket keys:
- `127.0.0.1:10:60` (Auth endpoint)
- `127.0.0.1:100:60` (Public endpoint)
- `127.0.0.1:200:60` (API endpoint)

#### Testing Rate Limiting

```bash
# Test auth endpoint (10 requests/minute)
for i in {1..12}; do
  curl -s -w "HTTP %{http_code}\n" http://localhost:8080/api/v1/rate-limit-test/auth | tail -1
done

# Expected: Requests 1-10: HTTP 200, Requests 11-12: HTTP 429

# Test public endpoint (100 requests/minute) - should work independently
for i in {1..5}; do
  curl -s -w "HTTP %{http_code}\n" http://localhost:8080/api/v1/rate-limit-test/public | tail -1
done

# Expected: All requests: HTTP 200 (bucket isolation working)
```

#### Response khi Rate Limit Exceeded

```json
{
  "error": "Too Many Requests",
  "message": "Rate limit exceeded. Please try again later.",
  "status": 429
}
```

## Kết luận

Việc implement cả CORS và Rate Limiting đã mang lại:

### CORS Benefits:
- **Bảo mật cao hơn**: Không hardcode origins
- **Linh hoạt hơn**: Dễ thay đổi theo môi trường
- **Dễ quản lý hơn**: Tập trung cấu hình trong .env
- **Monitoring tốt hơn**: Logging và validation tự động

### Rate Limiting Benefits:
- **Bảo vệ API**: Ngăn chặn abuse và overload
- **Bucket Isolation**: Mỗi endpoint type có rate limit riêng
- **Configurable**: Dễ dàng điều chỉnh qua environment variables
- **Monitoring**: Logging chi tiết cho debugging

Kiến trúc này đảm bảo ứng dụng có thể hoạt động an toàn trong môi trường production với khả năng mở rộng và bảo trì dễ dàng.

## Tài liệu liên quan

- `docs/ENV_SETUP.md`: Hướng dẫn thiết lập environment variables
- `.env.example`: Template cấu hình hoàn chỉnh
- `backend/src/main/java/com/backend/backend/config/CorsProperties.java`: Implementation chi tiết
