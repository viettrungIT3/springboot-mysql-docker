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
# MySQL
MYSQL_ROOT_PASSWORD=root
MYSQL_DATABASE=appdb
MYSQL_USER=appuser
MYSQL_PASSWORD=apppass
MYSQL_PORT=3306        # exposed port on host

# Backend
BACKEND_PORT=8080      # exposed port on host

# DB connection seen by backend inside Docker network
DB_HOST=mysql
DB_PORT=3306

# JPA/Hibernate
JPA_DDL_AUTO=update
JPA_SHOW_SQL=true

# Optional: basic user for Spring Security (dev only)
# SPRING_SECURITY_USER_NAME=admin
# SPRING_SECURITY_USER_PASSWORD=admin
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
│     │  └─ SecurityConfig.java      # BCrypt password encoder
│     ├─ dto/                        # DTO-first design
│     │  ├─ common/PageResponse.java
│     │  ├─ product/                 # Use-case driven DTOs
│     │  ├─ customer/, supplier/, administrator/
│     │  ├─ order/, orderitem/, stockentry/
│     │  └─ ...CreateRequest, ...UpdateRequest, ...Response
│     ├─ entity/                     # JPA entities with Lombok
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
│     ├─ service/                    # Business logic layer
│     │  ├─ ProductService.java      # Inventory management
│     │  ├─ OrderService.java        # Complex order processing
│     │  └─ ...Service.java          # Stock updates, totals calculation
│     ├─ controller/                 # RESTful API layer
│     │  ├─ ProductController.java   # /api/v1/products
│     │  ├─ OrderController.java     # /api/v1/orders
│     │  └─ ...Controller.java       # CRUD + pagination + custom endpoints
│     └─ exception/                  # Global error handling
│        ├─ GlobalExceptionHandler.java
│        └─ ResourceNotFoundException.java
├─ docker/
│  └─ init_database.sql              # creates schema and seeds sample data
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
    - `docker/init_database.sql` mounted to `/docker-entrypoint-initdb.d/init.sql`

- backend
  - Build: `backend/Dockerfile` (multi-stage: JDK build -> JRE runtime)
  - Ports: `${BACKEND_PORT:-8080}:8080`
  - Env: `SPRING_DATASOURCE_*`, `JPA_*` (derived from `.env`)

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
**Goal:** `make up`, `make down`, `make logs`, `make rebuild`.  
**Criteria:** Makefile works on macOS/Linux; README is updated.  
**📖 [README Day 1](docs/README_day1.md)**

### ✅ Day 2 — Profiles & Isolated Configuration  
**Goal:** `application.yml` with `dev`, `test`, `prod` profiles; use environment overrides.  
**Criteria:** Run dev via Docker Compose; DB configuration comes from .env.  
**📖 [README Day 2](docs/README_day2.md)**

### ✅ Day 3 — Validation & Global Error Handling
**Goal:** Bean Validation (JSR-380) for incoming DTOs; @ControllerAdvice + standardized JSON errors.  
**Criteria:** 400 response with clear field errors.  
**📖 [README Day 3](docs/README_day3.md)**

### ✅ Day 4 — DTO + MapStruct
**Goal:** Tách DTO khỏi entity, dùng MapStruct mapper.  
**Criteria:** Controller chỉ nhận/trả DTO; mapper có test đơn giản; partial update support.  
**🎯 COMPLETED:** Enterprise-grade architecture với 21 DTOs, 7 MapStruct mappers, business logic integration  
**📖 [README Day 4](docs/README_day4.md)**

---

## 🏆 **Current Architecture Status**

### **✅ Completed Features:**
- 🔧 **Development UX**: Comprehensive Makefile với 20+ commands
- ⚙️ **Configuration Management**: Multi-profile application.yml với environment isolation  
- 🛡️ **Input Validation**: Bean Validation với global error handling
- 🏗️ **DTO Architecture**: Complete separation of API contracts from domain entities
- 🚀 **MapStruct Integration**: High-performance compile-time mapping
- 💼 **Business Logic**: Inventory management, order processing, automatic calculations
- 🔐 **Security**: BCrypt password encryption, sensitive data protection
- 📊 **APIs**: 50+ RESTful endpoints với pagination và advanced operations

### **📈 Technical Metrics:**
- **7 Domain Entities** với Lombok integration
- **21 DTOs** thiết kế theo use-case patterns
- **7 MapStruct Mappers** với relationship handling
- **14 Controllers** với consistent RESTful design
- **Zero Manual Mapping** - Tất cả automated với type safety

**🌟 Ready for production deployment với enterprise-grade patterns!**

