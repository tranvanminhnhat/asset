## JWT Refresh Token Flow Diagram

### 1. LOGIN FLOW
```
┌─────────────────┐
│   FE: Login     │
│   Email/Pass    │
└────────┬────────┘
         │
         │ POST /auth/login
         ▼
┌─────────────────────┐
│ BE: AuthService     │
│ 1. Validate creds   │
│ 2. Load user        │
│ 3. Check verified   │
└────────┬────────────┘
         │
         │ Validate ✓
         ▼
┌────────────────────────────┐
│ BE: JwtTokenProvider       │
│ Generate Access Token:     │
│ - sub: userId              │
│ - email: user@mail         │
│ - type: "access"           │
│ - exp: now + 1 hour        │
│                            │
│ Generate Refresh Token:    │
│ - sub: userId              │
│ - email: user@mail         │
│ - type: "refresh"          │
│ - exp: now + 7 days        │
└────────┬───────────────────┘
         │
         │ Both generated
         ▼
┌──────────────────────────┐
│ BE: Database (user.token)│
│ Save refreshToken        │
└────────┬─────────────────┘
         │
         │ Saved ✓
         ▼
┌──────────────────────────────────┐
│ FE: Receive Response             │
│ {                                │
│   accessToken: "...",            │
│   refreshToken: "...",           │
│   userId: "...",                 │
│   email: "..."                   │
│ }                                │
└────────┬─────────────────────────┘
         │
         ▼
┌────────────────────────────────┐
│ FE: Store Tokens               │
│ Redux:                         │
│   - store accessToken          │
│   - store refreshToken         │
│   - store userId               │
│ localStorage:                  │
│   - refreshToken (optional)    │
│   - userId (optional)          │
└────────────────────────────────┘
         │
         ▼
   ✅ LOGIN SUCCESS
   Navigate to Dashboard
```

---

### 2. NORMAL API CALL (With Valid Token)
```
┌──────────────┐
│ FE: API Call │
│ GET /dash    │
└────────┬─────┘
         │
         │ Add to header:
         │ Authorization: Bearer <accessToken>
         ▼
┌─────────────────────────────┐
│ BE: JwtAuthenticationFilter │
│ 1. Extract token from       │
│    Authorization header     │
│ 2. Validate signature       │
│ 3. Check not expired        │
│ 4. Extract userId + email   │
└────────┬────────────────────┘
         │
         │ Validation ✓
         ▼
┌──────────────────────┐
│ BE: Load User        │
│ From Database by ID  │
└────────┬─────────────┘
         │
         │ User found ✓
         ▼
┌──────────────────────┐
│ BE: Set SecurityCtx  │
│ Principal = User     │
└────────┬─────────────┘
         │
         │ Security set ✓
         ▼
┌────────────────────┐
│ BE: Process Request│
│ Execute business   │
│ logic              │
└────────┬───────────┘
         │
         │ Success
         ▼
┌──────────────────┐
│ FE: Response 200 │
│ OK + Data        │
└──────────────────┘
         │
         ▼
   ✅ API CALL SUCCESS
```

---

