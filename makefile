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
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z0-9_-]+:.*?## / && /rebuild|boot|test|clean/ {printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)
	@echo "\n📖 DOCUMENTATION & API:"
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z0-9_-]+:.*?## / && /swagger|db-/ {printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)
	@echo "\n🎯 QUICK START:"
	@echo "  \033[33mmake dev-start\033[0m     → Start development environment"
	@echo "  \033[33mmake test-api\033[0m      → Test API validation"
	@echo "  \033[33mmake swagger\033[0m       → Open Swagger UI"
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
	@echo "  make swagger       → Open Swagger UI"
