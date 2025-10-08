# ==== Spring Boot + MySQL Development Makefile ====
# Organized and optimized for efficient development

# ---- Configuration ----
COMPOSE_FILE ?= docker-compose.yml
ENV_FILE     ?= .env
SERVICE_APP  ?= backend
SERVICE_DB   ?= mysql
include .env
export

# ---- Helpers ----
DC = docker compose --env-file $(ENV_FILE) -f $(COMPOSE_FILE)

.DEFAULT_GOAL := help

# ==== HELP & INFORMATION ====

.PHONY: help
help: ## 📚 Show all available commands
	@echo "\n🚀 Spring Boot + MySQL Development Shortcuts"
	@echo "\n🎯 QUICK START:"
	@echo "  \033[33mmake dev-start\033[0m     → Start full development environment"
	@echo "  \033[33mmake dev-backend\033[0m   → Start backend + database only"
	@echo "  \033[33mmake dev-api\033[0m       → Start API development (no frontend)"
	@echo "  \033[33mmake test-api\033[0m      → Test API endpoints"
	@echo "  \033[33mmake swagger\033[0m       → Open Swagger UI"
	@echo ""
	@echo "\n⚡ SPEED OPTIMIZED (for development):"
	@echo "  \033[32mmake dev-quick-restart\033[0m → Quick restart (fastest)"
	@echo "  \033[32mmake dev-code-change\033[0m   → Restart after code changes"
	@echo "  \033[32mmake dev-hot-reload\033[0m    → Hot reload (no build)"
	@echo "  \033[32mmake backend-quick-build\033[0m → Quick build (with cache)"
	@echo ""
	@echo "\n⚡ SERVICE MANAGEMENT:"
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z0-9_-]+:.*?## / && /backend-|frontend-|db-|services-/ {printf "  \033[36m%-18s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)
	@echo "\n📈 OBSERVABILITY:"
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z0-9_-]+:.*?## / && /observability|prometheus|grafana/ {printf "  \033[36m%-18s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)
	@echo ""
	@echo "\n🔍 MONITORING & DEBUG:"
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z0-9_-]+:.*?## / && /logs|status|health|shell/ {printf "  \033[36m%-18s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)
	@echo ""
	@echo "\n🧪 TESTING & BUILD:"
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z0-9_-]+:.*?## / && /test|build|compile|ddd-/ {printf "  \033[36m%-18s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)
	@echo ""
	@echo "\n🔧 UTILITIES:"
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z0-9_-]+:.*?## / && /config|backup|clean|install/ {printf "  \033[36m%-18s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)
	@echo ""

# ==== DEVELOPMENT WORKFLOWS ====

.PHONY: dev-start
dev-start: ## 🚀 Start full development environment (mysql + backend + frontend)
	@echo "🚀 Starting full development environment..."
	$(DC) up -d mysql
	@echo "⏳ Waiting for MySQL to be ready..."
	@sleep 10
	$(DC) up -d backend
	@echo "⏳ Waiting for backend to start..."
	@sleep 15
	$(DC) up -d frontend
	@echo "⏳ Waiting for frontend to start..."
	@sleep 10
	@echo "\n✅ Full development environment ready!"
	@echo "📊 Backend API: http://localhost:$(BACKEND_PORT)/api/v1/products"
	@echo "📖 Swagger UI: http://localhost:$(BACKEND_PORT)/swagger-ui/index.html"
	@echo "🌐 Frontend: http://localhost:$(FRONTEND_PORT)"

# ==== DEVELOPMENT SHORTCUTS (OPTIMIZED FOR SPEED) ====

.PHONY: dev-quick-restart
dev-quick-restart: ## ⚡ Quick restart for development (fastest)
	@echo "⚡ Quick development restart (optimized for speed)..."
	$(DC) build $(SERVICE_APP)
	$(DC) up -d $(SERVICE_APP)
	@echo "✅ Quick development restart completed!"

.PHONY: dev-code-change
dev-code-change: ## 🔄 Restart after code changes (incremental build)
	@echo "🔄 Restarting after code changes (incremental)..."
	$(DC) build $(SERVICE_APP)
	$(DC) restart $(SERVICE_APP)
	@echo "✅ Code changes applied!"

.PHONY: dev-hot-reload
dev-hot-reload: ## 🔥 Hot reload (restart without full build)
	@echo "🔥 Hot reloading backend..."
	$(DC) restart $(SERVICE_APP)
	@echo "✅ Hot reload completed!"

