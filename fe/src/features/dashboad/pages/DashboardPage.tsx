'use client';
import React from 'react';
import Link from 'next/link';
import { useDashboard } from '../hooks/useDashboard';
import HeroBanner from '../components/HeroBanner';
import PortfolioList from '../components/PortfolioList';
import SummaryBox from '../components/SummaryBox';
import PageLoader from '@/common/components/ui/PageLoader';
import { useI18n } from '@/common/i18n/I18nProvider';

const DashboardPage: React.FC = () => {
  const { pick } = useI18n();
  const { dashboardData, isLoading, error } = useDashboard();

  if (isLoading) {
    return (
      <div className="min-h-screen bg-slate-50 px-6 py-8">
        <div className="max-w-6xl mx-auto">
          <PageLoader text={pick({ vi: 'Dang tai du lieu dashboard...', en: 'Loading dashboard...', ja: 'ダッシュボードを読み込み中...' })} />
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-slate-50 px-6 py-8">
        <div className="max-w-6xl mx-auto">
          <div className="bg-red-50 border border-red-200 rounded-xl p-6 text-center">
            <p className="text-sm font-medium text-red-700">{pick({ vi: 'Co loi xay ra khi tai du lieu dashboard.', en: 'An error occurred while loading dashboard data.', ja: 'ダッシュボードデータの読み込み中にエラーが発生しました。' })}</p>
          </div>
        </div>
      </div>
    );
  }

  if (!dashboardData) {
    return (
      <div className="min-h-screen bg-slate-50 px-6 py-8">
        <div className="max-w-6xl mx-auto text-center">
          <p className="text-sm text-slate-500">{pick({ vi: 'Khong co du lieu dashboard.', en: 'No dashboard data.', ja: 'ダッシュボードデータがありません。' })}</p>
        </div>
      </div>
    );
  }

  const hasPortfolios = dashboardData.portfolios.length > 0;

  return (
    <div className="min-h-screen bg-slate-50 px-6 py-8 animate-fade-up">
      <div className="max-w-6xl mx-auto">
        <HeroBanner />

        <SummaryBox summary={dashboardData.summary} />

        <PortfolioList portfolios={dashboardData.portfolios} />

        {!hasPortfolios && (
          <div className="rounded-2xl border border-slate-200 bg-white p-8 text-center shadow-sm">
            <div className="mx-auto mb-4 w-14 h-14 rounded-2xl bg-emerald-50 flex items-center justify-center">
              <svg xmlns="http://www.w3.org/2000/svg" className="w-7 h-7 text-emerald-600" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <h3 className="text-base font-semibold text-slate-900 mb-1">{pick({ vi: 'Chua co du lieu giao dich', en: 'No transaction data yet', ja: '取引データがまだありません' })}</h3>
            <p className="text-sm text-slate-500 mb-5 max-w-sm mx-auto">
              {pick({ vi: 'Tao danh muc dau tu de bat dau theo doi giao dich va lich su tai san.', en: 'Create a portfolio to start tracking transactions and asset history.', ja: 'ポートフォリオを作成して取引と資産履歴の追跡を始めましょう。' })}
            </p>
            <Link
              href="/portfolio-selection"
              className="inline-flex items-center gap-2 rounded-xl bg-slate-900 px-5 py-2.5 text-sm font-semibold text-white transition hover:bg-slate-800"
            >
              {pick({ vi: 'Bat dau tao danh muc', en: 'Create portfolio', ja: 'ポートフォリオを作成' })}
              <svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M17 8l4 4m0 0l-4 4m4-4H3" />
              </svg>
            </Link>
          </div>
        )}
      </div>
    </div>
  );
};

export default DashboardPage;
