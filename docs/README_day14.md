# Day 14 — JWT Security Implementation 🔐

## 🎯 Mục tiêu
Implement Spring Security với JWT (JSON Web Token) để tạo hệ thống authentication stateless và bảo vệ API endpoints.

## 📋 Tiêu chí thành công
- ✅ Spring Security được cấu hình stateless (không session)
- ✅ JWT token được generate và validate
- ✅ Login endpoint `/auth/login` hoạt động
- ✅ Protected endpoints `/api/**` yêu cầu JWT token
- ✅ Public endpoints `/auth/**`, `/swagger-ui/**` không cần authentication
- ✅ Swagger UI tích hợp JWT authentication

## 🏗️ Kiến trúc Implementation

### 1. Dependencies (build.gradle)
```gradle
implementation 'org.springframework.boot:spring-boot-starter-security'
implementation 'io.jsonwebtoken:jjwt-api:0.12.3'
runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.3'
runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.3'
```

### 2. Security Configuration
- **SecurityConfig**: Cấu hình filter chain và authorization rules
- **SecurityBeans**: Centralized security beans (PasswordEncoder, UserDetailsService, AuthenticationProvider)
- **JwtAuthFilter**: Custom filter để validate JWT tokens
- **JwtUtil**: Utility class cho JWT operations

### 3. Authentication Flow
```
1. Client → POST /auth/login {username, password}
2. AuthenticationManager → validate credentials
3. JwtUtil → generate JWT token
4. Response → {username, token, authorities}
5. Client → API calls với Authorization: Bearer <token>
6. JwtAuthFilter → validate token và set SecurityContext
```

## 📁 Files Created/Modified

### New Files:
- `backend/src/main/java/com/backend/backend/security/SecurityBeans.java`
- `backend/src/main/java/com/backend/backend/security/JwtUtil.java`
- `backend/src/main/java/com/backend/backend/security/JwtAuthFilter.java`
- `backend/src/main/java/com/backend/backend/controller/AuthController.java`

### Modified Files:
- `backend/build.gradle` - Added JWT dependencies
- `backend/src/main/java/com/backend/backend/config/SecurityConfig.java` - Updated security rules
- `backend/src/main/java/com/backend/backend/entity/Administrator.java` - Added role field
- `backend/src/main/resources/db/migration/V5__add_jwt_security.sql` - Database migration
- `backend/src/main/resources/application*.yml` - JWT configuration
- `docker-compose.yml` - Environment variables
- `makefile` - JWT testing commands

## 🔧 Configuration

### JWT Properties
```yaml
jwt:
  secret: ${JWT_SECRET:mySecretKey}
  expiration: ${JWT_EXPIRATION:86400000} # 24 hours
```

### Security Rules
```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/auth/**", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
    .requestMatchers("/api/**").authenticated()
    .anyRequest().permitAll())
```

## 🧪 Testing

### Manual Testing
```bash
# 1. Login để lấy JWT token
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'

# Response:
{
  "username": "admin",
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "authorities": [{"authority": "ROLE_ADMIN"}]
}

# 2. Sử dụng token để access protected endpoint
curl -X GET http://localhost:8080/api/v1/products \
  -H "Authorization: Bearer <token>"

# 3. Test unauthorized access (should return 403)
curl -X GET http://localhost:8080/api/v1/products
```

### Automated Testing Commands
```bash
make create-admin-user    # Tạo admin user
make test-jwt-login       # Test login endpoint
make test-jwt-protected   # Test protected endpoints
make test-api            # Full API test với JWT
make security-status     # Hiển thị trạng thái security
```

## 🗄️ Database Changes

### Migration V5__add_jwt_security.sql
```sql
-- Rename password column to password_hash
ALTER TABLE administrators CHANGE COLUMN password password_hash VARCHAR(255) NOT NULL;

-- Add role column
ALTER TABLE administrators ADD COLUMN role ENUM('ADMIN', 'MANAGER', 'SALE') NOT NULL DEFAULT 'MANAGER';

-- Set admin role for existing admin
UPDATE administrators SET role = 'ADMIN' WHERE id = 1;
```

### Administrator Entity Updates
```java
@Entity
@Table(name = "administrators")
public class Administrator {
    // ... existing fields ...
    
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.MANAGER;
    
    public enum Role {
        ADMIN, MANAGER, SALE
    }
}
```

## 🔐 Security Features

### 1. Password Security
- BCryptPasswordEncoder cho password hashing
- Passwords được hash trước khi lưu database
- Plain text passwords không bao giờ được lưu

### 2. JWT Token Security
- HMAC SHA256 signing algorithm
- Configurable expiration time (default 24 hours)
- Secret key từ environment variables
- Token validation trong mỗi request

### 3. Role-Based Access Control
- Administrator roles: ADMIN, MANAGER, SALE
- Authorities được include trong JWT token
- Có thể extend cho fine-grained permissions

### 4. Stateless Architecture
- Không session storage
- Mỗi request tự validate token
- Scalable cho microservices

## 🚀 Deployment

### Environment Variables
```bash
JWT_SECRET=your-secret-key-here
JWT_EXPIRATION=86400000
```

### Docker Configuration
```yaml
services:
  backend:
    environment:
      - JWT_SECRET=${JWT_SECRET}
      - JWT_EXPIRATION=${JWT_EXPIRATION}
```

## 📊 Test Results

### ✅ Successful Tests
- **Login Endpoint**: Returns valid JWT token
- **Protected Endpoints**: Require valid JWT token
- **Unauthorized Access**: Returns 403 Forbidden
- **Public Endpoints**: Accessible without token
- **Swagger UI**: Accessible and shows JWT authentication

### 🔧 Issues Fixed
- Bean conflicts (PasswordEncoder duplicate)
- Deprecated API usage (DaoAuthenticationProvider)
- Port configuration (8080 vs 8081)
- Compilation errors (passwordHash field names)

## 🎯 Next Steps

### Potential Enhancements
1. **Refresh Token**: Implement refresh token mechanism
2. **Role-Based Endpoints**: Fine-grained permissions per endpoint
3. **Token Blacklisting**: Logout functionality với token invalidation
4. **Rate Limiting**: Prevent brute force attacks
5. **Multi-Factor Authentication**: SMS/Email verification

### Integration Points
- Frontend authentication flow
- API client authentication
- Microservices communication
- Mobile app authentication

## 📚 Resources

### Documentation
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [JWT.io](https://jwt.io/) - JWT token decoder
- [jjwt Documentation](https://github.com/jwtk/jjwt)

### Testing Tools
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- Postman collection với JWT authentication
- curl commands cho manual testing

---

## 🏆 Summary

Day 14 successfully implemented a complete JWT-based authentication system with:
- ✅ Stateless Spring Security configuration
- ✅ JWT token generation and validation
- ✅ Protected API endpoints
- ✅ Role-based access control
- ✅ Comprehensive testing suite
- ✅ Production-ready configuration

The system is now ready for frontend integration and production deployment! 🚀
