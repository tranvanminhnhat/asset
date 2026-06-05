import type { BaseQueryFn, FetchArgs, FetchBaseQueryError } from '@reduxjs/toolkit/query';
import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';
import {
  clearAuthTokens,
  clearLoggedOutFlag,
  getCookie,
  getStoredToken,
  setAuthTokens,
  setLoggedOutFlag,
} from '@/common/utils/authSession';

interface ApiResponse<T> {
  data?: T;
}

interface RefreshTokenPayload {
  accessToken: string;
  refreshToken: string;
}

const getResponsePayload = <T>(response: unknown): T | null => {
  if (!response || typeof response !== 'object') {
    return null;
  }

  const wrappedResponse = response as ApiResponse<T>;
  if (wrappedResponse.data && typeof wrappedResponse.data === 'object') {
    return wrappedResponse.data;
  }

  return response as T;
};

const rawBaseQuery = fetchBaseQuery({
  baseUrl: process.env.NEXT_PUBLIC_API_URL,
  prepareHeaders: (headers) => {
    const token = getCookie('accessToken') || getStoredToken('accessToken');
    if (token) {
      headers.set('Authorization', `Bearer ${token}`);
    }

    return headers;
  },
});

const shouldRefreshToken = (url: string) => {
  return !url.includes('/auth/login') && !url.includes('/auth/refresh-token') && !url.includes('/auth/logout');
};

const isAuthFailureStatus = (status?: number | string) => {
  return status === 401 || status === 403;
};

const redirectToLogin = () => {
  if (typeof window === 'undefined') {
    return;
  }

  const { pathname, search } = window.location;
  if (pathname === '/login' || pathname.startsWith('/login/')) {
    return;
  }

  const currentPath = `${pathname}${search}`;
  window.location.replace(`/login?from=${encodeURIComponent(currentPath)}`);
};

const clearSessionAndRedirect = () => {
  clearAuthTokens();
  setLoggedOutFlag();
  redirectToLogin();
};

const baseQueryWithReauth: BaseQueryFn<string | FetchArgs, unknown, FetchBaseQueryError> = async (
  args,
  api,
  extraOptions,
) => {
  let result = await rawBaseQuery(args, api, extraOptions);
  const requestUrl = typeof args === 'string' ? args : args.url;

  if (!isAuthFailureStatus(result.error?.status) || !shouldRefreshToken(requestUrl)) {
    return result;
  }

  const refreshToken = getCookie('refreshToken') || getStoredToken('refreshToken');
  if (!refreshToken) {
    clearSessionAndRedirect();
    return result;
  }

  const refreshResult = await rawBaseQuery(
    {
      url: '/auth/refresh-token',
      method: 'POST',
      body: { refreshToken },
    },
    api,
    extraOptions,
  );

  if (refreshResult.error) {
    clearSessionAndRedirect();
    return result;
  }

  const payload = getResponsePayload<RefreshTokenPayload>(refreshResult.data);
  if (!payload?.accessToken || !payload?.refreshToken) {
    clearSessionAndRedirect();
    return result;
  }

  setAuthTokens(payload.accessToken, payload.refreshToken);
  clearLoggedOutFlag();
  result = await rawBaseQuery(args, api, extraOptions);

  return result;
};

export const baseApi = createApi({
  reducerPath: 'api',
  baseQuery: baseQueryWithReauth,
  tagTypes: ['AllocationMaster'],
  endpoints: () => ({}),
});