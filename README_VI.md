# 🚀 Spring Boot + MySQL + Docker

Ứng dụng Spring Boot + MySQL **dùng Docker ở cấp độ doanh nghiệp**, với kiến trúc hiện đại theo mô hình thiết kế dựa trên **DTO-First Design**, ánh xạ **MapStruct**, xác thực toàn diện, và tích hợp business logic. Được build bằng **Gradle** với Docker multi-stage build (không cần cài JDK trên máy host).

> 📖 **Tiếng Anh**: [README.md](README.md) - Tài liệu tiếng Anh có sẵn

- ✅ **Domain-Driven Design (DDD)** - Kiến trúc sạch với bounded contexts
- ✅ **DTO-First Design** - Tách biệt hoàn toàn giữa hợp đồng API và các thực thể trong domain  
- ✅ **MapStruct Integration** - Ánh xạ kiểu an toàn giữa DTO và Entity  
- ✅ **Comprehensive Validation** - Bean validation với các quy tắc nghiệp vụ tùy chỉnh  
- ✅ **Centralized Configuration** - Quản lý cấu hình trong một file duy nhất  
- ✅ **Interactive Configuration Manager** - Giao diện trực quan, dễ sử dụng để quản lý cấu hình  
- ✅ **Automated Backup System** - Hỗ trợ backup và khôi phục cấu hình tự động
- ✅ **Caching Layer** - Spring Cache với Caffeine cải thiện hiệu suất 2 lần
- ✅ **Service-Specific Commands** - Makefile được tối ưu hóa cho development hiệu quả

## 🚀 Quick Start

### **🚀 Môi Trường Development Đầy Đủ**
```bash
# Clone repository
git clone <repository-url>
cd springboot-mysql-docker

# Thiết lập cấu hình với giao diện tương tác
make config

# Khởi động full stack (mysql + backend + frontend)
make dev-start

# Truy cập ứng dụng
# Backend: http://localhost:${BACKEND_PORT:-8080}
# Swagger UI: http://localhost:${BACKEND_PORT:-8080}/swagger-ui/index.html
# Frontend: http://localhost:${FRONTEND_PORT:-3000}
```

### **⚡ Development Theo Service**
```bash
# Chỉ development backend (API + Database)
make dev-backend

# Chỉ development API (Backend + Database, không có frontend)
make dev-api

# Kiểm tra trạng thái tất cả services
make status

# Restart service cụ thể
make backend-restart    # Restart chỉ backend
make frontend-restart   # Restart chỉ frontend
make db-restart         # Restart chỉ database
```

### 🚀 **Development Tối Ưu Tốc Độ (Khuyến nghị)**
```bash
# ⚡ NHANH NHẤT: Hot reload (không build, ~5 giây)
make dev-hot-reload     # Cho thay đổi cấu hình

# 🔄 NHANH: Build tăng dần (~30 giây)  
make dev-code-change    # Cho thay đổi Java code

# 🚀 TRUNG BÌNH: Quick restart (~45 giây)
make dev-quick-restart  # Cho thay đổi dependencies

# 📊 Hiển thị tips tối ưu hóa
make docker-optimize    # So sánh tốc độ build và khuyến nghị
```

### **🛑 Dừng & Reset**
```bash
# Dừng tất cả services
make dev-stop

# Clean up và reset (tạo lại DB volume)
make clean
make dev-start
```

## 🎯 Centralized Configuration (.env)
**Chỉ cần sửa 1 file duy nhất** - `.env` - để thay đổi tất cả cấu hình:

```env
# ============================================
# 🎯 CENTRALIZED CONFIGURATION - CHỈ SỬA FILE NÀY
# ============================================

# 🌐 PORT CONFIGURATION (chỉ cần sửa ở đây)
BACKEND_PORT=8080
MYSQL_PORT=3306

# 🗄️ DATABASE CONFIGURATION
MYSQL_ROOT_PASSWORD=root
MYSQL_DATABASE=appdb
MYSQL_USER=appuser
MYSQL_PASSWORD=apppass
DB_HOST=mysql

# ⚙️ SPRING BOOT CONFIGURATION
SPRING_PROFILES_ACTIVE=dev
JPA_DDL_AUTO=none
JPA_SHOW_SQL=true

# 🔐 SECURITY CONFIGURATION (dev only)
SPRING_SECURITY_USER_NAME=admin
SPRING_SECURITY_USER_PASSWORD=admin

# 🌱 DATA SEEDING CONFIGURATION
APP_SEED_ENABLED=true
APP_SEED_PRODUCTS=15
APP_SEED_CUSTOMERS=10

# ⚡️ CACHE CONFIGURATION
APP_CACHE_TTL_SECONDS=300
APP_CACHE_MAX_SIZE=1000
```

### 🚀 **Configuration Management Commands:**
```bash
# Thay đổi cấu hình với giao diện thân thiện
make config

# Xem cấu hình hiện tại
make config-show

# Backup cấu hình trước khi thay đổi
make config-backup

# Quản lý backup (xem, khôi phục, xóa cũ)
make config-list-backups
make config-restore BACKUP=.env.backup.20240907_113905
make config-clean-backups
```

### 📈 Observability & Client (Ngày 22, 26)
```bash
# Stack tuỳ chọn Prometheus + Grafana
make observability-up       # Khởi động Prometheus & Grafana
make observability-status   # Xem trạng thái
make observability-down     # Dừng stack

# Sinh và test nhanh client TypeScript (chạy trong Docker)
make client-gen             # Sinh vào clients/typescript-axios
make client-test            # Gọi thử API trong container Node
```

## 🏗️ Domain-Driven Design (DDD) Architecture

### **Bounded Contexts**
Ứng dụng được tổ chức thành các bounded contexts rõ ràng theo nguyên tắc DDD:

