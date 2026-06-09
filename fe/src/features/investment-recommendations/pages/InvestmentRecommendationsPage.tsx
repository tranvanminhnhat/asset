'use client';
import React from 'react';
import { useRouter } from 'next/navigation';
import { formatCurrency } from '@/lib/formatters';
import LogoutButton from '@/features/logout/components/LogoutButton';
import { useInvestmentRecommendations } from '../hooks/useInvestmentRecommendations';
import { AllocationDetail } from '../type/investment-recommendations.type';

const statusConfig: Record<string, { label: string; color: string; bg: string; border: string }> = {
  NOT_STARTED: { label: 'Chưa xử lý', color: 'text-slate-600', bg: 'bg-slate-100', border: 'border-slate-200' },
  IN_PROGRESS: { label: 'Đang xử lý', color: 'text-amber-700', bg: 'bg-amber-50', border: 'border-amber-200' },
  COMPLETED: { label: 'Hoàn thành', color: 'text-emerald-700', bg: 'bg-emerald-50', border: 'border-emerald-200' },
  LOCKED: { label: 'Khóa', color: 'text-slate-400', bg: 'bg-slate-50', border: 'border-slate-200' },
};

const typeConfig: Record<string, { color: string; bg: string; bar: string }> = {
  EMERGENCY: { color: 'text-amber-700', bg: 'bg-amber-50 border-amber-200', bar: 'bg-amber-500' },
  GOLD: { color: 'text-yellow-700', bg: 'bg-yellow-50 border-yellow-200', bar: 'bg-yellow-500' },
  ETF: { color: 'text-purple-700', bg: 'bg-purple-50 border-purple-200', bar: 'bg-purple-500' },
  STOCK: { color: 'text-blue-700', bg: 'bg-blue-50 border-blue-200', bar: 'bg-blue-500' },
};

const typeNames: Record<string, string> = {
  EMERGENCY: 'Quỹ Khẩn cấp',
  GOLD: 'Quỹ Vàng',
  ETF: 'Quỹ ETF',
  STOCK: 'Cổ Phiếu',
};

interface AllocationCardProps {
  allocation: AllocationDetail;
  onProcess: (allocation: AllocationDetail) => void;
}

