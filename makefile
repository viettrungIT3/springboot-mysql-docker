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

.PHONY: help
help: ## Hiển thị danh sách lệnh hữu ích
	@echo "Spring Boot + MySQL shortcuts:"
	@echo "  make up         - Start stack (detached)"
	@echo "  make down       - Stop stack và remove orphans"
	@echo "  make restart    - down + up"
	@echo "  make logs       - flow logs of backend"
	@echo "  make logs-all   - flow logs of all services"
	@echo "  make ps         - view container status"
	@echo "  make rebuild    - rebuild image backend (no-cache)"
	@echo "  make boot       - run Spring Boot local (profile=dev)"
	@echo "  make test       - run test Gradle"
	@echo "  make sh-app     - open shell container backend"
	@echo "  make sh-db      - open shell MySQL client inside DB container"
	@echo "  make db-logs    - flow log MySQL"
	@echo "  make clean      - down + xóa volumes & prune images(dangling)"
	@echo "  make swagger    - in URL Swagger UI"

.PHONY: up
up: ## Bật stack (detached)
	$(DC) up -d

.PHONY: down
down: ## Tắt stack và remove orphans
	$(DC) down --remove-orphans

.PHONY: restart
restart: ## Restart stack
	$(MAKE) down
	$(MAKE) up

.PHONY: logs
logs: ## Tail logs của backend
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
clean: ## Down + xóa volumes và prune images dangling
	$(DC) down -v --remove-orphans || true
	docker image prune -f || true

.PHONY: swagger
swagger: ## In URL Swagger UI
	@echo "Swagger UI: http://localhost:$(BACKEND_PORT)/swagger-ui/index.html"
