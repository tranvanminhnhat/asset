'use client';
import React, { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { Controller, useForm, useWatch } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { formatCurrency } from '@/lib/formatters';
import RiskOption from '../components/RiskOption';
import { useInvestmentAmount } from '../hooks/useInvestmentAmount';
import LogoutButton from '@/features/logout/components/LogoutButton';
import { InvestmentAmountRequest } from '../type/investment-amount.type';
import { investmentAmountSchema, InvestmentAmountFormValues } from '../validation/investment-amount.schema';

const InvestmentAmountPage: React.FC = () => {
  const router = useRouter();
  const {
    register,
    control,
    handleSubmit,
    setValue,
    formState: { errors },
  } = useForm<InvestmentAmountFormValues>({
    resolver: zodResolver(investmentAmountSchema),
    defaultValues: {
      amount: '',
      type: 'SAFE',
    },
  });
  const {
    investmentTypeOptions,
    isLoadingTypeOptions,
    isTypeOptionsError,
    submitInvestmentAmount,
    isLoading,
    error,
  } = useInvestmentAmount();

  const selectedType = useWatch({ control, name: 'type' });
  const amount = useWatch({ control, name: 'amount' }) || '';

  useEffect(() => {
    if (!investmentTypeOptions.length) {
      return;
    }

    const isValidSelection = investmentTypeOptions.some((option) => option.type === selectedType);

    if (!isValidSelection) {
      setValue('type', investmentTypeOptions[0].type);
    }
  }, [investmentTypeOptions, selectedType, setValue]);

  const normalizedAmount = Number(amount.replace(/[^\d]/g, ''));

  const handleContinue = async (formValues: InvestmentAmountFormValues) => {
    const parsedAmount = Number(formValues.amount.replace(/[^\d]/g, ''));

    if (!parsedAmount) {
      return;
    }

    const request: InvestmentAmountRequest = {
      amount: parsedAmount,
      type: formValues.type,
    };

    const response = await submitInvestmentAmount(request);

    if (response) {
      localStorage.setItem(
        'investmentAllocations',
        JSON.stringify({
          allocations: response.allocations,
          type: formValues.type,
          amount: parsedAmount,
        }),
      );
      router.push('/investment-recommendations');
    }
  };

  const scrollToResults = () => {
    const resultsSection = document.getElementById('results-section');
    if (resultsSection) {
      resultsSection.scrollIntoView({ behavior: 'smooth' });
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 px-4 py-10 animate-fade-up">
      <div className="max-w-2xl mx-auto">
        {/* Page header */}
        <div className="flex items-center justify-between mb-8">
          <div>
            <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Lập kế hoạch đầu tư</h1>
            <p className="text-sm text-slate-500 mt-1">Chọn chiến lược và số tiền phù hợp với bạn</p>
          </div>
          <LogoutButton />
        </div>

        {/* Steps */}
        <div className="flex items-center gap-2 mb-8">
          {['Số tiền & Chiến lược', 'Xem gợi ý', 'Xác nhận'].map((step, i) => (
            <React.Fragment key={step}>
              <div className={`flex items-center gap-2 text-xs font-medium ${i === 0 ? 'text-emerald-700' : 'text-slate-400'}`}>
                <span className={`w-6 h-6 rounded-full flex items-center justify-center text-xs font-bold ${i === 0 ? 'bg-emerald-600 text-white' : 'bg-slate-200 text-slate-500'}`}>
                  {i + 1}
                </span>
                {step}
              </div>
              {i < 2 && <div className="flex-1 h-px bg-slate-200" />}
            </React.Fragment>
          ))}
        </div>

        <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-7 space-y-7">
          {/* Risk Selection */}
          <div>
            <h2 className="text-base font-semibold text-slate-900 mb-1">Chiến lược đầu tư</h2>
            <p className="text-xs text-slate-500 mb-4">Chọn mức độ rủi ro phù hợp với mục tiêu của bạn</p>

            {isLoadingTypeOptions && (
              <div className="flex items-center gap-2 text-sm text-slate-500 py-4">
                <svg className="w-4 h-4 animate-spin text-emerald-500" fill="none" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
                </svg>
                Đang tải chiến lược đầu tư...
              </div>
            )}

            {isTypeOptionsError && !isLoadingTypeOptions && (
              <div className="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
                Không thể tải dữ liệu chiến lược đầu tư.
              </div>
            )}

            {!isLoadingTypeOptions && !isTypeOptionsError && (
              <Controller
                name="type"
                control={control}
                render={({ field }) => (
                  <div className="space-y-2.5">
                    {investmentTypeOptions.map((option) => (
                      <RiskOption
                        key={option.type}
                        label={option.label}
                        description={option.description}
                        isSelected={field.value === option.type}
                        onClick={() => field.onChange(option.type)}
                      />
                    ))}
                  </div>
                )}
              />
            )}
          </div>

          <div className="border-t border-slate-100" />

          {/* Amount Input */}
          <div>
            <h2 className="text-base font-semibold text-slate-900 mb-1">Số tiền đầu tư</h2>
            <p className="text-xs text-slate-500 mb-4">Nhập số tiền bạn muốn đầu tư</p>

            <div className="relative">
              <input
                type="text"
                inputMode="numeric"
                {...register('amount')}
                className="w-full border border-slate-300 rounded-xl px-4 py-3 pr-10 text-base font-semibold text-slate-900 focus:outline-none focus:ring-2 focus:ring-emerald-500/30 focus:border-emerald-500 transition hover:border-slate-400"
                placeholder="Ví dụ: 10,000,000"
              />
              <span className="absolute right-4 top-1/2 -translate-y-1/2 text-sm text-slate-400 font-medium">₫</span>
            </div>

            {amount && !Number.isNaN(normalizedAmount) && normalizedAmount > 0 && (
              <p className="mt-2 text-xs text-emerald-700 bg-emerald-50 border border-emerald-100 rounded-lg px-3 py-2">
                Số tiền: <span className="font-semibold">{formatCurrency(normalizedAmount)}</span>
              </p>
            )}
            {errors.amount && <p className="mt-2 text-xs text-red-600">{errors.amount.message}</p>}
          </div>

          {error && (
            <div className="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
              {error}
            </div>
          )}

          {/* Actions */}
          <div className="flex items-center justify-between pt-2">
            <button
              type="button"
              onClick={() => router.back()}
              className="px-5 py-2.5 border border-slate-300 text-slate-700 font-semibold text-sm rounded-xl hover:bg-slate-50 transition"
            >
              Quay lại
            </button>
            <button
              onClick={handleSubmit(handleContinue)}
              disabled={isLoading || isLoadingTypeOptions || !investmentTypeOptions.length}
              className="inline-flex items-center gap-2 px-6 py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white font-semibold text-sm rounded-xl transition disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {isLoading ? (
                <>
                  <svg className="w-4 h-4 animate-spin" fill="none" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
                  </svg>
                  Đang xử lý...
                </>
              ) : (
                <>
                  Xem gợi ý đầu tư
                  <svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                    <path strokeLinecap="round" strokeLinejoin="round" d="M17 8l4 4m0 0l-4 4m4-4H3" />
                  </svg>
                </>
              )}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default InvestmentAmountPage;

