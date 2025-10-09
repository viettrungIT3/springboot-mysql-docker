#!/usr/bin/env sh
set -e

BASE="${BASE:-http://localhost:8080}"
OUT_DIR="$(dirname "$0")"
TOKEN_FILE="$OUT_DIR/.token"

if [ ! -f "$TOKEN_FILE" ]; then
  echo "[PRODUCTS] ERROR: token file not found: $TOKEN_FILE" >&2
  exit 1
fi
TOKEN=$(cat "$TOKEN_FILE")

echo "[PRODUCTS] Create product"
curl -s -X POST "$BASE/api/v1/products" \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"name":"Laptop Demo","description":"Demo","price":1299.99,"quantityInStock":5}' | sed 's/.*/[PRODUCTS] Create: &/'

echo "[PRODUCTS] List products (page)"
curl -s "$BASE/api/v1/products?page=0&size=5&sort=id,desc" \
  -H "Authorization: Bearer $TOKEN" | sed 's/.*/[PRODUCTS] List: &/'

echo "[PRODUCTS] Export CSV"
curl -s -H 'Accept: text/csv' -H "Authorization: Bearer $TOKEN" \
  "$BASE/api/v1/products/export-csv" | head -n 3 | sed 's/.*/[PRODUCTS] CSV (head): &/'

exit 0


