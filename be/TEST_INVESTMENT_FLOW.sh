#!/bin/bash
# ============================================================
# Investment Workflow - cURL Test Commands
# ============================================================
# Prerequisites:
# 1. Backend running at http://localhost:8080
# 2. Valid JWT token obtained from login
# 3. Test data setup created

# Set these variables:
BASE_URL="http://localhost:8080"
JWT_TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."  # Replace with actual token
USER_ID="test-user-001"

echo "=================================="
echo "Investment Workflow - Test Suite"
echo "=================================="
echo "Base URL: $BASE_URL"
echo "User ID: $USER_ID"
echo ""

# ============================================================
# Phase 0: Get Allocation Master
# ============================================================
echo "📋 [Phase 0] Getting Allocation Master Strategies..."
echo "==> GET /api/v1/allocation-master"
curl -X GET "$BASE_URL/api/v1/allocation-master" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -s | jq '.'
echo ""
echo ""

# ============================================================
# Phase 1: Create Investment Session
# ============================================================
echo "📝 [Phase 1] Creating Investment Session..."
echo "==> POST /api/v1/investment/session"
RESPONSE=$(curl -X POST "$BASE_URL/api/v1/investment/session" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "strategyCode": "safe",
    "totalAmount": 10000000
  }' \
  -s)

echo "$RESPONSE" | jq '.'

# Extract sessionId from response
SESSION_ID=$(echo "$RESPONSE" | jq -r '.data.sessionId')
echo "✅ SessionId: $SESSION_ID"
echo ""
echo ""

# Store for later use
if [[ "$SESSION_ID" == "null" ]] || [[ -z "$SESSION_ID" ]]; then
  echo "❌ ERROR: Could not extract sessionId. Exiting."
  exit 1
fi

# ============================================================
# Phase 2: Get Recommendations
# ============================================================
echo "🎯 [Phase 2] Getting Recommendations..."
echo "==> GET /api/v1/investment/$SESSION_ID/recommendations"
RECOMMENDATIONS=$(curl -X GET "$BASE_URL/api/v1/investment/$SESSION_ID/recommendations" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -s)

echo "$RECOMMENDATIONS" | jq '.'
echo ""
echo ""

# ============================================================
# Phase 3a: Get ETF Assets
# ============================================================
echo "📊 [Phase 3a] Getting Available ETF Assets..."
echo "==> GET /api/v1/assets?type=ETF"
ETF_ASSETS=$(curl -X GET "$BASE_URL/api/v1/assets?type=ETF" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -s)

echo "$ETF_ASSETS" | jq '.'

# Extract first ETF asset code
ETF_CODE=$(echo "$ETF_ASSETS" | jq -r '.data[0].assetCode')
echo "✅ Selected ETF: $ETF_CODE"
echo ""
echo ""

# ============================================================
# Phase 3b: Select ETF Asset
# ============================================================
echo "🎲 [Phase 3b] Selecting ETF Asset..."
echo "==> POST /api/v1/investment/$SESSION_ID/allocation/2/select"
echo "Allocation ID 2 = ETF (based on 'safe' strategy)"

SELECTION=$(curl -X POST "$BASE_URL/api/v1/investment/$SESSION_ID/allocation/2/select" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "assetCode": "'$ETF_CODE'",
    "quantity": 10
  }' \
  -s)

echo "$SELECTION" | jq '.'

SELECTION_ID=$(echo "$SELECTION" | jq -r '.data.selectionId')
echo "✅ Selection ID: $SELECTION_ID"
echo ""
echo ""

# ============================================================
# Phase 3c: Complete ETF Allocation
# ============================================================
echo "✔️ [Phase 3c] Completing ETF Allocation..."
echo "==> POST /api/v1/investment/$SESSION_ID/allocation/2/complete"

curl -X POST "$BASE_URL/api/v1/investment/$SESSION_ID/allocation/2/complete" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -s | jq '.'
echo ""
echo ""

