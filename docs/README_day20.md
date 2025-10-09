# Day 20 — API Versioning & Deprecation

## Mục tiêu
Thêm filter chuyển hướng các legacy API paths (`/api/**`) sang versioned paths (`/api/v1/**`) kèm deprecation headers.

## Thay đổi chính

### 1. Filter Implementation
- **File**: `backend/src/main/java/com/backend/backend/infrastructure/web/versioning/ApiVersionRedirectFilter.java`
- **Chức năng**: 
  - Chặn requests đến `/api/**` (không phải `/api/v1/**`)
  - Thêm deprecation headers
  - Forward request đến `/api/v1/**`

### 2. Configuration
- **File**: `backend/src/main/java/com/backend/backend/infrastructure/web/versioning/ApiVersionConfig.java`
- **Chức năng**: Đăng ký filter với Spring Boot

### 3. Headers được thêm
```
Deprecation: true
Sunset: 2026-12-31
Link: </api/v1>; rel=successor-version
X-API-Deprecated: Use /api/v1 instead of /api
```

## Cách test

### 1. Test legacy path với curl
```bash
# Test legacy path (sẽ được forward)
curl -v http://localhost:8080/api/products

# Response sẽ có deprecation headers và forward đến /api/v1/products
```

### 2. Test versioned path
```bash
# Test versioned path (không bị ảnh hưởng)
curl -v http://localhost:8080/api/v1/products
```

## Lợi ích
- **Backward compatibility**: Legacy clients vẫn hoạt động
- **Migration path**: Có thời gian để migrate sang v1
- **Clear deprecation**: Headers rõ ràng về việc deprecated
- **Zero downtime**: Không ảnh hưởng đến existing clients

## Lưu ý cho dev
- Filter chạy với `Ordered.HIGHEST_PRECEDENCE + 1`
- Chỉ forward, không redirect (giữ nguyên URL trong browser)
- Có thể customize sunset date trong filter
