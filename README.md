## Overview

**Enterprise-grade** Dockerized Spring Boot + MySQL application featuring modern architecture patterns with DTO-driven design, MapStruct mapping, comprehensive validation, and business logic integration. Built with Gradle using multi-stage Docker builds (no JDK required on host).

### 🏗️ **Architecture Highlights:**
- ✅ **DTO-First Design** - Complete separation between API contracts and domain entities
- ✅ **MapStruct Integration** - High-performance compile-time mapping 
- ✅ **Business Logic** - Automatic inventory management, order processing
- ✅ **Security** - BCrypt password encryption, input validation
- ✅ **RESTful APIs** - CRUD + pagination + advanced operations

## Prerequisites
- Docker Desktop (or Docker Engine + Compose plugin)

## Quick Start
```bash
# 1) Configure environment
cp .env.example .env    # edit values if needed

# 2) Build and start
docker compose up -d --build

# 3) Open the app
# Backend: http://localhost:${BACKEND_PORT:-8080}
# Swagger UI: http://localhost:${BACKEND_PORT:-8080}/swagger-ui.html
```

To stop:
```bash
docker compose down
```

Reset all data (recreate DB volume) and rebuild:
```bash
docker compose down -v
docker compose up -d --build
```

## Environment (.env)
Edit `.env` to manage all configs (no code changes required):
```env
# Database configuration
MYSQL_ROOT_PASSWORD=root
MYSQL_DATABASE=appdb
MYSQL_USER=appuser
MYSQL_PASSWORD=apppass
MYSQL_PORT=3306        # exposed port on host

# Backend configuration
BACKEND_PORT=8080      # exposed port on host

# DB connection seen by backend inside Docker network
DB_HOST=mysql
DB_PORT=3306

# JPA/Hibernate (Flyway manages schema)
JPA_DDL_AUTO=none
JPA_SHOW_SQL=true

# Optional: basic user for Spring Security (dev only)
# SPRING_SECURITY_USER_NAME=admin
# SPRING_SECURITY_USER_PASSWORD=admin

# Data Seeding (dev/test profiles only)
# Choose one profile:
SPRING_PROFILES_ACTIVE=dev    # For development with seeding
# SPRING_PROFILES_ACTIVE=test  # For testing with seeding  
# SPRING_PROFILES_ACTIVE=prod  # For production without seeding

APP_SEED_ENABLED=true
APP_SEED_PRODUCTS=15
APP_SEED_CUSTOMERS=10
```

## Project Structure
```
springboot-mysql-docker/
├─ backend/
│  ├─ Dockerfile                     # multi-stage: builds JAR inside Docker
│  ├─ build.gradle                   # MapStruct + Lombok + Spring Boot
│  ├─ gradle/, gradlew, settings.gradle
│  └─ src/main/java/com/backend/backend/
│     ├─ BackendApplication.java
│     ├─ config/
│     │  ├─ OpenApiConfig.java
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
├─ docker-compose.yml
├─ .env (copy from .env.example)
├─ makefile                          # Development utilities
└─ README.md
```

## Services (Docker Compose)
- mysql
  - Image: `mysql:8.4`
  - Ports: `${MYSQL_PORT:-3306}:3306`
  - Env: `MYSQL_ROOT_PASSWORD`, `MYSQL_DATABASE`, `MYSQL_USER`, `MYSQL_PASSWORD`
  - Volumes:
    - Named volume for data (`db_data`)
    - ✅ **Flyway migrations** handle schema initialization automatically

- backend
  - Build: `backend/Dockerfile` (multi-stage: JDK build -> JRE runtime)
  - Ports: `${BACKEND_PORT:-8080}:8080`
  - Env: `SPRING_DATASOURCE_*`, `JPA_*`, `APP_SEED_*` (derived from `.env`)
  - ✅ **Data Seeding** automatically runs for dev/test profiles with configurable quantities

