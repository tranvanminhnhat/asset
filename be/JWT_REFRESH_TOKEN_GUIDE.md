# JWT Refresh Token Implementation Guide

## 概要 (Tổng Quan)

Hệ thống đã được nâng cấp để hỗ trợ **JWT Refresh Token** với cơ chế tự động làm mới Access Token khi hết hạn.

## Flow Chính

```
1. User Login
   ↓
2. Nhận Access Token + Refresh Token
   ↓
3. Sử dụng Access Token cho các API call
   ↓
4. Access Token hết hạn (401 Unauthorized)
   ↓
5. FE tự động gọi /auth/refresh-token
   ↓
6. BE kiểm tra Refresh Token:
   ✓ Hợp lệ → Trả về Access Token mới
   ✗ Hết hạn/Sai → Bắt login lại
   ↓
7. FE cập nhật Access Token mới
   ↓
8. Retry API call với token mới
```

---

## API Endpoints

### 1. **POST /auth/login** - Đăng Nhập
Trả về cả Access Token và Refresh Token

**Request:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "email": "user@example.com",
    "status": "VERIFIED",
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  },
  "message": {
    "code": "auth.login.info.001",
    "description": "Login successful"
  }
}
```

**Token Details:**
- **Access Token**: 
  - Expiry: 1 hour
  - Type: `access`
  - Dùng để call API
  
- **Refresh Token**:
  - Expiry: 7 days
  - Type: `refresh`
  - Lưu ở localStorage hoặc secure storage

---

### 2. **POST /auth/refresh-token** - Làm Mới Access Token
Gọi endpoint này khi Access Token hết hạn

**Request:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response (200 OK):**
```json
{
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  },
  "message": {
    "code": "auth.refresh-token.info.001",
    "description": "Token refreshed successfully"
  }
}
```

**Error Cases:**
- `401 Unauthorized` - Refresh Token không hợp lệ/hết hạn
  ```json
  {
    "message": {
      "code": "auth.register.logic.error.002",
      "description": "Invalid or expired refresh token"
    }
  }
  ```
  → **Action**: Redirect user to login page

---

## Frontend Implementation (React + Redux Toolkit)

### 1. **Store Tokens**

```typescript
// src/store/auth/authSlice.ts
import { createSlice, PayloadAction } from '@reduxjs/toolkit';

interface AuthState {
  accessToken: string | null;
  refreshToken: string | null;
  userId: string | null;
  email: string | null;
  isAuthenticated: boolean;
}

const initialState: AuthState = {
  accessToken: localStorage.getItem('accessToken'),
  refreshToken: localStorage.getItem('refreshToken'),
  userId: localStorage.getItem('userId'),
  email: localStorage.getItem('email'),
  isAuthenticated: !!localStorage.getItem('accessToken'),
};

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    setAuth: (state, action: PayloadAction<{
      accessToken: string;
      refreshToken: string;
      userId: string;
      email: string;
    }>) => {
      state.accessToken = action.payload.accessToken;
      state.refreshToken = action.payload.refreshToken;
      state.userId = action.payload.userId;
      state.email = action.payload.email;
      state.isAuthenticated = true;

      // Persist to localStorage
      localStorage.setItem('accessToken', action.payload.accessToken);
      localStorage.setItem('refreshToken', action.payload.refreshToken);
      localStorage.setItem('userId', action.payload.userId);
      localStorage.setItem('email', action.payload.email);
    },
    setAccessToken: (state, action: PayloadAction<string>) => {
      state.accessToken = action.payload;
      localStorage.setItem('accessToken', action.payload);
    },
    logout: (state) => {
      state.accessToken = null;
      state.refreshToken = null;
      state.userId = null;
      state.email = null;
      state.isAuthenticated = false;

      // Clear localStorage
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('userId');
      localStorage.removeItem('email');
    },
  },
});

export const { setAuth, setAccessToken, logout } = authSlice.actions;
export default authSlice.reducer;
```

### 2. **Configure RTK Query with Refresh Token Interceptor**

```typescript
// src/store/api/baseApi.ts
import { createApi, fetchBaseQuery, BaseQueryFn, FetchArgs, FetchBaseQueryError } from '@reduxjs/toolkit/query/react';
import { RootState } from '../store';
import { setAccessToken, logout } from '../auth/authSlice';

