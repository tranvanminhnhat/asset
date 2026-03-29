import React from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { registerSchema, RegisterForm } from '../validation/register.schema';
import { useRegister } from '../hooks/useRegister';

const LoginForm: React.FC = () => {
  const { handleRegister, isLoading, isError, isSuccess } = useRegister();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<RegisterForm>({
    resolver: zodResolver(registerSchema),
  });

  const onSubmit = (data: RegisterForm) => {
    handleRegister(data);
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <div>
        <label>Email:</label>
        <input type="email" {...register('email')} />
        {errors.email && <span>{errors.email.message}</span>}
      </div>
      <div>
        <label>Password:</label>
        <input type="password" {...register('password')} />
        {errors.password && <span>{errors.password.message}</span>}
      </div>
      <div>
        <label>Confirm Password:</label>
        <input type="password" {...register('confirmPassword')} />
        {errors.confirmPassword && <span>{errors.confirmPassword.message}</span>}
      </div>
      <button type="submit" disabled={isLoading}>
        Register
      </button>
      {isError && <span>Registration failed</span>}
      {isSuccess && <span>Registration successful</span>}
    </form>
  );
};

export default LoginForm;
