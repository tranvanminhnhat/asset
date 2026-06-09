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

const STATUS_MAP: Record<string, { label: string; className: string }> = {
  DRAFT: { label: 'Nháp', className: 'bg-slate-100 text-slate-600' },
  IN_PROGRESS: { label: 'Đang thực hiện', className: 'bg-blue-100 text-blue-700' },
  COMPLETED: { label: 'Hoàn thành', className: 'bg-emerald-100 text-emerald-700' },
  CANCELLED: { label: 'Đã huỷ', className: 'bg-red-100 text-red-700' },
};

const StatusBadge: React.FC<{ status: string }> = ({ status }) => {
  const map = STATUS_MAP[status] ?? { label: status, className: 'bg-slate-100 text-slate-600' };
  return (
    <span className={`inline-flex items-center px-2.5 py-1 rounded-full text-xs font-semibold ${map.className}`}>
      {map.label}
    </span>
  );
};

// ─── Plan detail drawer ───────────────────────────────────────────────────────

const PlanDetail: React.FC<{ planId: number; onClose: () => void }> = ({ planId, onClose }) => {
  const { data, isLoading } = useGetInvestmentPlanByIdQuery(planId);
  const plan = data?.data;

  return (
    <div className="fixed inset-0 z-50 flex items-end sm:items-center justify-center bg-black/40 backdrop-blur-sm" onClick={onClose}>
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-2xl max-h-[90vh] overflow-y-auto p-6" onClick={(e) => e.stopPropagation()}>
        <div className="flex items-center justify-between mb-5">
          <div>
            <h3 className="text-base font-semibold text-slate-900">Chi tiết Kế hoạch #{planId}</h3>
            {plan && <p className="text-xs text-slate-500 mt-0.5">Tạo lúc {formatDateTime(plan.createdAt)}</p>}
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
                { label: 'Tổng đầu tư', value: formatCurrency(plan.totalAmount), className: 'text-emerald-700 font-semibold' },
                { label: 'Trạng thái', value: <StatusBadge status={plan.status} /> },
                { label: 'User ID', value: `#${plan.userId}` },
                { label: 'Strategy ID', value: plan.strategyId ? `#${plan.strategyId}` : '—' },
              ].map(({ label, value, className }) => (
                <div key={label} className="bg-slate-50 rounded-xl p-3.5">
                  <p className="text-xs text-slate-500 mb-1">{label}</p>
                  <div className={`text-sm ${className ?? 'text-slate-900'}`}>{value}</div>
                </div>
              ))}
            </div>

            <h4 className="text-sm font-semibold text-slate-700 mb-3">Phân bổ ({plan.allocations?.length ?? 0})</h4>
            {plan.allocations?.length > 0 ? (
              <div className="space-y-2">
                {plan.allocations.map((alloc) => (
                  <div key={alloc.allocationId} className="bg-white border border-slate-200 rounded-xl p-4 flex items-center justify-between">
                    <div>
                      <p className="text-sm font-semibold text-slate-800">{alloc.assetType}</p>
                      <p className="text-xs text-slate-500 mt-0.5">{alloc.percentage}% phân bổ</p>
                    </div>
                    <p className="text-sm font-bold text-emerald-700">{formatCurrency(alloc.amount)}</p>
                  </div>
                ))}
              </div>
            ) : (
              <p className="text-sm text-slate-400 text-center py-4">Chưa có phân bổ</p>
            )}
          </>
        ) : (
          <p className="text-sm text-slate-400 text-center py-8">Không thể tải dữ liệu.</p>
        )}
      </div>
    </div>
  );
};

// ─── Plan list card ───────────────────────────────────────────────────────────

const PlanCard: React.FC<{ plan: InvestmentPlanResponse; onViewDetail: (id: number) => void }> = ({ plan, onViewDetail }) => (
  <div className="bg-white border border-slate-200 rounded-xl p-5 hover:shadow-md transition-shadow">
    <div className="flex items-start justify-between mb-3">
      <div>
        <p className="text-xs font-mono text-slate-400 mb-1">Kế hoạch #{plan.planId}</p>
        <p className="text-xl font-bold text-slate-900">{formatCurrency(plan.totalAmount)}</p>
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
      <span className="text-xs text-slate-500 whitespace-nowrap">{plan.allocations?.length ?? 0} phân bổ</span>
    </div>
    <div className="flex items-center justify-between text-xs text-slate-400">
      <span>{formatDateTime(plan.createdAt)}</span>
      <button onClick={() => onViewDetail(plan.planId)} className="text-emerald-600 hover:text-emerald-700 font-medium transition-colors">Xem chi tiết →</button>
    </div>
  </div>
);

// ─── Main page ────────────────────────────────────────────────────────────────

const InvestmentPlansPage: React.FC = () => {
  const [selectedPlanId, setSelectedPlanId] = useState<number | null>(null);
  const { data, isLoading, isError, refetch } = useGetInvestmentPlansQuery();
  const plans = data?.data ?? [];

  return (
    <div className="min-h-screen bg-slate-50">
      <div className="max-w-4xl mx-auto px-4 py-8">
        <PageHeader
          title="Kế hoạch đầu tư"
          description="Lịch sử và danh sách các kế hoạch đầu tư của bạn"
          actions={
            <Link href="/investment-amount">
              <button className="inline-flex items-center gap-2 px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white text-sm font-semibold rounded-xl transition-colors shadow-sm">
                + Kế hoạch mới
              </button>
            </Link>
          }
        />

        {isLoading ? (
          <div className="flex items-center justify-center py-20"><PageLoader /></div>
        ) : isError ? (
          <div className="text-center py-16">
            <p className="text-slate-500 mb-4">Không thể tải danh sách kế hoạch.</p>
            <button onClick={refetch} className="text-sm text-emerald-600 hover:underline">Thử lại</button>
          </div>
        ) : plans.length === 0 ? (
          <div className="text-center py-20">
            <div className="w-16 h-16 bg-slate-100 rounded-2xl flex items-center justify-center mx-auto mb-4">
              <svg className="w-8 h-8 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
            </div>
            <h3 className="text-base font-semibold text-slate-900 mb-1">Chưa có kế hoạch đầu tư</h3>
            <p className="text-sm text-slate-500 mb-6">Bắt đầu hành trình đầu tư của bạn ngay hôm nay.</p>
            <Link href="/investment-amount">
              <button className="inline-flex items-center gap-2 px-5 py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white text-sm font-semibold rounded-xl transition-colors shadow-sm">
                Tạo kế hoạch đầu tư
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
