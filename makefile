# ==== Project shortcuts (Day 1) ====
# Usage: make <target>
# Ex: make up, make logs, make rebuild, make sh-db, make boot

# ---- Config ----
COMPOSE_FILE ?= docker-compose.yml
ENV_FILE     ?= .env
SERVICE_APP  ?= backend
SERVICE_DB   ?= mysql
BACKEND_PORT ?= 8080
# ---- End config ----

# ---- Helpers ----
DC = docker compose --env-file $(ENV_FILE) -f $(COMPOSE_FILE)

.DEFAULT_GOAL := help

.PHONY: help
help: ## 📚 Hiển thị danh sách lệnh hữu ích
	@echo "\n🚀 Spring Boot + MySQL Development Shortcuts"
	@echo "\n📦 CONTAINER LIFECYCLE:"
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z0-9_-]+:.*?## / && /up|down|restart|dev-/ {printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)
	@echo "\n🔍 MONITORING & DEBUG:"
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z0-9_-]+:.*?## / && /logs|ps|health|sh-/ {printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)
	@echo "\n🔨 BUILD & TEST:"
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z0-9_-]+:.*?## / && /rebuild|boot|test|clean|unit-/ {printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)
	@echo "\n📖 DOCUMENTATION & API:"
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z0-9_-]+:.*?## / && /swagger|db-/ {printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)
	@echo "\n🎯 CONFIGURATION MANAGEMENT:"
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z0-9_-]+:.*?## / && /config/ {printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)
	@echo "\n🔐 JWT SECURITY:"
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z0-9_-]+:.*?## / && /jwt|admin|security/ {printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)
	@echo "\n🎯 QUICK START:"
	@echo "  \033[33mmake dev-start\033[0m     → Start development environment"
	@echo "  \033[33mmake test-api\033[0m      → Test API with JWT authentication"
	@echo "  \033[33mmake swagger\033[0m       → Open Swagger UI"
	@echo "  \033[33mmake create-admin-user\033[0m → Create default admin user"
	@echo ""

.PHONY: up
up: ## 🚀 Bật stack (detached)
	@echo "🚀 Starting Docker stack..."
	$(DC) up -d
	@echo "✅ Stack started successfully!"

.PHONY: dev-start
dev-start: ## 🔥 Start development environment (mysql + backend)
	@echo "🔥 Starting development environment..."
	$(DC) up -d mysql
	@echo "⏳ Waiting for MySQL to be ready..."
	@sleep 10
	$(DC) up -d backend
	@echo "⏳ Waiting for backend to start..."
	@sleep 15
	@echo "\n✅ Development environment ready!"
	@echo "📊 Backend API: http://localhost:$(BACKEND_PORT)/api/v1/products"
	@echo "📖 Swagger UI: http://localhost:$(BACKEND_PORT)/swagger-ui/index.html"
	@echo "🎯 Quick test: make test-api"

.PHONY: dev-rebuild
dev-rebuild: ## 🔄 Rebuild and restart backend for development
	@echo "🔄 Rebuilding backend..."
	$(DC) stop $(SERVICE_APP)
	$(DC) build $(SERVICE_APP)
	$(DC) up -d $(SERVICE_APP)
	@echo "⏳ Waiting for backend to restart..."
	@sleep 10
	@echo "✅ Backend rebuilt and restarted!"

.PHONY: down
down: ## 🛑 Tắt stack và remove orphans
	@echo "🛑 Stopping Docker stack..."
	$(DC) down --remove-orphans
	@echo "✅ Stack stopped successfully!"

.PHONY: restart
restart: ## 🔃 Restart stack
	@echo "🔃 Restarting stack..."
	$(MAKE) down
	$(MAKE) up

.PHONY: logs
logs: ## 📄 Tail logs của backend
	@echo "📄 Following backend logs (Ctrl+C to stop)..."
	$(DC) logs -f $(SERVICE_APP)

