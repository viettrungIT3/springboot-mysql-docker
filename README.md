## Overview

**Enterprise-grade** Dockerized Spring Boot + MySQL application featuring modern architecture patterns with DTO-driven design, MapStruct mapping, comprehensive validation, and business logic integration. Built with Gradle using multi-stage Docker builds (no JDK required on host).

> 📖 **Tiếng Việt**: [README_VI.md](README_VI.md) - Vietnamese documentation available

### 🏗️ **Architecture Highlights:**
- ✅ **Domain-Driven Design (DDD)** - Clean architecture with bounded contexts
- ✅ **DTO-First Design** - Complete separation between API contracts and domain entities
- ✅ **MapStruct Integration** - High-performance compile-time mapping 
- ✅ **Business Logic** - Automatic inventory management, order processing
- ✅ **Security** - BCrypt password encryption, input validation
- ✅ **RESTful APIs** - CRUD + pagination + advanced operations
- ✅ **Caching Layer** - Spring Cache with Caffeine for 2x performance improvement
- ✅ **Service-Specific Commands** - Optimized makefile for efficient development

## Prerequisites
- Docker Desktop (or Docker Engine + Compose plugin)
- Make (usually pre-installed on macOS/Linux)

## Quick Start

### 🚀 **Full Development Environment**
```bash
# 1) Configure environment
make config    # Interactive configuration manager

# 2) Start full stack (mysql + backend + frontend)
make dev-start

# 3) Open the app
# Backend: http://localhost:${BACKEND_PORT:-8080}
# Swagger UI: http://localhost:${BACKEND_PORT:-8080}/swagger-ui/index.html
# Frontend: http://localhost:${FRONTEND_PORT:-3000}
```

### ⚡ **Service-Specific Development**
```bash
# Backend development only (API + Database)
make dev-backend

# API development only (Backend + Database, no frontend)
make dev-api

# Check all services status
make status

# Restart specific service
make backend-restart    # Restart backend only
make frontend-restart   # Restart frontend only
make db-restart         # Restart database only
```

### 🚀 **Speed-Optimized Development (Recommended)**
```bash
# ⚡ FASTEST: Hot reload (no build, ~5 seconds)
make dev-hot-reload     # For configuration changes

# 🔄 FAST: Incremental build (~30 seconds)  
make dev-code-change    # For Java code changes

# 🚀 MEDIUM: Quick restart (~45 seconds)
make dev-quick-restart  # For dependency changes

# 📊 Show optimization tips
make docker-optimize    # Compare build speeds and get recommendations
```

### 🛑 **Stop & Cleanup**
```bash
# Stop all services
make dev-stop

# Clean up and reset (recreate DB volume)
make clean
make dev-start
```

