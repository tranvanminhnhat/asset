'use client';
import React from 'react';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { useRouter } from 'next/navigation';
import PortfolioSelector from '../components/PortfolioSelector';
import LogoutButton from '@/features/logout/components/LogoutButton';
import { portfolioSelectionSchema, PortfolioSelectionFormValues } from '../validation/portfolio-selection.schema';

// Mock data - in real app, this would come from API
const PORTFOLIO_OPTIONS = [
  {
    id: 'low',
    name: 'An Toàn',
    description: 'Đầu tư bảo thủ',
    riskLevel: 'LOW' as const,
    expectedReturn: 8.11,
  },
  {
    id: 'medium',
    name: 'Cân Bằng',
    description: 'Đầu tư cân bằng',
    riskLevel: 'MEDIUM' as const,
    expectedReturn: 13.68,
  },
  {
    id: 'high',
    name: 'Tăng Trưởng',
    description: 'Đầu tư tăng trưởng',
    riskLevel: 'HIGH' as const,
    expectedReturn: 53.47,
  },
];

const PortfolioSelectionPage: React.FC = () => {
  const router = useRouter();
  const {
    control,
    handleSubmit,
    formState: { errors },
  } = useForm<PortfolioSelectionFormValues>({
    resolver: zodResolver(portfolioSelectionSchema),
    defaultValues: { selectedPortfolio: 'medium' },
  });

  const onSubmit = (data: PortfolioSelectionFormValues) => {
    localStorage.setItem('investmentSelection', JSON.stringify({ portfolio: data.selectedPortfolio }));
    router.push('/investment-amount');
  };

  return (
    <div className="min-h-screen bg-slate-50 px-4 py-10 animate-fade-up">
      <div className="max-w-3xl mx-auto">
        {/* Header */}
        <div className="flex items-center justify-between mb-8">
          <div>
            <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Chọn danh mục đầu tư</h1>
            <p className="text-sm text-slate-500 mt-1">Chọn chiến lược phù hợp với mục tiêu của bạn</p>
          </div>
          <LogoutButton />
        </div>

        <form onSubmit={handleSubmit(onSubmit)}>
          <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-7 mb-6">
            <h2 className="text-base font-semibold text-slate-900 mb-1">Danh mục đầu tư</h2>
            <p className="text-xs text-slate-500 mb-5">Mỗi danh mục có mức độ rủi ro và lợi nhuận kỳ vọng khác nhau</p>

            <Controller
              name="selectedPortfolio"
              control={control}
              render={({ field }) => (
                <PortfolioSelector
                  options={PORTFOLIO_OPTIONS}
                  selectedId={field.value}
                  onSelect={field.onChange}
                />
              )}
            />
            {errors.selectedPortfolio && (
              <p className="mt-3 text-xs text-red-600">{errors.selectedPortfolio.message}</p>
            )}
          </div>

          <div className="flex items-center justify-between">
            <button
              type="button"
              onClick={() => router.back()}
              className="px-5 py-2.5 border border-slate-300 text-slate-700 font-semibold text-sm rounded-xl hover:bg-slate-50 transition"
            >
              Quay lại
            </button>
            <button
              type="submit"
              className="inline-flex items-center gap-2 px-6 py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white font-semibold text-sm rounded-xl transition"
            >
              Tiếp tục
              <svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M17 8l4 4m0 0l-4 4m4-4H3" />
              </svg>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default PortfolioSelectionPage;