.PHONY: logs-tail
logs-tail: ## 📄 Xem logs gần đây của backend (last 20 lines)
	@echo "📄 Recent backend logs (last 20 lines)..."
	$(DC) logs --tail=20 $(SERVICE_APP)

.PHONY: logs-correlation
logs-correlation: ## 🔍 Xem logs với correlation ID (usage: make logs-correlation ID=demo-123)
	@if [ -z "$(ID)" ]; then \
		echo "🔍 Recent logs with correlation IDs..."; \
		$(DC) logs --tail=30 $(SERVICE_APP) | grep -E "(corrId=|correlation)" || echo "No correlation IDs found in recent logs"; \
	else \
		echo "🔍 Logs for correlation ID: $(ID)"; \
		$(DC) logs --tail=100 $(SERVICE_APP) | grep "$(ID)" || echo "No logs found for correlation ID: $(ID)"; \
	fi

.PHONY: logs-all
logs-all: ## Tail logs tất cả services
	$(DC) logs -f

.PHONY: ps
ps: ## Trạng thái containers
	$(DC) ps

.PHONY: rebuild
rebuild: ## Build lại image backend (no-cache)
	$(DC) build --no-cache $(SERVICE_APP)

.PHONY: boot
boot: ## Chạy Spring Boot local (không dùng Docker), profile=dev
	SPRING_PROFILES_ACTIVE=dev ./gradlew bootRun

.PHONY: test
test: ## Chạy test Gradle local
	./gradlew clean test

.PHONY: sh-app
sh-app: ## Shell vào container backend
	$(DC) exec $(SERVICE_APP) sh -lc 'printenv | sort; echo "---"; /bin/sh'

.PHONY: sh-db
sh-db: ## Mở MySQL CLI ngay trong container db (dùng biến env từ .env)
	$(DC) exec $(SERVICE_DB) sh -lc 'mysql -u$${MYSQL_USER:-root} -p$${MYSQL_PASSWORD:-$$MYSQL_ROOT_PASSWORD} $${MYSQL_DATABASE}'

.PHONY: db-logs
db-logs: ## Tail logs MySQL
	$(DC) logs -f $(SERVICE_DB)

.PHONY: clean
clean: ## 🧹 Down + xóa volumes và prune images dangling
	@echo "🧹 Cleaning up Docker resources..."
	$(DC) down -v --remove-orphans || true
	@echo "🗑️  Removing dangling images..."
	docker image prune -f || true
	@echo "📊 Docker space usage:"
	@docker system df

.PHONY: health
health: ## 🏥 Check health of all services
	@echo "🏥 Health Check Report"
	@echo "===================="
	@echo "📦 Container Status:"
	@$(DC) ps --format "table {{.Name}}\t{{.Status}}\t{{.Ports}}"
	@echo "\n🔗 Backend Health:"
	@curl -s -o /dev/null -w "  Status: %{http_code} - %{url_effective}\n" http://localhost:$(BACKEND_PORT)/api/v1/products || echo "  ❌ Backend: Unreachable"
	@echo "\n🗄️  Database Connection:"
	@$(DC) exec $(SERVICE_DB) sh -c 'mysqladmin ping -h localhost' 2>/dev/null && echo "  ✅ Database: Connected" || echo "  ❌ Database: Error"
	@echo "\n📊 Resource Usage:"
	@docker stats --no-stream --format "table {{.Name}}\t{{.CPUPerc}}\t{{.MemUsage}}" $(shell docker compose ps -q 2>/dev/null) 2>/dev/null || echo "  No containers running"


.PHONY: swagger
swagger: ## 📖 Open Swagger UI in browser
	@echo "📖 Opening Swagger UI..."
	@echo "URL: http://localhost:$(BACKEND_PORT)/swagger-ui/index.html"
	@command -v open >/dev/null 2>&1 && open "http://localhost:$(BACKEND_PORT)/swagger-ui/index.html" || \
	 command -v xdg-open >/dev/null 2>&1 && xdg-open "http://localhost:$(BACKEND_PORT)/swagger-ui/index.html" || \
	 echo "⚠️  Please manually open the URL above"

