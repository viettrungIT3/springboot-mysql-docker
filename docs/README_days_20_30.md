# Days 20–30 — Tổng hợp thay đổi và hướng dẫn

## Day 20 — API Versioning & Deprecation
- Filter chuyển `/api/**` → `/api/v1/**` kèm headers Deprecation/Sunset/Link.
- Tài liệu: cập nhật README, kiểm thử bằng curl các legacy path.

## Day 21 — Actuator & Build Info
- Bật endpoints: health, info, metrics; sinh build-info.
- Kiểm tra: `/actuator/health`, `/actuator/info`, `/actuator/metrics`.

## Day 22 — Micrometer + Prometheus
- Thêm registry Prometheus, expose `/actuator/prometheus`.
- Optional stack: `docker-compose.observability.yml` + `prometheus/prometheus.yml`.
- Lệnh: `make observability-up|down|status`.

## Day 23 — CSV Import/Export (Products)
- Endpoints: `POST /api/v1/products/import-csv`, `GET /api/v1/products/export-csv`.
- Lệnh: `make product-import-csv FILE=...`, `make product-export-csv FILE=...`.

## Day 24 — Idempotency & Optimistic Locking
- Bắt buộc `Idempotency-Key` cho `POST /api/v1/orders` (Filter).
- Thêm `@Version` trong `Order` (optimistic locking).

## Day 25 — Business Rules Xác nhận Đơn
- Endpoint: `POST /api/v1/orders/{id}/confirm`.
- Hành vi: tính lại `totalAmount`, ghi `StockEntry` âm (xuất kho).

## Day 26 — OpenAPI Client Generation
- Docker hoá: `make client-gen`, `make client-test`.
- Client mẫu: `clients/typescript-axios` (không commit node_modules).

## Day 27 — CI Workflow
- GitHub Actions: Gradle tests, Docker build, upload SBOM.

## Day 28 — Slim Image + SBOM
- Runtime base: `eclipse-temurin:17-jre-alpine`.
- Lệnh: `make backend-slim-build`, `make backend-sbom` (Syft, SPDX JSON).

## Day 29 — Security Scans
- Trivy scan image (HIGH/CRITICAL) và OWASP Dependency Check (HTML artifact).
- Lệnh: `make security-scan-trivy`, `make security-scan-dep`.

## Day 30 — Demo Collections
- Postman: `docs/collections/postman_collection.json`.
- Insomnia: `docs/collections/insomnia.yaml`.
- Hướng dẫn: `docs/README_day30.md`.