### 3. EXPIRED ACCESS TOKEN FLOW (Auto-Refresh)
```
┌──────────────┐
│ FE: API Call │
│ GET /dash    │
└────────┬─────┘
         │
         │ Authorization: Bearer <EXPIRED_TOKEN>
         ▼
┌─────────────────────────────┐
│ BE: JwtAuthenticationFilter │
│ 1. Extract token            │
│ 2. Validate signature: ✓    │
│ 3. Check expiry: ✗ EXPIRED! │
└────────┬────────────────────┘
         │
         │ Invalid (expired)
         ▼
┌────────────────────┐
│ BE: Return 401     │
│ Unauthorized       │
└────────┬───────────┘
         │
         ▼
┌──────────────────────────────────┐
│ FE: Interceptor Detects 401      │
│ Check if we have refreshToken    │
└────────┬───────────────────────────┘
         │
         │ ✓ Have refreshToken
         ▼
┌────────────────────────────────────┐
│ FE: Auto-Call /auth/refresh-token  │
│ Body: { refreshToken: "..." }      │
└────────┬─────────────────────────────┘
         │
         │ POST /auth/refresh-token
         ▼
┌──────────────────────────────┐
│ BE: AuthService.refreshToken │
│ 1. Extract userId from token │
│ 2. Load user from DB         │
│ 3. Check stored token        │
│    matches provided token    │
└────────┬─────────────────────┘
         │
         │ Token match ✓
         │ Not expired ✓
         ▼
┌────────────────────────────────┐
│ BE: Generate New Tokens        │
│ - New accessToken (1 hour)     │
│ - New refreshToken (7 days)    │
└────────┬───────────────────────┘
         │
         │ New tokens created
         ▼
┌────────────────────────────┐
│ BE: Update DB              │
│ user.token = newRefresh    │
└────────┬───────────────────┘
         │
         │ DB saved ✓
         ▼
┌────────────────────────────────────┐
│ BE: Return 200 OK                  │
│ {                                  │
│   accessToken: "new...",           │
│   refreshToken: "new..."           │
│ }                                  │
└────────┬───────────────────────────┘
         │
         ▼
┌─────────────────────────────────────┐
│ FE: Interceptor Success             │
│ 1. Save new accessToken to Redux    │
│ 2. Save new refreshToken to storage │
│ 3. Retry original request           │
└────────┬────────────────────────────┘
         │
         │ RETRY: GET /dash
         │ Authorization: Bearer <NEW_TOKEN>
         ▼
┌──────────────────────────────┐
│ BE: JwtAuthenticationFilter  │
│ Validate new token:          │
│ - Signature ✓                │
│ - Expiry ✓ (just created)    │
└────────┬─────────────────────┘
         │
         │ Validation ✓
         ▼
┌────────────────────┐
│ BE: Process Request│
│ (as normal)        │
└────────┬───────────┘
         │
         │ Success
         ▼
┌──────────────────┐
│ FE: Response 200 │
│ OK + Data        │
└──────────────────┘
         │
         ▼
   ✅ AUTO-REFRESH SUCCESS
   User never saw error!
```

---

### 4. EXPIRED REFRESH TOKEN FLOW (Force Login)
```
┌──────────────┐
│ FE: API Call │
│ (any time)   │
└────────┬─────┘
         │
         │ Authorization: Bearer <EXPIRED_ACCESS>
         ▼
┌─────────────────────────────┐
│ BE: Returns 401             │
│ Unauthorized (expired)      │
└────────┬────────────────────┘
         │
         │ 401 Response
         ▼
┌──────────────────────────────────┐
│ FE: Interceptor Detects 401      │
│ Auto-call /auth/refresh-token    │
└────────┬───────────────────────────┘
         │
         │ Body: { refreshToken: "..." }
         ▼
┌──────────────────────────────────────┐
│ BE: AuthService.refreshToken()       │
│ 1. Extract userId from token        │
│ 2. Validate token signature: ✓      │
│ 3. Check expiry: ✗ EXPIRED!         │
│    (more than 7 days old)           │
└────────┬─────────────────────────────┘
         │
         │ Validation failed
         ▼
┌───────────────────────────┐
│ BE: Throw LogicException  │
│ "Invalid or expired       │
│  refresh token"           │
└────────┬──────────────────┘
         │
         │ 401 Response
         ▼
┌──────────────────────────────────┐
│ FE: Interceptor Catches Error    │
│ Check: refreshToken expired?     │
│ → YES, can't refresh             │
└────────┬───────────────────────────┘
         │
         │ Force logout
         ▼
┌────────────────────────────┐
│ FE: Dispatch logout()      │
│ 1. Clear Redux tokens      │
│ 2. Clear localStorage      │
│ 3. Redirect to /login      │
└────────────────────────────┘
         │
         ▼
┌──────────────────────────────────┐
│ FE: Show Message                 │
│ "Session expired. Please login   │
│  again."                         │
└──────────────────────────────────┘
         │
         ▼
   🔄 USER MUST LOGIN AGAIN
```

