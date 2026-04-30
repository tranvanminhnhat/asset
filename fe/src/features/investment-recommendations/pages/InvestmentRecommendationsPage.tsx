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
    return <div>Loading...</div>;
  }

  if (!data) {
    return (
      <div className="min-h-screen bg-white px-4 py-10">
        <div className="mx-auto max-w-3xl rounded-lg border border-slate-200 bg-slate-50 p-8">
          <h1 className="mb-3 text-2xl font-bold text-slate-900">Chưa có dữ liệu đề xuất</h1>
          <p className="mb-6 text-slate-600">
            Vui lòng quay lại bước nhập số tiền để gọi API recommend và lấy phân bổ đầu tư.
          </p>
          <button
            onClick={() => router.push('/investment-amount')}
            className="rounded-lg bg-emerald-600 px-6 py-3 font-semibold text-white transition hover:bg-emerald-700"
          >
            Quay lại nhập thông tin
          </button>
        </div>
      </div>
    );
  }

  const funds = mapAllocationsToFunds(data.allocations);
  const totalAmount = funds.reduce((sum, fund) => sum + fund.totalAmount, 0);
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
    <div className="min-h-screen bg-white px-4 py-10">
      <div className="max-w-6xl mx-auto">
        <div className="flex justify-end mb-4">
          <LogoutButton />
        </div>
        {/* Header Section */}
        <div className="mb-12">
          <h1 className="text-4xl font-bold text-slate-900 mb-3">{portfolio.title}</h1>
          <div>
            <p className="text-lg text-slate-600 mb-2">{portfolio.description}</p>
            <p className="text-slate-600 leading-relaxed">
              Đây là đề xuất dựa trên hồ sơ rủi ro của bạn, cung cấp một cấu trúc tài chính rõ ràng, đa dạng hóa và phù hợp với mục tiêu đầu tư dài hạn.
            </p>
          </div>
        </div>

        {/* Fund Allocation Section */}
        <div className="mb-12">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-2xl font-bold text-slate-900">Tổng quan các Quỹ Đầu tư</h2>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-6">
            {portfolio.funds.map((fund: FundAllocation) => (
              <FundCard key={fund.id} fund={fund} />
            ))}
          </div>
        </div>

        {/* Summary Section */}
        <div className="bg-slate-50 rounded-lg p-8 mb-8">
          <h3 className="text-2xl font-bold text-slate-900 mb-6">Kế hoạch đầu tư của bạn</h3>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div className="bg-white p-6 rounded-lg border border-slate-200">
              <p className="text-sm text-slate-600 mb-2">Tổng số tiền đầu tư</p>
              <p className="text-3xl font-bold text-slate-900">{formatCurrency(portfolio.totalAmount)}</p>
            </div>
            <div className="bg-white p-6 rounded-lg border border-slate-200">
              <p className="text-sm text-slate-600 mb-2">Chiến lược đầu tư</p>
              <p className="text-2xl font-bold text-emerald-600">{portfolio.investmentStrategy}</p>
            </div>
            <div className="bg-white p-6 rounded-lg border border-slate-200">
              <p className="text-sm text-slate-600 mb-2">Số quỹ</p>
              <p className="text-3xl font-bold text-slate-900">{portfolio.funds.length}</p>
            </div>
          </div>
        </div>

        {isError && (
          <div className="mb-6 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
            Không thể hoàn tất đầu tư. Vui lòng thử lại.
          </div>
        )}

        {/* Action Buttons */}
        <div className="flex gap-4 justify-between">
          <button
            onClick={() => router.back()}
            className="px-6 py-3 border border-slate-300 text-slate-900 font-semibold rounded-lg hover:bg-slate-50 transition"
          >
            Quay lại
          </button>
          <button
            onClick={handleCompleteInvestment}
            disabled={isSubmitting}
            className="px-6 py-3 bg-emerald-600 hover:bg-emerald-700 text-white font-semibold rounded-lg transition disabled:opacity-50"
          >
            {isSubmitting ? 'Đang hoàn tất...' : 'Hoàn thành đầu tư →'}
          </button>
        </div>
      </div>
    </div>
  );
};

export default InvestmentRecommendationsPage;