# ==== Day 3 - Validation & Testing shortcuts ====

.PHONY: test-swagger
test-swagger: ## 🧪 Test Swagger UI accessibility
	@echo "🧪 Testing Swagger UI accessibility..."
	@echo "📖 Swagger UI:"
	@curl -s -o /dev/null -w "  Status: %{http_code} - %{url_effective}\n" http://localhost:$(BACKEND_PORT)/swagger-ui/index.html || echo "  ❌ Swagger UI: Unreachable"
	@echo "📋 API Documentation:"
	@curl -s -o /dev/null -w "  Status: %{http_code} - %{url_effective}\n" http://localhost:$(BACKEND_PORT)/v3/api-docs || echo "  ❌ API Docs: Unreachable"
	@curl -s http://localhost:$(BACKEND_PORT)/v3/api-docs | jq -r '.info.title + " v" + .info.version' 2>/dev/null && echo "  ✅ API Documentation loaded" || echo "  ⚠️  API docs available but no jq parser"

.PHONY: test-api
test-api: ## 🧪 Test API endpoints với validation
	@echo "🧪 Testing API validation endpoints..."
	@echo "\n🔐 Test 1: Login to get JWT token"
	@TOKEN=$$(curl -X POST http://localhost:$(BACKEND_PORT)/auth/login \
		-H "Content-Type: application/json" \
		-d '{"username": "admin", "password": "admin123"}' \
		-s | jq -r '.token' 2>/dev/null || echo "")
	@if [ -z "$$TOKEN" ] || [ "$$TOKEN" = "null" ]; then \
		echo "  ❌ Login failed - check if admin user exists"; \
		echo "  💡 Try: make create-admin-user"; \
	else \
		echo "  ✅ Login successful, token obtained"; \
		echo "\n✅ Test 2: Valid product creation with JWT (expect 201)"; \
		curl -X POST http://localhost:$(BACKEND_PORT)/api/v1/products \
			-H "Content-Type: application/json" \
			-H "Authorization: Bearer $$TOKEN" \
			-d '{"name": "Test Product", "description": "Valid product", "price": 99.99, "quantityInStock": 10}' \
			-w "  Status: %{http_code}\n" -s | jq '.' 2>/dev/null || echo "  Response received"; \
		echo "\n❌ Test 3: Invalid product with JWT (expect 400)"; \
		curl -X POST http://localhost:$(BACKEND_PORT)/api/v1/products \
			-H "Content-Type: application/json" \
			-H "Authorization: Bearer $$TOKEN" \
			-d '{"name": "", "price": -5, "quantityInStock": -1}' \
			-w "  Status: %{http_code}\n" -s | jq '.fieldErrors // .' 2>/dev/null || echo "  Validation errors received"; \
		echo "\n🔍 Test 4: Resource not found with JWT (expect 404)"; \
		curl -X GET http://localhost:$(BACKEND_PORT)/api/v1/products/999 \
			-H "Authorization: Bearer $$TOKEN" \
			-w "  Status: %{http_code}\n" -s | jq '.' 2>/dev/null || echo "  404 error received"; \
		echo "\n📊 Test 5: List all products with JWT (expect 200)"; \
		curl -X GET http://localhost:$(BACKEND_PORT)/api/v1/products \
			-H "Authorization: Bearer $$TOKEN" \
			-w "  Status: %{http_code}\n" -s | jq 'length // "Response received"' 2>/dev/null || echo "  Product list received"; \
		echo "\n🚫 Test 6: Access without JWT (expect 401)"; \
		curl -X GET http://localhost:$(BACKEND_PORT)/api/v1/products \
			-w "  Status: %{http_code}\n" -s | jq '.' 2>/dev/null || echo "  401 error received"; \
	fi

# ==== Configuration Management ====

