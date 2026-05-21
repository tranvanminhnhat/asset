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
    <div className="min-h-screen bg-[#eff8f1] px-4 py-10">
      <div className="max-w-4xl mx-auto">
        <div className="flex justify-end mb-4">
          <LogoutButton />
        </div>
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-slate-900 mb-3">Phân Bổ Danh Mục Đầu Tư</h1>
          <p className="text-slate-600">
            Dựa trên hồ sơ của bạn, chúng tôi đề xuất phân bổ tài sản phù hợp.
          </p>
        </div>

        <form onSubmit={handleSubmit(onSubmit)}>
          <div className="bg-white rounded-lg shadow-md p-8 mb-8">
            <div className="mb-10">
              <h2 className="text-xl font-semibold text-slate-900 mb-2">Chọn danh mục đầu tư</h2>
              <p className="text-sm text-slate-600 mb-4">
                Lựa chọn phân bổ phù hợp với mục tiêu tài chính của bạn.
              </p>
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
                <p className="mt-2 text-sm text-red-600">{errors.selectedPortfolio.message}</p>
              )}
            </div>
          </div>

          <div className="flex gap-4 justify-between">
            <button
              type="button"
              onClick={() => router.back()}
              className="px-6 py-3 border border-slate-300 text-slate-900 font-semibold rounded-lg hover:bg-slate-50 transition"
            >
              Quay lại
            </button>
            <button
              type="submit"
              className="px-6 py-3 rounded-lg font-semibold transition bg-blue-600 text-white hover:bg-blue-700"
            >
              Tiếp tục
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default PortfolioSelectionPage;
