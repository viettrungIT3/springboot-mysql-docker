# Hướng dẫn thiết lập Environment Variables

## Tổng quan

File `.env.example` chứa template cho tất cả các biến môi trường cần thiết để chạy ứng dụng Spring Boot + MySQL + Docker.

## Cách sử dụng

### 1. Tạo file .env từ template
```bash
cp .env.example .env
```

### 2. Chỉnh sửa các giá trị phù hợp
Mở file `.env` và thay đổi các giá trị theo môi trường của bạn.

## Các phần cấu hình chính

### 🗄️ Database Configuration
```bash
MYSQL_ROOT_PASSWORD=root
MYSQL_DATABASE=appdb
MYSQL_USER=appuser
MYSQL_PASSWORD=apppass
MYSQL_PORT=3306
MYSQL_HOST=mysql
```

### 🚀 Backend Configuration
```bash
SPRING_PROFILES_ACTIVE=dev
BACKEND_PORT=8080
JPA_DDL_AUTO=none
JPA_SHOW_SQL=true
```

### 🌱 Seed Data Configuration
```bash
APP_SEED_ENABLED=true
APP_SEED_PRODUCTS=20
APP_SEED_CUSTOMERS=10
```

### 🔐 JWT Configuration
```bash
JWT_SECRET=mySecretKey123456789012345678901234567890
JWT_EXPIRATION=86400000
```

### 🌍 CORS Configuration
```bash
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5173,http://127.0.0.1:3000,http://127.0.0.1:5173
CORS_ALLOWED_HEADERS=Authorization,Content-Type,Accept,X-Requested-With,Origin
CORS_ALLOWED_METHODS=GET,POST,PUT,PATCH,DELETE,OPTIONS
CORS_ALLOW_CREDENTIALS=true
CORS_EXPOSED_HEADERS=Authorization
CORS_MAX_AGE=3600
CORS_ENABLE_LOGGING=true
```

## Cấu hình theo môi trường

### Development
```bash
SPRING_PROFILES_ACTIVE=dev
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5173,http://127.0.0.1:3000,http://127.0.0.1:5173
CORS_ENABLE_LOGGING=true
APP_SEED_ENABLED=true
JPA_SHOW_SQL=true
```

### Production
```bash
SPRING_PROFILES_ACTIVE=prod
CORS_ALLOWED_ORIGINS=https://my-frontend.com,https://www.my-frontend.com
CORS_ENABLE_LOGGING=false
CORS_MAX_AGE=1800
APP_SEED_ENABLED=false
JPA_SHOW_SQL=false
```

### Testing
```bash
SPRING_PROFILES_ACTIVE=test
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5173
CORS_ENABLE_LOGGING=true
APP_SEED_ENABLED=true
```

## Lưu ý bảo mật

### ⚠️ Quan trọng cho Production:

1. **JWT_SECRET**: Phải thay đổi thành secret key mạnh và duy nhất
2. **CORS_ALLOWED_ORIGINS**: Không sử dụng wildcard (*), chỉ whitelist domain chính thức
3. **Database passwords**: Sử dụng mật khẩu mạnh
4. **CORS_ENABLE_LOGGING**: Tắt trong production để tránh leak thông tin

### 🔒 Best Practices:

- Không commit file `.env` vào git
- Sử dụng `.env.example` làm template
- Rotate secrets định kỳ
- Sử dụng environment variables trong CI/CD

## Troubleshooting

### Lỗi CORS
```bash
# Kiểm tra CORS_ALLOWED_ORIGINS có đúng format không
curl -i http://localhost:8080/api/v1/cors-test -H "Origin: http://localhost:3000"
```

### Lỗi Database Connection
```bash
# Kiểm tra MySQL container có chạy không
docker ps | grep mysql

# Kiểm tra logs
docker logs mysql
```

### Lỗi JWT
```bash
# Kiểm tra JWT_SECRET có đủ dài không (tối thiểu 32 ký tự)
echo $JWT_SECRET | wc -c
```

## Quick Commands

```bash
# Tạo .env từ template
cp .env.example .env

# Restart ứng dụng sau khi thay đổi .env
make restart

# Kiểm tra logs
make logs

# Test CORS
curl -i http://localhost:8080/api/v1/cors-test -H "Origin: http://localhost:3000"
```
