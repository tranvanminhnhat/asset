'use client';
import React, { useState } from 'react';
import Link from 'next/link';
import { formatCurrency, formatDateTime } from '@/lib/formatters';
import PageHeader from '@/common/components/ui/PageHeader';
import PageLoader from '@/common/components/ui/PageLoader';
import {
  useGetInvestmentPlansQuery,
  useGetInvestmentPlanByIdQuery,
} from '../services/investment-plans.api';
import { InvestmentPlanResponse } from '../type/investment-plans.type';
import { useI18n } from '@/common/i18n/I18nProvider';

const STATUS_MAP: Record<string, { key: string; className: string }> = {
  DRAFT: { key: 'DRAFT', className: 'bg-slate-100 text-slate-600' },
  IN_PROGRESS: { key: 'IN_PROGRESS', className: 'bg-blue-100 text-blue-700' },
  COMPLETED: { key: 'COMPLETED', className: 'bg-emerald-100 text-emerald-700' },
  CANCELLED: { key: 'CANCELLED', className: 'bg-red-100 text-red-700' },
};

const StatusBadge: React.FC<{ status: string }> = ({ status }) => {
  const { pick } = useI18n();
  const map = STATUS_MAP[status] ?? { key: status, className: 'bg-slate-100 text-slate-600' };
  const label = {
    DRAFT: pick({ vi: 'Nhap', en: 'Draft', ja: '下書き' }),
    IN_PROGRESS: pick({ vi: 'Dang thuc hien', en: 'In progress', ja: '進行中' }),
    COMPLETED: pick({ vi: 'Hoan thanh', en: 'Completed', ja: '完了' }),
    CANCELLED: pick({ vi: 'Da huy', en: 'Cancelled', ja: 'キャンセル' }),
  }[map.key] ?? map.key;
  return (
    <span className={`inline-flex items-center px-2.5 py-1 rounded-full text-xs font-semibold ${map.className}`}>
      {label}
    </span>
  );
};

// ─── Plan detail drawer ───────────────────────────────────────────────────────

const PlanDetail: React.FC<{ planId: number; onClose: () => void }> = ({ planId, onClose }) => {
  const { pick, locale } = useI18n();
  const { data, isLoading } = useGetInvestmentPlanByIdQuery(planId);
  const plan = data?.data;

  return (
    <div className="fixed inset-0 z-50 flex items-end sm:items-center justify-center bg-black/40 backdrop-blur-sm" onClick={onClose}>
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-2xl max-h-[90vh] overflow-y-auto p-6" onClick={(e) => e.stopPropagation()}>
        <div className="flex items-center justify-between mb-5">
          <div>
            <h3 className="text-base font-semibold text-slate-900">{pick({ vi: 'Chi tiet ke hoach', en: 'Plan details', ja: 'プラン詳細' })} #{planId}</h3>
            {plan && <p className="text-xs text-slate-500 mt-0.5">{pick({ vi: 'Tao luc', en: 'Created at', ja: '作成日時' })} {formatDateTime(plan.createdAt, locale)}</p>}
          </div>
          <button onClick={onClose} className="p-1.5 rounded-lg hover:bg-slate-100 text-slate-400 hover:text-slate-700 transition-colors">
            <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}><path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" /></svg>
          </button>
        </div>

        {isLoading ? (
          <div className="flex items-center justify-center py-12"><PageLoader /></div>
        ) : plan ? (
          <>
            <div className="grid grid-cols-2 gap-4 mb-5">
              {[
                { label: pick({ vi: 'Tong dau tu', en: 'Total amount', ja: '合計金額' }), value: formatCurrency(plan.totalAmount, 'VND', locale), className: 'text-emerald-700 font-semibold' },
                { label: pick({ vi: 'Trang thai', en: 'Status', ja: 'ステータス' }), value: <StatusBadge status={plan.status} /> },
                { label: 'User ID', value: `#${plan.userId}` },
                { label: 'Strategy ID', value: plan.strategyId ? `#${plan.strategyId}` : '—' },
              ].map(({ label, value, className }) => (
                <div key={label} className="bg-slate-50 rounded-xl p-3.5">
                  <p className="text-xs text-slate-500 mb-1">{label}</p>
                  <div className={`text-sm ${className ?? 'text-slate-900'}`}>{value}</div>
                </div>
              ))}
            </div>

            <h4 className="text-sm font-semibold text-slate-700 mb-3">{pick({ vi: 'Phan bo', en: 'Allocations', ja: '配分' })} ({plan.allocations?.length ?? 0})</h4>
            {plan.allocations?.length > 0 ? (
              <div className="space-y-2">
                {plan.allocations.map((alloc) => (
                  <div key={alloc.allocationId} className="bg-white border border-slate-200 rounded-xl p-4 flex items-center justify-between">
                    <div>
                      <p className="text-sm font-semibold text-slate-800">{alloc.assetType}</p>
                      <p className="text-xs text-slate-500 mt-0.5">{alloc.percentage}% {pick({ vi: 'phan bo', en: 'allocation', ja: '配分' })}</p>
                    </div>
                    <p className="text-sm font-bold text-emerald-700">{formatCurrency(alloc.amount, 'VND', locale)}</p>
                  </div>
                ))}
              </div>
            ) : (
              <p className="text-sm text-slate-400 text-center py-4">{pick({ vi: 'Chua co phan bo', en: 'No allocations yet', ja: '配分はまだありません' })}</p>
            )}
          </>
        ) : (
          <p className="text-sm text-slate-400 text-center py-8">{pick({ vi: 'Khong the tai du lieu.', en: 'Unable to load data.', ja: 'データを読み込めません。' })}</p>
        )}
      </div>
    </div>
  );
};

