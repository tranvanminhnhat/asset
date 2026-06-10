# 🎯 Finalize Investment Session API Guide

## Overview

The **Finalize Investment Session** API allows users to submit their final investment allocation amounts for each asset type, completing the investment workflow.

---

## API Endpoint

```
POST /api/v1/investment/{sessionId}/finalize
```

---

## Request Format

### Path Parameter
- `sessionId` (Long, required): The investment session ID

### Request Body

```json
{
  "allocations": [
    {
      "assetType": "GOLD",
      "finalAmount": 15000000
    },
    {
      "assetType": "ETF",
      "finalAmount": 20000000
    },
    {
      "assetType": "STOCK",
      "finalAmount": 18500000
    },
    {
      "assetType": "EMERGENCY",
      "finalAmount": 16500000
    }
  ]
}
```

### Field Descriptions

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `allocations` | Array | Yes | List of final allocations |
| `assetType` | String | Yes | Asset type (GOLD, ETF, STOCK, EMERGENCY) |
| `finalAmount` | BigDecimal | Yes | Final invested amount for this asset type |

---

## Response Format

### Success Response (200 OK)

```json
{
  "status": "success",
  "message": {
    "key": "investment.session.finalize.info.001",
    "text": "Investment session finalized successfully",
    "source": "InvestmentWorkflowService"
  },
  "data": {
    "sessionId": 18,
    "status": "COMPLETED",
    "totalInvested": 70000000,
    "totalResidual": 0,
    "updatedCashBalance": 30000000,
    "completedAt": "2026-06-10T09:45:23.123456",
    "allocations": [
      {
        "allocationId": 1,
        "assetType": "GOLD",
        "recommendedAmount": 15000000,
        "investedAmount": 15000000,
        "residualAmount": 0,
        "status": "COMPLETED"
      },
      {
        "allocationId": 2,
        "assetType": "ETF",
        "recommendedAmount": 20000000,
        "investedAmount": 20000000,
        "residualAmount": 0,
        "status": "COMPLETED"
      },
      {
        "allocationId": 3,
        "assetType": "STOCK",
        "recommendedAmount": 18500000,
        "investedAmount": 18500000,
        "residualAmount": 0,
        "status": "COMPLETED"
      },
      {
        "allocationId": 4,
        "assetType": "EMERGENCY",
        "recommendedAmount": 16500000,
        "investedAmount": 16500000,
        "residualAmount": 0,
        "status": "COMPLETED"
      }
    ]
  }
}
```

### Field Descriptions

| Field | Type | Description |
|-------|------|-------------|
| `sessionId` | Long | Investment session ID |
| `status` | String | Session status (COMPLETED) |
| `totalInvested` | BigDecimal | Total amount invested across all allocations |
| `totalResidual` | BigDecimal | Total residual amount (unused) |
| `updatedCashBalance` | BigDecimal | User's updated cash balance |
| `completedAt` | LocalDateTime | Completion timestamp |
| `allocations` | Array | Details of each finalized allocation |
| `allocations[].allocationId` | Long | Allocation ID |
| `allocations[].assetType` | String | Asset type |
| `allocations[].recommendedAmount` | BigDecimal | Originally recommended amount |
| `allocations[].investedAmount` | BigDecimal | Final invested amount |
| `allocations[].residualAmount` | BigDecimal | Leftover amount |
| `allocations[].status` | String | Allocation status (COMPLETED) |

---

## Error Responses

### 400 Bad Request - Invalid Final Amount

```json
{
  "status": "error",
  "message": {
    "key": "error",
    "text": "Error: Final amount 25000000 exceeds recommended amount 20000000 for ETF",
    "source": "InvestmentWorkflowService"
  },
  "data": null
}
```

**Causes:**
- `finalAmount` exceeds `recommendedAmount`
- `finalAmount` is negative
- `assetType` doesn't exist in session

### 400 Bad Request - Invalid Session Status

```json
{
  "status": "error",
  "message": {
    "key": "error",
    "text": "Error: Session must be in REVIEW status to finalize",
    "source": "InvestmentWorkflowService"
  },
  "data": null
}
```

**Causes:**
- Session is not in REVIEW status
- Session has already been finalized

### 404 Not Found - Session Not Found

```json
{
  "status": "error",
  "message": {
    "key": "error",
    "text": "Error: Investment session not found",
    "source": "InvestmentWorkflowService"
  },
  "data": null
}
```

---

## Business Logic

### 1. Validation Phase
- ✅ Session exists and belongs to current user
- ✅ Session status is REVIEW
- ✅ User's cash balance exists
- ✅ Each finalAmount is non-negative
- ✅ Each finalAmount doesn't exceed recommendedAmount

### 2. Data Loading Phase
- Load session with all allocations
- Verify user ownership
- Load user's cash balance

### 3. Processing Phase
- For each allocation in request:
  - Update `investedAmount` = `finalAmount`
  - Calculate `residualAmount` = `recommendedAmount` - `finalAmount`
  - Set allocation status to `COMPLETED`
  - Save allocation to database
- Calculate total invested and total residual
- Add residual amount to user's cash balance
- Update session status to `COMPLETED`
- Create finalization event

### 4. Response Phase
- Return all updated allocations with final amounts
- Include user's new cash balance
- Include session completion timestamp

---

## Database Changes

### Tables Updated

#### `investment_session_allocations`
```sql
UPDATE investment_session_allocations
SET 
  invested_amount = ?,      -- finalAmount from request
  residual_amount = ?,       -- recommendedAmount - finalAmount
  status = 'COMPLETED',     -- allocation status
  updated_at = NOW()
WHERE session_id = ? AND asset_type = ?
```