### 📈 Observability & Client (Days 22, 26)
```bash
# Optional Prometheus + Grafana stack
make observability-up       # Start Prometheus & Grafana
make observability-status   # Check status
make observability-down     # Stop stack

# Generate and test sample TypeScript API client (Docker-based)
make client-gen             # Generate client into clients/typescript-axios
make client-test            # Smoke test client call via Docker
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

# ⚡️ CACHE CONFIGURATION
APP_CACHE_TTL_SECONDS=300
APP_CACHE_MAX_SIZE=1000
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

## 🏗️ Domain-Driven Design (DDD) Architecture

### **Bounded Contexts**
The application is organized into clear bounded contexts following DDD principles:

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
│     │  ├─ ProductService.java      # Inventory management with business logic
│     │  ├─ OrderService.java        # Complex order processing
│     │  ├─ CustomerService.java     # Customer management with business logic
│     │  ├─ SupplierService.java     # Supplier management with business logic
│     │  ├─ StockEntryService.java   # Stock management with business logic
│     │  ├─ UserService.java         # User management with authentication
│     │  ├─ JwtTokenService.java      # JWT token management
│     │  └─ PasswordService.java     # Password hashing and validation
│     ├─ util/                       # Utility classes
│     │  ├─ SlugUtil.java            # Slug generation and validation
│     │  └─ PageMapper.java          # Pagination utility
│     ├─ controller/                 # RESTful API layer
│     │  ├─ ProductController.java   # /api/v1/products (with business logic endpoints)
│     │  ├─ OrderController.java     # /api/v1/orders
│     │  ├─ CustomerController.java  # /api/v1/customers
│     │  ├─ SupplierController.java  # /api/v1/suppliers
│     │  ├─ UserController.java      # /api/v1/users
│     │  ├─ AuthController.java      # /auth (authentication endpoints)
│     │  └─ ...Controller.java       # CRUD + pagination + business logic endpoints
│     └─ exception/                  # Global error handling
│        ├─ GlobalExceptionHandler.java
│        └─ ResourceNotFoundException.java
├─ backend/src/main/resources/db/migration/
│  ├─ V1__init.sql                   # Flyway schema migration
│  ├─ V2__seed_base.sql              # Flyway seed data migration
│  ├─ V3__add_slug_products_customers.sql # Slug support migration
│  ├─ V4__add_audit_and_soft_delete.sql   # Audit fields and soft delete migration
│  ├─ V5__add_stock_entries.sql      # Stock management migration
│  ├─ V6__add_users_table.sql       # User authentication migration
│  └─ V7__add_user_role.sql         # User role enum migration
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
  - Env: `SPRING_DATASOURCE_*`, `JPA_*`, `APP_SEED_*`, `APP_CACHE_*` (auto from .env)
  - ✅ **Data Seeding** automatically runs for dev/test profiles with configurable quantities
  - ✅ **Dynamic Configuration** - Swagger server URL automatically updates based on BACKEND_PORT

## Swagger
- Swagger UI: `http://localhost:${BACKEND_PORT:-8080}/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:${BACKEND_PORT:-8080}/v3/api-docs`

## 🚀 API Overview

### **Modern RESTful Endpoints** with DTO-driven design:

#### **Core Resources** (Full CRUD + Pagination):
- 🛍️ **Products**: `/api/v1/products` - Inventory management with stock tracking + business logic
- 👥 **Customers**: `/api/v1/customers` - Customer management + business logic
- 🏪 **Suppliers**: `/api/v1/suppliers` - Supplier relationships + business logic
- 👨‍💼 **Users**: `/api/v1/users` - User management with secure passwords + authentication
- 📦 **Stock Entries**: `/api/v1/stock-entries` - Inventory transactions + business logic
- 📋 **Orders**: `/api/v1/orders` - Complex order processing + business logic
- 📦 **Order Items**: `/api/v1/order-items` - Individual order line items
- 🔐 **Authentication**: `/auth/*` - JWT-based authentication endpoints

#### **Advanced Features**:
- ✅ **Pagination**: `/page` endpoints with sorting
- ✅ **Partial Updates**: PATCH operations with selective field updates  
- ✅ **Business Logic**: Domain services with comprehensive business rules
- ✅ **Authentication**: JWT-based authentication with role-based access control
- ✅ **Validation**: Comprehensive input validation with meaningful errors
- ✅ **Security**: Password encryption and sensitive data protection
- ✅ **Slug Access**: Dual access patterns with both ID and slug-based endpoints
- ✅ **Soft Delete**: Records are marked as deleted but remain in database
- ✅ **Audit Trail**: Automatic timestamp management for all entities
- ✅ **Caching**: Spring Cache with Caffeine for performance optimization

### **Business Logic Features**:

#### **Domain Services with Business Rules**:
- ✅ **ProductService**: Stock management, price updates, low stock alerts, product statistics
- ✅ **CustomerService**: Customer validation, search functionality, contact management
- ✅ **SupplierService**: Supplier validation, active supplier tracking, contact management  
- ✅ **OrderService**: Order calculations, customer order history, order statistics
- ✅ **StockEntryService**: Stock transaction management, product stock tracking
- ✅ **UserService**: Authentication, password management, role-based access control

#### **Business Logic Endpoints**:
- 📊 **Statistics**: `/api/v1/products/stats`, `/api/v1/customers/stats`, `/api/v1/orders/stats`
- 🔍 **Search**: `/api/v1/products/search`, `/api/v1/customers/search`, `/api/v1/suppliers/search`
- 📈 **Analytics**: `/api/v1/products/low-stock`, `/api/v1/orders/by-date-range`
- 🎯 **Business Operations**: Stock reservations, order calculations, inventory management

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

