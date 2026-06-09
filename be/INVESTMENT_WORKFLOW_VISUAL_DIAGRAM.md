# 🎯 Investment Workflow - Visual Flow Diagram

## Complete User Flow with Status Transitions

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         INVESTMENT WORKFLOW COMPLETE FLOW                          │
└─────────────────────────────────────────────────────────────────────────────────────┘

                                    START
                                      ↓
                      ┌───────────────────────────┐
                      │  1. User Goes to Dashboard│
                      │  GET /allocation-master   │
                      │  [Loads Strategy Options] │
                      └───────────────┬───────────┘
                                      ↓
                      ┌───────────────────────────┐
                      │ 2. User Inputs Amount &   │
                      │    Clicks "Xem gợi ý"     │
                      │ POST /session (create)    │
                      │ [STATUS: DRAFT]           │
                      └───────────────┬───────────┘
                                      ↓
                      ┌───────────────────────────────────────┐
                      │ 3. System Generates Recommendations   │
                      │    GET /recommendations                │
                      │    Shows 4 Allocation Cards:           │
                      │    ├─ EMERGENCY (10%) - LOCKED ❌    │
                      │    ├─ GOLD (20%)                      │
                      │    ├─ ETF (50%)                       │
                      │    └─ STOCK (20%)                     │
                      └───────────────┬───────────────────────┘
                                      ↓


    ╔═══════════════════════════════════════════════════════════════╗
    ║        FOR EACH PROCESSABLE ALLOCATION (GOLD, ETF, STOCK)     ║
    ╠═══════════════════════════════════════════════════════════════╣
    ║                                                               ║
    ║  LOOP: Process Each Asset Type                               ║
    ║  ┌──────────────────────────────────────────────────────┐   ║
    ║  │ 4. User Clicks on Asset Type (e.g., ETF)           │   ║
    ║  │    [Status changes: NOT_STARTED]                    │   ║
    ║  │    GET /assets?type=ETF                             │   ║
    ║  │    [Show list of available assets]                  │   ║
    ║  └──────────────────┬───────────────────────────────────┘   ║
    ║                     ↓                                        ║
    ║  ┌──────────────────────────────────────────────────────┐   ║
    ║  │ 5. User Selects Asset & Enters Quantity             │   ║
    ║  │    POST /allocation/{id}/select                     │   ║
    ║  │    ├─ Validates: Qty > 0                            │   ║
    ║  │    ├─ Validates: (Qty × Price) ≤ Recommended        │   ║
    ║  │    ├─ Creates selection record                      │   ║
    ║  │    ├─ Updates allocation:                           │   ║
    ║  │    │  Status: IN_PROGRESS                           │   ║
    ║  │    │  investedAmount += new amount                  │   ║
    ║  │    │  residualAmount -= new amount                  │   ║
    ║  │    └─ Creates audit event                           │   ║
    ║  │    [Status changes: NOT_STARTED → IN_PROGRESS]     │   ║
    ║  └──────────────────┬───────────────────────────────────┘   ║
    ║                     ↓                                        ║
    ║  ┌──────────────────────────────────────────────────────┐   ║
    ║  │ 6. User Clicks "Hoàn thành" on Asset Card          │   ║
    ║  │    POST /allocation/{id}/complete                  │   ║
    ║  │    ├─ Marks allocation as COMPLETED                │   ║
    ║  │    ├─ Creates audit event                          │   ║
    ║  │    └─ Stays at same total (no recalculate)         │   ║
    ║  │    [Status changes: IN_PROGRESS → COMPLETED]       │   ║
    ║  └──────────────────┬───────────────────────────────────┘   ║
    ║                     ↓                                        ║
    ║  ┌──────────────────────────────────────────────────────┐   ║
    ║  │ 7. User Sees Updated Card with:                     │   ║
    ║  │    ✅ Invested Amount                               │   ║
    ║  │    💰 Residual Amount                               │   ║
    ║  │    ✔️ Completion Icon                               │   ║
    ║  └──────────────────┬───────────────────────────────────┘   ║
    ║                     ↓                                        ║
    ║         Is there another asset to process? YES → Back to 4 ║
    ║         (loop N times for N processable assets)              ║
    ║                                                               ║
    ╚═══════════════════════════════════════════════════════════════╝
                                      ↓
                      ┌───────────────────────────┐
                      │ 8. All Assets Processed   │
                      │    User Clicks "Tiếp tục" │
                      │ POST /review              │
                      │ [STATUS: DRAFT → REVIEW]  │
                      │ [STEP: RECOMMEND→REVIEW]  │
                      │                           │
                      │ System Calculates:        │
                      │ • totalInvested           │
                      │ • totalResidual           │
                      │ • percentages             │
                      └───────────────┬───────────┘
                                      ↓
                      ┌───────────────────────────┐
                      │ 9. Review Screen Show:    │
                      │    GET /detail (optional) │
                      │                           │
                      │    Breakdown by type:     │
                      │    • EMERGENCY: LOCKED    │
                      │    • GOLD: invested+resid │
                      │    • ETF: invested+resid  │
                      │    • STOCK: invested+resid│
                      │                           │
                      │    Totals:                │
                      │    • Total Invested       │
                      │    • Total Residual       │
                      └───────────────┬───────────┘
                                      ↓
                      ┌───────────────────────────┐
                      │ 10. User Confirms Review  │
                      │     Clicks "Xác nhận"     │
                      │ POST /finalize            │
                      │ [STATUS: REVIEW→COMPLETE] │
                      │ [STEP: REVIEW→FINALIZE]   │
                      │                           │
                      │ System Actions:           │
                      │ ✓ Sum all residuals       │
                      │ ✓ Add to cash balance     │
                      │ ✓ Create finalize event   │
                      │ ✓ Session → COMPLETED     │
                      └───────────────┬───────────┘
                                      ↓
                      ┌───────────────────────────┐
                      │ 11. Success Screen        │
                      │     Show:                 │
                      │     • Total Invested      │
                      │     • Residual Added      │
                      │     • New Cash Balance    │
                      │     • Status: COMPLETED ✅│
                      └───────────────┬───────────┘
                                      ↓
                                    END