```
com.backend.backend/
├── shared/                        # Shared Kernel
│   ├── domain/
│   │   ├── valueobject/           # Value Objects (Money, Slug, Email)
│   │   ├── entity/                 # Base Entity (BaseEntity)
│   │   └── exception/              # Domain Exceptions
│   └── infrastructure/
│       ├── util/
│       └── constant/
├── identity/                      # Identity & Access Management Context
│   ├── domain/                    # Domain layer
│   ├── application/               # Application layer
│   ├── infrastructure/            # Infrastructure layer
│   └── presentation/              # Presentation layer
├── customer/                      # Customer Management Context
├── catalog/                       # Product Catalog Context
├── order/                         # Order Management Context
└── infrastructure/                # Cross-cutting Infrastructure
    ├── config/                    # Configuration
    ├── web/                       # Web infrastructure
    └── exception/                 # Global exception handling
```

### **DDD Development Commands**
```bash
# Validate DDD structure
make ddd-validate

# Check migration progress
make ddd-migration-status

# DDD-specific development
make ddd-compile        # Fast Java compilation
make ddd-quick-test     # Quick test execution
make ddd-check          # Code quality check
make ddd-clean-build    # Clean build for refactoring
make ddd-restart-backend # Restart backend for DDD changes
make ddd-logs           # Watch backend logs during DDD development
make ddd-status         # Check DDD development status
```

### **Migration Phases**
- ✅ **Phase 1**: Foundation Setup (Shared Kernel, Infrastructure)
- ✅ **Phase 2**: Identity Context (Authentication & Authorization)
- ✅ **Phase 3**: Business Context (Domain Services & Business Logic)
- 🔄 **Phase 4**: Advanced Features (Business Logic Endpoints, Enhanced Validation)
- 🔄 **Phase 5**: Production Readiness (Monitoring, Performance Optimization)

## Project Structure
```
springboot-mysql-docker/
├─ .env                              # 🎯 CENTRALIZED CONFIGURATION (chỉ sửa file này)
├─ backups/
│  └─ env/                           # 📁 Configuration backups (tự động quản lý)
│     ├─ .env.backup.20240907_113905
│     ├─ .env.backup.20240907_113918
│     └─ ...
├─ config-manager.sh                 # 🛠️ Interactive configuration manager
├─ CONFIG_GUIDE.md                   # 📖 Centralized configuration guide
├─ backend/
│  ├─ Dockerfile                     # multi-stage: builds JAR inside Docker
│  ├─ build.gradle                   # MapStruct + Lombok + Spring Boot
│  ├─ gradle/, gradlew, settings.gradle
│  └─ src/main/java/com/backend/backend/
│     ├─ BackendApplication.java      # Main application class
│     ├─ shared/                      # 🏗️ Shared Kernel (DDD)
│     │  ├─ domain/
│     │  │  ├─ valueobject/           # Value Objects (Money, Slug, Email)
│     │  │  ├─ entity/                 # Base Entity (BaseEntity)
│     │  │  └─ exception/              # Domain Exceptions
│     │  └─ infrastructure/
│     ├─ infrastructure/              # 🏗️ Cross-cutting Infrastructure
│     │  ├─ config/                   # Configuration (Security, Cache, CORS)
│     │  ├─ web/                      # Web infrastructure (Filters, Logging)
│     │  └─ exception/                # Global exception handling
│     ├─ identity/                    # 🏗️ Identity & Access Management Context
│     ├─ customer/                    # 🏗️ Customer Management Context
│     ├─ catalog/                     # 🏗️ Product Catalog Context
│     ├─ order/                       # 🏗️ Order Management Context
│     ├─ config/                      # Configuration classes
│     │  ├─ CacheConfig.java          # Caffeine cache configuration
│     │  ├─ CacheNames.java           # Cache name constants
│     │  └─ SecurityConfig.java       # Spring Security configuration
│     ├─ dto/                         # Data Transfer Objects (organized by entity)
│     │  ├─ common/                   # Shared DTOs (PageResponse, etc.)
│     │  ├─ customer/                 # Customer DTOs
│     │  ├─ order/                    # Order DTOs
│     │  ├─ orderitem/                # OrderItem DTOs
│     │  ├─ product/                  # Product DTOs
│     │  ├─ supplier/                 # Supplier DTOs
│     │  └─ user/                     # User DTOs
│     ├─ entity/                      # Domain entities
│     │  ├─ Order.java, OrderItem.java, StockEntry.java
│     │  ├─ Product.java, Customer.java, Supplier.java
│     │  └─ User.java (with Role enum)
│     ├─ exception/                  # Exception handling
│     │  ├─ GlobalExceptionHandler.java
│     │  └─ ResourceNotFoundException.java
│     ├─ mapper/                     # MapStruct mappers
│     │  ├─ AdministratorMapper.java
│     │  ├─ CustomerMapper.java
│     │  ├─ OrderMapper.java
│     │  ├─ OrderItemMapper.java
│     │  ├─ ProductMapper.java
│     │  ├─ StockEntryMapper.java
│     │  └─ SupplierMapper.java
│     ├─ repository/                 # JPA repositories
│     │  ├─ customer/, supplier/, administrator/
│     │  ├─ order/, orderitem/
│     │  ├─ product/, stockentry/
│     │  └─ AdministratorRepository.java
│     ├─ service/                    # Business logic layer
│     │  ├─ ProductService.java      # Inventory management with business logic
│     │  ├─ OrderService.java        # Complex order processing
│     │  ├─ CustomerService.java     # Customer management with business logic
│     │  ├─ SupplierService.java     # Supplier management with business logic
│     │  ├─ StockEntryService.java   # Stock management with business logic
│     │  ├─ UserService.java         # User management with authentication
│     │  ├─ JwtTokenService.java      # JWT token management
│     │  └─ PasswordService.java     # Password hashing and validation
│     ├─ util/                       # Utility classes
│     │  ├─ PageMapper.java          # Pagination utility
│     │  └─ SlugUtil.java            # Slug generation utility
│     ├─ controller/                 # RESTful API layer
│     │  ├─ ProductController.java   # /api/v1/products (with business logic endpoints)
│     │  ├─ OrderController.java     # /api/v1/orders
│     │  ├─ CustomerController.java  # /api/v1/customers
│     │  ├─ SupplierController.java  # /api/v1/suppliers
│     │  ├─ UserController.java      # /api/v1/users
│     │  ├─ AuthController.java      # /auth (authentication endpoints)
│     │  └─ ...Controller.java       # CRUD + pagination + business logic endpoints
│     └─ bootstrap/                  # Application startup components
│        └─ DevTestDataSeeder.java   # Profile-based data seeding
│  └─ src/main/resources/
│     ├─ application.yml             # Main configuration
│     ├─ application-dev.yml         # Development profile
│     ├─ application-test.yml        # Test profile
│     ├─ application-prod.yml        # Production profile
│     ├─ db/
│     │  └─ migration/               # Flyway migrations
│     │     ├─ V1__init.sql          # Initial schema
│     │     ├─ V2__seed_base.sql     # Base data seeding
│     │     ├─ V3__add_slug_products_customers.sql
│     │     ├─ V4__add_audit_and_soft_delete.sql
│     │     ├─ V5__add_stock_entries.sql      # Stock management migration
│     │     ├─ V6__add_users_table.sql       # User authentication migration
│     │     └─ V7__add_user_role.sql         # User role enum migration
│     └─ logback-spring.xml          # Logging configuration
├─ docker-compose.yml                # Uses centralized .env variables
├─ makefile                          # Enhanced with config management commands
└─ docs/                             # Documentation
   ├─ README_day1.md
   ├─ README_day2.md
   └─ ...
```

