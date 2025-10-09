#!/usr/bin/env sh
set -e

BASE="${BASE:-http://localhost:8080}"
OUT_DIR="$(dirname "$0")"
TOKEN_FILE="$OUT_DIR/.token"

if [ ! -f "$TOKEN_FILE" ]; then
  echo "[CUST/SUP] ERROR: token file not found: $TOKEN_FILE" >&2
  exit 1
fi
TOKEN=$(cat "$TOKEN_FILE")

echo "[CUSTOMERS] List page"
curl -s -H "Authorization: Bearer $TOKEN" "$BASE/api/v1/customers?page=0&size=5&sort=id,desc" | sed 's/.*/[CUSTOMERS] Page: &/' || true

echo "[SUPPLIERS] List page"
curl -s -H "Authorization: Bearer $TOKEN" "$BASE/api/v1/suppliers/page?page=0&size=5" | sed 's/.*/[SUPPLIERS] Page: &/' || true

exit 0


