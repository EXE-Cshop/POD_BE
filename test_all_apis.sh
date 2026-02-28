#!/bin/bash

# Configuration
BASE_URL="http://localhost:8080/api/v1"
ADMIN_EMAIL="admin"
ADMIN_PASS="admin"

echo "========== C-Shop API Testing Script =========="

# 1. Login to get token
echo "[1] Logging in as admin..."
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"email\": \"$ADMIN_EMAIL\", \"password\": \"$ADMIN_PASS\"}")

TOKEN=$(echo $LOGIN_RESPONSE | grep -oP '(?<="accessToken":")[^"]*')

if [ -z "$TOKEN" ]; then
    echo "FAILED: Could not get access token."
    echo "Response: $LOGIN_RESPONSE"
    exit 1
fi

echo "SUCCESS: Logged in."
AUTH_HEADER="Authorization: Bearer $TOKEN"

# 2. Get User Info
echo -e "\n[2] Getting current user info..."
curl -s -X GET "$BASE_URL/users/me" -H "$AUTH_HEADER" | json_pp | head -n 20

# 3. List Products
echo -e "\n[3] Listing all products..."
PRODUCTS_RESPONSE=$(curl -s -X GET "$BASE_URL/products" -H "$AUTH_HEADER")
echo $PRODUCTS_RESPONSE | json_pp | head -n 20

PRODUCT_ID=$(echo $PRODUCTS_RESPONSE | grep -oP '(?<="id":)[0-9]*' | head -n 1)

# 4. Get Product Detail
if [ ! -z "$PRODUCT_ID" ]; then
    echo -e "\n[4] Getting detail for product ID $PRODUCT_ID..."
    DETAIL_RESPONSE=$(curl -s -X GET "$BASE_URL/products/$PRODUCT_ID/detail" -H "$AUTH_HEADER")
    VARIANT_ID=$(echo $DETAIL_RESPONSE | grep -oP '(?<="id":)[0-9]*' | head -n 2 | tail -n 1)
else
    echo "SKIP: No product found to get detail."
fi

# 5. Add to Cart
if [ ! -z "$VARIANT_ID" ]; then
    echo -e "\n[5] Adding variant ID $VARIANT_ID to cart..."
    curl -s -X POST "$BASE_URL/cart/items" \
      -H "$AUTH_HEADER" \
      -H "Content-Type: application/json" \
      -d "{\"productVariantId\": $VARIANT_ID, \"quantity\": 2}" | json_pp
else
    echo "SKIP: No variant found to add to cart."
fi

# 6. Checkout
echo -e "\n[6] Performing checkout..."
curl -s -X POST "http://localhost:8080/api/checkout" \
  -H "$AUTH_HEADER" \
  -H "Content-Type: application/json" \
  -d "{\"shippingAddress\": \"123 Mock Street, Test City\", \"paymentMethod\": \"COD\", \"note\": \"Manual Test Order\"}" | json_pp

# 7. List Orders
echo -e "\n[7] Listing orders..."
curl -s -X GET "$BASE_URL/orders" -H "$AUTH_HEADER" | json_pp | head -n 20

echo -e "\n========== Testing Completed =========="
