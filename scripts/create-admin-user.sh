#!/bin/bash

# Script to create admin user for JWT testing
# This avoids hardcoding credentials in makefile

ADMIN_USERNAME="admin"
ADMIN_PASSWORD="admin123"
ADMIN_EMAIL="admin@example.com"
ADMIN_FULLNAME="System Administrator"
ADMIN_ROLE="ADMIN"

# Hash the password using BCrypt (this is a test hash)
ADMIN_PASSWORD_HASH="\$2a\$10\$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi"

echo "👤 Creating default admin user..."
echo "📝 Username: $ADMIN_USERNAME"
echo "🔐 Password: $ADMIN_PASSWORD"
echo "🔑 Role: $ADMIN_ROLE"
echo ""
echo "⚠️  This will create a user in the database. Make sure backend is running."

# Create admin user via API
curl -X POST http://localhost:8080/api/v1/administrators \
  -H "Content-Type: application/json" \
  -d "{
    \"username\": \"$ADMIN_USERNAME\",
    \"passwordHash\": \"$ADMIN_PASSWORD_HASH\",
    \"email\": \"$ADMIN_EMAIL\",
    \"fullName\": \"$ADMIN_FULLNAME\",
    \"role\": \"$ADMIN_ROLE\"
  }" \
  -s > /dev/null

if [ $? -eq 0 ]; then
  echo "✅ Admin user created successfully!"
  echo "💡 You can now use 'make test-api' to test JWT authentication"
else
  echo "❌ Failed to create admin user"
  echo "💡 Make sure backend is running and database is accessible"
fi
