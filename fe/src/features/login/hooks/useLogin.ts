import { useLoginMutation } from '../services/login.api';
import { LoginRequest } from '../type/login.request';
import { useEffect } from 'react';

export const useLogin = () => {
  const [login, { isLoading, isError, isSuccess, data }] = useLoginMutation();

  useEffect(() => {
    if (isSuccess && data) {
      // Store token in both localStorage and cookies for compatibility
      localStorage.setItem('accessToken', data.data.accessToken);
      document.cookie = `accessToken=${data.data.accessToken}; path=/; max-age=86400; samesite=lax`;
      document.cookie = 'isLoggedOut=; path=/; max-age=0; samesite=lax';
    }
  }, [isSuccess, data]);

  const handleLogin = async (data: LoginRequest) => {
    try {
      await login(data).unwrap();
    } catch (error) {
      console.error('Login failed:', error);
    }
  };

  return { handleLogin, isLoading, isError, isSuccess, data };
};