.PHONY: config
config: ## 🎯 Mở Configuration Manager (chỉ cần sửa 1 chỗ)
	@echo "🎯 Opening Centralized Configuration Manager..."
	@./config-manager.sh

.PHONY: config-show
config-show: ## 📋 Hiển thị tất cả cấu hình hiện tại
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
config-backup: ## 💾 Backup cấu hình hiện tại
	@echo "💾 Creating configuration backup..."
	@mkdir -p backups/env
	@cp .env backups/env/.env.backup.$$(date +%Y%m%d_%H%M%S)
	@echo "✅ Configuration backed up to backups/env/"

.PHONY: config-list-backups
config-list-backups: ## 📋 Liệt kê tất cả backup cấu hình
	@echo "📋 Available Configuration Backups:"
	@echo "=================================="
	@if [ -d "backups/env" ] && [ "$$(ls -A backups/env 2>/dev/null)" ]; then \
		ls -la backups/env/.env.backup.* 2>/dev/null | while read line; do \
			filename=$$(echo $$line | awk '{print $$9}'); \
			date=$$(echo $$filename | sed 's/.*\.env\.backup\.//'); \
			size=$$(echo $$line | awk '{print $$5}'); \
			echo "  📄 $$filename ($$size bytes) - $$date"; \
		done; \
	else \
		echo "  ❌ No backups found in backups/env/"; \
	fi

.PHONY: config-restore
config-restore: ## 🔄 Khôi phục cấu hình từ backup (usage: make config-restore BACKUP=filename)
	@if [ -z "$(BACKUP)" ]; then \
		echo "❌ Usage: make config-restore BACKUP=filename"; \
		echo "📋 Available backups:"; \
		$(MAKE) config-list-backups; \
		exit 1; \
	fi
	@if [ -f "backups/env/$(BACKUP)" ]; then \
		echo "🔄 Restoring configuration from $(BACKUP)..."; \
		cp backups/env/$(BACKUP) .env; \
		echo "✅ Configuration restored!"; \
		echo "💡 Run 'make restart' to apply changes"; \
	else \
		echo "❌ Backup file '$(BACKUP)' not found in backups/env/"; \
		echo "📋 Available backups:"; \
		$(MAKE) config-list-backups; \
	fi

.PHONY: config-clean-backups
config-clean-backups: ## 🧹 Xóa các backup cũ (giữ lại 5 file gần nhất)
	@echo "🧹 Cleaning old configuration backups..."
	@if [ -d "backups/env" ]; then \
		count=$$(ls backups/env/.env.backup.* 2>/dev/null | wc -l); \
		if [ $$count -gt 5 ]; then \
			ls -t backups/env/.env.backup.* | tail -n +6 | xargs rm -f; \
			echo "✅ Removed $$(($$count - 5)) old backup(s)"; \
		else \
			echo "✅ No old backups to clean ($$count backups total)"; \
		fi; \
	else \
		echo "❌ No backup directory found"; \
	fi

# ==== Advanced Development Tools ====

.PHONY: install-deps
install-deps: ## 📦 Install development dependencies (jq, etc.)
	@echo "📦 Installing development dependencies..."
	@command -v jq >/dev/null 2>&1 || (echo "Installing jq..." && \
		(command -v apt-get >/dev/null 2>&1 && sudo apt-get install -y jq) || \
		(command -v yum >/dev/null 2>&1 && sudo yum install -y jq) || \
		(command -v brew >/dev/null 2>&1 && brew install jq) || \
		echo "⚠️  Please install jq manually for better JSON parsing")
	@echo "✅ Dependencies check completed"

.PHONY: full-reset
full-reset: ## 🗑️ Complete reset (stop, clean, rebuild, start)
	@echo "🗑️ Performing full reset..."
	$(MAKE) down
	$(MAKE) clean
	@echo "🔨 Rebuilding images..."
	$(DC) build --no-cache
	$(MAKE) dev-start
	@echo "✅ Full reset completed!"

