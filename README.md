## Overview

**Enterprise-grade** Dockerized Spring Boot + MySQL application featuring modern architecture patterns with DTO-driven design, MapStruct mapping, comprehensive validation, and business logic integration. Built with Gradle using multi-stage Docker builds (no JDK required on host).

> 📖 **Tiếng Việt**: [README_VI.md](README_VI.md) - Vietnamese documentation available

### 🏗️ **Architecture Highlights:**
- ✅ **DTO-First Design** - Complete separation between API contracts and domain entities
- ✅ **MapStruct Integration** - High-performance compile-time mapping 
- ✅ **Business Logic** - Automatic inventory management, order processing
- ✅ **Security** - BCrypt password encryption, input validation
- ✅ **RESTful APIs** - CRUD + pagination + advanced operations

## Prerequisites
- Docker Desktop (or Docker Engine + Compose plugin)
- Make (usually pre-installed on macOS/Linux)

## Quick Start
```bash
# 1) Configure environment
make config    # Interactive configuration manager

# 2) Build and start
make dev-start

# 3) Open the app
# Backend: http://localhost:${BACKEND_PORT:-8080}
# Swagger UI: http://localhost:${BACKEND_PORT:-8080}/swagger-ui/index.html
```

To stop:
```bash
make down
```

Reset all data (recreate DB volume) and rebuild:
```bash
make clean
make dev-start
```

## 🎯 Centralized Configuration (.env)
**Single file configuration** - Edit only `.env` to change all system settings:

```env
# ============================================
# 🎯 CENTRALIZED CONFIGURATION - EDIT THIS FILE ONLY
# ============================================

# 🌐 PORT CONFIGURATION (edit here only)
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
# Interactive configuration manager
make config

# View current configuration
make config-show

# Backup configuration before changes
make config-backup

# Manage backups (view, restore, clean old)
make config-list-backups
make config-restore BACKUP=.env.backup.20240907_113905
make config-clean-backups
```

## Project Structure
```
springboot-mysql-docker/
├─ .env                              # 🎯 CENTRALIZED CONFIGURATION (edit this file only)
├─ backups/
│  └─ env/                           # 📁 Configuration backups (auto-managed)
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
│     ├─ BackendApplication.java
│     ├─ config/
│     │  ├─ OpenApiConfig.java       # Dynamic server URL from .env
│     │  ├─ SecurityConfig.java      # BCrypt password encoder
│     │  ├─ SeedProperties.java      # Data seeding configuration
│     │  └─ AppConfig.java           # Configuration properties binding
│     ├─ dto/                        # DTO-first design
│     │  ├─ common/PageResponse.java
│     │  ├─ product/                 # Use-case driven DTOs
│     │  ├─ customer/, supplier/, administrator/
│     │  ├─ order/, orderitem/, stockentry/
│     │  └─ ...CreateRequest, ...UpdateRequest, ...Response
│     ├─ entity/                     # JPA entities with Lombok
│     │  ├─ base/AuditableEntity.java # Base class with audit fields
│     │  ├─ Product.java, Supplier.java, Customer.java
│     │  ├─ Order.java, OrderItem.java, StockEntry.java
│     │  └─ Administrator.java
│     ├─ mapper/                     # MapStruct mappers
│     │  ├─ ProductMapper.java, CustomerMapper.java
│     │  ├─ OrderMapper.java, StockEntryMapper.java
│     │  └─ ...Mapper.java (auto-generated implementations)
│     ├─ repository/                 # Spring Data JPA
│     │  ├─ ProductRepository.java, CustomerRepository.java
│     │  └─ ...Repository.java
│     ├─ bootstrap/                  # Application startup components
│     │  └─ DevTestDataSeeder.java   # Profile-based data seeding
│     ├─ service/                    # Business logic layer
│     │  ├─ ProductService.java      # Inventory management
│     │  ├─ OrderService.java        # Complex order processing
│     │  └─ ...Service.java          # Stock updates, totals calculation
│     ├─ util/                       # Utility classes
│     │  ├─ SlugUtil.java            # Slug generation and validation
│     │  └─ PageMapper.java          # Pagination utility
│     ├─ controller/                 # RESTful API layer
│     │  ├─ ProductController.java   # /api/v1/products
│     │  ├─ OrderController.java     # /api/v1/orders
│     │  └─ ...Controller.java       # CRUD + pagination + custom endpoints
│     └─ exception/                  # Global error handling
│        ├─ GlobalExceptionHandler.java
│        └─ ResourceNotFoundException.java
├─ backend/src/main/resources/db/migration/
│  ├─ V1__init.sql                   # Flyway schema migration
│  ├─ V2__seed_base.sql              # Flyway seed data migration
│  ├─ V3__add_slug_products_customers.sql # Slug support migration
│  └─ V4__add_audit_and_soft_delete.sql   # Audit fields and soft delete migration
├─ docs/                             # Documentation
│  ├─ README_day1.md, README_day2.md
│  ├─ README_day3.md, README_day4.md
│  └─ ...
├─ docker-compose.yml                # Uses centralized .env variables
├─ makefile                          # Enhanced with config management commands
└─ README.md
```

