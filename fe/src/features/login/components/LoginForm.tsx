import React, { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { loginSchema } from '../validation/login.schema';
import { useLogin } from '../hooks/useLogin';
import { LoginFormData } from '../type/login.form';

const LoginForm: React.FC = () => {
  const router = useRouter();
  const { handleLogin, isLoading, isError, isSuccess } = useLogin();

  useEffect(() => {
    if (isSuccess) {
      router.push('/dashboard');
    }
  }, [isSuccess, router]);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
  });

  const onSubmit = (data: LoginFormData) => {
    handleLogin(data);
  };

  return (
    <div className="max-w-md w-full mx-auto rounded-[32px] bg-white border border-slate-100 p-8 shadow-2xl">
      <div className="text-center mb-8">
        <div className="inline-flex items-center justify-center w-14 h-14 rounded-full bg-[#fff4d9] text-[#b28715] mb-4 shadow-sm">
          <svg viewBox="0 0 24 24" className="w-7 h-7" fill="currentColor" aria-hidden="true">
            <path d="M12 2l1.9 4.85 5.35.78-3.85 3.76.91 5.32L12 14.77l-4.31 2.27.91-5.32L4.75 7.63l5.35-.78L12 2z" />
          </svg>
        </div>
        <p className="text-3xl font-semibold text-emerald-950">Đăng nhập MoneyPilot</p>
        <p className="mt-3 text-sm text-slate-500 leading-6">
          Nhập email và mật khẩu để tiếp tục quản lý tài chính của bạn.
        </p>
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
        <div>
          <label htmlFor="email" className="block text-sm font-medium text-slate-700 mb-2">
            Địa chỉ Email
          </label>
          <input
            id="email"
            type="email"
            {...register('email')}
            className="w-full rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-900 shadow-sm outline-none transition focus:border-emerald-500 focus:bg-white focus:ring-2 focus:ring-emerald-100"
            placeholder="email@example.com"
          />
          {errors.email && (
            <p className="mt-2 text-sm text-red-600">{errors.email.message}</p>
          )}
        </div>

        <div>
          <label htmlFor="password" className="block text-sm font-medium text-slate-700 mb-2">
            Mật khẩu
          </label>
          <input
            id="password"
            type="password"
            {...register('password')}
            className="w-full rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-900 shadow-sm outline-none transition focus:border-emerald-500 focus:bg-white focus:ring-2 focus:ring-emerald-100"
            placeholder="********"
          />
          {errors.password && (
            <p className="mt-2 text-sm text-red-600">{errors.password.message}</p>
          )}
        </div>

        <button
          type="submit"
          disabled={isLoading}
          className="w-full rounded-2xl bg-emerald-950 px-5 py-3 text-sm font-semibold text-white shadow-sm transition hover:bg-emerald-800 disabled:cursor-not-allowed disabled:opacity-60"
        >
          {isLoading ? 'Đang đăng nhập...' : 'Đăng nhập'}
        </button>

        {isError && (
          <p className="text-center text-sm text-red-600">Đăng nhập không thành công. Vui lòng thử lại.</p>
        )}
        {isSuccess && (
          <p className="text-center text-sm text-emerald-700">Đăng nhập thành công! Chào mừng bạn quay lại.</p>
        )}
      </form>
    </div>
  );
};

export default LoginForm;