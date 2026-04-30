// FRONTEND EXAMPLE: RTK Query with Auto-Refresh Token
// File: src/store/api/baseApi.ts

import { createApi, fetchBaseQuery, BaseQueryFn, FetchArgs, FetchBaseQueryError } from '@reduxjs/toolkit/query/react';
import { RootState } from '../store';
import { setAccessToken, logout } from '../auth/authSlice';

// Create base query
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

// Wrapper with auto-refresh logic
const baseQueryWithReauth: BaseQueryFn<
  string | FetchArgs,
  unknown,
  FetchBaseQueryError
> = async (args, api, extraOptions) => {
  let result = await baseQuery(args, api, extraOptions);

  // If 401 Unauthorized
  if (result.error?.status === 401) {
    const state = api.getState() as RootState;
    const refreshToken = state.auth.refreshToken;

    if (refreshToken) {
      try {
        // Try to refresh token
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
          const newTokens = (refreshResult.data as any).data;

          // Update tokens in Redux
          api.dispatch(setAccessToken(newTokens.accessToken));

          // Also save new refresh token
          localStorage.setItem('refreshToken', newTokens.refreshToken);

          // Retry original request with new token
          result = await baseQuery(args, api, extraOptions);
        } else {
          // Refresh failed - logout user
          api.dispatch(logout());
          window.location.href = '/login?session-expired=true';
        }
      } catch (error) {
        api.dispatch(logout());
        window.location.href = '/login?session-expired=true';
      }
    } else {
      // No refresh token available
      api.dispatch(logout());
      window.location.href = '/login';
    }
  }

  return result;
};

export const baseApi = createApi({
  reducerPath: 'api',
  baseQuery: baseQueryWithReauth,
  endpoints: () => ({}),
});