.PHONY: backup-db
backup-db: ## 💾 Backup database to file
	@echo "💾 Creating database backup..."
	@mkdir -p ./backups
	$(DC) exec $(SERVICE_DB) sh -c 'mysqldump -u$${MYSQL_USER:-root} -p$${MYSQL_PASSWORD:-$$MYSQL_ROOT_PASSWORD} $${MYSQL_DATABASE}' > ./backups/backup_$(shell date +%Y%m%d_%H%M%S).sql
	@echo "✅ Database backed up to ./backups/"

.PHONY: watch-logs
watch-logs: ## 👁️ Watch logs with better formatting
	@echo "👁️ Watching logs (press Ctrl+C to stop)..."
	$(DC) logs -f --tail=50 | while read line; do \
		echo "$(shell date '+%H:%M:%S') | $$line"; \
	done

.PHONY: performance-test
performance-test: ## 🚀 Simple performance test
	@echo "🚀 Running simple performance test..."
	@command -v ab >/dev/null 2>&1 || (echo "⚠️  Apache Bench (ab) not found. Install with 'brew install httpie' or 'apt-get install apache2-utils'" && exit 1)
	@ab -n 100 -c 10 http://localhost:$(BACKEND_PORT)/api/v1/products > /tmp/perf_test.log 2>&1 && \
		cat /tmp/perf_test.log | grep -E "Requests per second|Time per request|Failed requests" || \
		echo "⚠️  Performance test failed. Check if backend is running."

.PHONY: dev-status
dev-status: ## 📊 Complete development environment status
	@echo "📊 Development Environment Status"
	@echo "================================="
	@echo "🐳 Docker Compose Status:"
	@$(DC) ps --format "table {{.Name}}\t{{.Status}}\t{{.Ports}}" 2>/dev/null || echo "  No services running"
	@echo "\n🌐 Network Connectivity:"
	@curl -s -o /dev/null -w "  Backend API: %{http_code} (%{time_total}s)\n" http://localhost:$(BACKEND_PORT)/api/v1/products || echo "  Backend: Unreachable"
	@curl -s -o /dev/null -w "  Swagger UI: %{http_code} (%{time_total}s)\n" http://localhost:$(BACKEND_PORT)/swagger-ui/index.html || echo "  Swagger: Unreachable"
	@echo "\n📋 Recent Activity:"
	@$(DC) logs --tail=3 $(SERVICE_APP) 2>/dev/null | sed 's/^/  /' || echo "  No recent backend logs"
	@echo "\n⚡ Quick Actions:"
	@echo "  make dev-start     → Start environment"
	@echo "  make test-api      → Test API endpoints"
	@echo "  make unit-test     → Run unit tests with coverage"
	@echo "  make swagger       → Open Swagger UI"

# ==== Day 7 - Unit Testing Commands ====

.PHONY: unit-test
unit-test: ## 🧪 Run unit tests with JaCoCo coverage report
	@echo "🧪 Running unit tests with coverage..."
	@mkdir -p backend/build/reports/jacoco/test/html
	$(DC) --profile test build test-runner
	$(DC) --profile test run --rm test-runner
	@echo "\n✅ Unit tests completed!"
	@echo "📊 Coverage report: backend/build/reports/jacoco/test/html/index.html"

.PHONY: unit-test-watch
unit-test-watch: ## 👁️ Run unit tests in watch mode (re-run on file changes)
	@echo "👁️ Starting unit tests in watch mode..."
	@echo "⚠️  This will re-run tests when source files change (Ctrl+C to stop)"
	$(DC) --profile test run --rm test-runner ./gradlew --no-daemon test --continuous

.PHONY: unit-test-single
unit-test-single: ## 🎯 Run single test class (usage: make unit-test-single CLASS=ProductServiceTest)
	@if [ -z "$(CLASS)" ]; then \
		echo "❌ Usage: make unit-test-single CLASS=ProductServiceTest"; \
		exit 1; \
	fi
	@echo "🎯 Running single test class: $(CLASS)..."
	$(DC) --profile test run --rm test-runner ./gradlew --no-daemon test --tests "*$(CLASS)*" --info