#### **Authentication & User Management**:
```bash
# Register new user
curl -X POST http://localhost:${BACKEND_PORT:-8080}/auth/register \
  -H 'Content-Type: application/json' \
  -d '{
    "username": "admin1", 
    "password": "securepass123", 
    "email": "admin@company.com",
    "fullName": "System Administrator"
  }'

# Login and get JWT token
curl -X POST http://localhost:${BACKEND_PORT:-8080}/auth/login \
  -H 'Content-Type: application/json' \
  -d '{
    "username": "admin1",
    "password": "securepass123"
  }'

# Response includes JWT token
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "username": "admin1",
    "email": "admin@company.com", 
    "fullName": "System Administrator",
    "role": "USER"
  }
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

### ⚡ **Speed-Optimized Operations:**
```bash
# 🚀 FASTEST: Hot reload (~5 seconds)
make dev-hot-reload     # Configuration changes only

# 🔄 FAST: Incremental build (~30 seconds)
make dev-code-change    # Java code changes

# 🚀 MEDIUM: Quick restart (~45 seconds)  
make dev-quick-restart  # Dependency changes

# 📊 Build speed comparison and tips
make docker-optimize    # Show optimization recommendations

# ⚠️ SLOW: Full rebuild (~3-5 minutes) - Use only when necessary
make backend-rebuild    # Clean build with no-cache
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

### 🔐 Security & Supply Chain (Days 28–29)
```bash
# Build slim backend image (alpine JRE) and generate SBOM (Syft)
make backend-slim-build
make backend-sbom           # outputs backend-sbom.spdx.json

# Security scans
make security-scan-trivy    # Trivy HIGH/CRITICAL scan for backend image
make security-scan-dep      # OWASP Dependency Check (HTML report in ./odc-report)
```

## 🚀 Docker Build Optimization

### **Build Speed Comparison:**
| Command | Time | Use Case | Description |
|---------|------|----------|-------------|
| `make dev-hot-reload` | ~5 seconds | Config changes | No build, just restart |
| `make dev-code-change` | ~30 seconds | Java code changes | Incremental build |
| `make dev-quick-restart` | ~45 seconds | Dependency changes | Build + restart |
| `make backend-rebuild` | ~3-5 minutes | Cache issues | Clean build (no-cache) |

### **Optimization Tips:**
```bash
# Show detailed optimization guide
make docker-optimize

# Use appropriate command for your changes:
# - Configuration (.env, application.yml) → dev-hot-reload
# - Java code changes → dev-code-change  
# - Dependencies (build.gradle) → dev-quick-restart
# - Cache issues → backend-rebuild (only when necessary)
```

### **Why These Commands Are Faster:**
- **Docker Layer Caching**: Incremental builds reuse cached layers
- **Smart Build Strategy**: Only rebuild what changed
- **No-Cache Avoidance**: `--no-cache` forces complete rebuild
- **Targeted Operations**: Specific commands for specific changes

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

### ✅ Day 16 — CORS & Rate Limiting Configuration 🌐🚦
* **Goal:** CORS per profile; Bucket4j rate limit public endpoints.
* **Criteria:** 429 when exceeding limit; configuration via environment variables.
* **🎯 COMPLETED:** Complete CORS configuration with environment variables and Bucket4j rate limiting with bucket isolation
* **📖 [README Day 16](docs/README_day16.md)**
* **Features:**
  - Environment-based CORS configuration
  - Bucket4j rate limiting with isolated buckets per endpoint type
  - Public (100/min), API (200/min), Auth (10/min) rate limits
  - Comprehensive testing and monitoring

### ✅ Day 17 — Caching Implementation ⚡️
* **Goal:** Spring Cache (Caffeine) for GET /products, /suppliers; cache TTL via properties; @CacheEvict when write.
* **Criteria:** Cache TTL via properties; @CacheEvict when write; 2x performance improvement.
* **🎯 COMPLETED:** Complete caching system with Spring Cache + Caffeine for significant performance boost
* **📖 [README Day 17](docs/README_day17.md)**
* **Features:**
  - Spring Cache with Caffeine in-memory caching
  - ProductService and SupplierService caching
  - Cache eviction on create/update/delete operations
  - Configurable TTL and cache size via environment variables


