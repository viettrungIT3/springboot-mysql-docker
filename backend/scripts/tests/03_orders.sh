#!/usr/bin/env sh
set -e

BASE="${BASE:-http://localhost:8080}"
OUT_DIR="$(dirname "$0")"
TOKEN_FILE="$OUT_DIR/.token"

if [ ! -f "$TOKEN_FILE" ]; then
  echo "[ORDERS] ERROR: token file not found: $TOKEN_FILE" >&2
  exit 1
fi
TOKEN=$(cat "$TOKEN_FILE")

echo "[ORDERS] Create order"
ORDER_JSON=$(curl -s -X POST "$BASE/api/v1/orders" \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer $TOKEN" \
  -H "Idempotency-Key: test-$(date +%s)" \
  -d '{"customerId":1,"items":[{"productId":1,"quantity":1}]}' )
echo "$ORDER_JSON" | sed 's/.*/[ORDERS] Create: &/'

ORDER_ID=$(echo "$ORDER_JSON" | sed -n 's/.*"id" *: *\([0-9]\+\).*/\1/p' | head -n1)
if [ -z "$ORDER_ID" ]; then
  echo "[ORDERS] WARN: cannot extract order id; defaulting to 1" >&2
  ORDER_ID=1
fi

echo "[ORDERS] Confirm order $ORDER_ID (with Idempotency-Key)"
curl -s -X POST "$BASE/api/v1/orders/$ORDER_ID/confirm" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Idempotency-Key: confirm-$(date +%s)" | sed 's/.*/[ORDERS] Confirm: &/'

exit 0