## Services (Docker Compose)
- mysql
  - Image: `mysql:8.4`
  - Ports: `${MYSQL_PORT:-3306}:3306` (from .env)
  - Env: `MYSQL_ROOT_PASSWORD`, `MYSQL_DATABASE`, `MYSQL_USER`, `MYSQL_PASSWORD` (from .env)
  - Volumes:
    - Named volume for data (`db_data`)
    - ✅ **Flyway migrations** handle schema initialization automatically

- backend
  - Build: `backend/Dockerfile` (multi-stage: JDK build -> JRE runtime)
  - Ports: `${BACKEND_PORT:-8080}:${BACKEND_PORT:-8080}` (from .env)
  - Env: `SPRING_DATASOURCE_*`, `JPA_*`, `APP_SEED_*` (auto from .env)
  - ✅ **Data Seeding** automatically runs for dev/test profiles with configurable quantities
  - ✅ **Dynamic Configuration** - Swagger server URL automatically updates based on BACKEND_PORT

## Swagger
- Swagger UI: `http://localhost:${BACKEND_PORT:-8080}/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:${BACKEND_PORT:-8080}/v3/api-docs`

## 🚀 API Overview

### **Modern RESTful Endpoints** with DTO-driven design:

#### **Core Resources** (Full CRUD + Pagination):
- 🛍️ **Products**: `/api/v1/products` - Inventory management with stock tracking
- 👥 **Customers**: `/api/v1/customers` - Customer management
- 🏪 **Suppliers**: `/api/v1/suppliers` - Supplier relationships  
- 👨‍💼 **Administrators**: `/api/v1/administrators` - User management with secure passwords
- 📦 **Stock Entries**: `/api/v1/stock-entries` - Inventory transactions
- 📋 **Orders**: `/api/v1/orders` - Complex order processing
- 📦 **Order Items**: `/api/v1/order-items` - Individual order line items

#### **Advanced Features**:
- ✅ **Pagination**: `/page` endpoints with sorting
- ✅ **Partial Updates**: PATCH operations with selective field updates  
- ✅ **Business Logic**: Automatic stock management and calculations
- ✅ **Validation**: Comprehensive input validation with meaningful errors
- ✅ **Security**: Password encryption and sensitive data protection
- ✅ **Slug Access**: Dual access patterns with both ID and slug-based endpoints
- ✅ **Soft Delete**: Records are marked as deleted but remain in database
- ✅ **Audit Trail**: Automatic timestamp management for all entities

### **Example API Usage**:

#### **Product Management**:
```bash
# Create product with validation
curl -X POST http://localhost:${BACKEND_PORT:-8080}/api/v1/products \
  -H 'Content-Type: application/json' \
  -d '{"name":"Laptop","description":"Gaming laptop","price":1299.99,"quantityInStock":10}'

# Get paginated products
curl "http://localhost:${BACKEND_PORT:-8080}/api/v1/products/page?page=0&size=5&sort=name"

# Partial update (only price)
curl -X PATCH http://localhost:${BACKEND_PORT:-8080}/api/v1/products/1 \
  -H 'Content-Type: application/json' \
  -d '{"price":1199.99}'

# Access by slug (alternative to ID)
curl "http://localhost:${BACKEND_PORT:-8080}/api/v1/products/slug/gaming-laptop"

# Soft delete (record remains in database with deleted_at timestamp)
curl -X DELETE http://localhost:${BACKEND_PORT:-8080}/api/v1/products/1
# Response: 204 No Content (record still exists in DB but filtered from queries)
```

#### **Order Processing** (with automatic stock updates):
```bash
# Create complex order with multiple items
curl -X POST http://localhost:${BACKEND_PORT:-8080}/api/v1/orders \
  -H 'Content-Type: application/json' \
  -d '{
    "customerId": 1,
    "items": [
      {"productId": 1, "quantity": 2},
      {"productId": 2, "quantity": 1}
    ]
  }'

# Response includes automatic calculations:
{
  "id": 1,
  "totalAmount": 156.18,           # Auto-calculated
  "orderDate": "2025-09-04T15:24:11Z",
  "items": [
    {
      "totalPrice": 2599.98,       # Auto-calculated  
      "quantity": 2,
      "price": 1299.99
    }
  ]
}

# Add item to existing order
curl -X POST "http://localhost:${BACKEND_PORT:-8080}/api/v1/orders/1/items?productId=3&quantity=1"
```