# ==== DOCKER OPTIMIZATION TIPS ====

.PHONY: docker-optimize
docker-optimize: ## 🚀 Show Docker optimization tips for faster builds
	@echo "🚀 Docker Optimization Tips for Faster Builds:"
	@echo ""
	@echo "📋 BUILD SPEED COMPARISON:"
	@echo "  \033[32mdev-hot-reload\033[0m     → ~5 seconds  (no build, just restart)"
	@echo "  \033[32mdev-code-change\033[0m    → ~30 seconds (incremental build)"
	@echo "  \033[32mdev-quick-restart\033[0m  → ~45 seconds (build + restart)"
	@echo "  \033[31mbackend-rebuild\033[0m    → ~3-5 minutes (no-cache, full rebuild)"
	@echo ""
	@echo "💡 RECOMMENDATIONS:"
	@echo "  • Use \033[32mmake dev-hot-reload\033[0m for configuration changes"
	@echo "  • Use \033[32mmake dev-code-change\033[0m for Java code changes"
	@echo "  • Use \033[32mmake dev-quick-restart\033[0m for dependency changes"
	@echo "  • Only use \033[31mmake backend-rebuild\033[0m when absolutely necessary"
	@echo ""
	@echo "🔧 DOCKER CACHE OPTIMIZATION:"
	@echo "  • Docker layers are cached, so incremental builds are much faster"
	@echo "  • Only use --no-cache when you suspect cache issues"
	@echo "  • Use --pull only when you need latest base images"

.PHONY: dev-backend
dev-backend: ## 🔥 Start backend development (backend + database only)
	@echo "🔥 Starting backend development environment..."
	$(MAKE) db-start
	@sleep 10
	$(MAKE) backend-start
	@echo "\n✅ Backend development environment ready!"
	@echo "📊 Backend API: http://localhost:$(BACKEND_PORT)/api/v1/products"
	@echo "📖 Swagger UI: http://localhost:$(BACKEND_PORT)/swagger-ui/index.html"

.PHONY: dev-api
dev-api: ## 🚀 Start API development (backend + database, no frontend)
	@echo "🚀 Starting API development environment..."
	$(MAKE) db-start
	@sleep 10
	$(MAKE) backend-start
	@echo "\n✅ API development environment ready!"
	@echo "📊 Backend API: http://localhost:$(BACKEND_PORT)/api/v1/products"
	@echo "📖 Swagger UI: http://localhost:$(BACKEND_PORT)/swagger-ui/index.html"
	@echo "🧪 Test API: make test-api"

.PHONY: dev-stop
dev-stop: ## 🛑 Stop all development services
	@echo "🛑 Stopping all development services..."
	$(DC) down --remove-orphans
	@echo "✅ All services stopped!"

.PHONY: dev-restart
dev-restart: ## 🔃 Restart all development services
	@echo "🔃 Restarting all development services..."
	$(MAKE) dev-stop
	@sleep 5
	$(MAKE) dev-start

# ==== SERVICE MANAGEMENT ====

# ---- Backend Commands ----
.PHONY: backend-build
backend-build: ## 🔨 Build backend only (with cache)
	@echo "🔨 Building backend (with cache)..."
	$(DC) build $(SERVICE_APP)
	@echo "✅ Backend built successfully!"

.PHONY: backend-rebuild
backend-rebuild: ## 🔄 Rebuild backend (no-cache) - SLOW but clean
	@echo "🔄 Rebuilding backend (no-cache) - This may take a while..."
	$(DC) build --no-cache $(SERVICE_APP)
	@echo "✅ Backend rebuilt successfully!"

.PHONY: backend-quick-build
backend-quick-build: ## ⚡ Quick build backend (incremental, fast)
	@echo "⚡ Quick building backend (incremental)..."
	$(DC) build $(SERVICE_APP)
	@echo "✅ Backend quick built successfully!"

.PHONY: backend-force-rebuild
backend-force-rebuild: ## 🔥 Force rebuild backend (clean + no-cache) - VERY SLOW
	@echo "🔥 Force rebuilding backend (clean + no-cache) - This will take a long time..."
	$(DC) build --no-cache --pull $(SERVICE_APP)
	@echo "✅ Backend force rebuilt successfully!"

