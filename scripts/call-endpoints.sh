#!/bin/bash

BASE_URL="http://localhost:8080"

echo "Calling endpoints to generate OpenStack API dumps..."

# OpenStack 관련 엔드포인트들 (GET 요청만)
endpoints=(
    "/api/v1/instances"
    "/api/v1/instance-types"
    "/api/v1/images"
    "/api/v1/keypairs"
    "/api/v1/networks"
    "/api/v1/interfaces"
    "/api/v1/routers"
    "/api/v1/security-groups"
    "/api/v1/security-rules"
    "/api/v1/volumes"
    "/api/v1/volume-snapshots"
    "/api/v1/projects"
)

for endpoint in "${endpoints[@]}"; do
    echo "Calling: $endpoint"
    curl -s -o /dev/null -w "Status: %{http_code}\n" "$BASE_URL$endpoint" || echo "  -> Error"
    sleep 1
done

echo "Done! Check response-dumps/ directory for OpenStack API dumps"