### ✅ Day 18 — DDD Architecture & Service-Specific Commands 🏗️⚡️
* **Goal:** Implement Domain-Driven Design architecture and optimize makefile with service-specific commands.
* **Criteria:** Clean architecture with bounded contexts, optimized development workflows, efficient service management.
* **🎯 COMPLETED:** Complete DDD foundation setup with optimized makefile for efficient development
* **Features:**
  - Domain-Driven Design architecture with bounded contexts
  - Shared kernel with value objects (Money, Slug, Email)
  - Infrastructure layer separation
  - Service-specific makefile commands (75 optimized commands)
  - DDD development workflow commands
  - Legacy command aliases for backward compatibility

### ✅ Day 19 — Error catalog & mã lỗi 🚨
* **Mục tiêu:** Chuẩn hóa mã lỗi (APP-xxxx), mapping exception → mã.
* **Tiêu chí:** Tài liệu trong README/Swagger.
* **🎯 HOÀN THÀNH:** Standardized error codes (APP-XXXX) with comprehensive error handling, documentation, and testing
* **📖 [README Day 19](docs/README_day19.md)**
* **Features:**
  - **Error Code System**: 100+ standardized error codes (APP-0001-0999) with categories
  - **Exception Hierarchy**: AppException base class with domain-specific exceptions
  - **Global Handler**: Enhanced GlobalExceptionHandler with automatic HTTP status mapping
  - **Error Response**: Standardized ErrorResponse DTO with error codes, titles, and descriptions
  - **Documentation**: Complete error codes documentation with examples and testing
  - **Testing**: Comprehensive unit and integration tests for error scenarios
  - **Business Logic**: Domain-specific exceptions for Product, Customer, Order, User
  - **Validation**: Enhanced validation error handling with field-specific error codes

### ✅ Day 20 — API Versioning & Deprecation
* Added filter to forward legacy `/api/**` → `/api/v1/**` with deprecation headers.

### ✅ Day 21 — Actuator & Build Info
* Enabled actuator endpoints (health, info, metrics) and build info generation.

### ✅ Day 22 — Micrometer + Prometheus
* Exposed `/actuator/prometheus`, optional `docker-compose.observability.yml`, and Prometheus config.

### ✅ Day 23 — CSV Import/Export (Products)
* Endpoints: `POST /api/v1/products/import-csv`, `GET /api/v1/products/export-csv`.

### ✅ Day 24 — Idempotency & Optimistic Locking
* Enforce `Idempotency-Key` header for `POST /api/v1/orders`; add `@Version` to `Order`.

### ✅ Day 25 — Order Confirmation Business Rules
* `POST /api/v1/orders/{id}/confirm`: recalc `totalAmount` and write `StockEntry` outflow.

### ✅ Day 26 — OpenAPI Client Generation
* Dockerized `client-gen` and `client-test`; commit sample client at `clients/typescript-axios`.

### ✅ Day 27 — CI Workflow
* GitHub Actions: Gradle tests, Docker build, SBOM upload.

### ✅ Day 28 — Slim Image + SBOM
* Alpine JRE base image and Syft SBOM generation.

### ✅ Day 29 — Security Scans
* Trivy image scan (HIGH/CRITICAL) and OWASP Dependency Check with artifact upload.

### ✅ Day 30 — Demo Collections
* Postman/Insomnia collections and E2E demo guide in `docs/`.


## ⚡ Service-Specific Commands

### **Development Workflows**
```bash
# Full development environment
make dev-start      # Start mysql + backend + frontend
make dev-backend    # Start backend + database only
make dev-api        # Start API development (no frontend)
make dev-stop       # Stop all services
make dev-restart    # Restart all services
```

### **Speed-Optimized Development**
```bash
# ⚡ FASTEST: Hot reload (~5 seconds)
make dev-hot-reload     # Configuration changes only

# 🔄 FAST: Incremental build (~30 seconds)
make dev-code-change    # Java code changes

# 🚀 MEDIUM: Quick restart (~45 seconds)
make dev-quick-restart  # Dependency changes

# 📊 Optimization tips and speed comparison
make docker-optimize    # Show build speed recommendations
```