## Services (Docker Compose)
- **mysql**
  - Image: `mysql:8.4`
  - Ports: `${MYSQL_PORT:-3306}:3306` (từ .env)
  - Environment: `MYSQL_ROOT_PASSWORD`, `MYSQL_DATABASE`, `MYSQL_USER`, `MYSQL_PASSWORD` (từ .env)
  - Volumes:
    - Named volume cho data (`db_data`)
    - ✅ **Flyway migrations** tự động xử lý khởi tạo schema

- **backend**
  - Build: `backend/Dockerfile` (multi-stage: JDK build -> JRE runtime)
  - Ports: `${BACKEND_PORT:-8080}:${BACKEND_PORT:-8080}` (từ .env)
  - Environment: `SPRING_DATASOURCE_*`, `JPA_*`, `APP_SEED_*`, `APP_CACHE_*` (tự động từ .env)
  - ✅ **Data Seeding** tự động chạy cho dev/test profiles với số lượng có thể cấu hình
  - ✅ **Dynamic Configuration** - Swagger server URL tự động cập nhật theo BACKEND_PORT

## Swagger
- Swagger UI: `http://localhost:${BACKEND_PORT:-8080}/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:${BACKEND_PORT:-8080}/v3/api-docs`

## API Endpoints

### 🛍️ **Products**: `/api/v1/products` - Quản lý kho hàng với theo dõi tồn kho + business logic
### 👥 **Customers**: `/api/v1/customers` - Quản lý khách hàng với hỗ trợ slug + business logic
### 🏢 **Suppliers**: `/api/v1/suppliers` - Quản lý nhà cung cấp với thông tin liên hệ + business logic
### 📋 **Orders**: `/api/v1/orders` - Quản lý đơn hàng với theo dõi sản phẩm + business logic
### 👨‍💼 **Users**: `/api/v1/users` - Quản lý người dùng với mật khẩu bảo mật + authentication
### 📦 **Stock Entries**: `/api/v1/stock-entries` - Giao dịch kho hàng + business logic
### 🔐 **Authentication**: `/auth/*` - JWT-based authentication endpoints

## Business Logic Features

### **Domain Services với Business Rules**:
- ✅ **ProductService**: Quản lý kho hàng, cập nhật giá, cảnh báo tồn kho thấp, thống kê sản phẩm
- ✅ **CustomerService**: Validation khách hàng, tìm kiếm, quản lý thông tin liên hệ
- ✅ **SupplierService**: Validation nhà cung cấp, theo dõi nhà cung cấp hoạt động, quản lý thông tin liên hệ
- ✅ **OrderService**: Tính toán đơn hàng, lịch sử đơn hàng khách hàng, thống kê đơn hàng
- ✅ **StockEntryService**: Quản lý giao dịch kho hàng, theo dõi tồn kho sản phẩm
- ✅ **UserService**: Authentication, quản lý mật khẩu, kiểm soát truy cập dựa trên vai trò

### **Business Logic Endpoints**:
- 📊 **Statistics**: `/api/v1/products/stats`, `/api/v1/customers/stats`, `/api/v1/orders/stats`
- 🔍 **Search**: `/api/v1/products/search`, `/api/v1/customers/search`, `/api/v1/suppliers/search`
- 📈 **Analytics**: `/api/v1/products/low-stock`, `/api/v1/orders/by-date-range`
- 🎯 **Business Operations**: Dự trữ kho hàng, tính toán đơn hàng, quản lý tồn kho

## API Examples

### **Quản lý Sản phẩm**:
```bash
# Tạo sản phẩm
curl -X POST http://localhost:${BACKEND_PORT:-8080}/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Gaming Laptop",
    "description": "High-performance gaming laptop",
    "price": 1299.99,
    "stockQuantity": 50
  }'

# Lấy danh sách sản phẩm với phân trang
curl "http://localhost:${BACKEND_PORT:-8080}/api/v1/products/page?page=0&size=5&sort=name"

# Cập nhật sản phẩm
curl -X PATCH http://localhost:${BACKEND_PORT:-8080}/api/v1/products/1 \
  -H "Content-Type: application/json" \
  -d '{"price": 1199.99}'

# Lấy sản phẩm theo slug
curl "http://localhost:${BACKEND_PORT:-8080}/api/v1/products/slug/gaming-laptop"

# Xóa sản phẩm
curl -X DELETE http://localhost:${BACKEND_PORT:-8080}/api/v1/products/1
```

