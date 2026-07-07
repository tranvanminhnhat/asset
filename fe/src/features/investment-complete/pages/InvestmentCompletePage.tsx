'use client';

import React, { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { formatCurrency, formatDateTime } from '@/lib/formatters';
import { FinalizeSessionResponse } from '../services/finalize-session.api';
import { SessionDetailReview } from '@/features/investment-review/type/investment-review.type';
import { useI18n } from '@/common/i18n/I18nProvider';

const STORAGE_KEY = 'investmentFinalizeResult';
const SESSION_DETAIL_KEY = 'investmentSessionDetail';

const InvestmentCompletePage: React.FC = () => {
  const router = useRouter();
  const { t, locale } = useI18n();
  const [data, setData] = useState<FinalizeSessionResponse | null>(null);
  const [sessionDetail, setSessionDetail] = useState<SessionDetailReview | null>(null);

  useEffect(() => {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (raw) {
      try { setData(JSON.parse(raw) as FinalizeSessionResponse); } catch { /* ignore */ }
    }
    const rawDetail = localStorage.getItem(SESSION_DETAIL_KEY);
    if (rawDetail) {
      try { setSessionDetail(JSON.parse(rawDetail) as SessionDetailReview); } catch { /* ignore */ }
    }
  }, []);

  const handleGoToDashboard = () => {
    localStorage.removeItem('investmentSessionId');
    localStorage.removeItem(STORAGE_KEY);
    localStorage.removeItem(SESSION_DETAIL_KEY);
    router.push('/dashboard');
  };

  if (!data) {
    return (
      <div className="min-h-screen bg-slate-50 flex items-center justify-center px-4">
        <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-8 max-w-md w-full text-center">
          <div className="w-14 h-14 rounded-2xl bg-slate-100 flex items-center justify-center mx-auto mb-4">
            <svg xmlns="http://www.w3.org/2000/svg" className="w-7 h-7 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
            </svg>
          </div>
          <h1 className="text-xl font-bold text-slate-900 mb-2">{t('complete.noDataTitle')}</h1>
          <p className="text-sm text-slate-500 mb-6">{t('complete.noDataSubtitle')}</p>
          <button
            onClick={() => router.push('/investment-review')}
            className="inline-flex items-center gap-2 rounded-xl bg-emerald-600 px-6 py-2.5 text-sm font-semibold text-white transition hover:bg-emerald-700"
          >
            {t('complete.backToReview')}
          </button>
        </div>
      </div>
    );
  }

  const allocations = sessionDetail?.allocations ?? [];
  const emergencyAllocation = allocations.find((a) => a.assetType === 'EMERGENCY');
  const nonEmergencyAllocations = allocations.filter((a) => a.assetType !== 'EMERGENCY');
  const totalResidualFromOthers = nonEmergencyAllocations.reduce((s, a) => s + Number(a.residualAmount ?? 0), 0);
  const emergencyTotal = Number(emergencyAllocation?.recommendedAmount ?? 0) + totalResidualFromOthers;

  const summaryItems = [
    ...nonEmergencyAllocations.map((a) => ({
      label: t(`assettype.${a.assetType.toLowerCase()}`, { fallback: a.assetType }),
      amount: Number(a.investedAmount ?? 0),
    })),
    ...(emergencyAllocation ? [{ label: t('assettype.emergency'), amount: emergencyTotal }] : []),
  ];

  return (
    <div className="min-h-screen bg-slate-50 flex items-center justify-center px-4 py-10">
      <div className="max-w-md w-full">
        {/* Success icon */}
        <div className="flex flex-col items-center mb-8">
          <div className="w-16 h-16 rounded-full bg-emerald-100 flex items-center justify-center mb-4">
            <svg xmlns="http://www.w3.org/2000/svg" className="w-9 h-9 text-emerald-600" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.5}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" />
            </svg>
          </div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight">{t('complete.successTitle')}</h1>
          <p className="text-sm text-slate-500 mt-1">#{data.sessionId} · {formatDateTime(data.completedAt, locale)}</p>
        </div>

        {/* Summary card */}
        <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden mb-4">
          <div className="bg-emerald-600 px-5 py-4">
            <p className="text-xs text-white/70 mb-0.5">{t('complete.totalInvested')}</p>
            <p className="text-3xl font-bold text-white">{formatCurrency(data.totalInvested, 'VND', locale)}</p>
          </div>

          {summaryItems.length > 0 && (
            <div className="divide-y divide-slate-100">
              {summaryItems.map(({ label, amount }) => (
                <div key={label} className="flex items-center justify-between px-5 py-3.5">
                  <span className="text-sm text-slate-600">{label}</span>
                  <span className="text-sm font-semibold text-slate-900">{formatCurrency(amount, 'VND', locale)}</span>
                </div>
              ))}
            </div>
          )}

          {data.totalResidual > 0 && (
            <div className="flex items-center justify-between px-5 py-3.5 bg-slate-50 border-t border-slate-100">
              <span className="text-xs text-slate-500">{t('complete.residualRefund')}</span>
              <span className="text-xs font-semibold text-rose-600">− {formatCurrency(data.totalResidual, 'VND', locale)}</span>
            </div>
          )}
        </div>

        {/* Go to dashboard */}
        <button
          onClick={handleGoToDashboard}
          className="w-full inline-flex items-center justify-center gap-2 px-6 py-3 bg-slate-900 hover:bg-slate-800 text-white font-semibold text-sm rounded-xl transition"
        >
          {t('complete.goDashboard')}
          <svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
          </svg>
        </button>
      </div>
    </div>
  );
};

export default InvestmentCompletePage;