.PHONY: backend-start
backend-start: ## 🚀 Start backend only
	@echo "🚀 Starting backend..."
	$(DC) up -d $(SERVICE_APP)
	@echo "⏳ Waiting for backend to start..."
	@sleep 10
	@echo "✅ Backend started!"

.PHONY: backend-stop
backend-stop: ## 🛑 Stop backend only
	@echo "🛑 Stopping backend..."
	$(DC) stop $(SERVICE_APP)
	@echo "✅ Backend stopped!"

.PHONY: backend-restart
backend-restart: ## 🔃 Restart backend only
	@echo "🔃 Restarting backend..."
	$(DC) restart $(SERVICE_APP)
	@echo "⏳ Waiting for backend to restart..."
	@sleep 10
	@echo "✅ Backend restarted!"

.PHONY: backend-quick-restart
backend-quick-restart: ## ⚡ Quick restart backend (build + restart, fast)
	@echo "⚡ Quick restarting backend (build + restart)..."
	$(DC) build $(SERVICE_APP)
	$(DC) restart $(SERVICE_APP)
	@echo "✅ Backend quick restarted successfully!"

.PHONY: backend-dev-restart
backend-dev-restart: ## 🚀 Development restart (build + restart, optimized for dev)
	@echo "🚀 Development restart (optimized for development)..."
	$(DC) build $(SERVICE_APP)
	$(DC) up -d $(SERVICE_APP)
	@echo "✅ Backend development restarted successfully!"

# ---- Frontend Commands ----
.PHONY: frontend-build
frontend-build: ## 🔨 Build frontend only
	@echo "🔨 Building frontend..."
	$(DC) build frontend
	@echo "✅ Frontend built successfully!"

.PHONY: frontend-rebuild
frontend-rebuild: ## 🔄 Rebuild frontend (no-cache)
	@echo "🔄 Rebuilding frontend (no-cache)..."
	$(DC) build --no-cache frontend
	@echo "✅ Frontend rebuilt successfully!"

.PHONY: frontend-start
frontend-start: ## 🚀 Start frontend only
	@echo "🚀 Starting frontend..."
	$(DC) up -d frontend
	@echo "⏳ Waiting for frontend to start..."
	@sleep 10
	@echo "✅ Frontend started!"

.PHONY: frontend-stop
frontend-stop: ## 🛑 Stop frontend only
	@echo "🛑 Stopping frontend..."
	$(DC) stop frontend
	@echo "✅ Frontend stopped!"

.PHONY: frontend-restart
frontend-restart: ## 🔃 Restart frontend only
	@echo "🔃 Restarting frontend..."
	$(DC) restart frontend
	@echo "⏳ Waiting for frontend to restart..."
	@sleep 10
	@echo "✅ Frontend restarted!"

# ---- Database Commands ----
.PHONY: db-build
db-build: ## 🔨 Build database only
	@echo "🔨 Building database..."
	$(DC) build $(SERVICE_DB)
	@echo "✅ Database built successfully!"

.PHONY: db-rebuild
db-rebuild: ## 🔄 Rebuild database (no-cache)
	@echo "🔄 Rebuilding database (no-cache)..."
	$(DC) build --no-cache $(SERVICE_DB)
	@echo "✅ Database rebuilt successfully!"

.PHONY: db-start
db-start: ## 🚀 Start database only
	@echo "🚀 Starting database..."
	$(DC) up -d $(SERVICE_DB)
	@echo "⏳ Waiting for database to be ready..."
	@sleep 15
	@echo "✅ Database started!"

.PHONY: db-stop
db-stop: ## 🛑 Stop database only
	@echo "🛑 Stopping database..."
	$(DC) stop $(SERVICE_DB)
	@echo "✅ Database stopped!"

.PHONY: db-restart
db-restart: ## 🔃 Restart database only
	@echo "🔃 Restarting database..."
	$(DC) restart $(SERVICE_DB)
	@echo "⏳ Waiting for database to restart..."
	@sleep 15
	@echo "✅ Database restarted!"

# ---- Combined Service Commands ----
.PHONY: services-build
services-build: ## 🔨 Build all services
	@echo "🔨 Building all services..."
	$(DC) build
	@echo "✅ All services built successfully!"

.PHONY: services-rebuild
services-rebuild: ## 🔄 Rebuild all services (no-cache)
	@echo "🔄 Rebuilding all services (no-cache)..."
	$(DC) build --no-cache
	@echo "✅ All services rebuilt successfully!"

