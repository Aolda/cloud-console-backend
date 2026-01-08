#!/bin/bash
BASE_URL="http://localhost:8080"

echo "Testing endpoints..."

curl -s "$BASE_URL/api/v1/instances" | head -c 100
echo ""
curl -s "$BASE_URL/api/v1/networks" | head -c 100
echo ""
curl -s "$BASE_URL/api/v1/volumes" | head -c 100
echo ""

echo "Check response-dumps/ for OpenStack API dumps"