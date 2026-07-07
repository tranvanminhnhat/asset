'use client';
import React from 'react';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { useRouter } from 'next/navigation';
import PortfolioSelector from '../components/PortfolioSelector';
import LogoutButton from '@/features/logout/components/LogoutButton';
import { portfolioSelectionSchema, PortfolioSelectionFormValues } from '../validation/portfolio-selection.schema';
import { useI18n } from '@/common/i18n/I18nProvider';

const BASE_PORTFOLIO_OPTIONS = [
  {
    id: 'low',
    riskLevel: 'LOW' as const,
    expectedReturn: 8.11,
  },
  {
    id: 'medium',
    riskLevel: 'MEDIUM' as const,
    expectedReturn: 13.68,
  },
  {
    id: 'high',
    riskLevel: 'HIGH' as const,
    expectedReturn: 53.47,
  },
];

const PortfolioSelectionPage: React.FC = () => {
  const router = useRouter();
  const { pick } = useI18n();

  const {
    control,
    handleSubmit,
    formState: { errors },
  } = useForm<PortfolioSelectionFormValues>({
    resolver: zodResolver(portfolioSelectionSchema),
    defaultValues: { selectedPortfolio: 'medium' },
  });

  const options = BASE_PORTFOLIO_OPTIONS.map((option) => ({
    ...option,
    name:
      option.id === 'low'
        ? pick({ vi: 'An toan', en: 'Safe', ja: '安全型' })
        : option.id === 'medium'
          ? pick({ vi: 'Can bang', en: 'Balanced', ja: 'バランス型' })
          : pick({ vi: 'Tang truong', en: 'Growth', ja: '成長型' }),
    description:
      option.id === 'low'
        ? pick({ vi: 'Dau tu bao thu', en: 'Conservative investing', ja: '保守的な投資' })
        : option.id === 'medium'
          ? pick({ vi: 'Dau tu can bang', en: 'Balanced investing', ja: 'バランス投資' })
          : pick({ vi: 'Dau tu tang truong', en: 'Growth investing', ja: '成長投資' }),
  }));

  const onSubmit = (data: PortfolioSelectionFormValues) => {
    localStorage.setItem('investmentSelection', JSON.stringify({ portfolio: data.selectedPortfolio }));
    router.push('/investment-amount');
  };

  return (
    <div className="min-h-screen bg-slate-50 px-4 py-10 animate-fade-up">
      <div className="max-w-3xl mx-auto">
        <div className="flex items-center justify-between mb-8">
          <div>
            <h1 className="text-2xl font-bold text-slate-900 tracking-tight">{pick({ vi: 'Chon danh muc dau tu', en: 'Select portfolio', ja: 'ポートフォリオ選択' })}</h1>
            <p className="text-sm text-slate-500 mt-1">{pick({ vi: 'Chon chien luoc phu hop voi muc tieu cua ban', en: 'Choose a strategy that matches your goals', ja: '目標に合う戦略を選択してください' })}</p>
          </div>
          <LogoutButton />
        </div>

        <form onSubmit={handleSubmit(onSubmit)}>
          <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-7 mb-6">
            <h2 className="text-base font-semibold text-slate-900 mb-1">{pick({ vi: 'Danh muc dau tu', en: 'Portfolio options', ja: 'ポートフォリオ候補' })}</h2>
            <p className="text-xs text-slate-500 mb-5">{pick({ vi: 'Moi danh muc co muc do rui ro va loi nhuan ky vong khac nhau', en: 'Each option has different risk and expected return', ja: '各オプションでリスクと期待収益が異なります' })}</p>

            <Controller
              name="selectedPortfolio"
              control={control}
              render={({ field }) => (
                <PortfolioSelector
                  options={options}
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
              {pick({ vi: 'Quay lai', en: 'Back', ja: '戻る' })}
            </button>
            <button
              type="submit"
              className="inline-flex items-center gap-2 px-6 py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white font-semibold text-sm rounded-xl transition"
            >
              {pick({ vi: 'Tiep tuc', en: 'Continue', ja: '続行' })}
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