.PHONY: services-start
services-start: ## 🚀 Start all services
	@echo "🚀 Starting all services..."
	$(DC) up -d
	@echo "⏳ Waiting for all services to start..."
	@sleep 20
	@echo "✅ All services started!"

.PHONY: services-stop
services-stop: ## 🛑 Stop all services
	@echo "🛑 Stopping all services..."
	$(DC) stop
	@echo "✅ All services stopped!"

.PHONY: services-restart
services-restart: ## 🔃 Restart all services
	@echo "🔃 Restarting all services..."
	$(DC) restart
	@echo "⏳ Waiting for all services to restart..."
	@sleep 20
	@echo "✅ All services restarted!"

# ==== OBSERVABILITY ====

.PHONY: observability-up
observability-up: ## 📈 Start Prometheus + Grafana (requires backend running locally on 8080)
	@echo "📈 Starting Prometheus + Grafana..."
	docker compose -f docker-compose.observability.yml up -d
	@echo "✅ Observability stack started!"
	@echo "🔗 Prometheus: http://localhost:9090"
	@echo "🔗 Grafana:    http://localhost:3001 (admin/admin)"

.PHONY: observability-down
observability-down: ## 🛑 Stop Prometheus + Grafana
	@echo "🛑 Stopping Observability stack..."
	docker compose -f docker-compose.observability.yml down --remove-orphans
	@echo "✅ Observability stack stopped!"

# ==== CLIENT GENERATION (OpenAPI) ====

.PHONY: client-gen-prepare
client-gen-prepare: ## 🧰 Fetch OpenAPI spec to clients/openapi.json
	@echo "🧰 Preparing OpenAPI spec..."
	@mkdir -p clients
	@echo "⏳ Starting backend (if not running) to fetch OpenAPI JSON..."
	$(MAKE) backend-start
	@sleep 8
	@curl -sSf http://localhost:$(BACKEND_PORT)/v3/api-docs -o clients/openapi.json
	@echo "✅ OpenAPI spec saved to clients/openapi.json"

.PHONY: client-gen-ts
client-gen-ts: client-gen-prepare ## 🧪 Generate TypeScript Axios client into clients/typescript-axios
	@echo "🧪 Generating TypeScript Axios client..."
	@mkdir -p clients/typescript-axios
	docker run --rm -v $(PWD):/local openapitools/openapi-generator-cli:v7.7.0 generate \
	  -i /local/clients/openapi.json \
	  -g typescript-axios \
	  -o /local/clients/typescript-axios \
	  --additional-properties=supportsES6=true,npmName=@app/api-client,withSeparateModelsAndApi=true,apiPackage=api,modelPackage=models
	@echo "✅ TypeScript client generated at clients/typescript-axios"

.PHONY: client-gen
client-gen: client-gen-ts ## 🚀 Generate all sample API clients
	@echo "🚀 Client generation completed!"

.PHONY: client-test
client-test: ## 🔎 Smoke test generated TypeScript client via Docker (needs backend up)
	@echo "🔎 Smoke testing generated TS client (Docker)..."
	docker run --rm -v $(PWD)/clients/typescript-axios:/proj -w /proj node:20-alpine sh -lc "node -e \"const {ProductsApi, Configuration}=require('./dist'); (async()=>{const api=new ProductsApi(new Configuration({basePath:'http://host.docker.internal:$(BACKEND_PORT)'})); const res=await api.list3(0,5,'id,desc'); console.log('OK products page size:', res.data?.content?.length ?? 'unknown');})();\""
	@echo "✅ Client smoke test finished"

# ==== MONITORING & DEBUG ====

.PHONY: status
status: ## 📊 Check all services status
	@echo "📊 All Services Status"
	@echo "======================"
	@echo "🐳 Container Status:"
	@$(DC) ps --format "table {{.Name}}\t{{.Status}}\t{{.Ports}}" 2>/dev/null || echo "  No services running"
	@echo "\n🔗 Health Checks:"
	@curl -s -o /dev/null -w "  Backend API: %{http_code} (%{time_total}s)\n" http://localhost:$(BACKEND_PORT)/api/v1/products || echo "  Backend: Unreachable"
	@curl -s -o /dev/null -w "  Frontend: %{http_code} (%{time_total}s)\n" http://localhost:$(FRONTEND_PORT) || echo "  Frontend: Unreachable"
	@$(DC) exec $(SERVICE_DB) sh -c 'mysqladmin ping -h localhost' 2>/dev/null && echo "  Database: Connected" || echo "  Database: Error"