## Swagger
- Swagger UI: `http://localhost:${BACKEND_PORT:-8080}/swagger-ui.html`
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
curl -X POST http://localhost:8080/api/v1/products \
  -H 'Content-Type: application/json' \
  -d '{"name":"Laptop","description":"Gaming laptop","price":1299.99,"quantityInStock":10}'

# Get paginated products
curl "http://localhost:8080/api/v1/products/page?page=0&size=5&sort=name"

# Partial update (only price)
curl -X PATCH http://localhost:8080/api/v1/products/1 \
  -H 'Content-Type: application/json' \
  -d '{"price":1199.99}'

# Access by slug (alternative to ID)
curl "http://localhost:8080/api/v1/products/slug/gaming-laptop"

# Soft delete (record remains in database with deleted_at timestamp)
curl -X DELETE http://localhost:8080/api/v1/products/1
# Response: 204 No Content (record still exists in DB but filtered from queries)
```

#### **Order Processing** (with automatic stock updates):
```bash
# Create complex order with multiple items
curl -X POST http://localhost:8080/api/v1/orders \
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
curl -X POST "http://localhost:8080/api/v1/orders/1/items?productId=3&quantity=1"
```

#### **Inventory Management**:
```bash
# Add stock (automatically updates product inventory)
curl -X POST http://localhost:8080/api/v1/stock-entries \
  -H 'Content-Type: application/json' \
  -d '{"productId": 1, "supplierId": 1, "quantity": 50}'
```

#### **Secure Administration**:
```bash
# Create admin with encrypted password
curl -X POST http://localhost:8080/api/v1/administrators \
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
```bash
# Start in background
docker compose up -d --build

# Tail logs
docker compose logs -f backend
docker compose logs -f mysql

# Stop containers
docker compose down

# Reset volumes (wipe DB)
docker compose down -v && docker compose up -d --build
```

## Troubleshooting
- Port already in use
  - Change `BACKEND_PORT` or `MYSQL_PORT` in `.env` and `docker compose up -d --build`.
- MySQL password or db name not applied
  - MySQL uses a persisted volume. Run `docker compose down -v` to remove volume and re-initialize.
- Slow or failing dependency downloads during Docker build
  - The Dockerfile retries Gradle steps and caches Gradle; re-run `docker compose up -d --build`.
- Swagger not accessible
  - Ensure backend is healthy: `docker compose logs backend`. Open `http://localhost:${BACKEND_PORT}/swagger-ui.html`.




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
* **Criteria:** `/v3/api-docs` is valid; `swagger-ui.html` looks good, has examples.
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
* **🎯 COMPLETED:** Enterprise-grade database migration strategy with Flyway, single source of truth cho schema, automated migrations
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

---

## 🏆 **Current Architecture Status**

### **✅ Completed Features:**
- 🔧 **Development UX**: Comprehensive Makefile with 20+ commands
- ⚙️ **Configuration Management**: Multi-profile application.yml with environment isolation  
- 🛡️ **Input Validation**: Bean Validation with global error handling
- 🏗️ **DTO Architecture**: Complete separation of API contracts from domain entities
- 🚀 **MapStruct Integration**: High-performance compile-time mapping
- 💼 **Business Logic**: Inventory management, order processing, automatic calculations
- 🔐 **Security**: BCrypt password encryption, sensitive data protection
- 📊 **APIs**: 50+ RESTful endpoints with pagination, sorting, filtering and search
- 📄 **Pagination**: PageResponse<T> standard with metadata, PageMapper utility
- 📖 **Documentation**: Swagger/OpenAPI with detailed parameter descriptions
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
- **Zero Manual Mapping** - All automated with type safety
- **Unified Pagination** - All list endpoints use PageResponse<T>
- **Dual Access Patterns** - ID and slug-based API endpoints
- **Soft Delete System** - All entities support soft delete with SQL restriction filtering
- **Complete Audit Trail** - Automatic created_at, updated_at, deleted_at management
- **Single Source of Truth** - Schema managed in Flyway migrations
- **Automated Data Seeding** - Development/test environments ready with sample data

**🌟 Ready for production deployment with enterprise-grade patterns!**