#### **Inventory Management**:
```bash
# Add stock (automatically updates product inventory)
curl -X POST http://localhost:${BACKEND_PORT:-8080}/api/v1/stock-entries \
  -H 'Content-Type: application/json' \
  -d '{"productId": 1, "supplierId": 1, "quantity": 50}'
```

#### **Secure Administration**:
```bash
# Create admin with encrypted password
curl -X POST http://localhost:${BACKEND_PORT:-8080}/api/v1/administrators \
  -H 'Content-Type: application/json' \
  -d '{
    "username": "admin1", 
    "password": "securepass123", 
    "email": "admin@company.com",
    "fullName": "System Administrator"
  }'

# Response excludes password for security
{
  "id": 1,
  "username": "admin1",
  "email": "admin@company.com", 
  "fullName": "System Administrator"
}
```

## Default Credentials
- MySQL root: `root` / `root`
- MySQL app user: `appuser` / `apppass` (DB: `appdb`)

## Useful Commands

### 🚀 **Basic Operations:**
```bash
# Start development environment
make dev-start

# Start in background
make up

# Tail logs
make logs          # Backend logs
make logs-all      # All services logs
make logs-tail     # Recent backend logs

# Stop containers
make down

# Reset volumes (wipe DB) and rebuild
make clean
make dev-start
```

### 🎯 **Configuration Management:**
```bash
# Interactive configuration manager
make config

# View current configuration
make config-show

# Backup configuration
make config-backup

# List all backups
make config-list-backups

# Restore from backup
make config-restore BACKUP=.env.backup.20240907_113905

# Clean old backups (keep latest 5)
make config-clean-backups
```

### 🔧 **Development Utilities:**
```bash
# Start development environment
make dev-start

# Test API endpoints
make test-api

# Test Swagger UI
make test-swagger

# Open Swagger UI in browser
make swagger

# Check system status
make dev-status

# View all available commands
make help
```

## Troubleshooting

### 🔧 **Configuration Issues:**
- **Port already in use**
  - Edit `BACKEND_PORT` or `MYSQL_PORT` in `.env` and run `make restart`
  - Or use `make config` for interactive configuration changes

- **Configuration not applied**
  - Run `make restart` after changing `.env`
  - Check current configuration: `make config-show`

- **Backup configuration before changes**
  - Always run `make config-backup` before making changes
  - Restore if needed: `make config-restore BACKUP=filename`

### 🐛 **Common Issues:**
- **MySQL password or db name not applied**
  - MySQL uses a persisted volume. Run `make clean` to remove volume and re-initialize.

- **Slow or failing dependency downloads during Docker build**
  - Dockerfile has retry Gradle steps and caches Gradle; re-run `make dev-rebuild`.

- **Swagger not accessible**
  - Ensure backend is healthy: `make logs-tail`
  - Open `http://localhost:${BACKEND_PORT}/swagger-ui/index.html`
  - Test with `make test-swagger`




-----

## 🗓️ Development Journey

