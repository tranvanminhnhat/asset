'use client';
import React, { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { Controller, useForm, useWatch } from 'react-hook-form';
import { formatCurrency } from '@/lib/formatters';
import RiskOption from '../components/RiskOption';
import { useInvestmentAmount } from '../hooks/useInvestmentAmount';
import LogoutButton from '@/features/logout/components/LogoutButton';
import { InvestmentAmountRequest, InvestmentType } from '../type/investment-amount.type';

interface InvestmentAmountFormValues {
  amount: string;
  type: InvestmentType;
}

const InvestmentAmountPage: React.FC = () => {
  const router = useRouter();
  const {
    register,
    control,
    handleSubmit,
    setValue,
    formState: { errors },
  } = useForm<InvestmentAmountFormValues>({
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
    <div className="min-h-screen bg-white px-4 py-10">
      <div className="max-w-4xl mx-auto">
        <div className="flex justify-end mb-4">
          <LogoutButton />
        </div>
        {/* Hero Section */}
        <div className="mb-16 flex flex-col md:flex-row gap-8 items-center">
          <div className="flex-1">
            <h1 className="text-4xl font-bold text-slate-900 mb-4">
              Hành trình tích lũy tài sản của bạn bắt đầu từ đây
            </h1>
            <p className="text-slate-600 text-lg mb-6">
              Khám phá cơn gió và sông đé được mục tiêu tài chính của bạn. Công cụ của chúng tôi cung cấp các dự kiến với hiển thị quy hoạch.
            </p>
            <button
              onClick={scrollToResults}
              className="bg-emerald-600 hover:bg-emerald-700 text-white font-semibold py-3 px-8 rounded-lg transition flex items-center gap-2"
            >
              Bắt đầu lập kế hoạch ngay →
            </button>
          </div>
          <div className="flex-1 hidden md:block">
            <div className="w-full h-64 bg-linear-to-br from-slate-300 to-slate-400 rounded-lg"></div>
          </div>
        </div>

        <div id="results-section" className="bg-slate-50 rounded-lg p-8 mb-8">
          {/* Risk Selection */}
          <div className="mb-10">
            <h2 className="text-2xl font-bold text-slate-900 mb-2">Xác định hộ số tài chính của bạn</h2>
            <p className="text-slate-600 mb-6">
              Hãy cho chúng tôi biết về chi phí và thích rủi ro của bạn để chúng tôi có thể đưa ra các đề xuất phù hợp.
            </p>
            {isLoadingTypeOptions && (
              <p className="text-sm text-slate-600">Đang tải lựa chọn chiến lược đầu tư...</p>
            )}

            {isTypeOptionsError && !isLoadingTypeOptions && (
              <div className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
                Không thể tải dữ liệu chiến lược đầu tư.
              </div>
            )}

            {!isLoadingTypeOptions && !isTypeOptionsError && (
              <Controller
                name="type"
                control={control}
                render={({ field }) => (
                  <div className="space-y-3">
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

          <hr className="my-8" />

          {/* Amount Input */}
          <div className="mb-8">
            <label className="block text-sm font-semibold text-slate-900 mb-3">
              💰 Nhập số tiền đầu tư
            </label>
            <div className="relative">
              <input
                type="text"
                inputMode="numeric"
                {...register('amount', {
                  required: 'Vui lòng nhập số tiền đầu tư.',
                  validate: (value) => Number(value.replace(/[^\d]/g, '')) > 0 || 'Số tiền phải lớn hơn 0.',
                })}
                className="w-full px-4 py-3 border-2 border-slate-300 rounded-lg focus:outline-none focus:border-blue-500 text-lg font-semibold"
                placeholder="Ví dụ: 10000000"
              />
              <span className="absolute right-4 top-1/2 transform -translate-y-1/2 text-slate-600">₫</span>
              {amount && !Number.isNaN(normalizedAmount) && (
                <p className="mt-3 text-sm text-slate-600">
                  Số tiền dự kiến: {formatCurrency(normalizedAmount)}
                </p>
              )}
            </div>
            {errors.amount && <p className="mt-2 text-sm text-red-600">{errors.amount.message}</p>}
          </div>

          <hr className="my-8" />

            {error && (
              <div className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
                {error}
              </div>
            )}

        </div>

        <div className="flex gap-4 justify-between">
          <button
            onClick={() => router.back()}
            className="px-6 py-3 border border-slate-300 text-slate-900 font-semibold rounded-lg hover:bg-slate-50 transition"
          >
            Đặt lại
          </button>
          <button
            onClick={handleSubmit(handleContinue)}
            disabled={isLoading || isLoadingTypeOptions || !investmentTypeOptions.length}
            className="px-6 py-3 bg-emerald-600 hover:bg-emerald-700 text-white font-semibold rounded-lg transition disabled:opacity-50"
          >
            {isLoading ? 'Đang xử lý...' : 'Tiếp tục →'}
          </button>
        </div>
      </div>
    </div>
  );
};

export default InvestmentAmountPage;