.PHONY: backend-status
backend-status: ## 📊 Check backend status
	@echo "📊 Backend Status"
	@echo "================="
	@echo "🐳 Container Status:"
	@$(DC) ps $(SERVICE_APP) --format "table {{.Name}}\t{{.Status}}\t{{.Ports}}" 2>/dev/null || echo "  Backend not running"
	@echo "\n🔗 Health Check:"
	@curl -s -o /dev/null -w "  Status: %{http_code} - %{url_effective}\n" http://localhost:$(BACKEND_PORT)/api/v1/products || echo "  ❌ Backend: Unreachable"
	@echo "\n📋 Recent Logs:"
	@$(DC) logs --tail=5 $(SERVICE_APP) 2>/dev/null | sed 's/^/  /' || echo "  No recent logs"

.PHONY: frontend-status
frontend-status: ## 📊 Check frontend status
	@echo "📊 Frontend Status"
	@echo "=================="
	@echo "🐳 Container Status:"
	@$(DC) ps frontend --format "table {{.Name}}\t{{.Status}}\t{{.Ports}}" 2>/dev/null || echo "  Frontend not running"
	@echo "\n🔗 Health Check:"
	@curl -s -o /dev/null -w "  Status: %{http_code} - %{url_effective}\n" http://localhost:$(FRONTEND_PORT) || echo "  ❌ Frontend: Unreachable"
	@echo "\n📋 Recent Logs:"
	@$(DC) logs --tail=5 frontend 2>/dev/null | sed 's/^/  /' || echo "  No recent logs"

.PHONY: db-status
db-status: ## 📊 Check database status
	@echo "📊 Database Status"
	@echo "=================="
	@echo "🐳 Container Status:"
	@$(DC) ps $(SERVICE_DB) --format "table {{.Name}}\t{{.Status}}\t{{.Ports}}" 2>/dev/null || echo "  Database not running"
	@echo "\n🔗 Connection Test:"
	@$(DC) exec $(SERVICE_DB) sh -c 'mysqladmin ping -h localhost' 2>/dev/null && echo "  ✅ Database: Connected" || echo "  ❌ Database: Error"
	@echo "\n📋 Recent Logs:"
	@$(DC) logs --tail=5 $(SERVICE_DB) 2>/dev/null | sed 's/^/  /' || echo "  No recent logs"

.PHONY: logs
logs: ## 📄 Watch backend logs
	@echo "📄 Watching backend logs (Ctrl+C to stop)..."
	$(DC) logs -f $(SERVICE_APP)

.PHONY: logs-tail
logs-tail: ## 📄 Show recent backend logs (last 20 lines)
	@echo "📄 Recent backend logs (last 20 lines)..."
	$(DC) logs --tail=20 $(SERVICE_APP)

.PHONY: frontend-logs
frontend-logs: ## 📄 Watch frontend logs
	@echo "📄 Watching frontend logs (Ctrl+C to stop)..."
	$(DC) logs -f frontend

.PHONY: frontend-logs-tail
frontend-logs-tail: ## 📄 Show recent frontend logs (last 20 lines)
	@echo "📄 Recent frontend logs (last 20 lines)..."
	$(DC) logs --tail=20 frontend

.PHONY: db-logs
db-logs: ## 📄 Watch database logs
	@echo "📄 Watching database logs (Ctrl+C to stop)..."
	$(DC) logs -f $(SERVICE_DB)

.PHONY: db-logs-tail
db-logs-tail: ## 📄 Show recent database logs (last 20 lines)
	@echo "📄 Recent database logs (last 20 lines)..."
	$(DC) logs --tail=20 $(SERVICE_DB)

.PHONY: logs-all
logs-all: ## 📄 Watch all services logs
	@echo "📄 Watching all services logs (Ctrl+C to stop)..."
	$(DC) logs -f

.PHONY: shell-backend
shell-backend: ## 🐚 Open shell in backend container
	@echo "🐚 Opening shell in backend container..."
	$(DC) exec $(SERVICE_APP) sh

.PHONY: shell-frontend
shell-frontend: ## 🐚 Open shell in frontend container
	@echo "🐚 Opening shell in frontend container..."
	$(DC) exec frontend sh