.PHONY: unit-test-clean
unit-test-clean: ## 🧹 Clean test reports and build artifacts
	@echo "🧹 Cleaning test artifacts..."
	@rm -rf backend/build/reports/tests/
	@rm -rf backend/build/reports/jacoco/
	@rm -rf backend/build/test-results/
	@echo "✅ Test artifacts cleaned!"

.PHONY: unit-coverage
unit-coverage: ## 📊 Generate and open coverage report
	@echo "📊 Generating coverage report..."
	$(DC) --profile test run --rm test-runner ./gradlew --no-daemon jacocoTestReport
	@echo "📂 Opening coverage report..."
	@if [ -f backend/build/reports/jacoco/test/html/index.html ]; then \
		echo "✅ Coverage report: backend/build/reports/jacoco/test/html/index.html"; \
		command -v open >/dev/null 2>&1 && open backend/build/reports/jacoco/test/html/index.html || \
		command -v xdg-open >/dev/null 2>&1 && xdg-open backend/build/reports/jacoco/test/html/index.html || \
		echo "📖 Please open: backend/build/reports/jacoco/test/html/index.html"; \
	else \
		echo "❌ Coverage report not found. Run 'make unit-test' first."; \
	fi

.PHONY: unit-test-logs
unit-test-logs: ## 📄 Show detailed test logs
	@echo "📄 Recent test logs..."
	@if [ -d backend/build/reports/tests/test ]; then \
		find backend/build/reports/tests/test -name "*.html" -exec echo "📂 {}" \; -exec cat {} \; | head -50; \
	else \
		echo "❌ No test logs found. Run 'make unit-test' first."; \
	fi

.PHONY: test-all
test-all: ## 🚀 Run all types of tests (unit + API validation)
	@echo "🚀 Running comprehensive test suite..."
	@echo "\n1️⃣ Running unit tests..."
	$(MAKE) unit-test
	@echo "\n2️⃣ Starting backend for API tests..."
	$(MAKE) dev-start
	@sleep 10
	@echo "\n3️⃣ Running API validation tests..."
	$(MAKE) test-api
	@echo "\n✅ All tests completed successfully!"

# ==== Day 8 - Integration Tests với Testcontainers ====

.PHONY: integration-test
integration-test: ## 🧪 Run integration tests với Testcontainers (MySQL)
	@echo "🧪 Running integration tests với Testcontainers..."
	@echo "🐳 Testcontainers sẽ tự động khởi động MySQL container"
	cd backend && ./gradlew clean test --tests "*IT" --info
	@echo "\n✅ Integration tests completed!"
	@echo "📊 Coverage report: backend/build/reports/jacoco/test/html/index.html"

.PHONY: integration-test-watch
integration-test-watch: ## 👁️ Run integration tests in watch mode
	@echo "👁️ Starting integration tests in watch mode..."
	@echo "⚠️  This will re-run tests when source files change (Ctrl+C to stop)"
	cd backend && ./gradlew --no-daemon test --tests "*IT" --continuous

.PHONY: integration-test-single
integration-test-single: ## 🎯 Run single integration test (usage: make integration-test-single CLASS=ProductRepositoryIT)
	@if [ -z "$(CLASS)" ]; then \
		echo "❌ Usage: make integration-test-single CLASS=ProductRepositoryIT"; \
		exit 1; \
	fi
	@echo "🎯 Running single integration test: $(CLASS)..."
	cd backend && ./gradlew --no-daemon test --tests "*$(CLASS)*" --info

.PHONY: test-containers
test-containers: ## 🐳 Test Testcontainers setup (pull images, check connectivity)
	@echo "🐳 Testing Testcontainers setup..."
	@echo "📥 Pulling MySQL image (first run may take time)..."
	docker pull mysql:8.0
	@echo "✅ MySQL image ready"
	@echo "🧪 Running quick integration test..."
	cd backend && ./gradlew test --tests "*IntegrationTestBase*" --info
	@echo "✅ Testcontainers setup verified!"

