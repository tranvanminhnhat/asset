import React from 'react';
import Link from 'next/link';
import { DashboardPortfolioItem } from '../type/dashboard.type';
import PortfolioCard from './PortfolioCard';
import { useI18n } from '@/common/i18n/I18nProvider';

interface PortfolioListProps {
  portfolios: DashboardPortfolioItem[];
}

const PortfolioList: React.FC<PortfolioListProps> = ({ portfolios }) => {
  const { pick } = useI18n();

  if (portfolios.length === 0) {
    return (
      <div className="mb-8 rounded-2xl border border-dashed border-slate-300 bg-white/70 p-10 text-center">
        <div className="mx-auto mb-4 w-14 h-14 rounded-2xl bg-slate-100 flex items-center justify-center">
          <svg xmlns="http://www.w3.org/2000/svg" className="w-7 h-7 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
          </svg>
        </div>
        <h2 className="text-base font-semibold text-slate-900 mb-1">{pick({ vi: 'Chua co danh muc dau tu', en: 'No portfolio yet', ja: 'ポートフォリオはまだありません' })}</h2>
        <p className="text-sm text-slate-500 max-w-sm mx-auto mb-6">
          {pick({ vi: 'Hay tao danh muc dau tien de bat dau theo doi tai san, giao dich va hieu suat.', en: 'Create your first portfolio to start tracking assets, transactions, and performance.', ja: '最初のポートフォリオを作成して資産・取引・パフォーマンスを追跡しましょう。' })}
        </p>
        <div className="flex items-center justify-center gap-3">
          <Link
            href="/portfolio-selection"
            className="inline-flex items-center gap-2 rounded-xl bg-emerald-600 px-5 py-2.5 text-sm font-semibold text-white transition hover:bg-emerald-700 shadow-sm"
          >
            <svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M12 4v16m8-8H4" />
            </svg>
            {pick({ vi: 'Tao danh muc dau tien', en: 'Create first portfolio', ja: '最初のポートフォリオを作成' })}
          </Link>
          <Link
            href="/investment-recommendations"
            className="inline-flex items-center gap-2 rounded-xl border border-slate-300 bg-white px-5 py-2.5 text-sm font-semibold text-slate-700 transition hover:bg-slate-50"
          >
            {pick({ vi: 'Xem goi y dau tu', en: 'View recommendations', ja: '提案を見る' })}
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="mb-8">
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-base font-semibold text-slate-900">{pick({ vi: 'Danh muc dau tu', en: 'Portfolio', ja: 'ポートフォリオ' })}</h2>
        <span className="text-xs text-slate-500 bg-slate-100 rounded-full px-2.5 py-0.5 font-medium">
          {portfolios.length} {pick({ vi: 'danh muc', en: 'items', ja: '件' })}
        </span>
      </div>
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {portfolios.map((portfolio) => (
          <PortfolioCard key={portfolio.assetType} portfolio={portfolio} />
        ))}
      </div>
    </div>
  );
};

export default PortfolioList;