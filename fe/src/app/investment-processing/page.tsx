'use client';

import React, { Suspense } from 'react';
import InvestmentProcessingPage from '@/features/investment-processing/pages/InvestmentProcessingPage';
import { useI18n } from '@/common/i18n/I18nProvider';

const Page: React.FC = () => {
  const { pick } = useI18n();

  return (
    <Suspense fallback={
      <div className="min-h-screen bg-slate-50 flex items-center justify-center">
        <div className="text-center">
          <svg className="w-8 h-8 animate-spin text-emerald-500 mx-auto mb-3" fill="none" viewBox="0 0 24 24">
            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
          </svg>
          <p className="text-sm text-slate-500">{pick({ vi: 'Dang tai...', en: 'Loading...', ja: '読み込み中...' })}</p>
        </div>
      </div>
    }>
      <InvestmentProcessingPage />
    </Suspense>
  );
};

export default Page;