const baseQueryWithReauth: BaseQueryFn<
  string | FetchArgs,
  unknown,
  FetchBaseQueryError
> = async (args, api, extraOptions) => {
  let result = await baseQuery(args, api, extraOptions);

  // If 401 Unauthorized, try to refresh token
  if (result.error?.status === 401) {
    const state = api.getState() as RootState;
    const refreshToken = state.auth.refreshToken;

    if (refreshToken) {
      const refreshResult = await baseQuery(
        {
          url: '/auth/refresh-token',
          method: 'POST',
          body: { refreshToken },
        },
        api,
        extraOptions
      );

      if (refreshResult.data) {
        const { accessToken, refreshToken: newRefreshToken } = (refreshResult.data as any).data;

        // Update tokens in Redux
        api.dispatch(setAccessToken(accessToken));

        // Retry original request with new token
        result = await baseQuery(args, api, extraOptions);
      } else {
        // Refresh token invalid/expired - logout user
        api.dispatch(logout());
        window.location.href = '/login?session-expired=true';
      }
    } else {
      // No refresh token - redirect to login
      api.dispatch(logout());
      window.location.href = '/login';
    }
  }

  return result;
};

const baseQuery = fetchBaseQuery({
  baseUrl: 'http://localhost:8080',
  prepareHeaders: (headers, { getState }) => {
    const token = (getState() as RootState).auth.accessToken;
    if (token) {
      headers.set('Authorization', `Bearer ${token}`);
    }
    return headers;
  },
});

export const baseApi = createApi({
  reducerPath: 'api',
  baseQuery: baseQueryWithReauth,
  endpoints: () => ({}),
});
```

### 3. **Login Handler (Updated)**

```typescript
// src/features/login/hooks/useLogin.ts
import { useLoginMutation } from '../services/login.api';
import { LoginRequest } from '../type/login.request';
import { useDispatch } from 'react-redux';
import { setAuth } from '@/src/store/auth/authSlice';
import { useNavigate } from 'react-router-dom';

export const useLogin = () => {
  const [login, { isLoading, isError, isSuccess, error }] = useLoginMutation();
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const handleLogin = async (credentials: LoginRequest) => {
    try {
      const result = await login(credentials).unwrap();
      const { userId, email, accessToken, refreshToken } = result.data;

      // Store tokens and user info in Redux
      dispatch(setAuth({
        accessToken,
        refreshToken,
        userId,
        email,
      }));

      // Redirect to dashboard
      navigate('/dashboard');
    } catch (err) {
      console.error('Login failed:', err);
    }
  };

  return { handleLogin, isLoading, isError, isSuccess, error };
};
```

### 4. **Logout Handler**

```typescript
// src/features/auth/hooks/useLogout.ts
import { useLogoutMutation } from '../services/auth.api';
import { useDispatch } from 'react-redux';
import { logout as logoutAction } from '@/src/store/auth/authSlice';
import { useNavigate } from 'react-router-dom';

