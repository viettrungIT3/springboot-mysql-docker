## Overview

Dockerized Spring Boot + MySQL application with Swagger UI. The backend is built with Gradle and uses a multi-stage Dockerfile to compile the JAR inside Docker (no JDK required on the host). Database schema and seed data are initialized via `docker/init_database.sql`.

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
│  ├─ build.gradle
│  ├─ gradle/, gradlew, settings.gradle
│  └─ src/main/java/com/backend/backend/
│     ├─ BackendApplication.java
│     ├─ config/
│     │  ├─ OpenApiConfig.java
│     │  └─ SecurityConfig.java
│     ├─ entity/
│     │  ├─ Product.java, Supplier.java, Customer.java
│     │  ├─ Order.java, OrderItem.java, StockEntry.java
│     │  └─ Administrator.java
│     ├─ repository/
│     │  ├─ ProductRepository.java, SupplierRepository.java, CustomerRepository.java
│     │  ├─ OrderRepository.java, OrderItemRepository.java, StockEntryRepository.java
│     │  └─ AdministratorRepository.java
│     └─ controller/
│        ├─ ProductController.java, SupplierController.java, CustomerController.java
│        ├─ OrderController.java, OrderItemController.java, StockEntryController.java
│        └─ AdministratorController.java
├─ docker/
│  └─ init_database.sql              # creates schema and seeds sample data
├─ docker-compose.yml
├─ .env (copy from .env.example)
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

## API Overview
CRUD endpoints are available for:
- `/api/products`
- `/api/suppliers`
- `/api/customers`
- `/api/orders`
- `/api/order-items`
- `/api/stock-entries`
- `/api/administrators`

Example requests:
```bash
# List products
curl http://localhost:8080/api/products

# Create a product
curl -X POST http://localhost:8080/api/products \
  -H 'Content-Type: application/json' \
  -d '{"name":"Marker","description":"Black marker","price":2.5,"quantityInStock":50}'

# Create an order (customer/product must exist)
curl -X POST http://localhost:8080/api/orders \
  -H 'Content-Type: application/json' \
  -d '{"customer":{"id":1},"totalAmount":5.00,"items":[{"product":{"id":1},"quantity":2,"price":2.50}]}'
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

## 30-Day Roadmap 

### Day 1 — Makefile & Dev UX
  * **Goal:** `make up`, `make down`, `make logs`, `make rebuild`.
  * **Criteria:** Makefile works on macOS/Linux; README is updated.
  * [README Day 1](docs/README_day1.md)

### Day 2 — Profiles & Isolated Configuration
  * **Goal:** `application.yml` with `dev`, `test`, `prod` profiles; use environment overrides.
  * Criteria: Run dev via Docker Compose; DB configuration comes from .env.
  * [README Day 2](docs/README_day2.md)

### Day 3 - Validation & Global Error Handling
  * **Goal:** Bean Validation (JSR‑380) for incoming DTOs; @ControllerAdvice + standardized JSON errors.
  **Criteria:** 400 response with clear field errors.
  * [README Day 3](docs/README_day3.md)
