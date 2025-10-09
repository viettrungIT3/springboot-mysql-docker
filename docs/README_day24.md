# Day 24 — Idempotency & Optimistic Locking

## Mục tiêu
Thêm Idempotency-Key cho POST orders và optimistic locking để đảm bảo data consistency.

## Thay đổi chính

### 1. Idempotency Filter
- **File**: `backend/src/main/java/com/backend/backend/infrastructure/web/idempotency/IdempotencyFilter.java`
- **Chức năng**: 
  - Bắt buộc `Idempotency-Key` header cho `POST /api/v1/orders`
  - Trả về 400 nếu thiếu key
  - Trả về 409 nếu key đã tồn tại

### 2. Filter Configuration
- **File**: `backend/src/main/java/com/backend/backend/infrastructure/web/idempotency/IdempotencyConfig.java`
- **Chức năng**: Đăng ký filter với Spring Boot

### 3. Optimistic Locking
- **File**: `backend/src/main/java/com/backend/backend/entity/Order.java`
- **Thay đổi**:
  ```java
  @Version
  @Column(name = "version")
  private Long version;
  ```

## Cách hoạt động

### 1. Idempotency Filter
```java
// Kiểm tra method và path
if ("POST".equalsIgnoreCase(req.getMethod()) && 
    req.getRequestURI().startsWith("/api/v1/orders")) {
    
    String key = req.getHeader("Idempotency-Key");
    if (key == null || key.isBlank()) {
        // Trả về 400
    }
    
    // Kiểm tra key đã tồn tại
    Long existed = seenKeys.putIfAbsent(key, System.currentTimeMillis());
    if (existed != null) {
        // Trả về 409
    }
}
```

### 2. Optimistic Locking
- **JPA Version**: Tự động tăng version khi update
- **Conflict Detection**: JPA throw `OptimisticLockingFailureException`
- **Retry Logic**: Có thể implement retry cho conflicts

## Cách test

### 1. Test Idempotency-Key
```bash
# Test thiếu key (400)
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{"customerId": 1, "items": [{"productId": 1, "quantity": 1}]}'

# Test với key (200)
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: test-key-123" \
  -d '{"customerId": 1, "items": [{"productId": 1, "quantity": 1}]}'

# Test duplicate key (409)
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: test-key-123" \
  -d '{"customerId": 1, "items": [{"productId": 1, "quantity": 1}]}'
```

### 2. Test Optimistic Locking
```bash
# Tạo order
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: test-key-456" \
  -d '{"customerId": 1, "items": [{"productId": 1, "quantity": 1}]}'

# Update order (sẽ tăng version)
curl -X PATCH http://localhost:8080/api/v1/orders/1 \
  -H "Content-Type: application/json" \
  -d '{"items": [{"productId": 1, "quantity": 2}]}'
```

## Response Codes

### 1. Idempotency Filter
- **400 Bad Request**: Thiếu `Idempotency-Key` header
- **409 Conflict**: `Idempotency-Key` đã tồn tại

### 2. Optimistic Locking
- **200 OK**: Update thành công
- **409 Conflict**: Version conflict (cần retry)

## Lưu trữ Idempotency Keys

### 1. In-Memory Storage
- **Hiện tại**: Sử dụng `ConcurrentHashMap`
- **Ưu điểm**: Nhanh, đơn giản
- **Nhược điểm**: Mất data khi restart

### 2. Production Recommendations
- **Redis**: Distributed cache
- **Database**: Persistent storage
- **TTL**: Expire keys sau thời gian nhất định

## Lợi ích

### 1. Idempotency
- **Duplicate Prevention**: Tránh tạo duplicate orders
- **Retry Safety**: Client có thể retry an toàn
- **API Reliability**: API trở nên reliable hơn

### 2. Optimistic Locking
- **Data Consistency**: Đảm bảo data consistency
- **Concurrent Updates**: Xử lý concurrent updates
- **Performance**: Không lock database

## Lưu ý cho dev

### 1. Idempotency Keys
- **Uniqueness**: Keys phải unique
- **TTL**: Cần implement TTL cho production
- **Storage**: Cân nhắc persistent storage

### 2. Optimistic Locking
- **Retry Logic**: Implement retry cho conflicts
- **Error Handling**: Handle `OptimisticLockingFailureException`
- **User Experience**: Thông báo lỗi rõ ràng cho user