# ============================================================
# Phase 3d: Get GOLD Assets & Process
# ============================================================
echo "📊 [Phase 3d] Getting Available GOLD Assets..."
echo "==> GET /api/v1/assets?type=GOLD"
GOLD_ASSETS=$(curl -X GET "$BASE_URL/api/v1/assets?type=GOLD" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -s)

echo "$GOLD_ASSETS" | jq '.'

GOLD_CODE=$(echo "$GOLD_ASSETS" | jq -r '.data[0].assetCode')
echo "✅ Selected GOLD: $GOLD_CODE"
echo ""

echo "🎲 [Phase 3d] Selecting GOLD Asset..."
echo "==> POST /api/v1/investment/$SESSION_ID/allocation/3/select"

curl -X POST "$BASE_URL/api/v1/investment/$SESSION_ID/allocation/3/select" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "assetCode": "'$GOLD_CODE'",
    "quantity": 5
  }' \
  -s | jq '.'
echo ""

echo "✔️ Completing GOLD Allocation..."
echo "==> POST /api/v1/investment/$SESSION_ID/allocation/3/complete"

curl -X POST "$BASE_URL/api/v1/investment/$SESSION_ID/allocation/3/complete" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -s | jq '.'
echo ""
echo ""

# ============================================================
# Phase 3e: Get STOCK Assets & Process
# ============================================================
echo "📊 [Phase 3e] Getting Available STOCK Assets..."
echo "==> GET /api/v1/assets?type=STOCK"
STOCK_ASSETS=$(curl -X GET "$BASE_URL/api/v1/assets?type=STOCK" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -s)

echo "$STOCK_ASSETS" | jq '.'

STOCK_CODE=$(echo "$STOCK_ASSETS" | jq -r '.data[0].assetCode')
echo "✅ Selected STOCK: $STOCK_CODE"
echo ""

echo "🎲 [Phase 3e] Selecting STOCK Asset..."
echo "==> POST /api/v1/investment/$SESSION_ID/allocation/4/select"

curl -X POST "$BASE_URL/api/v1/investment/$SESSION_ID/allocation/4/select" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "assetCode": "'$STOCK_CODE'",
    "quantity": 100
  }' \
  -s | jq '.'
echo ""

echo "✔️ Completing STOCK Allocation..."
echo "==> POST /api/v1/investment/$SESSION_ID/allocation/4/complete"

curl -X POST "$BASE_URL/api/v1/investment/$SESSION_ID/allocation/4/complete" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -s | jq '.'
echo ""
echo ""

# ============================================================
# Phase 5: Review Investment Session
# ============================================================
echo "👁️ [Phase 5] Reviewing Investment Session..."
echo "==> POST /api/v1/investment/$SESSION_ID/review"

REVIEW=$(curl -X POST "$BASE_URL/api/v1/investment/$SESSION_ID/review" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -s)

echo "$REVIEW" | jq '.'
echo ""
echo ""

# ============================================================
# Phase 6: Get Session Details
# ============================================================
echo "📋 [Phase 6] Getting Complete Session Details..."
echo "==> GET /api/v1/investment/$SESSION_ID/detail"

DETAILS=$(curl -X GET "$BASE_URL/api/v1/investment/$SESSION_ID/detail" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -s)

echo "$DETAILS" | jq '.'
echo ""
echo ""

# ============================================================
# Phase 7: Finalize Investment Session
# ============================================================
echo "🎉 [Phase 7] Finalizing Investment Session..."
echo "==> POST /api/v1/investment/$SESSION_ID/finalize"

FINALIZE=$(curl -X POST "$BASE_URL/api/v1/investment/$SESSION_ID/finalize" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -s)

echo "$FINALIZE" | jq '.'
echo ""
echo ""

echo "=================================="
echo "✅ Test Suite Completed!"
echo "=================================="
echo "Session ID: $SESSION_ID"
echo "Check database for updated cash balance"
echo ""

