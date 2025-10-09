#!/usr/bin/env sh
set -e

BASE="${BASE:-http://localhost:8080}"
OUT_DIR="$(dirname "$0")"
TOKEN_FILE="$OUT_DIR/.token"

if [ ! -f "$TOKEN_FILE" ]; then
  echo "[CATALOG] ERROR: token file not found: $TOKEN_FILE" >&2
  exit 1
fi
TOKEN=$(cat "$TOKEN_FILE")

echo "[CATALOG] Low-stock products"
curl -s -H "Authorization: Bearer $TOKEN" "$BASE/api/v1/products/low-stock" | sed 's/.*/[CATALOG] Low-stock: &/' || true

echo "[CATALOG] Products stats"
curl -s -H "Authorization: Bearer $TOKEN" "$BASE/api/v1/products/stats" | sed 's/.*/[CATALOG] Stats: &/' || true

exit 0


