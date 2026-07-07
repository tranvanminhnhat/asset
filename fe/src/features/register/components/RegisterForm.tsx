import React from 'react';
import Link from 'next/link';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { registerSchema, type RegisterForm } from '../validation/register.schema';
import { useRegister } from '../hooks/useRegister';
import Input from '@/common/components/ui/Input';
import Button from '@/common/components/ui/Button';
import { useI18n } from '@/common/i18n/I18nProvider';

const RegisterForm: React.FC = () => {
  const { pick } = useI18n();
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
        <h1 className="text-2xl font-bold text-slate-900 tracking-tight">{pick({ vi: 'Tao tai khoan', en: 'Create account', ja: 'アカウント作成' })}</h1>
        <p className="mt-1.5 text-sm text-slate-500">{pick({ vi: 'Bat dau hanh trinh dau tu voi MoneyPilot', en: 'Start your investment journey with MoneyPilot', ja: 'MoneyPilotで投資の旅を始めましょう' })}</p>
      </div>

      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-7">
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <Input
            label={pick({ vi: 'Dia chi Email', en: 'Email address', ja: 'メールアドレス' })}
            id="email"
            type="email"
            {...register('email')}
            error={errors.email?.message}
            placeholder="email@example.com"
          />

          <Input
            label={pick({ vi: 'Mat khau', en: 'Password', ja: 'パスワード' })}
            id="password"
            type="password"
            {...register('password')}
            error={errors.password?.message}
            placeholder="••••••••"
          />

          <Input
            label={pick({ vi: 'Xac nhan mat khau', en: 'Confirm password', ja: 'パスワード確認' })}
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
            {isLoading
              ? pick({ vi: 'Dang dang ky...', en: 'Creating account...', ja: '登録中...' })
              : pick({ vi: 'Dang ky', en: 'Sign up', ja: '登録' })}
          </Button>

          {isError && (
            <p className="text-center text-xs text-red-600 bg-red-50 border border-red-100 rounded-lg py-2.5">
              {pick({ vi: 'Dang ky khong thanh cong. Vui long thu lai.', en: 'Registration failed. Please try again.', ja: '登録に失敗しました。再試行してください。' })}
            </p>
          )}
          {isSuccess && (
            <p className="text-center text-xs text-emerald-700 bg-emerald-50 border border-emerald-100 rounded-lg py-2.5">
              {pick({ vi: 'Dang ky thanh cong! Vui long kiem tra email de xac nhan.', en: 'Registration successful! Please check your email for verification.', ja: '登録に成功しました。確認メールをご確認ください。' })}
            </p>
          )}
        </form>

        <div className="mt-5 pt-5 border-t border-slate-100 text-center">
          <p className="text-sm text-slate-500">
            {pick({ vi: 'Da co tai khoan?', en: 'Already have an account?', ja: 'すでにアカウントをお持ちですか？' })}{' '}
            <Link href="/login" className="font-semibold text-emerald-600 hover:text-emerald-700 transition-colors">
              {pick({ vi: 'Dang nhap', en: 'Sign in', ja: 'ログイン' })}
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default RegisterForm;