#### `user_cash_balances`
```sql
UPDATE user_cash_balances
SET 
  cash_amount = cash_amount + ?,  -- add totalResidual
  updated_at = NOW()
WHERE user_id = ?
```

#### `investment_sessions`
```sql
UPDATE investment_sessions
SET 
  status = 'COMPLETED',
  current_step = 'FINALIZE',
  updated_at = NOW()
WHERE id = ?
```

#### `investment_session_events` (INSERT)
```sql
INSERT INTO investment_session_events (session_id, event_type, payload, created_at)
VALUES (?, 'SESSION_FINALIZED', '{"totalInvested": ..., ...}', NOW())
```

---

## Example Usage

### Request

```bash
curl -X POST "http://localhost:8080/api/v1/investment/18/finalize" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "allocations": [
      {
        "assetType": "GOLD",
        "finalAmount": 15000000
      },
      {
        "assetType": "ETF",
        "finalAmount": 20000000
      },
      {
        "assetType": "STOCK",
        "finalAmount": 18500000
      },
      {
        "assetType": "EMERGENCY",
        "finalAmount": 16500000
      }
    ]
  }'
```

### Response

```json
{
  "status": "success",
  "message": {
    "key": "investment.session.finalize.info.001",
    "text": "Investment session finalized successfully",
    "source": "InvestmentWorkflowService"
  },
  "data": {
    "sessionId": 18,
    "status": "COMPLETED",
    "totalInvested": 70000000,
    "totalResidual": 0,
    "updatedCashBalance": 30000000,
    "completedAt": "2026-06-10T09:45:23.123456",
    "allocations": [
      {
        "allocationId": 54,
        "assetType": "GOLD",
        "recommendedAmount": 15000000,
        "investedAmount": 15000000,
        "residualAmount": 0,
        "status": "COMPLETED"
      },
      {
        "allocationId": 55,
        "assetType": "ETF",
        "recommendedAmount": 20000000,
        "investedAmount": 20000000,
        "residualAmount": 0,
        "status": "COMPLETED"
      },
      {
        "allocationId": 56,
        "assetType": "STOCK",
        "recommendedAmount": 18500000,
        "investedAmount": 18500000,
        "residualAmount": 0,
        "status": "COMPLETED"
      },
      {
        "allocationId": 57,
        "assetType": "EMERGENCY",
        "recommendedAmount": 16500000,
        "investedAmount": 16500000,
        "residualAmount": 0,
        "status": "COMPLETED"
      }
    ]
  }
}
```

---

## Common Scenarios

### Scenario 1: Partial Investment
User invests less than recommended amount

```json
{
  "allocations": [
    {
      "assetType": "GOLD",
      "finalAmount": 12000000    // Less than 15M recommended
    },
    {
      "assetType": "ETF",
      "finalAmount": 20000000
    },
    {
      "assetType": "STOCK",
      "finalAmount": 18500000
    },
    {
      "assetType": "EMERGENCY",
      "finalAmount": 16500000
    }
  ]
}
```

**Result:**
- Gold allocation: `investedAmount = 12M`, `residualAmount = 3M`
- Residual 3M is added back to cash balance
- Total cash balance increases

### Scenario 2: No Investment for Asset Type
User doesn't invest in one asset class

```json
{
  "allocations": [
    {
      "assetType": "GOLD",
      "finalAmount": 0    // No investment
    },
    {
      "assetType": "ETF",
      "finalAmount": 30000000    // More than recommended
    }
    // ... others
  ]
}
```

**Result:**
- Validation fails: finalAmount exceeds recommendedAmount for ETF

---

## Frontend Integration

### TypeScript/React Example

```typescript
interface FinalizeAllocation {
  assetType: string;
  finalAmount: number;
}

interface FinalizeRequest {
  allocations: FinalizeAllocation[];
}

const finalizeInvestment = async (sessionId: number, request: FinalizeRequest) => {
  const response = await fetch(
    `http://localhost:8080/api/v1/investment/${sessionId}/finalize`,
    {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${jwtToken}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(request)
    }
  );

  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message.text);
  }

  const result = await response.json();
  return result.data;  // Contains allocations, totalInvested, etc.
};

// Usage
const finalizeData = await finalizeInvestment(18, {
  allocations: [
    { assetType: 'GOLD', finalAmount: 15000000 },
    { assetType: 'ETF', finalAmount: 20000000 },
    { assetType: 'STOCK', finalAmount: 18500000 },
    { assetType: 'EMERGENCY', finalAmount: 16500000 }
  ]
});

console.log('Investment completed at:', finalizeData.completedAt);
console.log('Total invested:', finalizeData.totalInvested);
console.log('Updated cash balance:', finalizeData.updatedCashBalance);
```

---

## Key Points

✅ **One-way Operation**: Once finalized, session cannot be modified or re-opened
✅ **Atomic Transactions**: All updates happen together or none at all
✅ **Validation Strict**: finalAmount must be ≤ recommendedAmount
✅ **Residual Handling**: Any unspent amount returns to user's cash
✅ **Audit Trail**: Event created for every finalization
✅ **User Isolation**: Users can only finalize their own sessions

---

## Troubleshooting

### Issue: "Session must be in REVIEW status"
**Solution**: Ensure session has been reviewed before finalizing

### Issue: "Final amount exceeds recommended amount"
**Solution**: Reduce finalAmount to be ≤ recommendedAmount

### Issue: "Investment session not found"
**Solution**: Verify sessionId exists and belongs to the current user

---

## Related APIs

- `POST /api/v1/investment` - Create session
- `GET /api/v1/investment/{sessionId}/recommendation` - Get recommendations
- `GET /api/v1/investment/{sessionId}/detail` - Get session details
- `POST /api/v1/investment/{sessionId}/review` - Review session

