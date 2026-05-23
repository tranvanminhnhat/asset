'use client';
import React from 'react';
import { useRouter } from 'next/navigation';
import { formatCurrency } from '@/lib/formatters';
import FundCard from '../components/FundCard';
import LogoutButton from '@/features/logout/components/LogoutButton';
import { useInvestmentRecommendations } from '../hooks/useInvestmentRecommendations';
import { mapAllocationsToFunds } from '../mappers/investment-recommendations.mapper';
import { FundAllocation } from '../type/investment-recommendations.type';

const InvestmentRecommendationsPage: React.FC = () => {
  const router = useRouter();
  const { data, isLoading, isSubmitting, isError, submitInvestmentComplete } = useInvestmentRecommendations();

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

  if (!data) {
    return (
      <div className="min-h-screen bg-slate-50 flex items-center justify-center px-4">
        <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-8 max-w-md w-full text-center">
          <div className="w-14 h-14 rounded-2xl bg-slate-100 flex items-center justify-center mx-auto mb-4">
            <svg xmlns="http://www.w3.org/2000/svg" className="w-7 h-7 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
            </svg>
          </div>
          <h1 className="text-xl font-bold text-slate-900 mb-2">Chưa có dữ liệu đề xuất</h1>
          <p className="text-sm text-slate-500 mb-6">
            Vui lòng quay lại bước nhập số tiền để gọi API và lấy phân bổ đầu tư.
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

  const funds = mapAllocationsToFunds(data.allocations);
  const totalAmount = funds.reduce((sum, fund) => sum + fund.totalAmount, 0);
  const totalSessionAmount = funds.reduce((sum, fund) => sum + fund.amount, 0);
  const totalCarryOverAmount = funds.reduce((sum, fund) => sum + fund.carryOverAmount, 0);
  const totalResidualAmount = funds.reduce((sum, fund) => sum + fund.residualAmount, 0);
  const portfolio = {
    id: '1',
    title: 'Đề xuất Đầu tư Toàn diện',
    description: 'Phân tích Chi tiết Danh mục Đầu tư',
    totalAmount,
    currency: 'VND',
    funds,
    investmentStrategy: data.type,
  };

  const handleCompleteInvestment = async () => {
    try {
      await submitInvestmentComplete();
      router.push('/investment-complete');
    } catch (error) {
      console.error('Investment complete failed:', error);
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 px-4 py-10 animate-fade-up">
      <div className="max-w-5xl mx-auto">
        {/* Header */}
        <div className="flex items-center justify-between mb-8">
          <div>
            <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Đề xuất đầu tư</h1>
            <p className="text-sm text-slate-500 mt-1">Chiến lược: <span className="font-semibold text-emerald-700">{portfolio.investmentStrategy}</span></p>
          </div>
          <LogoutButton />
        </div>

        {/* Steps */}
        <div className="flex items-center gap-2 mb-8">
          {['Số tiền & Chiến lược', 'Xem gợi ý', 'Xác nhận'].map((step, i) => (
            <React.Fragment key={step}>
              <div className={`flex items-center gap-2 text-xs font-medium ${i === 1 ? 'text-emerald-700' : 'text-slate-400'}`}>
                <span className={`w-6 h-6 rounded-full flex items-center justify-center text-xs font-bold ${
                  i === 0 ? 'bg-emerald-600 text-white' : i === 1 ? 'bg-emerald-600 text-white' : 'bg-slate-200 text-slate-500'
                }`}>{i + 1}</span>
                {step}
              </div>
              {i < 2 && <div className="flex-1 h-px bg-slate-200" />}
            </React.Fragment>
          ))}
        </div>

        {/* Fund Cards */}
        <div className="mb-6">
          <h2 className="text-base font-semibold text-slate-900 mb-4">Phân bổ theo danh mục</h2>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
            {portfolio.funds.map((fund: FundAllocation) => (
              <FundCard key={fund.id} fund={fund} />
            ))}
          </div>
        </div>

        {/* Summary */}
        <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-6 mb-6">
          <h3 className="text-sm font-semibold text-slate-900 mb-4">Tóm tắt kế hoạch đầu tư</h3>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            {[
              { label: 'Tiền chia phiên này', value: formatCurrency(totalSessionAmount), cls: 'text-slate-900' },
              { label: 'Tổng có thể đầu tư', value: formatCurrency(portfolio.totalAmount), cls: 'text-slate-900' },
              { label: 'Dư từ phiên trước', value: formatCurrency(totalCarryOverAmount), cls: 'text-amber-600' },
              { label: 'Dư phiên này', value: formatCurrency(totalResidualAmount), cls: 'text-rose-600' },
            ].map(({ label, value, cls }) => (
              <div key={label} className="bg-slate-50 rounded-xl p-4 border border-slate-100">
                <p className="text-xs text-slate-500 mb-1">{label}</p>
                <p className={`text-base font-bold ${cls}`}>{value}</p>
              </div>
            ))}
          </div>
        </div>

        {isError && (
          <div className="mb-6 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
            Không thể hoàn tất đầu tư. Vui lòng thử lại.
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
            onClick={handleCompleteInvestment}
            disabled={isSubmitting}
            className="inline-flex items-center gap-2 px-6 py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white font-semibold text-sm rounded-xl transition disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {isSubmitting ? (
              <>
                <svg className="w-4 h-4 animate-spin" fill="none" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
                </svg>
                Đang hoàn tất...
              </>
            ) : (
              <>
                Hoàn thành đầu tư
                <svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" />
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
