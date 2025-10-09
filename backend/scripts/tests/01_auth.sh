#!/usr/bin/env sh
set -e

BASE="${BASE:-http://localhost:8080}"
OUT_DIR="$(dirname "$0")"
TOKEN_FILE="$OUT_DIR/.token"

echo "[AUTH] Using BASE=$BASE"

USERNAME="user$RANDOM"
PASSWORD="#User12345678"
EMAIL="$USERNAME@example.com"

echo "[AUTH] Register $USERNAME"
REG_STATUS=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE/auth/register" \
  -H 'Content-Type: application/json' \
  -d "{\"username\":\"$USERNAME\",\"password\":\"$PASSWORD\",\"email\":\"$EMAIL\",\"fullName\":\"Test User\"}")
echo "[AUTH] Register status: $REG_STATUS"

echo "[AUTH] Login $USERNAME"
LOGIN_JSON=$(curl -s -X POST "$BASE/auth/login" \
  -H 'Content-Type: application/json' \
  -d "{\"username\":\"$USERNAME\",\"password\":\"$PASSWORD\"}")
echo "$LOGIN_JSON" | sed 's/.*/[AUTH] Login response: &/'

# Extract token (basic parsing without jq)
TOKEN=$(echo "$LOGIN_JSON" | sed -n 's/.*"token" *: *"\([^"]*\)".*/\1/p')
if [ -z "$TOKEN" ]; then
  echo "[AUTH] ERROR: Cannot extract token" >&2
  exit 1
fi
echo "$TOKEN" > "$TOKEN_FILE"
echo "[AUTH] Token saved to $TOKEN_FILE"

echo "[AUTH] Profile"
curl -s -H "Authorization: Bearer $TOKEN" "$BASE/auth/profile" | sed 's/.*/[AUTH] Profile: &/'

exit 0


