'use client';
import React, { useState } from 'react';
import { useRouter } from 'next/navigation';
import { formatCurrency, formatDateTime } from '@/lib/formatters';
import LogoutButton from '@/features/logout/components/LogoutButton';
import { useInvestmentReview } from '../hooks/useInvestmentReview';
import { AllocationDetailReview } from '../type/investment-review.type';
import { useFinalizeSessionMutation } from '@/features/investment-complete/services/finalize-session.api';

const FINALIZE_STORAGE_KEY = 'investmentFinalizeResult';

const statusConfig: Record<string, { label: string; color: string; bg: string }> = {
  NOT_STARTED: { label: 'Chưa xử lý', color: 'text-slate-600', bg: 'bg-slate-100' },
  IN_PROGRESS: { label: 'Đang xử lý', color: 'text-amber-700', bg: 'bg-amber-50' },
  COMPLETED: { label: 'Hoàn thành', color: 'text-emerald-700', bg: 'bg-emerald-50' },
  LOCKED: { label: 'Khóa', color: 'text-slate-400', bg: 'bg-slate-50' },
};

const typeNames: Record<string, string> = {
  EMERGENCY: 'Quỹ Khẩn cấp',
  GOLD: 'Quỹ Vàng',
  ETF: 'Quỹ ETF',
  STOCK: 'Cổ Phiếu',
};