.PHONY: test-no-db
test-no-db: ## 🚀 Run tests without local MySQL (using Testcontainers only)
	@echo "🚀 Running tests without local MySQL dependency..."
	@echo "🐳 Using Testcontainers for database..."
	cd backend && ./gradlew clean test
	@echo "\n✅ Tests completed without local MySQL!"
	@echo "📊 Coverage report: backend/build/reports/jacoco/test/html/index.html"

.PHONY: test-full-suite
test-full-suite: ## 🎯 Run complete test suite (unit + integration + API)
	@echo "🎯 Running complete test suite..."
	@echo "\n1️⃣ Running unit tests..."
	$(MAKE) unit-test
	@echo "\n2️⃣ Running integration tests với Testcontainers..."
	$(MAKE) integration-test
	@echo "\n3️⃣ Starting backend for API tests..."
	$(MAKE) dev-start
	@sleep 10
	@echo "\n4️⃣ Running API validation tests..."
	$(MAKE) test-api
	@echo "\n✅ Complete test suite finished successfully!"

# ==== Day 14 - JWT Security Commands ====

.PHONY: create-admin-user
create-admin-user: ## 👤 Tạo admin user mặc định (username: admin, password: admin123)
	@echo "👤 Creating default admin user..."
	@echo "📝 Username: admin"
	@echo "🔐 Password: admin123"
	@echo "🔑 Role: ADMIN"
	@echo ""
	@echo "⚠️  This will create a user in the database. Make sure backend is running."
	@read -p "Continue? (y/N): " confirm && [ "$$confirm" = "y" ] || exit 1
	@./scripts/create-admin-user.sh
	@echo "✅ Admin user created successfully!"
	@echo "💡 You can now use 'make test-api' to test JWT authentication"

.PHONY: test-jwt-login
test-jwt-login: ## 🔐 Test JWT login endpoint
	@echo "🔐 Testing JWT login..."
	@echo "\n✅ Test 1: Valid login (admin/admin123)"
	@curl -X POST http://localhost:$(BACKEND_PORT)/auth/login \
		-H "Content-Type: application/json" \
		-d '{"username": "admin", "password": "admin123"}' \
		-w "  Status: %{http_code}\n" -s | jq '.' 2>/dev/null || echo "  Login response received"
	@echo "\n❌ Test 2: Invalid login (wrong password)"
	@curl -X POST http://localhost:$(BACKEND_PORT)/auth/login \
		-H "Content-Type: application/json" \
		-d '{"username": "admin", "password": "wrongpassword"}' \
		-w "  Status: %{http_code}\n" -s | jq '.' 2>/dev/null || echo "  Error response received"
	@echo "\n❌ Test 3: Invalid login (non-existent user)"
	@curl -X POST http://localhost:$(BACKEND_PORT)/auth/login \
		-H "Content-Type: application/json" \
		-d '{"username": "nonexistent", "password": "password"}' \
		-w "  Status: %{http_code}\n" -s | jq '.' 2>/dev/null || echo "  Error response received"

