# Days 20–30 — Tổng hợp thay đổi và hướng dẫn

## 📚 Tài liệu chi tiết
Mỗi ngày đã được tách thành file riêng với hướng dẫn chi tiết:

- **[Day 20](README_day20.md)** — API Versioning & Deprecation
- **[Day 21](README_day21.md)** — Actuator & Build Info  
- **[Day 22](README_day22.md)** — Micrometer + Prometheus
- **[Day 23](README_day23.md)** — CSV Import/Export (Products)
- **[Day 24](README_day24.md)** — Idempotency & Optimistic Locking
- **[Day 25](README_day25.md)** — Business Rules Xác nhận Đơn
- **[Day 26](README_day26.md)** — OpenAPI Client Generation
- **[Day 27](README_day27.md)** — CI Workflow
- **[Day 28](README_day28.md)** — Slim Image + SBOM
- **[Day 29](README_day29.md)** — Security Scans
- **[Day 30](README_day30.md)** — Demo Collections

## 🚀 Tóm tắt nhanh

### Day 20 — API Versioning & Deprecation
- Filter chuyển `/api/**` → `/api/v1/**` kèm headers Deprecation/Sunset/Link
- **Test**: `curl -v http://localhost:8080/api/products`

### Day 21 — Actuator & Build Info
- Bật endpoints: health, info, metrics; sinh build-info
- **Test**: `curl http://localhost:8080/actuator/health`

### Day 22 — Micrometer + Prometheus
- Thêm registry Prometheus, expose `/actuator/prometheus`
- **Lệnh**: `make observability-up|down|status`

### Day 23 — CSV Import/Export (Products)
- Endpoints: `POST /api/v1/products/import-csv`, `GET /api/v1/products/export-csv`
- **Lệnh**: `make product-import-csv FILE=...`, `make product-export-csv FILE=...`

### Day 24 — Idempotency & Optimistic Locking
- Bắt buộc `Idempotency-Key` cho `POST /api/v1/orders` (Filter)
- Thêm `@Version` trong `Order` (optimistic locking)

### Day 25 — Business Rules Xác nhận Đơn
- Endpoint: `POST /api/v1/orders/{id}/confirm`
- Hành vi: tính lại `totalAmount`, ghi `StockEntry` âm (xuất kho)

### Day 26 — OpenAPI Client Generation
- Docker hoá: `make client-gen`, `make client-test`
- Client mẫu: `clients/typescript-axios` (không commit node_modules)

### Day 27 — CI Workflow
- GitHub Actions: Gradle tests, Docker build, upload SBOM

### Day 28 — Slim Image + SBOM
- Alpine JRE base image và Syft SBOM generation
- **Lệnh**: `make backend-slim-build`, `make backend-sbom`

### Day 29 — Security Scans
- Trivy image scan (HIGH/CRITICAL) và OWASP Dependency Check
- **Lệnh**: `make security-scan-trivy`, `make security-scan-dep`

### Day 30 — Demo Collections
- Postman/Insomnia collections và E2E demo guide trong `docs/`