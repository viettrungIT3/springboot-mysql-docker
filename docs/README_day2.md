# Day 2 — Profiles & Configuration

## 🎯 Mục tiêu
- Tách cấu hình ứng dụng thành nhiều **profile** (`dev`, `test`, `prod`).
- Cho phép override cấu hình bằng biến môi trường (`.env`).
- Chạy ứng dụng theo môi trường mong muốn.

---

## 1. Cấu hình Spring Boot

Trong `src/main/resources/`:

### `application.yml` (chung)
```yaml
spring:
  application:
    name: springboot-mysql-docker
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    open-in-view: false
    properties:
      hibernate:
        format_sql: true

logging:
  level:
    root: INFO
```

### `application-dev.yml`
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/${MYSQL_DATABASE:appdb}?useSSL=false&allowPublicKeyRetrieval=true
    username: ${MYSQL_USER:appuser}
    password: ${MYSQL_PASSWORD:apppass}
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

server:
  port: 8080

logging:
  level:
    root: DEBUG
```

### `application-test.yml`

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: false

server:
  port: 0
```

### `application-prod.yml`
```yaml
spring:
  datasource:
    url: jdbc:mysql://${MYSQL_HOST:mysql}:${MYSQL_PORT:3306}/${MYSQL_DATABASE:appdb}?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
    username: ${MYSQL_USER:appuser}
    password: ${MYSQL_PASSWORD:apppass}
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false

server:
  port: 8080
```

## 2. Docker Compose
docker-compose.yml đã được cập nhật để dùng profile thay vì override trực tiếp:

```yml
services:
  backend:
    environment:
      SPRING_PROFILES_ACTIVE: ${SPRING_PROFILES_ACTIVE:-prod}
      MYSQL_HOST: ${DB_HOST:-mysql}
      MYSQL_PORT: ${DB_PORT:-3306}
      MYSQL_DATABASE: ${MYSQL_DATABASE:-appdb}
      MYSQL_USER: ${MYSQL_USER:-appuser}
      MYSQL_PASSWORD: ${MYSQL_PASSWORD:-apppass}
```

👉 Lưu ý: các biến SPRING_DATASOURCE_* trước đây đã được gỡ bỏ để không đè lên cấu hình trong application-*.yml.

## 3. File .env

Ví dụ .env mặc định:
```env
MYSQL_ROOT_PASSWORD=root
MYSQL_DATABASE=appdb
MYSQL_USER=appuser
MYSQL_PASSWORD=apppass
MYSQL_PORT=3306
BACKEND_PORT=8080
DB_HOST=mysql
DB_PORT=3306

# profile đang dùng
SPRING_PROFILES_ACTIVE=prod
```

## 4. Cách chạy
```
make up
make logs
```

## 5. Kết quả

Cấu hình tách biệt rõ ràng cho `dev`, `test`, `prod`.

Có thể chuyển đổi profile dễ dàng qua biến `SPRING_PROFILES_ACTIVE`.

Dự án trở nên “12-factor friendly” hơn và dễ deploy trên môi trường khác nhau.