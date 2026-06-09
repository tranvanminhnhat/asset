'use client';

import React, { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { formatCurrency, formatDateTime } from '@/lib/formatters';
import LogoutButton from '@/features/logout/components/LogoutButton';
import { FinalizeSessionResponse } from '../services/finalize-session.api';

const STORAGE_KEY = 'investmentFinalizeResult';

const InvestmentCompletePage: React.FC = () => {
  const router = useRouter();
  const [data, setData] = useState<FinalizeSessionResponse | null>(null);

  useEffect(() => {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (raw) {
      try {
        setData(JSON.parse(raw) as FinalizeSessionResponse);
      } catch {
        // ignore
      }
    }
  }, []);

  const handleGoToDashboard = () => {
    localStorage.removeItem('investmentSessionId');
    localStorage.removeItem(STORAGE_KEY);
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
          <h1 className="text-xl font-bold text-slate-900 mb-2">Không tìm thấy dữ liệu</h1>
          <p className="text-sm text-slate-500 mb-6">Vui lòng quay lại trang xem lại và thực hiện xác nhận.</p>
          <button
            onClick={() => router.push('/investment-review')}
            className="inline-flex items-center gap-2 rounded-xl bg-emerald-600 px-6 py-2.5 text-sm font-semibold text-white transition hover:bg-emerald-700"
          >
            Quay lại xem lại
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-slate-50 px-4 py-10 animate-fade-up">
      <div className="max-w-4xl mx-auto">
        {/* Header */}
        <div className="flex items-center justify-between mb-8">
          <div>
            <div className="flex items-center gap-3 mb-1">
              <div className="w-8 h-8 rounded-full bg-emerald-100 flex items-center justify-center">
                <svg xmlns="http://www.w3.org/2000/svg" className="w-5 h-5 text-emerald-600" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.5}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" />
                </svg>
              </div>
              <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Đầu tư hoàn thành!</h1>
            </div>
            <p className="text-sm text-slate-500">Phiên đầu tư của bạn đã được hoàn tất thành công</p>
          </div>
          <LogoutButton />
        </div>

        {/* Steps */}
        <div className="flex items-center gap-2 mb-8">
          {['Số tiền & Chiến lược', 'Xử lý danh mục', 'Xem lại', 'Hoàn thành'].map((step, i) => (
            <React.Fragment key={step}>
              <div className="flex items-center gap-2 text-xs font-medium text-emerald-700">
                <span className="w-6 h-6 rounded-full flex items-center justify-center text-xs font-bold bg-emerald-600 text-white">{i + 1}</span>
                <span className="hidden sm:inline">{step}</span>
              </div>
              {i < 3 && <div className="flex-1 h-px bg-emerald-200" />}
            </React.Fragment>
          ))}
        </div>

        {/* Success banner */}
        <div className="rounded-2xl bg-gradient-to-r from-emerald-600 to-emerald-500 p-6 mb-6 text-white shadow-lg shadow-emerald-500/25">
          <div className="flex items-center gap-4">
            <div className="w-12 h-12 rounded-full bg-white/20 flex items-center justify-center shrink-0">
              <svg xmlns="http://www.w3.org/2000/svg" className="w-7 h-7 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.5}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" />
              </svg>
            </div>
            <div>
              <p className="text-sm font-semibold text-white/80 mb-0.5">Phiên #{data.sessionId} — {data.status}</p>
              <p className="text-2xl font-bold">{formatCurrency(data.totalInvested)}</p>
              <p className="text-sm text-white/70">đã được đầu tư thành công</p>
            </div>
          </div>
        </div>

        {/* Stats */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
          {[
            { label: 'Đã đầu tư', value: formatCurrency(data.totalInvested), cls: 'text-emerald-700' },
            { label: 'Tiền dư hoàn về', value: formatCurrency(data.totalResidual), cls: 'text-rose-600' },
            { label: 'Số dư tiền mặt', value: formatCurrency(data.updatedCashBalance), cls: 'text-slate-900' },
            { label: 'Hoàn thành lúc', value: formatDateTime(data.completedAt), cls: 'text-slate-700' },
          ].map(({ label, value, cls }) => (
            <div key={label} className="bg-white rounded-xl border border-slate-200 shadow-sm p-4">
              <p className="text-xs text-slate-500 mb-1">{label}</p>
              <p className={`text-sm font-bold ${cls}`}>{value}</p>
            </div>
          ))}
        </div>

        {/* Actions */}
        <div className="flex items-center justify-between">
          <button
            onClick={() => router.push('/investment-review')}
            className="px-5 py-2.5 border border-slate-300 text-slate-700 font-semibold text-sm rounded-xl hover:bg-slate-50 transition"
          >
            Xem lại chi tiết
          </button>
          <button
            onClick={handleGoToDashboard}
            className="inline-flex items-center gap-2 px-6 py-2.5 bg-slate-900 hover:bg-slate-800 text-white font-semibold text-sm rounded-xl transition"
          >
            Về Dashboard
            <svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
            </svg>
          </button>
        </div>
      </div>
    </div>
  );
};

export default InvestmentCompletePage;