const AllocationRow: React.FC<{ allocation: AllocationDetailReview }> = ({ allocation }) => {
  const sts = statusConfig[allocation.status] ?? statusConfig.NOT_STARTED;
  const percentDisplay = Number(allocation.percentage).toFixed(1);

  return (
    <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-5">
      <div className="flex items-center justify-between mb-3">
        <div className="flex items-center gap-2">
          <h4 className="text-sm font-semibold text-slate-900">
            {typeNames[allocation.assetType] ?? allocation.assetType}
          </h4>
          <span className="text-xs text-slate-400">({percentDisplay}%)</span>
        </div>
        <span className={`inline-flex items-center px-2.5 py-1 rounded-lg text-xs font-semibold ${sts.color} ${sts.bg}`}>
          {sts.label}
        </span>
      </div>

      <div className="grid grid-cols-3 gap-3 mb-3">
        <div className="bg-slate-50 rounded-lg p-3">
          <p className="text-xs text-slate-500 mb-0.5">Đề xuất</p>
          <p className="text-sm font-semibold text-slate-900">{formatCurrency(allocation.recommendedAmount)}</p>
        </div>
        <div className="bg-emerald-50 rounded-lg p-3">
          <p className="text-xs text-slate-500 mb-0.5">Đã đầu tư</p>
          <p className="text-sm font-semibold text-emerald-700">{formatCurrency(allocation.investedAmount)}</p>
        </div>
        <div className="bg-rose-50 rounded-lg p-3">
          <p className="text-xs text-slate-500 mb-0.5">Dư lại</p>
          <p className="text-sm font-semibold text-rose-600">{formatCurrency(allocation.residualAmount)}</p>
        </div>
      </div>

      {allocation.selections && allocation.selections.length > 0 && (
        <div className="border-t border-slate-100 pt-3">
          <p className="text-xs text-slate-500 font-medium mb-2">Tài sản đã chọn</p>
          <div className="space-y-1.5">
            {allocation.selections.map((sel) => (
              <div key={sel.selectionId} className="flex items-center justify-between">
                <div>
                  <span className="text-xs font-semibold text-slate-900">{sel.assetCode}</span>
                  <span className="text-xs text-slate-400 ml-2">{sel.assetName}</span>
                </div>
                <div className="text-right">
                  <span className="text-xs text-slate-500">x{sel.quantity} @ {formatCurrency(sel.buyPrice)}</span>
                  <span className="text-xs font-semibold text-slate-900 ml-2">{formatCurrency(sel.investedAmount)}</span>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

const InvestmentReviewPage: React.FC = () => {
  const router = useRouter();
  const { sessionId, sessionDetail, isLoading, isError } = useInvestmentReview();
  const [finalizeSession, { isLoading: isFinalizing }] = useFinalizeSessionMutation();
  const [finalizeError, setFinalizeError] = useState<string | null>(null);

  if (isLoading) {
    return (
      <div className="min-h-screen bg-slate-50 flex items-center justify-center">
        <div className="text-center">
          <svg className="w-8 h-8 animate-spin text-emerald-500 mx-auto mb-3" fill="none" viewBox="0 0 24 24">
            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
          </svg>
          <p className="text-sm text-slate-500">Đang tải dữ liệu...</p>
        </div>
      </div>
    );
  }

  if (!sessionId || isError || !sessionDetail) {
    return (
      <div className="min-h-screen bg-slate-50 flex items-center justify-center px-4">
        <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-8 max-w-md w-full text-center">
          <div className="w-14 h-14 rounded-2xl bg-slate-100 flex items-center justify-center mx-auto mb-4">
            <svg xmlns="http://www.w3.org/2000/svg" className="w-7 h-7 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
            </svg>
          </div>
          <h1 className="text-xl font-bold text-slate-900 mb-2">Không tìm thấy phiên đầu tư</h1>
          <p className="text-sm text-slate-500 mb-6">Vui lòng quay lại đề xuất đầu tư.</p>
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

  const allocations = sessionDetail.allocations ?? [];
  const totalInvested = allocations.reduce((sum, a) => sum + Number(a.investedAmount ?? 0), 0);
  const totalResidual = allocations.reduce((sum, a) => sum + Number(a.residualAmount ?? 0), 0);

  const handleFinalize = async () => {
    if (!sessionId) return;
    setFinalizeError(null);
    try {
      const res = await finalizeSession(sessionId).unwrap();
      if (res.data) {
        localStorage.setItem(FINALIZE_STORAGE_KEY, JSON.stringify(res.data));
      }
      router.push('/investment-complete');
    } catch {
      setFinalizeError('Không thể hoàn tất phiên đầu tư. Vui lòng thử lại.');
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 px-4 py-10 animate-fade-up">
      <div className="max-w-4xl mx-auto">
        {/* Header */}
        <div className="flex items-center justify-between mb-8">
          <div>
            <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Xem lại đầu tư</h1>
            <p className="text-sm text-slate-500 mt-1">
              Chiến lược: <span className="font-semibold text-emerald-700">{sessionDetail.strategyCode}</span>
              <span className="mx-2 text-slate-300">•</span>
              Phiên: <span className="font-semibold text-slate-700">#{sessionDetail.sessionId}</span>
            </p>
          </div>
          <LogoutButton />
        </div>

        {/* Steps */}
        <div className="flex items-center gap-2 mb-8">
          {['Số tiền & Chiến lược', 'Xử lý danh mục', 'Xem lại', 'Hoàn thành'].map((step, i) => (
            <React.Fragment key={step}>
              <div className={`flex items-center gap-2 text-xs font-medium ${i <= 2 ? 'text-emerald-700' : 'text-slate-400'}`}>
                <span className={`w-6 h-6 rounded-full flex items-center justify-center text-xs font-bold ${
                  i <= 2 ? 'bg-emerald-600 text-white' : 'bg-slate-200 text-slate-500'
                }`}>{i + 1}</span>
                <span className="hidden sm:inline">{step}</span>
              </div>
              {i < 3 && <div className="flex-1 h-px bg-slate-200" />}
            </React.Fragment>
          ))}
        </div>

        {/* Summary stats */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
          {[
            { label: 'Tổng số tiền', value: formatCurrency(sessionDetail.totalAmount), cls: 'text-slate-900' },
            { label: 'Tổng đã đầu tư', value: formatCurrency(totalInvested), cls: 'text-emerald-700' },
            { label: 'Tổng dư lại', value: formatCurrency(totalResidual), cls: 'text-rose-600' },
            { label: 'Tiền dự kiến hoàn về', value: formatCurrency(totalResidual), cls: 'text-amber-600' },
          ].map(({ label, value, cls }) => (
            <div key={label} className="bg-white rounded-xl border border-slate-200 shadow-sm p-4">
              <p className="text-xs text-slate-500 mb-1">{label}</p>
              <p className={`text-base font-bold ${cls}`}>{value}</p>
            </div>
          ))}
        </div>

        {/* Session info */}
        <div className="bg-white rounded-2xl border border-slate-200 shadow-sm px-6 py-4 mb-6 flex items-center justify-between">
          <div>
            <p className="text-xs text-slate-500">Trạng thái phiên</p>
            <p className="text-sm font-semibold text-slate-900 mt-0.5">{sessionDetail.status}</p>
          </div>
          <div>
            <p className="text-xs text-slate-500">Ngày tạo</p>
            <p className="text-sm font-semibold text-slate-900 mt-0.5">{formatDateTime(sessionDetail.createdAt)}</p>
          </div>
          <div>
            <p className="text-xs text-slate-500">Chiến lược</p>
            <p className="text-sm font-semibold text-emerald-700 mt-0.5">{sessionDetail.strategyCode}</p>
          </div>
        </div>

        {/* Allocations */}
        <div className="mb-6">
          <h2 className="text-base font-semibold text-slate-900 mb-4">Chi tiết phân bổ</h2>
          <div className="space-y-4">
            {allocations.map((allocation) => (
              <AllocationRow key={allocation.allocationId} allocation={allocation} />
            ))}
          </div>
        </div>

        {/* Actions */}
        <div className="flex flex-col gap-3">
          {finalizeError && (
            <div className="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
              {finalizeError}
            </div>
          )}
          <div className="flex items-center justify-between">
            <button
              onClick={() => router.push('/investment-recommendations')}
              className="px-5 py-2.5 border border-slate-300 text-slate-700 font-semibold text-sm rounded-xl hover:bg-slate-50 transition"
            >
              Quay lại
            </button>
            <button
              onClick={handleFinalize}
              disabled={isFinalizing}
              className="inline-flex items-center gap-2 px-6 py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white font-semibold text-sm rounded-xl transition disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {isFinalizing ? (
                <>
                  <svg className="animate-spin w-4 h-4" fill="none" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
                  </svg>
                  Đang xử lý...
                </>
              ) : (
                <>
                  Xác nhận &amp; Hoàn thành
                  <svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                    <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" />
                  </svg>
                </>
              )}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default InvestmentReviewPage;
