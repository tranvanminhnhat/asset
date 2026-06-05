import { useLoginMutation } from '../services/login.api';
import { LoginRequest } from '../type/login.request';
import { useEffect } from 'react';
import { clearLoggedOutFlag, setAuthTokens } from '@/common/utils/authSession';

export const useLogin = () => {
  const [login, { isLoading, isError, isSuccess, data }] = useLoginMutation();

  useEffect(() => {
    if (isSuccess && data) {
      setAuthTokens(data.data.accessToken, data.data.refreshToken);
      clearLoggedOutFlag();
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
