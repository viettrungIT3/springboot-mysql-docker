# Day 30 — Demo Collections

## Mục tiêu
Tạo Postman và Insomnia collections cùng hướng dẫn E2E để demo API cho stakeholders.

## Thay đổi chính

### 1. Postman Collection
- **File**: `docs/collections/postman_collection.json`
- **Content**: Complete API collection với examples

### 2. Insomnia Collection
- **File**: `docs/collections/insomnia.yaml`
- **Content**: Insomnia workspace với requests

### 3. E2E Guide
- **File**: `docs/README_day30.md`
- **Content**: Hướng dẫn demo từ A-Z

## Postman Collection

### 1. Collection Structure
```json
{
  "info": {
    "name": "Spring Boot MySQL Docker API",
    "description": "Complete API collection for Spring Boot application"
  },
  "item": [
    {
      "name": "Authentication",
      "item": [
        {
          "name": "Register User",
          "request": {
            "method": "POST",
            "url": "{{baseUrl}}/auth/register",
            "body": {
              "raw": "{\n  \"username\": \"admin\",\n  \"password\": \"securepass123\",\n  \"email\": \"admin@example.com\",\n  \"fullName\": \"System Administrator\"\n}"
            }
          }
        }
      ]
    }
  ]
}
```

### 2. Environment Variables
```json
{
  "name": "Spring Boot API",
  "values": [
    {
      "key": "baseUrl",
      "value": "http://localhost:8080"
    },
    {
      "key": "token",
      "value": ""
    }
  ]
}
```

### 3. Request Examples
- **Authentication**: Register, Login, Get Profile
- **Products**: CRUD operations, Search, Statistics
- **Orders**: Create, Update, Confirm, List
- **Customers**: CRUD operations, Search
- **Suppliers**: CRUD operations, Search
- **Stock Entries**: CRUD operations, List

## Insomnia Collection

### 1. Workspace Structure
```yaml
name: Spring Boot MySQL Docker API
description: Complete API workspace for Spring Boot application
resources:
  - _type: request_group
    name: Authentication
    children:
      - _type: request
        name: Register User
        method: POST
        url: http://localhost:8080/auth/register
        body:
          mimeType: application/json
          text: |
            {
              "username": "admin",
              "password": "securepass123",
              "email": "admin@example.com",
              "fullName": "System Administrator"
            }
```

### 2. Environment Configuration
```yaml
environments:
  - name: Development
    data:
      baseUrl: http://localhost:8080
      token: ""
```

### 3. Request Templates
- **Authentication**: Complete auth flow
- **CRUD Operations**: All entity operations
- **Business Logic**: Order confirmation, CSV import/export
- **Search & Filter**: Advanced search operations

## E2E Demo Guide

### 1. Prerequisites
```bash
# Start application
make dev-start

# Check services
make status

# Verify endpoints
curl http://localhost:8080/actuator/health
```

### 2. Authentication Flow
```bash
# 1. Register user
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "securepass123",
    "email": "admin@example.com",
    "fullName": "System Administrator"
  }'

# 2. Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "securepass123"
  }'

# 3. Use token
export TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/v1/products
```

### 3. Product Management
```bash
# 1. Create product
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Gaming Laptop",
    "description": "High-performance gaming laptop",
    "price": 1299.99,
    "quantityInStock": 10
  }'

# 2. List products
curl -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/api/v1/products/page?page=0&size=5&sort=name"

# 3. Search products
curl -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/api/v1/products/search?q=laptop"
```

### 4. Order Management
```bash
# 1. Create order
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Idempotency-Key: test-key-123" \
  -d '{
    "customerId": 1,
    "items": [
      {"productId": 1, "quantity": 2},
      {"productId": 2, "quantity": 1}
    ]
  }'

# 2. Confirm order
curl -X POST http://localhost:8080/api/v1/orders/1/confirm \
  -H "Authorization: Bearer $TOKEN"

# 3. List orders
curl -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/api/v1/orders/page?page=0&size=5"
```

### 5. CSV Import/Export
```bash
# 1. Export products
curl -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/api/v1/products/export-csv" \
  -o products.csv

# 2. Import products
curl -X POST http://localhost:8080/api/v1/products/import-csv \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@products.csv"
```

## Demo Scenarios

### 1. Basic CRUD Operations
- **Create**: Tạo entities mới
- **Read**: Đọc entities
- **Update**: Cập nhật entities
- **Delete**: Xóa entities

### 2. Business Logic
- **Order Confirmation**: Xác nhận đơn hàng
- **Stock Management**: Quản lý tồn kho
- **Search & Filter**: Tìm kiếm và lọc

### 3. Advanced Features
- **Pagination**: Phân trang
- **Sorting**: Sắp xếp
- **CSV Import/Export**: Import/export CSV
- **Authentication**: Xác thực

## Collection Features

### 1. Postman Features
- **Environment Variables**: Dynamic URLs
- **Pre-request Scripts**: Auto-setup
- **Tests**: Response validation
- **Documentation**: Inline documentation

### 2. Insomnia Features
- **Workspace**: Organized workspace
- **Environment**: Environment management
- **Plugins**: Plugin support
- **Sync**: Cloud sync

### 3. Common Features
- **Examples**: Real examples
- **Documentation**: Complete documentation
- **Error Handling**: Error scenarios
- **Authentication**: Auth flow

## Lợi ích

### 1. Demo Ready
- **Complete**: Complete API coverage
- **Examples**: Real examples
- **Documentation**: Clear documentation

### 2. Development
- **Testing**: API testing
- **Documentation**: API documentation
- **Collaboration**: Team collaboration

### 3. Stakeholder Communication
- **Visual**: Visual API exploration
- **Interactive**: Interactive demos
- **Professional**: Professional presentation

## Lưu ý cho dev

### 1. Collection Maintenance
- **Update**: Update khi API thay đổi
- **Version**: Version control collections
- **Testing**: Test collections regularly

### 2. Demo Preparation
- **Data**: Prepare demo data
- **Environment**: Setup demo environment
- **Scripts**: Prepare demo scripts

### 3. Documentation
- **Examples**: Keep examples updated
- **Screenshots**: Add screenshots
- **Videos**: Record demo videos