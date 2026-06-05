import React from 'react';
import Link from 'next/link';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { registerSchema, type RegisterForm } from '../validation/register.schema';
import { useRegister } from '../hooks/useRegister';
import Input from '@/common/components/ui/Input';
import Button from '@/common/components/ui/Button';

const RegisterForm: React.FC = () => {
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
    <div className="w-full max-w-sm">
      {/* Logo */}
      <div className="text-center mb-8">
        <div className="inline-flex items-center justify-center w-12 h-12 rounded-2xl bg-emerald-600 mb-4 shadow-lg shadow-emerald-600/30">
          <svg className="w-6 h-6 text-white" fill="currentColor" viewBox="0 0 20 20">
            <path fillRule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clipRule="evenodd" />
          </svg>
        </div>
        <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Tạo tài khoản</h1>
        <p className="mt-1.5 text-sm text-slate-500">Bắt đầu hành trình đầu tư với MoneyPilot</p>
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

          <Input
            label="Xác nhận mật khẩu"
            id="confirmPassword"
            type="password"
            {...register('confirmPassword')}
            error={errors.confirmPassword?.message}
            placeholder="••••••••"
          />

          <Button
            type="submit"
            variant="secondary"
            size="lg"
            loading={isLoading}
            className="w-full mt-1"
          >
            {isLoading ? 'Đang đăng ký...' : 'Đăng ký'}
          </Button>

          {isError && (
            <p className="text-center text-xs text-red-600 bg-red-50 border border-red-100 rounded-lg py-2.5">
              Đăng ký không thành công. Vui lòng thử lại.
            </p>
          )}
          {isSuccess && (
            <p className="text-center text-xs text-emerald-700 bg-emerald-50 border border-emerald-100 rounded-lg py-2.5">
              Đăng ký thành công! Vui lòng kiểm tra email để xác nhận.
            </p>
          )}
        </form>

        <div className="mt-5 pt-5 border-t border-slate-100 text-center">
          <p className="text-sm text-slate-500">
            Đã có tài khoản?{' '}
            <Link href="/login" className="font-semibold text-emerald-600 hover:text-emerald-700 transition-colors">
              Đăng nhập
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default RegisterForm;