### **Quản lý Đơn hàng**:
```bash
# Tạo đơn hàng
curl -X POST http://localhost:${BACKEND_PORT:-8080}/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "items": [
      {"productId": 1, "quantity": 2},
      {"productId": 2, "quantity": 1}
    ]
  }'

# Thêm sản phẩm vào đơn hàng hiện có
curl -X POST "http://localhost:${BACKEND_PORT:-8080}/api/v1/orders/1/items?productId=3&quantity=1"
```

### **Quản lý Kho hàng**:
```bash
# Tạo nhập kho
curl -X POST http://localhost:${BACKEND_PORT:-8080}/api/v1/stock-entries \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "supplierId": 1,
    "quantity": 100,
    "entryType": "IN"
  }'
```

#### **Authentication & User Management**:
```bash
# Đăng ký user mới
curl -X POST http://localhost:${BACKEND_PORT:-8080}/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "securepass123",
    "email": "admin@example.com",
    "fullName": "System Administrator"
  }'

# Đăng nhập và lấy JWT token
curl -X POST http://localhost:${BACKEND_PORT:-8080}/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "securepass123"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "username": "admin",
    "email": "admin@example.com",
    "fullName": "System Administrator",
    "role": "USER"
  }
}
```

## Useful Commands

### 🔧 **Thao tác Cơ bản:**
```bash
# Khởi động môi trường development
make dev-start

# Dừng tất cả services
make down

# Xem logs
make logs

# Xem logs của backend
make logs-tail

# Khởi động lại services
make restart

# Xóa tất cả (bao gồm database)
make clean

# Rebuild và start
make dev-rebuild
```

### ⚡ **Thao tác Tối Ưu Tốc Độ:**
```bash
# 🚀 NHANH NHẤT: Hot reload (~5 giây)
make dev-hot-reload     # Chỉ cho thay đổi cấu hình

# 🔄 NHANH: Build tăng dần (~30 giây)
make dev-code-change    # Cho thay đổi Java code

# 🚀 TRUNG BÌNH: Quick restart (~45 giây)  
make dev-quick-restart  # Cho thay đổi dependencies

# 📊 So sánh tốc độ build và tips
make docker-optimize    # Hiển thị khuyến nghị tối ưu hóa

# ⚠️ CHẬM: Full rebuild (~3-5 phút) - Chỉ dùng khi cần thiết
make backend-rebuild    # Clean build với no-cache
```

### ⚙️ **Quản lý Cấu hình:**
```bash
# Giao diện quản lý cấu hình tương tác
make config

# Xem cấu hình hiện tại
make config-show

# Backup cấu hình
make config-backup

# Liệt kê các backup
make config-list-backups

# Khôi phục từ backup
make config-restore BACKUP=.env.backup.20240907_113905

# Xóa các backup cũ
make config-clean-backups
```

### 🔧 **Tiện ích Development:**
```bash
# Khởi động môi trường development
make dev-start

# Test Swagger UI
make test-swagger

# Mở Swagger UI trong browser
make swagger

# Hiển thị tất cả lệnh có sẵn
make help
```

### 🔐 Bảo mật & Chuỗi cung ứng (Ngày 28–29)
```bash
# Build image backend slim (alpine JRE) và sinh SBOM (Syft)
make backend-slim-build
make backend-sbom           # xuất backend-sbom.spdx.json

# Quét bảo mật
make security-scan-trivy    # Trivy HIGH/CRITICAL cho image backend
make security-scan-dep      # OWASP Dependency Check (báo cáo HTML trong ./odc-report)
```

## Prerequisites
- **Docker Desktop** (hoặc Docker Engine + Compose plugin)
- **Make** (thường đã có sẵn trên macOS/Linux)
- **Git**

## 🚀 Docker Build Optimization

### **So Sánh Tốc Độ Build:**
| Command | Thời gian | Trường hợp sử dụng | Mô tả |
|---------|-----------|-------------------|-------|
| `make dev-hot-reload` | ~5 giây | Thay đổi config | Không build, chỉ restart |
| `make dev-code-change` | ~30 giây | Thay đổi Java code | Build tăng dần |
| `make dev-quick-restart` | ~45 giây | Thay đổi dependencies | Build + restart |
| `make backend-rebuild` | ~3-5 phút | Vấn đề cache | Clean build (no-cache) |

### **Tips Tối Ưu Hóa:**
```bash
# Hiển thị hướng dẫn tối ưu hóa chi tiết
make docker-optimize

# Sử dụng command phù hợp cho từng loại thay đổi:
# - Cấu hình (.env, application.yml) → dev-hot-reload
# - Thay đổi Java code → dev-code-change  
# - Dependencies (build.gradle) → dev-quick-restart
# - Vấn đề cache → backend-rebuild (chỉ khi cần thiết)
```

### **Tại Sao Các Commands Này Nhanh Hơn:**
- **Docker Layer Caching**: Build tăng dần tái sử dụng cached layers
- **Smart Build Strategy**: Chỉ rebuild những gì thay đổi
- **No-Cache Avoidance**: `--no-cache` buộc rebuild hoàn toàn
- **Targeted Operations**: Commands cụ thể cho từng loại thay đổi

## Troubleshooting

### 🔧 **Configuration Issues:**
- **Port already in use**
  - Sửa `BACKEND_PORT` hoặc `MYSQL_PORT` trong `.env` và chạy `make restart`
  - Hoặc dùng `make config` để thay đổi với giao diện thân thiện

- **Configuration not applied**
  - Chạy `make restart` sau khi thay đổi `.env`
  - Kiểm tra cấu hình hiện tại: `make config-show`

- **Backup configuration before changes**
  - Luôn chạy `make config-backup` trước khi thay đổi
  - Khôi phục nếu cần: `make config-restore BACKUP=filename`

### 🐛 **Common Issues:**
- **MySQL password or db name not applied**
  - MySQL sử dụng volume được lưu trữ. Chạy `make clean` để xóa volume và khởi tạo lại.

- **Slow or failing dependency downloads during Docker build**
  - Dockerfile có retry Gradle steps và cache Gradle; chạy lại `make dev-rebuild`.

