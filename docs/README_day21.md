# Day 21 — Actuator & Build Info

## Mục tiêu
Bật Spring Boot Actuator endpoints và sinh build information cho monitoring và debugging.

## Thay đổi chính

### 1. Application Configuration
- **File**: `backend/src/main/resources/application.yml`
- **Thay đổi**:
  ```yaml
  management:
    endpoints:
      web:
        exposure:
          include: health,info,metrics,prometheus
    endpoint:
      health:
        show-details: when-authorized
      info:
        env:
          enabled: true
    metrics:
      tags:
        application: ${spring.application.name}
  ```

### 2. Build Configuration
- **File**: `backend/build.gradle`
- **Thay đổi**:
  ```gradle
  plugins {
    id 'org.springframework.boot.build-info'
  }
  
  bootBuildInfo {
    properties {
      artifact = project.name
      group = project.group
      version = project.version
      name = 'Spring Boot MySQL Docker Backend'
      time = new Date().format("yyyy-MM-dd'T'HH:mm:ssZ")
    }
  }
  ```

## Endpoints có sẵn

### 1. Health Check
- **URL**: `http://localhost:8080/actuator/health`
- **Chức năng**: Kiểm tra trạng thái ứng dụng
- **Response**: 
  ```json
  {
    "status": "UP",
    "components": {
      "db": {"status": "UP"},
      "diskSpace": {"status": "UP"}
    }
  }
  ```

### 2. Application Info
- **URL**: `http://localhost:8080/actuator/info`
- **Chức năng**: Thông tin build và environment
- **Response**:
  ```json
  {
    "build": {
      "artifact": "backend",
      "name": "Spring Boot MySQL Docker Backend",
      "time": "2025-10-08T14:30:00Z",
      "version": "0.0.1-SNAPSHOT"
    }
  }
  ```

### 3. Metrics
- **URL**: `http://localhost:8080/actuator/metrics`
- **Chức năng**: Danh sách tất cả metrics
- **URL chi tiết**: `http://localhost:8080/actuator/metrics/jvm.memory.used`

### 4. Prometheus Metrics
- **URL**: `http://localhost:8080/actuator/prometheus`
- **Chức năng**: Metrics format cho Prometheus

## Cách test

### 1. Test health endpoint
```bash
curl http://localhost:8080/actuator/health
```

### 2. Test info endpoint
```bash
curl http://localhost:8080/actuator/info
```

### 3. Test metrics
```bash
curl http://localhost:8080/actuator/metrics
```

## Lợi ích
- **Monitoring**: Theo dõi health và performance
- **Debugging**: Thông tin chi tiết về ứng dụng
- **Production ready**: Chuẩn bị cho production monitoring
- **Integration**: Tích hợp với monitoring tools (Prometheus, Grafana)

## Lưu ý cho dev
- Health details chỉ hiển thị khi authorized (có thể config)
- Info endpoint có thể customize thêm thông tin
- Metrics có thể được extend với custom metrics