════════════════════════════════════════════════════════════════════════════════════════

                     SESSION & ALLOCATION STATUS FLOW DIAGRAM

                          ┌──────────────────┐
                          │ SESSION STATUSES │
                          └──────────────────┘

                    DRAFT (初期状態)
                      ↓
                  POST /session
                      ↓
          ┌─────────────────────────┐
          │  STATUS: DRAFT          │
          │  STEP: RECOMMEND        │
          │  • Recommendations      │
          │  • Ready to select      │
          │  • Allocations created  │
          └─────────────┬───────────┘
                        ↓ (User selects assets)
          ┌─────────────────────────┐
          │  STATUS: DRAFT          │
          │  STEP: RECOMMEND        │
          │  • Allocations updated  │
          │  with selections        │
          └─────────────┬───────────┘
                        ↓ POST /review
          ┌─────────────────────────┐
          │  STATUS: REVIEW         │
          │  STEP: REVIEW           │
          │  • Total calculated     │
          │  • Ready to finalize    │
          └─────────────┬───────────┘
                        ↓ POST /finalize
          ┌─────────────────────────┐
          │  STATUS: COMPLETED ✅   │
          │  STEP: FINALIZE         │
          │  • Cash balance updated │
          │  • Session finished     │
          └─────────────────────────┘


                     ALLOCATION STATUS FLOW DIAGRAM

          ┌──────────────────────────────┐
          │  EMERGENCY ALLOCATION        │
          │  [Special Case]              │
          └──────────────────────────────┘
                    ↓
          ┌──────────────────────────────┐
          │ STATUS: LOCKED 🔒           │
          │ (Never changes)              │
          │ Cannot be selected by user   │
          │ Only counted in residuals    │
          └──────────────────────────────┘


          ┌──────────────────────────────┐
          │  OTHER ALLOCATIONS           │
          │  (GOLD, ETF, STOCK)          │
          └──────────────────────────────┘
                    ↓
          ┌──────────────────────────────┐
          │ STATUS: NOT_STARTED          │
          │ • No assets selected         │
          │ • investedAmount = 0         │
          │ • residualAmount = recommended
          └──────────────┬───────────────┘
                         ↓ POST /select
          ┌──────────────────────────────┐
          │ STATUS: IN_PROGRESS          │
          │ • Asset selected             │
          │ • investedAmount > 0         │
          │ • residualAmount updated     │
          └──────────────┬───────────────┘
                         ↓ POST /complete
          ┌──────────────────────────────┐
          │ STATUS: COMPLETED ✅         │
          │ • Allocation finalized       │
          │ • Ready for review           │
          └──────────────────────────────┘