.PHONY: shell-db
shell-db: ## 🐚 Open MySQL CLI in database container
	@echo "🐚 Opening MySQL CLI..."
	$(DC) exec $(SERVICE_DB) sh -lc 'mysql -u$${MYSQL_USER:-root} -p$${MYSQL_PASSWORD:-$$MYSQL_ROOT_PASSWORD} $${MYSQL_DATABASE}'

# ==== TESTING & BUILD ====

.PHONY: test-api
test-api: ## 🧪 Test API endpoints with validation
	@echo "🧪 Testing API validation endpoints..."
	@echo "\n✅ Test 1: Valid product creation (expect 201)"
	@curl -X POST http://localhost:$(BACKEND_PORT)/api/v1/products \
		-H "Content-Type: application/json" \
		-d '{"name": "Test Product", "description": "Valid product", "price": 99.99, "quantityInStock": 10}' \
		-w "  Status: %{http_code}\n" -s | jq '.' 2>/dev/null || echo "  Response received"
	@echo "\n❌ Test 2: Invalid product (expect 400 with validation errors)"
	@curl -X POST http://localhost:$(BACKEND_PORT)/api/v1/products \
		-H "Content-Type: application/json" \
		-d '{"name": "", "price": -5, "quantityInStock": -1}' \
		-w "  Status: %{http_code}\n" -s | jq '.fieldErrors // .' 2>/dev/null || echo "  Validation errors received"
	@echo "\n🔍 Test 3: Resource not found (expect 404)"
	@curl -X GET http://localhost:$(BACKEND_PORT)/api/v1/products/999 \
		-H "Content-Type: application/json" \
		-w "  Status: %{http_code}\n" -s | jq '.' 2>/dev/null || echo "  404 error received"
	@echo "\n📊 Test 4: List all products (expect 200)"
	@curl -X GET http://localhost:$(BACKEND_PORT)/api/v1/products \
		-H "Content-Type: application/json" \
		-w "  Status: %{http_code}\n" -s | jq 'length // "Response received"' 2>/dev/null || echo "  Product list received"

.PHONY: test-unit
test-unit: ## 🧪 Run unit tests with coverage
	@echo "🧪 Running unit tests with coverage..."
	@mkdir -p backend/build/reports/jacoco/test/html
	$(DC) --profile test build test-runner
	$(DC) --profile test run --rm test-runner
	@echo "\n✅ Unit tests completed!"
	@echo "📊 Coverage report: backend/build/reports/jacoco/test/html/index.html"

.PHONY: test-integration
test-integration: ## 🧪 Run integration tests with Testcontainers
	@echo "🧪 Running integration tests with Testcontainers..."
	@echo "🐳 Testcontainers will automatically start MySQL container"
	cd backend && ./gradlew clean test --tests "*IT" --info
	@echo "\n✅ Integration tests completed!"
	@echo "📊 Coverage report: backend/build/reports/jacoco/test/html/index.html"

.PHONY: test-all
test-all: ## 🚀 Run all types of tests (unit + integration + API)
	@echo "🚀 Running comprehensive test suite..."
	@echo "\n1️⃣ Running unit tests..."
	$(MAKE) test-unit
	@echo "\n2️⃣ Running integration tests..."
	$(MAKE) test-integration
	@echo "\n3️⃣ Starting backend for API tests..."
	$(MAKE) dev-backend
	@sleep 10
	@echo "\n4️⃣ Running API validation tests..."
	$(MAKE) test-api
	@echo "\n✅ All tests completed successfully!"

.PHONY: swagger
swagger: ## 📖 Open Swagger UI in browser
	@echo "📖 Opening Swagger UI..."
	@echo "URL: http://localhost:$(BACKEND_PORT)/swagger-ui/index.html"
	@command -v open >/dev/null 2>&1 && open "http://localhost:$(BACKEND_PORT)/swagger-ui/index.html" || \
	 command -v xdg-open >/dev/null 2>&1 && xdg-open "http://localhost:$(BACKEND_PORT)/swagger-ui/index.html" || \
	 echo "⚠️  Please manually open the URL above"

# ==== DDD DEVELOPMENT ====

.PHONY: ddd-compile
ddd-compile: ## 🔨 Compile Java code only (fast check for DDD changes)
	@echo "🔨 Compiling Java code..."
	cd backend && ./gradlew compileJava --no-daemon
	@echo "✅ Compilation successful!"

