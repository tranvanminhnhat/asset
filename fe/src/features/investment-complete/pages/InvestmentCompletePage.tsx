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
      <div className="min-h-screen bg-white px-4 py-10">
        <div className="max-w-3xl mx-auto bg-slate-50 border border-slate-200 rounded-lg p-8">
          <h1 className="text-2xl font-bold text-slate-900 mb-3">Chưa có dữ liệu hoàn thành đầu tư</h1>
          <p className="text-slate-600 mb-6">
            Bạn chưa hoàn tất phiên đầu tư nào trong lần truy cập này. Vui lòng quay lại màn hình đề xuất để gửi yêu cầu.
          </p>
          <button
            onClick={() => router.push('/investment-recommendations')}
            className="px-6 py-3 bg-emerald-600 hover:bg-emerald-700 text-white font-semibold rounded-lg transition"
          >
            Quay lại đề xuất
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-white px-4 py-10">
      <div className="max-w-6xl mx-auto">
        <div className="flex justify-end mb-4">
          <LogoutButton />
        </div>
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-slate-900 mb-3">Hoàn thành đầu tư</h1>
          <p className="text-slate-600 text-lg">Kế hoạch của bạn đã được ghi nhận thành công.</p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-8">
          <div className="bg-slate-50 border border-slate-200 rounded-lg p-5">
            <p className="text-sm text-slate-600 mb-2">Tổng số tiền</p>
            <p className="text-2xl font-bold text-slate-900">{formatCurrency(data.totalAmount)}</p>
          </div>
          <div className="bg-slate-50 border border-slate-200 rounded-lg p-5">
            <p className="text-sm text-slate-600 mb-2">Hồ sơ rủi ro</p>
            <p className="text-2xl font-bold text-emerald-600">{data.type}</p>
          </div>
          <div className="bg-slate-50 border border-slate-200 rounded-lg p-5">
            <p className="text-sm text-slate-600 mb-2">Trạng thái</p>
            <p className="text-2xl font-bold text-slate-900">{data.status || 'CONFIRMED'}</p>
          </div>
        </div>

        <div className="bg-white border border-slate-200 rounded-lg p-6 mb-8">
          <h2 className="text-2xl font-bold text-slate-900 mb-4">Thông tin phiên đầu tư</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
            <div>
              <p className="text-slate-500">Session ID</p>
              <p className="font-semibold text-slate-900 break-all">{data.sessionId}</p>
            </div>
            <div>
              <p className="text-slate-500">Plan ID</p>
              <p className="font-semibold text-slate-900">{data.planId}</p>
            </div>
            <div>
              <p className="text-slate-500">User ID</p>
              <p className="font-semibold text-slate-900">{data.userId}</p>
            </div>
            <div>
              <p className="text-slate-500">Thời gian tạo</p>
              <p className="font-semibold text-slate-900">{formatDateTime(data.createdAt)}</p>
            </div>
          </div>
        </div>

        <div className="bg-white border border-slate-200 rounded-lg p-6 mb-8">
          <h2 className="text-2xl font-bold text-slate-900 mb-4">Phân bổ đầu tư</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {data.allocations.map((allocation, index) => {
              const quantity = Number.isFinite(allocation.quantity)
                ? Math.max(1, Math.floor(allocation.quantity))
                : 1;
              const totalAmount = allocation.amount * quantity;

              return (
                <div key={`${allocation.type}-${index}`} className="bg-slate-50 border border-slate-200 rounded-lg p-5">
                  <p className="text-sm text-slate-500 mb-2">{allocation.type}</p>
                  <p className="text-xl font-bold text-slate-900 mb-1">{formatCurrency(totalAmount)}</p>
                  <p className="text-xs text-slate-500 mb-1">
                    {quantity} quantity x {formatCurrency(allocation.amount)}/quantity
                  </p>
                  <p className="text-sm text-slate-600">Tỷ lệ: {(allocation.percent * 100).toFixed(2)}%</p>
                </div>
              );
            })}
          </div>
        </div>

        <div className="bg-white border border-slate-200 rounded-lg p-6 mb-8">
          <h2 className="text-2xl font-bold text-slate-900 mb-4">Snapshot & ghi chú</h2>
          <div className="space-y-4">
            <div>
              <p className="text-slate-500 text-sm mb-1">Recommendation Snapshot</p>
              <p className="text-slate-900 whitespace-pre-wrap wrap-break-word">
                {data.recommendationSnapshot || 'Không có snapshot'}
              </p>
            </div>
          </div>
        </div>

        <div className="flex gap-4 justify-between">
          <button
            onClick={() => router.push('/investment-recommendations')}
            className="px-6 py-3 border border-slate-300 text-slate-900 font-semibold rounded-lg hover:bg-slate-50 transition"
          >
            Xem lại đề xuất
          </button>
          <button
            onClick={() => router.push('/dashboard')}
            className="px-6 py-3 bg-emerald-600 hover:bg-emerald-700 text-white font-semibold rounded-lg transition"
          >
            Về Dashboard
          </button>
        </div>
      </div>
    </div>
  );
};

export default InvestmentCompletePage;
