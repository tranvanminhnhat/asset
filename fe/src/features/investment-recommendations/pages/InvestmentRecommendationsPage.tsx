'use client';
import React from 'react';
import { useRouter } from 'next/navigation';
import { formatCurrency } from '@/lib/formatters';
import LogoutButton from '@/features/logout/components/LogoutButton';
import { useInvestmentRecommendations } from '../hooks/useInvestmentRecommendations';
import { AllocationDetail } from '../type/investment-recommendations.type';
import { useI18n } from '@/common/i18n/I18nProvider';

const statusConfig: Record<string, { key: string; color: string; bg: string; border: string }> = {
  NOT_STARTED: { key: 'NOT_STARTED', color: 'text-slate-600', bg: 'bg-slate-100', border: 'border-slate-200' },
  IN_PROGRESS: { key: 'IN_PROGRESS', color: 'text-amber-700', bg: 'bg-amber-50', border: 'border-amber-200' },
  COMPLETED: { key: 'COMPLETED', color: 'text-emerald-700', bg: 'bg-emerald-50', border: 'border-emerald-200' },
  LOCKED: { key: 'LOCKED', color: 'text-slate-400', bg: 'bg-slate-50', border: 'border-slate-200' },
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
  locale: string;
  pick: (values: { vi: string; en: string; ja: string }) => string;
}