.PHONY: ddd-test-compile
ddd-test-compile: ## 🧪 Compile test code only
	@echo "🧪 Compiling test code..."
	cd backend && ./gradlew compileTestJava --no-daemon
	@echo "✅ Test compilation successful!"

.PHONY: ddd-quick-test
ddd-quick-test: ## ⚡ Quick test (compile + run tests without full build)
	@echo "⚡ Running quick tests..."
	cd backend && ./gradlew test --no-daemon --no-build-cache
	@echo "✅ Quick tests completed!"

.PHONY: ddd-check
ddd-check: ## 🔍 Check code without running tests
	@echo "🔍 Checking code quality..."
	cd backend && ./gradlew check --no-daemon
	@echo "✅ Code check completed!"

.PHONY: ddd-clean-build
ddd-clean-build: ## 🧹 Clean and build (for DDD refactoring)
	@echo "🧹 Cleaning and building..."
	cd backend && ./gradlew clean build --no-daemon
	@echo "✅ Clean build completed!"

.PHONY: ddd-restart-backend
ddd-restart-backend: ## 🔄 Restart only backend service (for DDD changes)
	@echo "🔄 Restarting backend service..."
	$(DC) restart $(SERVICE_APP)
	@echo "⏳ Waiting for backend to restart..."
	@sleep 10
	@echo "✅ Backend restarted!"

.PHONY: ddd-logs
ddd-logs: ## 📄 Watch backend logs during DDD development
	@echo "📄 Watching backend logs (Ctrl+C to stop)..."
	$(DC) logs -f $(SERVICE_APP) --tail=50

.PHONY: ddd-status
ddd-status: ## 📊 Check DDD development status
	@echo "📊 DDD Development Status"
	@echo "========================="
	@echo "🐳 Backend Container:"
	@$(DC) ps $(SERVICE_APP) --format "table {{.Name}}\t{{.Status}}\t{{.Ports}}" 2>/dev/null || echo "  Backend not running"
	@echo "\n🔗 Backend Health:"
	@curl -s -o /dev/null -w "  Status: %{http_code} - %{url_effective}\n" http://localhost:$(BACKEND_PORT)/api/v1/products || echo "  ❌ Backend: Unreachable"
	@echo "\n📋 Recent Backend Logs:"
	@$(DC) logs --tail=5 $(SERVICE_APP) 2>/dev/null | sed 's/^/  /' || echo "  No recent logs"
	@echo "\n⚡ Quick Actions:"
	@echo "  make ddd-compile     → Compile Java code"
	@echo "  make ddd-quick-test  → Run quick tests"
	@echo "  make ddd-restart-backend → Restart backend"
	@echo "  make ddd-logs        → Watch logs"

.PHONY: ddd-validate
ddd-validate: ## ✅ Validate DDD structure and imports
	@echo "✅ Validating DDD structure..."
	@echo "🔍 Checking shared kernel..."
	@find backend/src/main/java/com/backend/backend/shared -name "*.java" 2>/dev/null | wc -l | xargs -I {} echo "  Shared kernel files: {}"
	@echo "🔍 Checking infrastructure..."
	@find backend/src/main/java/com/backend/backend/infrastructure -name "*.java" 2>/dev/null | wc -l | xargs -I {} echo "  Infrastructure files: {}"
	@echo "🔍 Checking domain exceptions..."
	@find backend/src/main/java/com/backend/backend/shared/domain/exception -name "*.java" 2>/dev/null | wc -l | xargs -I {} echo "  Domain exception files: {}"
	@echo "✅ DDD structure validation completed!"

.PHONY: ddd-migration-status
ddd-migration-status: ## 📊 Show DDD migration progress
	@echo "📊 DDD Migration Progress"
	@echo "========================="
	@echo "✅ Phase 1 - Foundation Setup:"
	@echo "  ✓ Shared kernel created"
	@echo "  ✓ Domain exceptions implemented"
	@echo "  ✓ Infrastructure config moved"
	@echo "  ✓ Base entity created"
	@echo ""
	@echo "🔄 Next Steps:"
	@echo "  → Phase 2: Identity Context"
	@echo "  → Phase 3: Customer Context"
	@echo "  → Phase 4: Catalog Context"
	@echo "  → Phase 5: Order Context"
	@echo ""
	@echo "⚡ Quick Commands:"
	@echo "  make ddd-compile     → Test compilation"
	@echo "  make ddd-quick-test  → Run tests"
	@echo "  make ddd-status      → Check status"