.PHONY: test-jwt-protected
test-jwt-protected: ## 🛡️ Test JWT protected endpoints
	@echo "🛡️ Testing JWT protected endpoints..."
	@echo "\n🔐 Step 1: Login to get JWT token"
	@TOKEN=$$(curl -X POST http://localhost:$(BACKEND_PORT)/auth/login \
		-H "Content-Type: application/json" \
		-d '{"username": "admin", "password": "admin123"}' \
		-s | jq -r '.token' 2>/dev/null || echo "")
	@if [ -z "$$TOKEN" ] || [ "$$TOKEN" = "null" ]; then \
		echo "  ❌ Login failed - check if admin user exists"; \
		echo "  💡 Try: make create-admin-user"; \
		exit 1; \
	fi
	@echo "  ✅ JWT token obtained: $${TOKEN:0:20}..."
	@echo "\n🛡️ Step 2: Test protected endpoints with JWT"
	@echo "  📊 Testing /api/v1/products..."
	@curl -X GET http://localhost:$(BACKEND_PORT)/api/v1/products \
		-H "Authorization: Bearer $$TOKEN" \
		-w "  Status: %{http_code}\n" -s | jq 'length // "Response received"' 2>/dev/null || echo "  Products list received"
	@echo "  👥 Testing /api/v1/administrators..."
	@curl -X GET http://localhost:$(BACKEND_PORT)/api/v1/administrators \
		-H "Authorization: Bearer $$TOKEN" \
		-w "  Status: %{http_code}\n" -s | jq 'length // "Response received"' 2>/dev/null || echo "  Administrators list received"
	@echo "\n🚫 Step 3: Test without JWT (should fail)"
	@curl -X GET http://localhost:$(BACKEND_PORT)/api/v1/products \
		-w "  Status: %{http_code}\n" -s | jq '.' 2>/dev/null || echo "  401 error received"

.PHONY: test-jwt-expired
test-jwt-expired: ## ⏰ Test JWT token expiration
	@echo "⏰ Testing JWT token expiration..."
	@echo "💡 This test requires a short-lived token (set JWT_EXPIRATION=1000 in .env)"
	@echo "⚠️  Make sure to set JWT_EXPIRATION=1000 (1 second) in your .env file first"
	@read -p "Continue? (y/N): " confirm && [ "$$confirm" = "y" ] || exit 1
	@echo "\n🔐 Step 1: Login to get short-lived token"
	@TOKEN=$$(curl -X POST http://localhost:$(BACKEND_PORT)/auth/login \
		-H "Content-Type: application/json" \
		-d '{"username": "admin", "password": "admin123"}' \
		-s | jq -r '.token' 2>/dev/null || echo "")
	@if [ -z "$$TOKEN" ] || [ "$$TOKEN" = "null" ]; then \
		echo "  ❌ Login failed"; \
		exit 1; \
	fi
	@echo "  ✅ Token obtained: $${TOKEN:0:20}..."
	@echo "\n⏳ Step 2: Wait for token to expire (2 seconds)"
	@sleep 2
	@echo "\n🚫 Step 3: Try to use expired token"
	@curl -X GET http://localhost:$(BACKEND_PORT)/api/v1/products \
		-H "Authorization: Bearer $$TOKEN" \
		-w "  Status: %{http_code}\n" -s | jq '.' 2>/dev/null || echo "  401 error received"

.PHONY: security-status
security-status: ## 🔒 Check security configuration status
	@echo "🔒 Security Configuration Status"
	@echo "================================"
	@echo "🌐 Backend URL: http://localhost:$(BACKEND_PORT)"
	@echo "🔐 Login Endpoint: http://localhost:$(BACKEND_PORT)/auth/login"
	@echo "📖 Swagger UI: http://localhost:$(BACKEND_PORT)/swagger-ui/index.html"
	@echo ""
	@echo "🛡️ Protected Endpoints (require JWT):"
	@echo "  - /api/v1/products"
	@echo "  - /api/v1/customers"
	@echo "  - /api/v1/orders"
	@echo "  - /api/v1/suppliers"
	@echo "  - /api/v1/administrators"
	@echo ""
	@echo "🔓 Public Endpoints (no JWT required):"
	@echo "  - /auth/login"
	@echo "  - /swagger-ui/**"
	@echo "  - /v3/api-docs/**"
	@echo ""
	@echo "🧪 Quick Tests:"
	@echo "  make test-jwt-login     → Test login endpoint"
	@echo "  make test-jwt-protected → Test protected endpoints"
	@echo "  make test-api          → Full API test with JWT"
	@echo "  make create-admin-user → Create default admin user"