export const useLogout = () => {
  const [logout] = useLogoutMutation();
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const handleLogout = async () => {
    try {
      await logout().unwrap();
    } catch (err) {
      console.error('Logout error:', err);
    } finally {
      // Clear Redux state regardless of API result
      dispatch(logoutAction());
      navigate('/login');
    }
  };

  return { handleLogout };
};
```

---

## Backend Token Validation

### Token Claims Structure

**Access Token:**
```json
{
  "sub": "550e8400-e29b-41d4-a716-446655440000",  // userId
  "email": "user@example.com",
  "type": "access",
  "iat": 1700000000,
  "exp": 1700003600
}
```

**Refresh Token:**
```json
{
  "sub": "550e8400-e29b-41d4-a716-446655440000",  // userId
  "email": "user@example.com",
  "type": "refresh",
  "iat": 1700000000,
  "exp": 1700604800
}
```

### Validation Rules

1. **Token Format**: Must start with "Bearer " prefix in Authorization header
2. **Token Type**: 
   - Access Token: Used for API calls (type: "access")
   - Refresh Token: Used only for /auth/refresh-token endpoint (type: "refresh")
3. **Token Signature**: Verified using HS256 secret key
4. **Token Expiry**: Checked against current timestamp
5. **Refresh Token Storage**: Stored in database for additional validation

---

## Error Handling

### Common Error Scenarios

| Scenario | Status | Action |
|----------|--------|--------|
| Invalid credentials | 401 | Show error message, stay on login |
| Access Token expired | 401 | Automatically call /auth/refresh-token |
| Refresh Token expired | 401 | Redirect to login page |
| Refresh Token not stored (tampering) | 401 | Redirect to login page |
| Network error during refresh | 500+ | Retry with exponential backoff, then logout |

### Implementation Example

```typescript
// src/services/apiErrorHandler.ts
export const handleApiError = (error: any) => {
  if (error.status === 401) {
    // Trigger refresh token flow automatically (handled by RTK Query)
    return;
  }

  if (error.status === 403) {
    // Forbidden - insufficient permissions
    showNotification('You do not have permission to access this resource');
    return;
  }

  if (error.status >= 500) {
    // Server error
    showNotification('Server error. Please try again later.');
    return;
  }

  // Other errors
  showNotification(error.data?.message?.description || 'An error occurred');
};
```

---

## Security Best Practices

✅ **DO:**
- Store Access Token in memory or short-lived localStorage
- Store Refresh Token in httpOnly cookie (if possible) or secure storage
- Validate token type before using it
- Clear all tokens on logout
- Use HTTPS in production
- Set proper CORS headers

❌ **DON'T:**
- Store sensitive tokens in sessionStorage
- Log tokens in console/analytics
- Send tokens in URL parameters
- Use same token for both access and refresh
- Keep tokens longer than necessary

---

## Configuration

### BE: `application.yaml`

```yaml
jwt:
  secret-key: "secret-key-demo-secret-key-demo-secret-key-demo"
  access-token-expiry: 3600000  # 1 hour in milliseconds
  refresh-token-expiry: 604800000  # 7 days in milliseconds

app:
  frontend:
    login-url: "http://localhost:3000/login"
```

### FE: `.env`

```env
VITE_API_BASE_URL=http://localhost:8080
VITE_LOGIN_PAGE_URL=http://localhost:3000/login
```

---

## Testing

### Test Cases

1. **Login with valid credentials**
   - Expect: 200 OK with accessToken + refreshToken

2. **Use accessToken to call API**
   - Expect: 200 OK with API response

3. **Wait for accessToken to expire (1 hour)**
   - Call API again
   - Expect: 401 Unauthorized, then auto-refresh, then retry succeeds

4. **Manually send invalid refreshToken**
   - Expect: 401 Unauthorized, redirect to login

5. **Logout**
   - Expect: Clear tokens, redirect to login page

### cURL Examples

```bash
# 1. Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"123456"}'

# 2. Use Access Token
curl -X GET http://localhost:8080/dashboard \
  -H "Authorization: Bearer <accessToken>"

# 3. Refresh Token
curl -X POST http://localhost:8080/auth/refresh-token \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"<refreshToken>"}'

# 4. Logout
curl -X POST http://localhost:8080/auth/logout \
  -H "Authorization: Bearer <accessToken>"
```

---

## Troubleshooting

### "Invalid or expired refresh token"
- Verify refresh token is being sent correctly
- Check token hasn't expired (7 days)
- Ensure token stored in DB matches sent token

### "User not found"
- Verify userId in token matches DB record
- Check user account hasn't been deleted

### "Token validation failed"
- Verify token format: "Bearer <token>"
- Check Authorization header is present
- Ensure token hasn't been modified

### Still can't login after refresh?
- Clear browser cache and localStorage
- Check server logs for detailed error
- Verify JWT secret key matches between BE and filters

---

## Summary

The new refresh token system provides:
- **Automatic token renewal** without user interruption
- **Secure token storage** with separate access/refresh tokens
- **Better security** with shorter access token lifespan
- **Graceful degradation** when refresh fails (logout)

All changes are backward compatible with existing code!