---

### 5. LOGOUT FLOW
```
┌──────────────┐
│ FE: Logout   │
│ Button Click │
└────────┬─────┘
         │
         │ POST /auth/logout
         │ Authorization: Bearer <accessToken>
         ▼
┌──────────────────────────┐
│ BE: AuthService.logout() │
│ 1. Clear SecurityCtx     │
└────────┬─────────────────┘
         │
         │ Cleared ✓
         ▼
┌────────────────────┐
│ BE: Return 200 OK  │
│ {message: "..."}   │
└────────┬───────────┘
         │
         ▼
┌──────────────────────────────┐
│ FE: Handle Logout Response   │
│ 1. Dispatch logout() action  │
│ 2. Clear Redux tokens        │
│ 3. Clear localStorage        │
└────────┬─────────────────────┘
         │
         │ Cleared ✓
         ▼
┌────────────────────────────┐
│ FE: Navigate to /login     │
│ Show: "Logged out"         │
└────────────────────────────┘
         │
         ▼
   ✅ LOGOUT COMPLETE
   All tokens cleared
```

---

## Summary Table

| Scenario | Token Status | BE Action | FE Action | Result |
|----------|--------------|-----------|-----------|--------|
| **Login** | N/A | Generate 2 tokens, save refresh to DB | Store both tokens | ✅ Success |
| **Normal API** | Valid | Validate, process | Add to header | ✅ Success |
| **Expired Access** | Expired | Return 401 | Auto-refresh with refresh token | ✅ Success (transparent) |
| **Expired Refresh** | Expired | Return 401 | No retry possible | ❌ Force login |
| **Invalid Token** | Invalid | Return 401 | No refresh available | ❌ Force login |
| **Logout** | Valid | Clear context | Clear storage, navigate | ✅ Success |

---

## Token Lifecycle

```
LOGIN (t=0)
│
├─ Access Token
│  │
│  ├─ Issued: t=0
│  ├─ Expires: t=1 hour
│  │
│  └─ Status: VALID ----→ EXPIRED ✗
│
└─ Refresh Token
   │
   ├─ Issued: t=0
   ├─ Expires: t=7 days
   │
   └─ Status: VALID ----→ VALID ----→ VALID ----→ EXPIRED ✗
                  (1h)        (2h)        (3h)        (7d)
                  ↓           ↓           ↓           ↓
            (auto-refresh triggers when access token expires)
```

---

## Code Flow in Sequence

```
FE                          BE
│                           │
├─ POST /auth/login ────────→
│                           ├─ Validate creds
│                           ├─ Generate tokens
│                           ├─ Save refresh to DB
│  ← 200 + tokens ──────────┤
│                           │
├─ Store tokens            │
│  (Redux + localStorage)   │
│                           │
├─ GET /protected ─────────→
│  (with accessToken)       ├─ Validate token
│  ← 200 + data ────────────┤
│                           │
│ [1 hour later]            │
│                           │
├─ GET /protected ─────────→
│  (with EXPIRED token)     ├─ Detect expired
│  ← 401 ───────────────────┤
│                           │
├─ POST /refresh-token ────→
│  (with refreshToken)      ├─ Validate refresh
│  ← 200 + new tokens ──────┤ ├─ Update DB
│                           │ ├─ Generate new
├─ Update tokens           │
│  (Redux + storage)        │
│                           │
├─ GET /protected ─────────→ (RETRY)
│  (with NEW token)         ├─ Validate token
│  ← 200 + data ────────────┤
│                           │
└─ Done!                    └─ Done!
```

