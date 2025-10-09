# Day 22 — Micrometer + Prometheus

## Mục tiêu
Tích hợp Micrometer với Prometheus registry và thiết lập optional observability stack.

## Thay đổi chính

### 1. Dependencies
- **File**: `backend/build.gradle`
- **Thêm**:
  ```gradle
  implementation 'io.micrometer:micrometer-registry-prometheus'
  ```

### 2. Configuration
- **File**: `backend/src/main/resources/application.yml`
- **Thay đổi**:
  ```yaml
  management:
    endpoints:
      web:
        exposure:
          include: health,info,metrics,prometheus
    metrics:
      tags:
        application: ${spring.application.name}
  ```

### 3. Docker Compose Observability
- **File**: `docker-compose.observability.yml`
- **Services**: Prometheus + Grafana
- **Ports**: 
  - Prometheus: 9090
  - Grafana: 3000 (admin/admin)

### 4. Prometheus Configuration
- **File**: `prometheus/prometheus.yml`
- **Target**: `backend:8080/actuator/prometheus`

## Cách sử dụng

### 1. Khởi động observability stack
```bash
make observability-up
```

### 2. Kiểm tra trạng thái
```bash
make observability-status
```

### 3. Dừng stack
```bash
make observability-down
```

### 4. Truy cập services
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin)

## Metrics có sẵn

### 1. JVM Metrics
- `jvm.memory.used` - Memory usage
- `jvm.gc.pause` - GC pause time
- `jvm.threads.live` - Active threads

### 2. HTTP Metrics
- `http.server.requests` - HTTP request metrics
- `http.server.requests.duration` - Request duration

### 3. Application Metrics
- `spring.data.repository.invocations` - Repository calls
- `cache.gets` - Cache hit/miss

## Cách test

### 1. Test Prometheus endpoint
```bash
curl http://localhost:8080/actuator/prometheus
```

### 2. Test với observability stack
```bash
# Khởi động stack
make observability-up

# Tạo traffic
curl http://localhost:8080/api/v1/products

# Xem metrics trong Prometheus
open http://localhost:9090
```

## Grafana Dashboard

### 1. Import dashboard
- Vào Grafana → Import
- Sử dụng dashboard ID: 4701 (JVM Micrometer)

### 2. Custom dashboard
- Tạo dashboard mới
- Add panel với Prometheus data source
- Query: `jvm_memory_used_bytes{application="backend"}`

## Lợi ích
- **Real-time monitoring**: Theo dõi metrics real-time
- **Visualization**: Dashboard trực quan với Grafana
- **Alerting**: Có thể setup alerts
- **Scalability**: Dễ dàng scale monitoring

## Lưu ý cho dev
- Stack là optional, không ảnh hưởng đến app chính
- Có thể customize Prometheus config
- Grafana có thể persist data với volume