- **Swagger not accessible**
  - Đảm bảo backend healthy: `make logs-tail`
  - Mở `http://localhost:${BACKEND_PORT}/swagger-ui/index.html`
  - Kiểm tra với `make test-swagger`



-----

## 📚 Mục lục Tài liệu

### **Hướng dẫn Chi tiết Theo Ngày:**
- **[Ngày 1-19](docs/README_day1.md)** — Nền tảng & Tính năng Cốt lõi
- **[Ngày 20-30](docs/README_days_20_30.md)** — Tính năng Nâng cao & Sẵn sàng Production
- **[Hướng dẫn Từng Ngày](docs/)** — Chi tiết implementation đầy đủ cho mỗi ngày

## 🗓️ Hành trình Phát triển

### ✅ Day 1 — Makefile & Dev UX
* **Mục tiêu:** Makefile với các targets `up`, `down`, `logs`, `dev-start`, `swagger`, `test-swagger`, `test-api`.  
* **Tiêu chí:** Makefile hoạt động trên macOS/Linux; README được cập nhật.  
* **🎯 HOÀN THÀNH:** Makefile toàn diện với 30+ commands bao gồm quản lý cấu hình
* **📖 [README Day 1](docs/README_day1.md)**
* **[Git changelog](https://github.com/viettrungIT3/springboot-mysql-docker/pull/1/files)**

### ✅ Day 2 — Profiles & Isolated Configuration  
* **Mục tiêu:** `application.yml` với các profiles `dev`, `test`, `prod`; sử dụng environment overrides.  
* **Tiêu chí:** Chạy dev qua Docker Compose; DB configuration từ .env.  
* **🎯 HOÀN THÀNH:** Cấu hình dựa trên profiles với environment variable overrides
* **📖 [README Day 2](docs/README_day2.md)**
* **[Git changelog](https://github.com/viettrungIT3/springboot-mysql-docker/pull/2/files)**

### ✅ Day 3 — DTOs & MapStruct  
* **Mục tiêu:** DTOs cho tất cả entities; MapStruct mappers; validation annotations.  
* **Tiêu chí:** API trả về DTOs; validation hoạt động; mappers được test.  
* **🎯 HOÀN THÀNH:** Kiến trúc DTO hoàn chỉnh với tích hợp MapStruct và validation toàn diện
* **📖 [README Day 3](docs/README_day3.md)**
* **[Git changelog](https://github.com/viettrungIT3/springboot-mysql-docker/pull/3/files)**

### ✅ Day 4 — Controllers & Business Logic  
* **Mục tiêu:** REST controllers với CRUD operations; business logic trong services.  
* **Tiêu chí:** Tất cả endpoints hoạt động; business rules được thực thi; error handling đúng cách.  
* **🎯 HOÀN THÀNH:** REST API hoàn chỉnh với tích hợp business logic và error handling đúng cách
* **📖 [README Day 4](docs/README_day4.md)**
* **[Git changelog](https://github.com/viettrungIT3/springboot-mysql-docker/pull/4/files)**

### ✅ Day 5 — Pagination & Search  
* **Mục tiêu:** Paginated responses; search functionality; standardized PageResponse<T>.  
* **Tiêu chí:** Swagger hiển thị parameters đúng; trả về `Page` metadata.  
* **🎯 HOÀN THÀNH:** Pagination chuẩn hóa với PageResponse<T>, PageMapper utility, search functionality và Swagger documentation toàn diện
* **📖 [README Day 5](docs/README_day5.md)**
* **[Git changelog](https://github.com/viettrungIT3/springboot-mysql-docker/pull/5/files)**

### ✅ Day 6 — Swagger Polish + OpenAPI
* **Mục tiêu:** Title, description, contact, server URLs; tag endpoints.
* **Tiêu chí:** `/v3/api-docs` hợp lệ; `swagger-ui/index.html` đẹp, có examples.
* **🎯 HOÀN THÀNH:** Swagger documentation chuyên nghiệp với dynamic server URLs và API examples toàn diện
* **📖 [README Day 6](docs/README_day6.md)**
* **[Git changelog](https://github.com/viettrungIT3/springboot-mysql-docker/pull/6/files)**

### ✅ Day 7 — Security & Authentication
* **Mục tiêu:** Basic security với username/password; bảo vệ admin endpoints.
* **Tiêu chí:** Admin endpoints yêu cầu authentication; Swagger có auth button.
* **🎯 HOÀN THÀNH:** Triển khai basic security với admin authentication và tích hợp Swagger
* **📖 [README Day 7](docs/README_day7.md)**
* **[Git changelog](https://github.com/viettrungIT3/springboot-mysql-docker/pull/7/files)**

### ✅ Day 8 — Testing & Testcontainers
* **Mục tiêu:** Integration tests với Testcontainers; test tất cả CRUD operations.
* **Tiêu chí:** Tests chạy trong CI; cover happy path và error cases.
* **🎯 HOÀN THÀNH:** Integration testing toàn diện với Testcontainers bao gồm tất cả CRUD operations và error scenarios
* **📖 [README Day 8](docs/README_day8.md)**
* **[Git changelog](https://github.com/viettrungIT3/springboot-mysql-docker/pull/8/files)**

### ✅ Day 9 — Flyway Migrations 🛫
* **Mục tiêu:** Di chuyển schema và seed từ init_database.sql sang Flyway V1__init.sql. App auto-migrate khi start; xóa mount init SQL trong Compose.
* **Tiêu chí:** Flyway là single source of truth; Testcontainers hoạt động với migrations.
* **🎯 HOÀN THÀNH:** Chiến lược database migration cấp doanh nghiệp với Flyway, single source of truth cho schema, automated migrations
* **📖 [README Day 9](docs/README_day9.md)**
* **[Git changelog](https://github.com/viettrungIT3/springboot-mysql-docker/pull/9/files)**

### ✅ Day 10 — Data Seeding with Docker & Makefile 🌱
* **Mục tiêu:** Lightweight data seeding cho dev/test (CommandLineRunner) với configurable và idempotent capabilities.
* **Tiêu chí:** Seeding chạy tự động trong dev/test; có thể cấu hình qua environment variables; idempotent.
* **🎯 HOÀN THÀNH:** Profile-based data seeding với DataFaker, idempotent seeding, configurable quantities, tích hợp Docker & Makefile
* **📖 [README Day 10](docs/README_day10.md)**
* **[Git changelog](https://github.com/viettrungIT3/springboot-mysql-docker/pull/10/files)**

### ✅ Day 11 — Logging & Observability 📊
* **Mục tiêu:** Logback JSON (profile-dependent), correlation ID filter.
* **Tiêu chí:** Logs có traceId; log level có thể cấu hình qua environment variable: LOG_LEVEL=INFO.
* **🎯 HOÀN THÀNH:** Structured logging với correlation IDs, profile-based configuration, và environment-controlled log levels
* **📖 [README Day 11](docs/README_day11.md)**
* **[Git changelog](https://github.com/viettrungIT3/springboot-mysql-docker/pull/11/files)**

### ✅ Day 12 — Slug System & SEO-Friendly URLs 🔗
* **Mục tiêu:** Thêm slug field vào Product và Customer; auto-generate từ name; unique constraint; API endpoints hỗ trợ cả ID và slug access.
* **Tiêu chí:** Slug auto-generated từ name, unique constraint, API endpoints hỗ trợ cả ID và slug access.
* **🎯 HOÀN THÀNH:** Global slug system với SlugUtil, unique constraints, dual access patterns (ID/slug), comprehensive API coverage
* **📖 [README Day 12](docs/README_day12.md)**
* **[Git changelog](https://github.com/viettrungIT3/springboot-mysql-docker/pull/12/files)**

### ✅ Day 13 — Soft Delete & Auditing 🗑️
* **Mục tiêu:** Thêm audit fields (created_at, updated_at) và soft delete (deleted_at) vào tất cả entities; cập nhật repositories và services.
* **Tiêu chí:** Tất cả entities có audit fields; soft delete hoạt động; repositories filter deleted records.
* **🎯 HOÀN THÀNH:** Complete audit trail với automatic timestamp management và soft delete functionality trên tất cả entities
* **📖 [README Day 13](docs/README_day13.md)**
* **[Git changelog](https://github.com/viettrungIT3/springboot-mysql-docker/pull/13/files)**

### ✅ Day 15 — Password Hashing & Admin UI Base 🔐
* **Mục tiêu:** Implement password hashing cho Administrator và tạo Next.js Admin UI với authentication.
* **Tiêu chí:** BCrypt password hashing, Next.js frontend với Ant Design, Docker integration, full-stack authentication.
* **🎯 HOÀN THÀNH:** Complete authentication system với modern frontend, password security, và admin dashboard
* **📖 [README Day 15](docs/README_day15.md)**
* **[Git changelog](https://github.com/viettrungIT3/springboot-mysql-docker/pull/15/files)**

### ✅ Day 16 — CORS & Rate Limiting Configuration 🌐🚦
* **Mục tiêu:** CORS per profile; Bucket4j rate limit public endpoints.
* **Tiêu chí:** 429 khi vượt limit; cấu hình qua environment variables.
* **🎯 HOÀN THÀNH:** Complete CORS configuration với environment variables và Bucket4j rate limiting với bucket isolation
* **📖 [README Day 16](docs/README_day16.md)**
* **Tính năng:**
  - Environment-based CORS configuration
  - Bucket4j rate limiting với isolated buckets per endpoint type
  - Public (100/min), API (200/min), Auth (10/min) rate limits
  - Comprehensive testing và monitoring

### ✅ Day 17 — Caching Implementation ⚡️
* **Mục tiêu:** Spring Cache (Caffeine) cho GET /products, /suppliers; cache TTL qua properties; @CacheEvict khi write.
* **Tiêu chí:** Cache TTL qua properties; @CacheEvict khi write; cải thiện hiệu suất 2 lần.
* **🎯 HOÀN THÀNH:** Hệ thống caching hoàn chỉnh với Spring Cache + Caffeine cải thiện hiệu suất đáng kể
* **📖 [README Day 17](docs/README_day17.md)**
* **Tính năng:**
  - Spring Cache với Caffeine in-memory caching
  - Caching cho ProductService và SupplierService
  - Cache eviction khi create/update/delete operations
  - Cấu hình TTL và cache size qua environment variables
  - Cải thiện hiệu suất 1.3-2 lần trên các endpoint được cache
  - Testing và monitoring toàn diện

### ✅ Day 18 — DDD Architecture & Service-Specific Commands 🏗️⚡️
* **Mục tiêu:** Implement Domain-Driven Design architecture và tối ưu hóa makefile với service-specific commands.
* **Tiêu chí:** Clean architecture với bounded contexts, optimized development workflows, efficient service management.
* **🎯 HOÀN THÀNH:** Complete DDD foundation setup với optimized makefile cho development hiệu quả
* **📖 [README Day 18](docs/README_day18.md)**
* **Tính năng:**
  - Domain-Driven Design architecture với bounded contexts
  - Shared kernel với value objects (Money, Slug, Email)
  - Infrastructure layer separation
  - Service-specific makefile commands (75 optimized commands)
  - DDD development workflow commands
  - Legacy command aliases cho backward compatibility

### ✅ Day 19 — Error catalog & mã lỗi 🚨
* **Mục tiêu:** Chuẩn hóa mã lỗi (APP-xxxx), mapping exception → mã.
* **Tiêu chí:** Tài liệu trong README/Swagger.
* **🎯 HOÀN THÀNH:** Standardized error codes (APP-XXXX) with comprehensive error handling, documentation, and testing
* **📖 [README Day 19](docs/README_day19.md)**
* **Tính năng:**
  - **Error Code System**: 100+ standardized error codes (APP-0001-0999) with categories
  - **Exception Hierarchy**: AppException base class with domain-specific exceptions
  - **Global Handler**: Enhanced GlobalExceptionHandler with automatic HTTP status mapping
  - **Error Response**: Standardized ErrorResponse DTO with error codes, titles, and descriptions
  - **Documentation**: Complete error codes documentation with examples and testing
  - **Testing**: Comprehensive unit and integration tests for error scenarios
  - **Business Logic**: Domain-specific exceptions for Product, Customer, Order, User
  - **Validation**: Enhanced validation error handling with field-specific error codes

### ✅ Day 20 — API Versioning & Deprecation
* **Mục tiêu:** Thêm filter forward `/api/**` → `/api/v1/**` kèm deprecation headers.
* **Tiêu chí:** Legacy paths hoạt động với deprecation warnings; migration path rõ ràng.
* **🎯 HOÀN THÀNH:** API versioning với deprecation headers và backward compatibility hoàn hảo
* **📖 [README Day 20](docs/README_day20.md)**

### ✅ Day 21 — Actuator & Build Info
* **Mục tiêu:** Bật actuator endpoints (health, info, metrics) và sinh build-info.
* **Tiêu chí:** Monitoring endpoints accessible; build info available.
* **🎯 HOÀN THÀNH:** Complete monitoring setup với actuator endpoints và build information
* **📖 [README Day 21](docs/README_day21.md)**

### ✅ Day 22 — Micrometer + Prometheus
* **Mục tiêu:** Expose `/actuator/prometheus`, optional observability stack với Prometheus & Grafana.
* **Tiêu chí:** Metrics exposed; optional monitoring stack available.
* **🎯 HOÀN THÀNH:** Full observability stack với Prometheus metrics và Grafana dashboards
* **📖 [README Day 22](docs/README_day22.md)**

### ✅ Day 23 — CSV Import/Export (Products)
* **Mục tiêu:** Thêm CSV import/export functionality cho bulk product operations.
* **Tiêu chí:** Import/export endpoints hoạt động; handle large datasets efficiently.
* **🎯 HOÀN THÀNH:** Complete CSV import/export với Apache Commons CSV và makefile integration
* **📖 [README Day 23](docs/README_day23.md)**

### ✅ Day 24 — Idempotency & Optimistic Locking
* **Mục tiêu:** Bắt buộc `Idempotency-Key` header cho `POST /api/v1/orders`; thêm `@Version` vào `Order`.
* **Tiêu chí:** Prevent duplicate orders; handle concurrent updates.
* **🎯 HOÀN THÀNH:** Idempotency protection và optimistic locking cho data consistency
* **📖 [README Day 24](docs/README_day24.md)**

### ✅ Day 25 — Business Rules Xác nhận Đơn
* **Mục tiêu:** `POST /api/v1/orders/{id}/confirm`: tính lại `totalAmount`, ghi `StockEntry` outflow.
* **Tiêu chí:** Business rules enforced; stock movements recorded.
* **🎯 HOÀN THÀNH:** Complete order confirmation với business rules và inventory management
* **📖 [README Day 25](docs/README_day25.md)**

### ✅ Day 26 — OpenAPI Client Generation
* **Mục tiêu:** Docker hoá `client-gen` và `client-test`; commit sample client `clients/typescript-axios`.
* **Tiêu chí:** Generated client hoạt động; Docker-based workflow.
* **🎯 HOÀN THÀNH:** TypeScript API client generation với Docker-based workflow và testing
* **📖 [README Day 26](docs/README_day26.md)**

### ✅ Day 27 — CI Workflow
* **Mục tiêu:** GitHub Actions: Gradle tests, Docker build, SBOM upload.
* **Tiêu chí:** Automated CI pipeline; artifacts uploaded.
* **🎯 HOÀN THÀNH:** Complete CI/CD pipeline với automated testing, building, và artifact management
* **📖 [README Day 27](docs/README_day27.md)**

### ✅ Day 28 — Slim Image + SBOM
* **Mục tiêu:** Alpine JRE base image và Syft SBOM generation.
* **Tiêu chí:** Smaller image size; SBOM generated.
* **🎯 HOÀN THÀNH:** Optimized Docker image với Alpine JRE và comprehensive SBOM generation
* **📖 [README Day 28](docs/README_day28.md)**

### ✅ Day 29 — Security Scans
* **Mục tiêu:** Trivy image scan (HIGH/CRITICAL) và OWASP Dependency Check với artifact upload.
* **Tiêu chí:** Security vulnerabilities detected; reports generated.
* **🎯 HOÀN THÀNH:** Comprehensive security scanning với Trivy và OWASP Dependency Check
* **📖 [README Day 29](docs/README_day29.md)**

### ✅ Day 30 — Demo Collections
* **Mục tiêu:** Postman/Insomnia collections và E2E demo guide trong `docs/`.
* **Tiêu chí:** Complete API collections; demo guide available.
* **🎯 HOÀN THÀNH:** Professional API collections và comprehensive demo documentation
* **📖 [README Day 30](docs/README_day30.md)**

## ⚡ Service-Specific Commands

### **Development Workflows**
```bash
# Môi trường development đầy đủ
make dev-start      # Start mysql + backend + frontend
make dev-backend    # Start backend + database only
make dev-api        # Start API development (no frontend)
make dev-stop       # Stop all services
make dev-restart    # Restart all services
```

### **Development Tối Ưu Tốc Độ**
```bash
# ⚡ NHANH NHẤT: Hot reload (~5 giây)
make dev-hot-reload     # Chỉ cho thay đổi cấu hình

# 🔄 NHANH: Build tăng dần (~30 giây)
make dev-code-change    # Cho thay đổi Java code

# 🚀 TRUNG BÌNH: Quick restart (~45 giây)
make dev-quick-restart  # Cho thay đổi dependencies

# 📊 Tips tối ưu hóa và so sánh tốc độ
make docker-optimize    # Hiển thị khuyến nghị tốc độ build
```

### **Service Management**
```bash
# Backend commands (tối ưu tốc độ)
make backend-build      # Build backend only (với cache)
make backend-quick-build # Quick build backend (tăng dần, nhanh)
make backend-rebuild    # Rebuild backend (no-cache) - CHẬM
make backend-force-rebuild # Force rebuild (clean + no-cache) - RẤT CHẬM
make backend-start      # Start backend only
make backend-stop       # Stop backend only
make backend-restart    # Restart backend only
make backend-quick-restart # Quick restart (build + restart, nhanh)
make backend-dev-restart # Development restart (tối ưu cho dev)
make backend-status     # Check backend status
make backend-logs       # Watch backend logs

# Frontend commands
make frontend-build     # Build frontend only
make frontend-rebuild   # Rebuild frontend (no-cache)
make frontend-start     # Start frontend only
make frontend-stop      # Stop frontend only
make frontend-restart   # Restart frontend only
make frontend-status    # Check frontend status
make frontend-logs      # Watch frontend logs

# Database commands
make db-build           # Build database only
make db-rebuild         # Rebuild database (no-cache)
make db-start           # Start database only
make db-stop            # Stop database only
make db-restart         # Restart database only
make db-status          # Check database status
make db-logs            # Watch database logs

# Combined services
make services-build     # Build all services
make services-rebuild   # Rebuild all services (no-cache)
make services-start     # Start all services
make services-stop      # Stop all services
make services-restart   # Restart all services
make status             # Check all services status
```

### **DDD Development Commands**
```bash
# DDD structure validation
make ddd-validate           # Validate DDD structure
make ddd-migration-status   # Show migration progress

# DDD development workflow
make ddd-compile            # Fast Java compilation
make ddd-quick-test         # Quick test execution
make ddd-check              # Code quality check
make ddd-clean-build        # Clean build for refactoring
make ddd-restart-backend    # Restart backend for DDD changes
make ddd-logs               # Watch backend logs during DDD development
make ddd-status             # Check DDD development status
```

### **Legacy Aliases (Backward Compatibility)**
```bash
make up         # → services-start
make down       # → services-stop
make restart    # → services-restart
make rebuild    # → services-rebuild
make ps         # → status
make health     # → status
make sh-app     # → shell-backend
make sh-db      # → shell-db
```

## 🏆 Trạng thái Kiến trúc Hiện tại & Chỉ số Kỹ thuật

- 🔧 **Development UX**: Makefile toàn diện với 30+ commands bao gồm quản lý cấu hình
- ⚙️ **Centralized Configuration**: Quản lý cấu hình một file với đồng bộ hóa tự động
- 🏗️ **DTO Architecture**: Tách biệt hoàn toàn API contracts khỏi domain entities
- 🔄 **MapStruct Integration**: Ánh xạ kiểu an toàn với validation toàn diện
- 📖 **Documentation**: Swagger/OpenAPI với dynamic server URLs và mô tả parameters chi tiết
- 🛫 **Database Migrations**: Quản lý schema dựa trên Flyway với automated migrations
- 🌱 **Data Seeding**: Profile-based seeding với DataFaker, idempotent seeding, configurable quantities
- 🗑️ **Soft Delete & Auditing**: Complete audit trail với automatic timestamp management và soft delete functionality
- 🔐 **Security**: BCrypt password hashing với authentication system
- 🌐 **CORS Configuration**: Environment-based CORS với endpoint-specific rules và comprehensive validation
- 🚦 **Rate Limiting**: Bucket4j-based rate limiting với isolated buckets per endpoint type (Public/API/Auth)
- ⚡️ **Caching Layer**: Spring Cache với Caffeine cải thiện hiệu suất 2 lần cho read operations
- 🌐 **Frontend**: Next.js Admin UI với Ant Design và TypeScript
- 🐳 **Full Stack**: Docker integration với multi-service development
- 🔗 **API Integration**: Seamless frontend-backend communication
- 🧪 **Testing**: Integration testing toàn diện với Testcontainers
- 📊 **Observability**: Structured logging với correlation IDs và profile-based configuration
- 🔗 **SEO-Friendly URLs**: Global slug system với dual access patterns

### 📊 **Chỉ số Code:**
- **8 Controllers** với thiết kế RESTful nhất quán và Swagger docs (bao gồm AuthController)
- **8 Services** với business logic và validation (6 domain services + 2 security services)
- **7 Repositories** với JPA và custom query methods
- **7 Entities** với audit fields và soft delete support
- **21 DTOs** với comprehensive validation annotations và organized by entity
- **7 Mappers** với tích hợp MapStruct
- **7 Flyway Migrations** với automated schema management và audit fields (V1-V7)
- **1 Data Seeder** với profile-based configuration và idempotent seeding
- **1 Centralized Configuration System** với đồng bộ hóa tự động trên tất cả components
- **Clean Project Structure** với organized packages và consistent naming conventions
- **Speed-Optimized Build System** với 4-tier build commands (5s-5min) và smart Docker caching

### 🎯 **Tính năng Chính:**
- **Complete Audit Trail** - Quản lý tự động created_at, updated_at, deleted_at
- **Single Source of Truth** - Schema được quản lý trong Flyway migrations, Configuration trong .env
- **Automated Data Seeding** - Môi trường development/test sẵn sàng với sample data
- **Interactive Configuration** - Quản lý cấu hình thân thiện với backup/restore
- **Dynamic Swagger URLs** - Server URLs tự động cập nhật dựa trên cấu hình
- **Comprehensive Testing** - Integration tests bao gồm tất cả CRUD operations và error scenarios
- **Password Security** - BCrypt hashing với secure authentication flow
- **CORS Security** - Environment-based CORS với endpoint-specific rules và comprehensive validation
- **Rate Limiting** - Bucket4j-based protection với isolated buckets per endpoint type
- **Business Logic** - Domain services với comprehensive business rules và validation
- **Clean Architecture** - Project structure cleanup với organized packages và consistent naming
- **JWT Authentication** - Stateless authentication với role-based access control
- **Build Optimization** - Speed-optimized Docker commands với incremental builds và smart caching