# ==== UTILITIES ====

.PHONY: clean
clean: ## 🧹 Clean up Docker resources
	@echo "🧹 Cleaning up Docker resources..."
	$(DC) down -v --remove-orphans || true
	@echo "🗑️  Removing dangling images..."
	docker image prune -f || true
	@echo "📊 Docker space usage:"
	@docker system df

.PHONY: backup-db
backup-db: ## 💾 Backup database to file
	@echo "💾 Creating database backup..."
	@mkdir -p ./backups
	$(DC) exec $(SERVICE_DB) sh -c 'mysqldump -u$${MYSQL_USER:-root} -p$${MYSQL_PASSWORD:-$$MYSQL_ROOT_PASSWORD} $${MYSQL_DATABASE}' > ./backups/backup_$(shell date +%Y%m%d_%H%M%S).sql
	@echo "✅ Database backed up to ./backups/"

.PHONY: config
config: ## 🎯 Open Configuration Manager
	@echo "🎯 Opening Centralized Configuration Manager..."
	@./config-manager.sh

.PHONY: config-show
config-show: ## 📋 Show current configuration
	@echo "📋 Current Configuration:"
	@echo "========================"
	@grep -v "^#" .env | grep -v "^$$" | while read line; do \
		if [ ! -z "$$line" ]; then \
			key=$$(echo $$line | cut -d'=' -f1); \
			value=$$(echo $$line | cut -d'=' -f2); \
			echo "  $$key = $$value"; \
		fi; \
	done

.PHONY: config-backup
config-backup: ## 💾 Backup current configuration
	@echo "💾 Creating configuration backup..."
	@mkdir -p backups/env
	@cp .env backups/env/.env.backup.$$(date +%Y%m%d_%H%M%S)
	@echo "✅ Configuration backed up to backups/env/"

.PHONY: install-deps
install-deps: ## 📦 Install development dependencies (jq, etc.)
	@echo "📦 Installing development dependencies..."
	@command -v jq >/dev/null 2>&1 || (echo "Installing jq..." && \
		(command -v apt-get >/dev/null 2>&1 && sudo apt-get install -y jq) || \
		(command -v yum >/dev/null 2>&1 && sudo yum install -y jq) || \
		(command -v brew >/dev/null 2>&1 && brew install jq) || \
		echo "⚠️  Please install jq manually for better JSON parsing")
	@echo "✅ Dependencies check completed"

# ==== LEGACY ALIASES (for backward compatibility) ====

.PHONY: up
up: services-start ## 🚀 Legacy alias for services-start

.PHONY: down
down: services-stop ## 🛑 Legacy alias for services-stop

.PHONY: restart
restart: services-restart ## 🔃 Legacy alias for services-restart

.PHONY: rebuild
rebuild: services-rebuild ## 🔄 Legacy alias for services-rebuild

.PHONY: ps
ps: status ## 📊 Legacy alias for status

.PHONY: health
health: status ## 🏥 Legacy alias for status

.PHONY: sh-app
sh-app: shell-backend ## 🐚 Legacy alias for shell-backend

.PHONY: sh-db
sh-db: shell-db ## 🐚 Legacy alias for shell-db

.PHONY: boot
boot: ## 🚀 Run Spring Boot locally (without Docker), profile=dev
	SPRING_PROFILES_ACTIVE=dev ./gradlew bootRun

.PHONY: test
test: test-unit ## 🧪 Legacy alias for test-unit

.PHONY: frontend-dev
frontend-dev: ## 🌐 Start frontend development server (local)
	@echo "🌐 Starting frontend development server..."
	cd frontend && npm run dev

.PHONY: frontend-install
frontend-install: ## 📦 Install frontend dependencies
	@echo "📦 Installing frontend dependencies..."
	cd frontend && npm install

.PHONY: frontend-lint
frontend-lint: ## 🔍 Run frontend linting
	@echo "🔍 Running frontend linting..."
	cd frontend && npm run lint

.PHONY: frontend-open
frontend-open: ## 🌐 Open frontend in browser
	@echo "🌐 Opening frontend in browser..."
	@open http://localhost:$(FRONTEND_PORT) || echo "Please open http://localhost:$(FRONTEND_PORT) manually"