const AllocationCard: React.FC<AllocationCardProps> = ({ allocation, onProcess, locale, pick }) => {
  const cfg = typeConfig[allocation.assetType] ?? { color: 'text-slate-700', bg: 'bg-slate-50 border-slate-200', bar: 'bg-emerald-500' };
  const sts = statusConfig[allocation.status] ?? statusConfig.NOT_STARTED;
  const statusLabel = {
    NOT_STARTED: pick({ vi: 'Chua xu ly', en: 'Not started', ja: '未処理' }),
    IN_PROGRESS: pick({ vi: 'Dang xu ly', en: 'In progress', ja: '処理中' }),
    COMPLETED: pick({ vi: 'Hoan thanh', en: 'Completed', ja: '完了' }),
    LOCKED: pick({ vi: 'Khoa', en: 'Locked', ja: 'ロック' }),
  }[sts.key] ?? sts.key;
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
        {pick({
          vi: typeNames[allocation.assetType] ?? allocation.assetType,
          en: allocation.assetType === 'EMERGENCY' ? 'Emergency Fund' : allocation.assetType === 'GOLD' ? 'Gold Fund' : allocation.assetType === 'ETF' ? 'ETF Fund' : allocation.assetType === 'STOCK' ? 'Stock' : allocation.assetType,
          ja: allocation.assetType === 'EMERGENCY' ? '緊急資金' : allocation.assetType === 'GOLD' ? 'ゴールドファンド' : allocation.assetType === 'ETF' ? 'ETFファンド' : allocation.assetType === 'STOCK' ? '株式' : allocation.assetType,
        })}
      </h4>

      <div className="w-full bg-slate-100 rounded-full h-1.5 mb-3">
        <div className={`h-1.5 rounded-full ${cfg.bar}`} style={{ width: `${Math.min(Number(percentDisplay), 100)}%` }} />
      </div>

      <div className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-lg border text-xs font-semibold mb-3 ${sts.color} ${sts.bg} ${sts.border}`}>
        <span className={`w-1.5 h-1.5 rounded-full ${isCompleted ? 'bg-emerald-500' : isLocked ? 'bg-slate-400' : allocation.status === 'IN_PROGRESS' ? 'bg-amber-500' : 'bg-slate-400'}`} />
        {statusLabel}
      </div>

      <div className="space-y-1.5">
        <div className="flex justify-between">
          <span className="text-xs text-slate-500">{pick({ vi: 'De xuat', en: 'Recommended', ja: '提案額' })}</span>
          <span className="text-xs font-semibold text-slate-900">{formatCurrency(allocation.recommendedAmount, 'VND', locale)}</span>
        </div>
        {isCompleted && (
          <>
            <div className="flex justify-between">
              <span className="text-xs text-slate-500">{pick({ vi: 'Da dau tu', en: 'Invested', ja: '投資済み' })}</span>
              <span className="text-xs font-semibold text-emerald-700">{formatCurrency(allocation.investedAmount, 'VND', locale)}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-xs text-slate-500">{pick({ vi: 'Du lai', en: 'Remaining', ja: '残額' })}</span>
              <span className="text-xs font-semibold text-rose-600">{formatCurrency(allocation.residualAmount, 'VND', locale)}</span>
            </div>
          </>
        )}
      </div>

      {isClickable && !isCompleted && (
        <div className="mt-3 pt-3 border-t border-slate-100">
          <span className="text-xs font-semibold text-emerald-600 flex items-center gap-1">
            {pick({ vi: 'Nhan de xu ly', en: 'Click to process', ja: 'クリックして処理' })}
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
  const { pick, locale } = useI18n();
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
          <p className="text-sm text-slate-500">{pick({ vi: 'Dang tai de xuat dau tu...', en: 'Loading investment recommendations...', ja: '投資提案を読み込み中...' })}</p>
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
          <h1 className="text-xl font-bold text-slate-900 mb-2">{pick({ vi: 'Chua co phien dau tu', en: 'No investment session found', ja: '投資セッションがありません' })}</h1>
          <p className="text-sm text-slate-500 mb-6">
            {pick({ vi: 'Vui long quay lai de tao phien dau tu moi.', en: 'Please go back and create a new session.', ja: '戻って新しいセッションを作成してください。' })}
          </p>
          <button
            onClick={() => router.push('/investment-amount')}
            className="inline-flex items-center gap-2 rounded-xl bg-emerald-600 px-6 py-2.5 text-sm font-semibold text-white transition hover:bg-emerald-700"
          >
            {pick({ vi: 'Quay lai nhap thong tin', en: 'Back to input', ja: '入力画面へ戻る' })}
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
            <h1 className="text-2xl font-bold text-slate-900 tracking-tight">{pick({ vi: 'De xuat dau tu', en: 'Investment recommendations', ja: '投資提案' })}</h1>
            <p className="text-sm text-slate-500 mt-1">
              {pick({ vi: 'Chien luoc:', en: 'Strategy:', ja: '戦略:' })} <span className="font-semibold text-emerald-700">{sessionDetail.strategyCode}</span>
              <span className="mx-2 text-slate-300">•</span>
              {pick({ vi: 'Phien:', en: 'Session:', ja: 'セッション:' })} <span className="font-semibold text-slate-700">#{sessionDetail.sessionId}</span>
            </p>
          </div>
          <LogoutButton />
        </div>

        {/* Steps */}
        <div className="flex items-center gap-2 mb-8">
          {[
            pick({ vi: 'So tien & Chien luoc', en: 'Amount & Strategy', ja: '金額と戦略' }),
            pick({ vi: 'Xu ly danh muc', en: 'Process portfolio', ja: 'ポートフォリオ処理' }),
            pick({ vi: 'Xem lai', en: 'Review', ja: '確認' }),
            pick({ vi: 'Hoan thanh', en: 'Complete', ja: '完了' }),
          ].map((step, i) => (
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
              <p className="text-xs text-slate-500">{pick({ vi: 'Nhan vao tung danh muc de xu ly dau tu', en: 'Tap each allocation card to process', ja: '各配分カードをクリックして処理してください' })}</p>
            </div>
          </div>
          <div className="text-right">
            <p className="text-xs text-slate-500">{pick({ vi: 'Tong dau tu', en: 'Total amount', ja: '合計金額' })}</p>
            <p className="text-base font-bold text-slate-900">{formatCurrency(sessionDetail.totalAmount, 'VND', locale)}</p>
          </div>
        </div>

        {/* Allocation Cards */}
        <div className="mb-6">
          <h2 className="text-base font-semibold text-slate-900 mb-4">{pick({ vi: 'Phan bo theo danh muc', en: 'Allocation by category', ja: 'カテゴリ別配分' })}</h2>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
            {allocations.map((allocation) => (
              <AllocationCard
                key={allocation.allocationId}
                allocation={allocation}
                onProcess={handleProcess}
                locale={locale}
                pick={pick}
              />
            ))}
          </div>
        </div>

        {/* Summary */}
        <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-6 mb-6">
          <h3 className="text-sm font-semibold text-slate-900 mb-4">{pick({ vi: 'Tom tat phien dau tu', en: 'Session summary', ja: 'セッション概要' })}</h3>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            {[
              { label: pick({ vi: 'Tong so tien', en: 'Total amount', ja: '総額' }), value: formatCurrency(sessionDetail.totalAmount, 'VND', locale), cls: 'text-slate-900' },
              { label: pick({ vi: 'Da dau tu', en: 'Invested', ja: '投資済み' }), value: formatCurrency(totalInvested, 'VND', locale), cls: 'text-emerald-700' },
              { label: pick({ vi: 'Du lai', en: 'Remaining', ja: '残額' }), value: formatCurrency(totalResidual, 'VND', locale), cls: 'text-rose-600' },
              { label: pick({ vi: 'Trang thai', en: 'Status', ja: 'ステータス' }), value: sessionDetail.status, cls: 'text-slate-700' },
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
            {pick({ vi: 'Khong the chuyen sang buoc xem lai. Vui long thu lai.', en: 'Cannot continue to review. Please try again.', ja: '確認ステップへ進めません。再試行してください。' })}
          </div>
        )}

        {/* Actions */}
        <div className="flex items-center justify-between">
          <button
            onClick={() => router.back()}
            className="px-5 py-2.5 border border-slate-300 text-slate-700 font-semibold text-sm rounded-xl hover:bg-slate-50 transition"
          >
            {pick({ vi: 'Quay lai', en: 'Back', ja: '戻る' })}
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
                {pick({ vi: 'Dang xu ly...', en: 'Processing...', ja: '処理中...' })}
              </>
            ) : (
              <>
                {pick({ vi: 'Tiep tuc xem lai', en: 'Continue to review', ja: '確認へ進む' })}
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