const AllocationCard: React.FC<AllocationCardProps> = ({ allocation, onProcess }) => {
  const cfg = typeConfig[allocation.assetType] ?? { color: 'text-slate-700', bg: 'bg-slate-50 border-slate-200', bar: 'bg-emerald-500' };
  const sts = statusConfig[allocation.status] ?? statusConfig.NOT_STARTED;
  const isLocked = allocation.status === 'LOCKED';
  const isCompleted = allocation.status === 'COMPLETED';
  const isClickable = !isLocked;
  const percentDisplay = Number(allocation.percentage).toFixed(1);

  return (
    <div
      className={[
        'bg-white rounded-xl border p-5 shadow-sm transition-all duration-200',
        isLocked ? 'opacity-60 cursor-not-allowed border-slate-200' : 'cursor-pointer hover:shadow-md hover:-translate-y-0.5 border-slate-200',
        isCompleted ? 'ring-2 ring-emerald-200' : '',
      ].join(' ')}
      onClick={() => isClickable && onProcess(allocation)}
    >
      <div className="flex items-center gap-2 mb-3">
        <div className={`px-2.5 py-1 rounded-lg border text-xs font-bold ${cfg.bg} ${cfg.color}`}>
          {allocation.assetType}
        </div>
        <span className="text-xs font-bold text-slate-900 ml-auto">{percentDisplay}%</span>
        {isCompleted && (
          <div className="w-6 h-6 rounded-full bg-emerald-100 flex items-center justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" className="w-3.5 h-3.5 text-emerald-600" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.5}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" />
            </svg>
          </div>
        )}
        {isLocked && (
          <div className="w-6 h-6 rounded-full bg-slate-100 flex items-center justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" className="w-3.5 h-3.5 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
            </svg>
          </div>
        )}
      </div>

      <h4 className="text-sm font-semibold text-slate-900 mb-3">
        {typeNames[allocation.assetType] ?? allocation.assetType}
      </h4>

      <div className="w-full bg-slate-100 rounded-full h-1.5 mb-3">
        <div className={`h-1.5 rounded-full ${cfg.bar}`} style={{ width: `${Math.min(Number(percentDisplay), 100)}%` }} />
      </div>

      <div className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-lg border text-xs font-semibold mb-3 ${sts.color} ${sts.bg} ${sts.border}`}>
        <span className={`w-1.5 h-1.5 rounded-full ${isCompleted ? 'bg-emerald-500' : isLocked ? 'bg-slate-400' : allocation.status === 'IN_PROGRESS' ? 'bg-amber-500' : 'bg-slate-400'}`} />
        {sts.label}
      </div>

      <div className="space-y-1.5">
        <div className="flex justify-between">
          <span className="text-xs text-slate-500">Đề xuất</span>
          <span className="text-xs font-semibold text-slate-900">{formatCurrency(allocation.recommendedAmount)}</span>
        </div>
        {isCompleted && (
          <>
            <div className="flex justify-between">
              <span className="text-xs text-slate-500">Đã đầu tư</span>
              <span className="text-xs font-semibold text-emerald-700">{formatCurrency(allocation.investedAmount)}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-xs text-slate-500">Dư lại</span>
              <span className="text-xs font-semibold text-rose-600">{formatCurrency(allocation.residualAmount)}</span>
            </div>
          </>
        )}
      </div>

      {isClickable && !isCompleted && (
        <div className="mt-3 pt-3 border-t border-slate-100">
          <span className="text-xs font-semibold text-emerald-600 flex items-center gap-1">
            Nhấn để xử lý
            <svg xmlns="http://www.w3.org/2000/svg" className="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M9 5l7 7-7 7" />
            </svg>
          </span>
        </div>
      )}
    </div>
  );
};

const InvestmentRecommendationsPage: React.FC = () => {
  const router = useRouter();
  const {
    sessionId,
    sessionDetail,
    isLoading,
    isError,
    isReviewing,
    isReviewError,
    submitReview,
  } = useInvestmentRecommendations();

  if (isLoading) {
    return (
      <div className="min-h-screen bg-slate-50 flex items-center justify-center">
        <div className="text-center">
          <svg className="w-8 h-8 animate-spin text-emerald-500 mx-auto mb-3" fill="none" viewBox="0 0 24 24">
            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
          </svg>
          <p className="text-sm text-slate-500">Đang tải đề xuất đầu tư...</p>
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
              <path strokeLinecap="round" strokeLinejoin="round" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
            </svg>
          </div>
          <h1 className="text-xl font-bold text-slate-900 mb-2">Chưa có phiên đầu tư</h1>
          <p className="text-sm text-slate-500 mb-6">
            Vui lòng quay lại để tạo phiên đầu tư mới.
          </p>
          <button
            onClick={() => router.push('/investment-amount')}
            className="inline-flex items-center gap-2 rounded-xl bg-emerald-600 px-6 py-2.5 text-sm font-semibold text-white transition hover:bg-emerald-700"
          >
            Quay lại nhập thông tin
          </button>
        </div>
      </div>
    );
  }

  const allocations = sessionDetail.allocations ?? [];
  const nonLockedAllocations = allocations.filter((a) => a.status !== 'LOCKED');
  const completedCount = nonLockedAllocations.filter((a) => a.status === 'COMPLETED').length;
  const allCompleted = nonLockedAllocations.length > 0 && completedCount === nonLockedAllocations.length;

  const totalInvested = allocations.reduce((sum, a) => sum + (Number(a.investedAmount) ?? 0), 0);
  const totalResidual = allocations.reduce((sum, a) => sum + (Number(a.residualAmount) ?? 0), 0);

  const handleProcess = (allocation: AllocationDetail) => {
    const params = new URLSearchParams({
      sessionId: String(sessionId),
      allocationId: String(allocation.allocationId),
      type: allocation.assetType,
      recommendedAmount: String(allocation.recommendedAmount),
    });
    router.push(`/investment-processing?${params.toString()}`);
  };

  const handleContinueToReview = async () => {
    try {
      await submitReview();
      router.push('/investment-review');
    } catch {
      // error shown via isReviewError
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 px-4 py-10 animate-fade-up">
      <div className="max-w-5xl mx-auto">
        {/* Header */}
        <div className="flex items-center justify-between mb-8">
          <div>
            <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Đề xuất đầu tư</h1>
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
              <div className={`flex items-center gap-2 text-xs font-medium ${i <= 1 ? 'text-emerald-700' : 'text-slate-400'}`}>
                <span className={`w-6 h-6 rounded-full flex items-center justify-center text-xs font-bold ${
                  i <= 1 ? 'bg-emerald-600 text-white' : 'bg-slate-200 text-slate-500'
                }`}>{i + 1}</span>
                <span className="hidden sm:inline">{step}</span>
              </div>
              {i < 3 && <div className="flex-1 h-px bg-slate-200" />}
            </React.Fragment>
          ))}
        </div>

        {/* Progress indicator */}
        <div className="bg-white rounded-2xl border border-slate-200 shadow-sm px-6 py-4 mb-6 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-emerald-50 flex items-center justify-center">
              <svg xmlns="http://www.w3.org/2000/svg" className="w-5 h-5 text-emerald-600" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
              </svg>
            </div>
            <div>
              <p className="text-sm font-semibold text-slate-900">
                {completedCount}/{nonLockedAllocations.length} danh mục đã xử lý
              </p>
              <p className="text-xs text-slate-500">Nhấn vào từng danh mục để xử lý đầu tư</p>
            </div>
          </div>
          <div className="text-right">
            <p className="text-xs text-slate-500">Tổng đầu tư</p>
            <p className="text-base font-bold text-slate-900">{formatCurrency(sessionDetail.totalAmount)}</p>
          </div>
        </div>

        {/* Allocation Cards */}
        <div className="mb-6">
          <h2 className="text-base font-semibold text-slate-900 mb-4">Phân bổ theo danh mục</h2>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
            {allocations.map((allocation) => (
              <AllocationCard
                key={allocation.allocationId}
                allocation={allocation}
                onProcess={handleProcess}
              />
            ))}
          </div>
        </div>

        {/* Summary */}
        <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-6 mb-6">
          <h3 className="text-sm font-semibold text-slate-900 mb-4">Tóm tắt phiên đầu tư</h3>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            {[
              { label: 'Tổng số tiền', value: formatCurrency(sessionDetail.totalAmount), cls: 'text-slate-900' },
              { label: 'Đã đầu tư', value: formatCurrency(totalInvested), cls: 'text-emerald-700' },
              { label: 'Dư lại', value: formatCurrency(totalResidual), cls: 'text-rose-600' },
              { label: 'Trạng thái', value: sessionDetail.status, cls: 'text-slate-700' },
            ].map(({ label, value, cls }) => (
              <div key={label} className="bg-slate-50 rounded-xl p-4 border border-slate-100">
                <p className="text-xs text-slate-500 mb-1">{label}</p>
                <p className={`text-base font-bold ${cls}`}>{value}</p>
              </div>
            ))}
          </div>
        </div>

        {isReviewError && (
          <div className="mb-4 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
            Không thể chuyển sang bước xem lại. Vui lòng thử lại.
          </div>
        )}

        {/* Actions */}
        <div className="flex items-center justify-between">
          <button
            onClick={() => router.back()}
            className="px-5 py-2.5 border border-slate-300 text-slate-700 font-semibold text-sm rounded-xl hover:bg-slate-50 transition"
          >
            Quay lại
          </button>
          <button
            onClick={handleContinueToReview}
            disabled={!allCompleted || isReviewing}
            title={!allCompleted ? `Còn ${nonLockedAllocations.length - completedCount} danh mục chưa xử lý` : ''}
            className="inline-flex items-center gap-2 px-6 py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white font-semibold text-sm rounded-xl transition disabled:opacity-40 disabled:cursor-not-allowed"
          >
            {isReviewing ? (
              <>
                <svg className="w-4 h-4 animate-spin" fill="none" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
                </svg>
                Đang xử lý...
              </>
            ) : (
              <>
                Tiếp tục xem lại
                <svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M17 8l4 4m0 0l-4 4m4-4H3" />
                </svg>
              </>
            )}
          </button>
        </div>
      </div>
    </div>
  );
};

export default InvestmentRecommendationsPage;
