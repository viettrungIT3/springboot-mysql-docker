# 🚀 Spring Boot + MySQL + Docker

Ứng dụng Spring Boot + MySQL **dùng Docker ở cấp độ doanh nghiệp**, với kiến trúc hiện đại theo mô hình thiết kế dựa trên **DTO-First Design**, ánh xạ **MapStruct**, xác thực toàn diện, và tích hợp business logic. Được build bằng **Gradle** với Docker multi-stage build (không cần cài JDK trên máy host).

> 📖 **Tiếng Anh**: [README.md](README.md) - Tài liệu tiếng Anh có sẵn

- ✅ **DTO-First Design** - Tách biệt hoàn toàn giữa hợp đồng API và các thực thể trong domain  
- ✅ **MapStruct Integration** - Ánh xạ kiểu an toàn giữa DTO và Entity  
- ✅ **Comprehensive Validation** - Bean validation với các quy tắc nghiệp vụ tùy chỉnh  
- ✅ **Centralized Configuration** - Quản lý cấu hình trong một file duy nhất  
- ✅ **Interactive Configuration Manager** - Giao diện trực quan, dễ sử dụng để quản lý cấu hình  
- ✅ **Automated Backup System** - Hỗ trợ backup và khôi phục cấu hình tự động

## 🚀 Quick Start

```bash
# Clone repository
git clone <repository-url>
cd springboot-mysql-docker

# Thiết lập cấu hình với giao diện tương tác
make config

# Khởi động môi trường development
make dev-start

# Truy cập Swagger UI
make swagger
```

**Dừng & Reset:**
```bash
make down
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
│     ├─ api/                        # API layer
│     │  └─ ApiError.java            # Global error handling
│     ├─ config/                     # Configuration classes
│     │  ├─ AppConfig.java           # Application configuration
│     │  ├─ OpenApiConfig.java       # Dynamic server URL from .env
│     │  ├─ SecurityConfig.java      # Security configuration
│     │  └─ SeedProperties.java      # Data seeding configuration
│     ├─ controller/                 # REST controllers
│     │  ├─ customer/, supplier/, administrator/
│     │  ├─ order/, orderitem/
│     │  ├─ product/, stockentry/
│     │  └─ Administrator.java
│     ├─ dto/                        # Data Transfer Objects
│     │  ├─ common/                  # Shared DTOs (PageResponse, etc.)
│     │  ├─ customer/, supplier/, administrator/
│     │  ├─ order/, orderitem/
│     │  ├─ product/, stockentry/
│     │  └─ CustomerDTO.java
│     ├─ entity/                     # JPA entities
│     │  ├─ base/                    # Base entity with audit fields
│     │  ├─ customer/, supplier/, administrator/
│     │  ├─ order/, orderitem/
│     │  ├─ product/, stockentry/
│     │  └─ Administrator.java
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
│     ├─ service/                    # Business logic
│     │  ├─ customer/, supplier/, administrator/
│     │  ├─ order/, orderitem/
│     │  ├─ product/, stockentry/
│     │  └─ AdministratorService.java
│     ├─ util/                       # Utility classes
│     │  ├─ PageMapper.java          # Pagination utility
│     │  └─ SlugUtil.java            # Slug generation utility
│     ├─ web/                        # Web layer components
│     │  └─ WebConfig.java           # Web configuration
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
│     │     └─ V4__add_audit_and_soft_delete.sql
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
  - Environment: `SPRING_DATASOURCE_*`, `JPA_*`, `APP_SEED_*` (tự động từ .env)
  - ✅ **Data Seeding** tự động chạy cho dev/test profiles với số lượng có thể cấu hình
  - ✅ **Dynamic Configuration** - Swagger server URL tự động cập nhật theo BACKEND_PORT

## Swagger
- Swagger UI: `http://localhost:${BACKEND_PORT:-8080}/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:${BACKEND_PORT:-8080}/v3/api-docs`

## API Endpoints

### 🛍️ **Products**: `/api/v1/products` - Quản lý kho hàng với theo dõi tồn kho
### 👥 **Customers**: `/api/v1/customers` - Quản lý khách hàng với hỗ trợ slug
### 🏢 **Suppliers**: `/api/v1/suppliers` - Quản lý nhà cung cấp với thông tin liên hệ
### 📋 **Orders**: `/api/v1/orders` - Quản lý đơn hàng với theo dõi sản phẩm
### 👨‍💼 **Administrators**: `/api/v1/administrators` - Quản lý người dùng với mật khẩu bảo mật
### 📦 **Stock Entries**: `/api/v1/stock-entries` - Giao dịch kho hàng

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

#### **Quản trị Bảo mật**:
```bash
# Tạo administrator
curl -X POST http://localhost:${BACKEND_PORT:-8080}/api/v1/administrators \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "securepass123",
    "email": "admin@example.com",
    "fullName": "System Administrator"
  }'
```

**Response:**
```json
{
  "id": 1,
  "username": "admin",
  "email": "admin@example.com",
  "fullName": "System Administrator"
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

## Prerequisites
- **Docker Desktop** (hoặc Docker Engine + Compose plugin)
- **Make** (thường đã có sẵn trên macOS/Linux)
- **Git**

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
- 🌐 **Frontend**: Next.js Admin UI với Ant Design và TypeScript
- 🐳 **Full Stack**: Docker integration với multi-service development
- 🔗 **API Integration**: Seamless frontend-backend communication
- 🧪 **Testing**: Integration testing toàn diện với Testcontainers
- 📊 **Observability**: Structured logging với correlation IDs và profile-based configuration
- 🔗 **SEO-Friendly URLs**: Global slug system với dual access patterns

### 📊 **Chỉ số Code:**
- **15 Controllers** với thiết kế RESTful nhất quán và Swagger docs (bao gồm AuthenticationController)
- **15 Services** với business logic và validation (bao gồm AuthenticationService)
- **14 Repositories** với JPA và custom query methods
- **14 Entities** với audit fields và soft delete support
- **16 DTOs** với comprehensive validation annotations (bao gồm LoginRequest/Response)
- **14 Mappers** với tích hợp MapStruct
- **4 Flyway Migrations** với automated schema management và audit fields
- **1 Data Seeder** với profile-based configuration và idempotent seeding
- **1 Centralized Configuration System** với đồng bộ hóa tự động trên tất cả components
- **1 Next.js Frontend** với 15+ components và services

### 🎯 **Tính năng Chính:**
- **Complete Audit Trail** - Quản lý tự động created_at, updated_at, deleted_at
- **Single Source of Truth** - Schema được quản lý trong Flyway migrations, Configuration trong .env
- **Automated Data Seeding** - Môi trường development/test sẵn sàng với sample data
- **Interactive Configuration** - Quản lý cấu hình thân thiện với backup/restore
- **Dynamic Swagger URLs** - Server URLs tự động cập nhật dựa trên cấu hình
- **Comprehensive Testing** - Integration tests bao gồm tất cả CRUD operations và error scenarios
- **Password Security** - BCrypt hashing với secure authentication flow
- **Modern Frontend** - Next.js Admin UI với responsive design và professional UX
- **Full Stack Integration** - Seamless frontend-backend communication với Docker