// ─── Plan list card ───────────────────────────────────────────────────────────

const PlanCard: React.FC<{ plan: InvestmentPlanResponse; onViewDetail: (id: number) => void }> = ({ plan, onViewDetail }) => {
  const { pick, locale } = useI18n();

  return (
    <div className="bg-white border border-slate-200 rounded-xl p-5 hover:shadow-md transition-shadow">
    <div className="flex items-start justify-between mb-3">
      <div>
        <p className="text-xs font-mono text-slate-400 mb-1">{pick({ vi: 'Ke hoach', en: 'Plan', ja: 'プラン' })} #{plan.planId}</p>
        <p className="text-xl font-bold text-slate-900">{formatCurrency(plan.totalAmount, 'VND', locale)}</p>
      </div>
      <StatusBadge status={plan.status} />
    </div>
    <div className="flex items-center gap-3 mb-4">
      <div className="flex-1 bg-slate-100 rounded-full h-1.5">
        <div
          className="bg-emerald-500 h-1.5 rounded-full transition-all"
          style={{ width: `${plan.status === 'COMPLETED' ? 100 : plan.status === 'IN_PROGRESS' ? 60 : 20}%` }}
        />
      </div>
      <span className="text-xs text-slate-500 whitespace-nowrap">{plan.allocations?.length ?? 0} {pick({ vi: 'phan bo', en: 'allocations', ja: '配分' })}</span>
    </div>
    <div className="flex items-center justify-between text-xs text-slate-400">
      <span>{formatDateTime(plan.createdAt, locale)}</span>
      <button onClick={() => onViewDetail(plan.planId)} className="text-emerald-600 hover:text-emerald-700 font-medium transition-colors">{pick({ vi: 'Xem chi tiet', en: 'View detail', ja: '詳細を見る' })} →</button>
    </div>
  </div>
  );
};

// ─── Main page ────────────────────────────────────────────────────────────────

const InvestmentPlansPage: React.FC = () => {
  const { pick } = useI18n();
  const [selectedPlanId, setSelectedPlanId] = useState<number | null>(null);
  const { data, isLoading, isError, refetch } = useGetInvestmentPlansQuery();
  const plans = data?.data ?? [];

  return (
    <div className="min-h-screen bg-slate-50">
      <div className="max-w-4xl mx-auto px-4 py-8">
        <PageHeader
          title={pick({ vi: 'Ke hoach dau tu', en: 'Investment plans', ja: '投資プラン' })}
          description={pick({ vi: 'Lich su va danh sach cac ke hoach dau tu cua ban', en: 'Your investment plan history and list', ja: 'あなたの投資プラン一覧と履歴' })}
          actions={
            <Link href="/investment-amount">
              <button className="inline-flex items-center gap-2 px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white text-sm font-semibold rounded-xl transition-colors shadow-sm">
                + {pick({ vi: 'Ke hoach moi', en: 'New plan', ja: '新規プラン' })}
              </button>
            </Link>
          }
        />

        {isLoading ? (
          <div className="flex items-center justify-center py-20"><PageLoader /></div>
        ) : isError ? (
          <div className="text-center py-16">
            <p className="text-slate-500 mb-4">Không thể tải danh sách kế hoạch.</p>
            <button onClick={refetch} className="text-sm text-emerald-600 hover:underline">{pick({ vi: 'Thu lai', en: 'Retry', ja: '再試行' })}</button>
          </div>
        ) : plans.length === 0 ? (
          <div className="text-center py-20">
            <div className="w-16 h-16 bg-slate-100 rounded-2xl flex items-center justify-center mx-auto mb-4">
              <svg className="w-8 h-8 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
            </div>
            <h3 className="text-base font-semibold text-slate-900 mb-1">{pick({ vi: 'Chua co ke hoach dau tu', en: 'No investment plans yet', ja: '投資プランはまだありません' })}</h3>
            <p className="text-sm text-slate-500 mb-6">{pick({ vi: 'Bat dau hanh trinh dau tu cua ban ngay hom nay.', en: 'Start your investment journey today.', ja: '今日から投資を始めましょう。' })}</p>
            <Link href="/investment-amount">
              <button className="inline-flex items-center gap-2 px-5 py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white text-sm font-semibold rounded-xl transition-colors shadow-sm">
                {pick({ vi: 'Tao ke hoach dau tu', en: 'Create investment plan', ja: '投資プランを作成' })}
              </button>
            </Link>
          </div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            {plans.map((plan) => (
              <PlanCard key={plan.planId} plan={plan} onViewDetail={setSelectedPlanId} />
            ))}
          </div>
        )}

        {selectedPlanId !== null && (
          <PlanDetail planId={selectedPlanId} onClose={() => setSelectedPlanId(null)} />
        )}
      </div>
    </div>
  );
};

export default InvestmentPlansPage;
