import React, { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { loginSchema, type LoginForm } from '../validation/login.schema';
import { useLogin } from '../hooks/useLogin';
import Input from '@/common/components/ui/Input';
import Button from '@/common/components/ui/Button';

const LoginForm: React.FC = () => {
  const router = useRouter();
  const { handleLogin, isLoading, isError, isSuccess } = useLogin();

  useEffect(() => {
    if (isSuccess) {
      window.location.href = '/dashboard';
    }
  }, [isSuccess]);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginForm>({
    resolver: zodResolver(loginSchema),
  });

  const onSubmit = (data: LoginForm) => {
    handleLogin(data);
  };

  return (
    <div className="w-full max-w-sm">
      {/* Logo */}
      <div className="text-center mb-8">
        <div className="inline-flex items-center justify-center w-12 h-12 rounded-2xl bg-emerald-600 mb-4 shadow-lg shadow-emerald-600/30">
          <svg className="w-6 h-6 text-white" fill="currentColor" viewBox="0 0 20 20">
            <path d="M2 10a8 8 0 1116 0A8 8 0 012 10zm6.293-4.707a1 1 0 011.414 0L14 9.586l-1.414 1.414L11 9.414V14a1 1 0 11-2 0V9.414l-1.586 1.586L5.999 9.586l2.294-4.293z" />
          </svg>
        </div>
        <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Đăng nhập</h1>
        <p className="mt-1.5 text-sm text-slate-500">
          Chào mừng trở lại MoneyPilot
        </p>
      </div>

      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-7">
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <Input
            label="Địa chỉ Email"
            id="email"
            type="email"
            {...register('email')}
            error={errors.email?.message}
            placeholder="email@example.com"
          />

          <Input
            label="Mật khẩu"
            id="password"
            type="password"
            {...register('password')}
            error={errors.password?.message}
            placeholder="••••••••"
          />

          <Button
            type="submit"
            variant="secondary"
            size="lg"
            loading={isLoading}
            className="w-full mt-1"
          >
            {isLoading ? 'Đang đăng nhập...' : 'Đăng nhập'}
          </Button>

          {isError && (
            <p className="text-center text-xs text-red-600 bg-red-50 border border-red-100 rounded-lg py-2.5">
              Email hoặc mật khẩu không chính xác. Vui lòng thử lại.
            </p>
          )}
          {isSuccess && (
            <p className="text-center text-xs text-emerald-700 bg-emerald-50 border border-emerald-100 rounded-lg py-2.5">
              Đăng nhập thành công! Đang chuyển hướng...
            </p>
          )}
        </form>

        <div className="mt-5 pt-5 border-t border-slate-100 text-center">
          <p className="text-sm text-slate-500">
            Chưa có tài khoản?{' '}
            <Link href="/register" className="font-semibold text-emerald-600 hover:text-emerald-700 transition-colors">
              Đăng ký ngay
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default LoginForm;