════════════════════════════════════════════════════════════════════════════════════════

                         ALLOCATION DETAIL FLOW DIAGRAM

          Session: ID=8, Amount=10,000,000
          Strategy: "safe"

          ├─ Allocation 1 (EMERGENCY)
          │  ├─ Recommended: 1,000,000 (10%)
          │  ├─ Status: LOCKED 🔒
          │  ├─ investedAmount: 0
          │  ├─ residualAmount: 1,000,000
          │  └─ Selections: [] (empty, user cannot select)
          │
          ├─ Allocation 2 (ETF)
          │  ├─ Recommended: 5,000,000 (50%)
          │  ├─ Status: IN_PROGRESS → COMPLETED
          │  ├─ investedAmount: 1,005
          │  ├─ residualAmount: 4,998,995
          │  └─ Selections: [
          │     ├─ ETF001 × 10 = 1,005
          │     └─ (more selections possible)
          │    ]
          │
          ├─ Allocation 3 (GOLD)
          │  ├─ Recommended: 2,000,000 (20%)
          │  ├─ Status: IN_PROGRESS → COMPLETED
          │  ├─ investedAmount: 500,000
          │  ├─ residualAmount: 1,500,000
          │  └─ Selections: [
          │     ├─ GOLD001 × 5 = 250,000
          │     └─ (more selections possible)
          │    ]
          │
          └─ Allocation 4 (STOCK)
             ├─ Recommended: 2,000,000 (20%)
             ├─ Status: IN_PROGRESS → COMPLETED
             ├─ investedAmount: 1,000,000
             ├─ residualAmount: 1,000,000
             └─ Selections: [
                ├─ STOCK001 × 100 = 1,000,000
                └─ (more selections possible)
               ]

          ─────────────────────────────────────
          REVIEW TOTALS:
          • Total Invested:  1,005 + 500,000 + 1,000,000 = 1,501,005
          • Total Residual: 1,000,000 + 4,998,995 + 1,500,000 + 1,000,000 = 7,998,995
          ─────────────────────────────────────

          FINALIZE ACTION:
          • Add residual to cash: 7,998,995
          • New cash balance: 10,000,000 + 7,998,995 = 17,998,995

════════════════════════════════════════════════════════════════════════════════════════
```

## Key Points to Remember

### 🔐 Security
- All requests require **JWT token** with valid userId
- User can only access their own sessions
- Ownership is verified on every request

### ⚡ Performance
- Session with allocations loaded using **LEFT JOIN FETCH** (1 query)
- Asset selections fetched separately (N queries for allocations)
- All events logged to `investment_session_events` table

### ✔️ Validation Rules
1. **Quantity > 0** ← Check before POST select
2. **(Qty × Price) ≤ Recommended** ← Validation error if exceeds
3. **EMERGENCY is LOCKED** ← Skip in UI (no process button)
4. **Complete all processable allocations** ← Before review
5. **Session status REVIEW** ← Required for finalize

### 💾 Database Changes
- **investment_sessions** ← Created
- **investment_session_allocations** ← Created (with cascade delete)
- **investment_asset_selections** ← Created (with cascade delete)
- **investment_session_events** ← Created (audit trail)
- **user_cash_balance** ← Created (updated on finalize)

### 📊 Calculations
```
investedAmount = SUM(quantity × buyPrice) for all selections
residualAmount = recommendedAmount - investedAmount

totalInvested = SUM(allocation.investedAmount)
totalResidual = SUM(allocation.residualAmount)

newCashBalance = currentCashBalance + totalResidual
```

---

**All set! Ready to test the complete investment workflow! 🚀**

