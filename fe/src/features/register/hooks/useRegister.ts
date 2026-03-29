import { useRegisterMutation } from '../services/register.api';
import { RegisterRequest } from '../type/register.request';

export const useRegister = () => {
  const [register, { isLoading, isError, isSuccess }] = useRegisterMutation();

  const handleRegister = async (data: RegisterRequest) => {
    try {
      await register(data).unwrap();
    } catch (error) {
      console.error('Registration failed:', error);
    }
  };

  return { handleRegister, isLoading, isError, isSuccess };
};
