'use client';

import React, { useMemo } from 'react';
import { useRouter } from 'next/navigation';
import { formatCurrency, formatDateTime } from '@/lib/formatters';
import {
  InvestmentCompleteApiResponse,
  InvestmentCompleteResponse,
} from '@/features/investment-recommendations/type/investment-complete.type';
import LogoutButton from '@/features/logout/components/LogoutButton';

const STORAGE_KEY = 'investmentCompleteResult';

const getCompletePayload = (
  response: InvestmentCompleteApiResponse | InvestmentCompleteResponse,
): InvestmentCompleteResponse | null => {
  const candidate = 'data' in response ? response.data : response;

  if (!candidate || typeof candidate !== 'object') {
    return null;
  }

  if (!('sessionId' in candidate) || !('allocations' in candidate) || !Array.isArray(candidate.allocations)) {
    return null;
  }

  return candidate as InvestmentCompleteResponse;
};

const InvestmentCompletePage: React.FC = () => {
  const router = useRouter();

  const data = useMemo<InvestmentCompleteResponse | null>(() => {
    if (typeof window === 'undefined') {
      return null;
    }

    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) {
      return null;
    }

    try {
      const parsed = JSON.parse(raw) as InvestmentCompleteApiResponse | InvestmentCompleteResponse;
      return getCompletePayload(parsed);
    } catch (error) {
      console.error('Cannot parse investment complete result:', error);
      return null;
    }
  }, []);

  if (!data) {
    return (
      <div className="min-h-screen bg-slate-50 flex items-center justify-center px-4">
        <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-8 max-w-md w-full text-center">
          <div className="w-14 h-14 rounded-2xl bg-slate-100 flex items-center justify-center mx-auto mb-4">
            <svg xmlns="http://www.w3.org/2000/svg" className="w-7 h-7 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
            </svg>
          </div>
          <h1 className="text-xl font-bold text-slate-900 mb-2">Chưa có dữ liệu hoàn thành</h1>
          <p className="text-sm text-slate-500 mb-6">
            Vui lòng quay lại màn hình đề xuất để gửi yêu cầu.
          </p>
          <button
            onClick={() => router.push('/investment-recommendations')}
            className="inline-flex items-center gap-2 rounded-xl bg-emerald-600 px-6 py-2.5 text-sm font-semibold text-white transition hover:bg-emerald-700"
          >
            Quay lại đề xuất
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
              <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Hoàn thành đầu tư</h1>
            </div>
            <p className="text-sm text-slate-500">Kế hoạch của bạn đã được ghi nhận thành công</p>
          </div>
          <LogoutButton />
        </div>

        {/* Stats */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
          {[
            { label: 'Tổng số tiền', value: formatCurrency(data.totalAmount), cls: 'text-slate-900' },
            { label: 'Hồ sơ rủi ro', value: data.type, cls: 'text-emerald-600' },
            { label: 'Trạng thái', value: data.status || 'CONFIRMED', cls: 'text-slate-900' },
          ].map(({ label, value, cls }) => (
            <div key={label} className="bg-white rounded-xl border border-slate-200 shadow-sm p-5">
              <p className="text-xs text-slate-500 mb-1">{label}</p>
              <p className={`text-xl font-bold ${cls}`}>{value}</p>
            </div>
          ))}
        </div>

        {/* Session info */}
        <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-6 mb-6">
          <h2 className="text-base font-semibold text-slate-900 mb-4">Thông tin phiên đầu tư</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-3 text-sm">
            {[
              { label: 'Session ID', value: data.sessionId },
              { label: 'Plan ID', value: data.planId },
              { label: 'User ID', value: data.userId },
              { label: 'Thời gian tạo', value: formatDateTime(data.createdAt) },
            ].map(({ label, value }) => (
              <div key={label} className="bg-slate-50 rounded-xl p-3 border border-slate-100">
                <p className="text-xs text-slate-500 mb-0.5">{label}</p>
                <p className="text-sm font-medium text-slate-900 break-all">{value}</p>
              </div>
            ))}
          </div>
        </div>

        {/* Allocations */}
        <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-6 mb-6">
          <h2 className="text-base font-semibold text-slate-900 mb-4">Phân bổ đầu tư</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-3">
            {data.allocations.map((allocation, index) => {
              const quantity = Number.isFinite(allocation.quantity)
                ? Math.max(1, Math.floor(allocation.quantity))
                : 1;
              const totalAmount = allocation.amount * quantity;

              return (
                <div key={`${allocation.type}-${index}`} className="bg-slate-50 rounded-xl border border-slate-100 p-4">
                  <span className="inline-block text-xs font-bold bg-slate-200 text-slate-700 rounded-full px-2.5 py-0.5 mb-2">{allocation.type}</span>
                  <p className="text-base font-bold text-slate-900 mb-1">{formatCurrency(totalAmount)}</p>
                  <p className="text-xs text-slate-500">{quantity} × {formatCurrency(allocation.amount)}</p>
                  <p className="text-xs text-slate-500">Tỷ lệ: {(allocation.percent * 100).toFixed(2)}%</p>
                </div>
              );
            })}
          </div>
        </div>

        {/* Actions */}
        <div className="flex items-center justify-between">
          <button
            onClick={() => router.push('/investment-recommendations')}
            className="px-5 py-2.5 border border-slate-300 text-slate-700 font-semibold text-sm rounded-xl hover:bg-slate-50 transition"
          >
            Xem lại đề xuất
          </button>
          <button
            onClick={() => router.push('/dashboard')}
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