### ✅ Day 1 — Makefile & Dev UX
* **Goal:** `make up`, `make down`, `make logs`, `make rebuild`.  
* **Criteria:** Makefile works on macOS/Linux; README is updated.  
* **📖 [README Day 1](docs/README_day1.md)**
* **[Git changelog](https://github.com/viettrungIT3/springboot-mysql-docker/pull/1/files)**

### ✅ Day 2 — Profiles & Isolated Configuration  
* **Goal:** `application.yml` with `dev`, `test`, `prod` profiles; use environment overrides.  
* **Criteria:** Run dev via Docker Compose; DB configuration comes from .env.  
* **📖 [README Day 2](docs/README_day2.md)**
* **[Git changelog](https://github.com/viettrungIT3/springboot-mysql-docker/pull/2/files)**

### ✅ Day 3 — Validation & Global Error Handling
* **Goal:** Bean Validation (JSR-380) for incoming DTOs; @ControllerAdvice + standardized JSON errors.  
* **Criteria:** 400 response with clear field errors.  
* **📖 [README Day 3](docs/README_day3.md)**
* **[Git changelog](https://github.com/viettrungIT3/springboot-mysql-docker/pull/3/files)**

### ✅ Day 4 — DTO + MapStruct
* **Goal:** Separate DTOs from entities, use MapStruct mappers.
* **Criteria:**  Controller only receives/returns DTOs; mappers have simple tests.
* **🎯 COMPLETED:** Enterprise-grade architecture with 21 DTOs, 7 MapStruct mappers, business logic integration  
* **📖 [README Day 4](docs/README_day4.md)**
* **[Git changelog](https://github.com/viettrungIT3/springboot-mysql-docker/pull/4/files)**

### ✅ Day 5 — Pagination, Sort, Filter
* **Goal:** Standardize list endpoints: `?page=…&size=…&sort=…&search=…`
* **Criteria:** Swagger displays correct parameters; returns `Page` metadata.
* **🎯 COMPLETED:** Standardized pagination with PageResponse<T>, PageMapper utility, search functionality and comprehensive Swagger documentation
* **📖 [README Day 5](docs/README_day5.md)**
* **[Git changelog](https://github.com/viettrungIT3/springboot-mysql-docker/pull/5/files)**

### ✅ Day 6 — Swagger Polish + OpenAPI
* **Goal:** Title, description, contact, server URLs; tag endpoints.
* **Criteria:** `/v3/api-docs` is valid; `swagger-ui/index.html` looks good, has examples.
* **🎯 COMPLETED:** 
* **📖 [README Day 6](docs/README_day6.md)**
* **[Git changelog](https://github.com/viettrungIT3/springboot-mysql-docker/pull/6/files)**

### ✅ Day 7 — Basic Unit Testing
* **Goal:** JUnit 5 + Mockito for the core service layer.
* **Criteria:** Coverage > 50% for core services.
* **🎯 COMPLETED:** 
* **📖 [README Day 7](docs/README_day7.md)**
* **[Git changelog](https://github.com/viettrungIT3/springboot-mysql-docker/pull/7/files)**

### ✅ Day 8 — Testcontainers for Integration Tests
* **Goal:** Spin up MySQL with Testcontainers, test repositories.
* **Criteria:** Tests run with ./gradlew test without needing a local MySQL installation.
* **🎯 COMPLETED:** 
* **📖 [README Day 8](docs/README_day8.md)**
* **[Git changelog](https://github.com/viettrungIT3/springboot-mysql-docker/pull/8/files)**

### ✅ Day 9 — Flyway Migrations 🛫
* **Goal:** Move schema and seed from init_database.sql to Flyway V1__init.sql. App auto-migrate on start; remove mount init SQL in Compose.
* **Criteria:** Flyway is the single source of truth; Testcontainers work with migrations.
* **🎯 COMPLETED:** Enterprise-grade database migration strategy with Flyway, single source of truth for schema, automated migrations
* **📖 [README Day 9](docs/README_day9.md)**
* **[Git changelog](https://github.com/viettrungIT3/springboot-mysql-docker/pull/9/files)**

### ✅ Day 10 — Data Seeding with Docker & Makefile 🌱
* **Goal:** Lightweight data seeding for dev/test (CommandLineRunner) with configurable and idempotent capabilities.
* **Criteria:** Dev startup has sample products, customers,... Disabled in prod. Idempotent (no duplicates on restart), configurable quantities via environment variables.
* **🎯 COMPLETED:** Profile-based data seeding with DataFaker, idempotent seeding, configurable quantities, Docker & Makefile integration
* **📖 [README Day 10](docs/README_day10.md)**
* **[Git changelog](https://github.com/viettrungIT3/springboot-mysql-docker/pull/10/files)**

### ✅ Day 11 - — Standardized Logging
* **Goal:** Logback JSON (profile-dependent), correlation ID filter.
* **Criteria:** Logs have a traceId; log level can be configured via environment variable: LOG_LEVEL=INFO.
* **🎯 COMPLETED:** 
* **📖 [README Day 11](docs/README_day11.md)**
* **[Git changelog](https://github.com/viettrungIT3/springboot-mysql-docker/pull/11/files)**

### ✅ Day 12 — Global ID/Slug Feature 🔗
* **Goal:** Add slug support for Products and Customers with automatic generation and uniqueness validation.
* **Criteria:** Slug auto-generated from name, unique constraint, API endpoints support both ID and slug access.
* **🎯 COMPLETED:** Global slug system with SlugUtil, unique constraints, dual access patterns (ID/slug), comprehensive API coverage
* **📖 [README Day 12](docs/README_day12.md)**
* **[Git changelog](https://github.com/viettrungIT3/springboot-mysql-docker/pull/12/files)**

### ✅ Day 13 — Soft Delete & Auditing 🗑️⏱️
* **Goal:** Add audit fields (created_at, updated_at, deleted_at) and implement soft delete for all entities.
* **Criteria:** Soft delete is the default; deleted records are filtered out; audit timestamps are automatically managed.
* **🎯 COMPLETED:** Complete audit system with AuditableEntity base class, soft delete implementation, automatic timestamp management, SQL restriction filtering
* **📖 [README Day 13](docs/README_day13.md)**
* **[Git changelog](https://github.com/viettrungIT3/springboot-mysql-docker/pull/13/files)**

### ✅ Day 14 — JWT Security Implementation 🔐
* **Goal:** Spring Security stateless + JWT; login endpoint; protect API endpoints.
* **Criteria:** /api/** requires token except /auth/**, /swagger-ui/**; JWT authentication works perfectly.
* **🎯 COMPLETED:** Complete JWT authentication system with stateless security, role-based access control, and comprehensive testing
* **📖 [README Day 14](docs/README_day14.md)**
* **[Git changelog](https://github.com/viettrungIT3/springboot-mysql-docker/pull/14/files)**

### ✅ Day 15 — Password Hashing & Admin UI Base 🔐
* **Mục tiêu:** Implement password hashing for Administrator and create Next.js Admin UI with authentication.
* **Tiêu chí:** BCrypt password hashing, Next.js frontend with Ant Design, Docker integration, full-stack authentication.
* **🎯 HOÀN THÀNH:** Complete authentication system with modern frontend, password security, and admin dashboard ui
* **📖 [README Day 15](docs/README_day15.md)**
* **[Git changelog](https://github.com/viettrungIT3/springboot-mysql-docker/pull/15/files)**

---

## 🏆 **Current Architecture Status**

### **✅ Completed Features:**
- 🔧 **Development UX**: Comprehensive Makefile with 30+ commands including configuration management
- ⚙️ **Centralized Configuration**: Single-file configuration management with automatic synchronization
- 🎯 **Configuration Manager**: Interactive script for easy configuration changes with backup management
- 📁 **Backup System**: Automated backup management with folder organization and cleanup
- 🛡️ **Input Validation**: Bean Validation with global error handling
- 🏗️ **DTO Architecture**: Complete separation of API contracts from domain entities
- 🚀 **MapStruct Integration**: High-performance compile-time mapping
- 💼 **Business Logic**: Inventory management, order processing, automatic calculations
- 🔐 **JWT Security**: Stateless authentication with JWT tokens, role-based access control, and protected API endpoints
- 📊 **APIs**: 50+ RESTful endpoints with pagination, sorting, filtering and search
- 📄 **Pagination**: PageResponse<T> standard with metadata, PageMapper utility
- 📖 **Documentation**: Swagger/OpenAPI with dynamic server URLs and detailed parameter descriptions
- 🛫 **Database Migrations**: Flyway-based schema management with automated migrations
- 🌱 **Data Seeding**: Profile-based seeding with DataFaker, idempotent seeding, configurable quantities
- 🔗 **Slug System**: Global slug support for Products and Customers with dual access patterns
- 🗑️ **Soft Delete & Auditing**: Complete audit trail with automatic timestamp management and soft delete functionality

### **📈 Technical Metrics:**
- **7 Domain Entities** with Lombok integration and AuditableEntity base class
- **21 DTOs** designed with use-case patterns
- **7 MapStruct Mappers** with relationship handling
- **1 PageMapper Utility** for pagination standardization
- **1 SlugUtil Utility** for slug generation and validation
- **1 AuditableEntity Base Class** with automatic timestamp management
- **14 Controllers** with consistent RESTful design and Swagger docs
- **4 Flyway Migrations** with automated schema management and audit fields
- **1 Data Seeder** with profile-based configuration and idempotent seeding
- **1 Configuration Manager Script** with interactive interface and backup management
- **1 Centralized Configuration System** with automatic synchronization across all components
- **Zero Manual Mapping** - All automated with type safety
- **Unified Pagination** - All list endpoints use PageResponse<T>
- **Dual Access Patterns** - ID and slug-based API endpoints
- **Soft Delete System** - All entities support soft delete with SQL restriction filtering
- **Complete Audit Trail** - Automatic created_at, updated_at, deleted_at management
- **Single Source of Truth** - Schema managed in Flyway migrations, Configuration in .env
- **Automated Data Seeding** - Development/test environments ready with sample data
- **Smart Backup Management** - Automated backup with folder organization and cleanup

**🌟 Ready for production deployment with enterprise-grade patterns!**

