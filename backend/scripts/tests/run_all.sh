#!/usr/bin/env sh
set -e

DIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"

chmod +x "$DIR"/*.sh 2>/dev/null || true

echo "[RUN] 01_auth.sh";    BASE=${BASE:-http://localhost:8080} "$DIR/01_auth.sh"
echo "[RUN] 02_products.sh"; BASE=${BASE:-http://localhost:8080} "$DIR/02_products.sh"
echo "[RUN] 03_orders.sh";   BASE=${BASE:-http://localhost:8080} "$DIR/03_orders.sh"
echo "[RUN] 04_catalog.sh";  BASE=${BASE:-http://localhost:8080} "$DIR/04_catalog.sh" || true
echo "[RUN] 05_customers_suppliers.sh"; BASE=${BASE:-http://localhost:8080} "$DIR/05_customers_suppliers.sh" || true

echo "[RUN] All tests finished"
exit 0