### **Service Management**
```bash
# Backend commands (speed-optimized)
make backend-build      # Build backend only (with cache)
make backend-quick-build # Quick build backend (incremental, fast)
make backend-rebuild    # Rebuild backend (no-cache) - SLOW
make backend-force-rebuild # Force rebuild (clean + no-cache) - VERY SLOW
make backend-start      # Start backend only
make backend-stop       # Stop backend only
make backend-restart    # Restart backend only
make backend-quick-restart # Quick restart (build + restart, fast)
make backend-dev-restart # Development restart (optimized for dev)
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

## 🏆 **Current Architecture Status**

### **✅ Completed Features:**
- 🔧 **Development UX**: Comprehensive Makefile with 30+ commands including configuration management
- ⚙️ **Centralized Configuration**: Single-file configuration management with automatic synchronization
- 🎯 **Configuration Manager**: Interactive script for easy configuration changes with backup management
- 📁 **Backup System**: Automated backup management with folder organization and cleanup
- 🛡️ **Input Validation**: Bean Validation with global error handling
- 🏗️ **DTO Architecture**: Complete separation of API contracts from domain entities
- 🚀 **MapStruct Integration**: High-performance compile-time mapping
- 💼 **Business Logic**: Domain services with comprehensive business rules and validation
- 🔐 **JWT Security**: Stateless authentication with JWT tokens, role-based access control, and protected API endpoints
- 🌐 **CORS Configuration**: Environment-based CORS with endpoint-specific rules and comprehensive validation
- 🚦 **Rate Limiting**: Bucket4j-based rate limiting with isolated buckets per endpoint type (Public/API/Auth)
- ⚡️ **Caching Layer**: Spring Cache with Caffeine for 2x performance improvement on read operations
- 📊 **APIs**: 50+ RESTful endpoints with pagination, sorting, filtering and business logic
- 📄 **Pagination**: PageResponse<T> standard with metadata, PageMapper utility
- 📖 **Documentation**: Swagger/OpenAPI with dynamic server URLs and detailed parameter descriptions
- 🛫 **Database Migrations**: Flyway-based schema management with automated migrations (V1-V7)
- 🌱 **Data Seeding**: Profile-based seeding with DataFaker, idempotent seeding, configurable quantities
- 🔗 **Slug System**: Global slug support for Products and Customers with dual access patterns
- 🚨 **Error Catalog**: Standardized error codes (APP-XXXX) with comprehensive error handling and documentation
- 🗑️ **Soft Delete & Auditing**: Complete audit trail with automatic timestamp management and soft delete functionality
- 🏗️ **Clean Architecture**: Project structure cleanup with organized packages and consistent naming
- ⚡ **Build Optimization**: Speed-optimized Docker commands with incremental builds and smart caching

### **📈 Technical Metrics:**
- **7 Domain Entities** with Lombok integration and AuditableEntity base class
- **21 DTOs** designed with use-case patterns and organized by entity
- **7 MapStruct Mappers** with relationship handling
- **1 PageMapper Utility** for pagination standardization
- **1 SlugUtil Utility** for slug generation and validation
- **1 AuditableEntity Base Class** with automatic timestamp management
- **8 Controllers** with consistent RESTful design and Swagger docs (including AuthController)
- **7 Flyway Migrations** with automated schema management and audit fields (V1-V7)
- **1 Data Seeder** with profile-based configuration and idempotent seeding
- **1 Configuration Manager Script** with interactive interface and backup management
- **1 Centralized Configuration System** with automatic synchronization across all components
- **6 Domain Services** with comprehensive business logic and validation
- **2 Security Services** (JwtTokenService, PasswordService) for authentication
- **Zero Manual Mapping** - All automated with type safety
- **Unified Pagination** - All list endpoints use PageResponse<T>
- **Dual Access Patterns** - ID and slug-based API endpoints
- **Soft Delete System** - All entities support soft delete with SQL restriction filtering
- **Complete Audit Trail** - Automatic created_at, updated_at, deleted_at management
- **Single Source of Truth** - Schema managed in Flyway migrations, Configuration in .env
- **Automated Data Seeding** - Development/test environments ready with sample data
- **Smart Backup Management** - Automated backup with folder organization and cleanup
- **Clean Project Structure** - Organized packages with consistent naming conventions
- **Speed-Optimized Build System** - 4-tier build commands (5s-5min) with smart Docker caching

**🌟 Ready for production deployment with enterprise-grade patterns!**

