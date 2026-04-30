const ACCESS_TOKEN_MAX_AGE = 60 * 60;
const REFRESH_TOKEN_MAX_AGE = 7 * 24 * 60 * 60;

const setCookie = (name: string, value: string, maxAge: number) => {
  if (typeof document === 'undefined') {
    return;
  }

  document.cookie = `${name}=${value}; path=/; max-age=${maxAge}; samesite=lax`;
};

const clearCookie = (name: string) => {
  if (typeof document === 'undefined') {
    return;
  }

  document.cookie = `${name}=; path=/; max-age=0; samesite=lax`;
  document.cookie = `${name}=; path=/; max-age=0; samesite=lax; secure`;
};

export const getCookie = (name: string) => {
  if (typeof document === 'undefined') {
    return null;
  }

  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);

  if (parts.length === 2) {
    return parts.pop()?.split(';').shift() ?? null;
  }

  return null;
};

export const getStoredToken = (key: 'accessToken' | 'refreshToken') => {
  if (typeof window === 'undefined') {
    return null;
  }

  return localStorage.getItem(key);
};

export const setAuthTokens = (accessToken: string, refreshToken: string) => {
  if (typeof window !== 'undefined') {
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
  }

  setCookie('accessToken', accessToken, ACCESS_TOKEN_MAX_AGE);
  setCookie('refreshToken', refreshToken, REFRESH_TOKEN_MAX_AGE);
};

export const clearAuthTokens = () => {
  if (typeof window !== 'undefined') {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
  }

  clearCookie('accessToken');
  clearCookie('refreshToken');
};

export const hasAuthToken = () => {
  return Boolean(
    getCookie('accessToken') ||
      getCookie('refreshToken') ||
      getStoredToken('accessToken') ||
      getStoredToken('refreshToken'),
  );
};

export const setLoggedOutFlag = () => {
  setCookie('isLoggedOut', '1', 24 * 60 * 60);
};

export const clearLoggedOutFlag = () => {
  clearCookie('isLoggedOut');
};

export const isLoggedOut = () => {
  return Boolean(getCookie('isLoggedOut'));
};
