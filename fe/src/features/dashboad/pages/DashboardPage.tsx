'use client';
import React from 'react';
import Link from 'next/link';
import { useDashboard } from '../hooks/useDashboard';
import HeroBanner from '../components/HeroBanner';
import PortfolioList from '../components/PortfolioList';
import SummaryBox from '../components/SummaryBox';
import TransactionForm from '../components/TransactionForm';
import TransactionHistory from '../components/TransactionHistory';

const DashboardPage: React.FC = () => {
  const {
    dashboardData,
    isLoading,
    error,
    transactionError,
    isSubmittingTransaction,
    selectedPortfolioId,
    setSelectedPortfolioId,
    transactions,
    submitTransaction,
  } = useDashboard();

  if (isLoading) {
    return (
      <div className="min-h-screen bg-[#eff8f1] px-4 py-10">
        <div className="max-w-6xl mx-auto">
          <div className="text-center">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-emerald-600 mx-auto"></div>
            <p className="mt-4 text-slate-600">Đang tải dữ liệu...</p>
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-[#eff8f1] px-4 py-10">
        <div className="max-w-6xl mx-auto">
          <div className="text-center">
            <p className="text-red-600">Có lỗi xảy ra khi tải dữ liệu dashboard.</p>
          </div>
        </div>
      </div>
    );
  }

  if (!dashboardData) {
    return (
      <div className="min-h-screen bg-[#eff8f1] px-4 py-10">
        <div className="max-w-6xl mx-auto text-center text-slate-600">
          Không có dữ liệu dashboard.
        </div>
      </div>
    );
  }

  const hasPortfolios = dashboardData.portfolios.length > 0;

  return (
    <div className="min-h-screen bg-[#eff8f1] px-4 py-10">
      <div className="max-w-6xl mx-auto">
        <HeroBanner />

        <PortfolioList portfolios={dashboardData.portfolios} />

        <SummaryBox summary={dashboardData.summary} />

        {transactionError && hasPortfolios && (
          <div className="mb-6 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
            Không thể lưu giao dịch. Vui lòng kiểm tra dữ liệu và thử lại.
          </div>
        )}

        {hasPortfolios ? (
          <div className="grid grid-cols-1 gap-6 lg:grid-cols-[1.1fr,1.4fr]">
            <TransactionForm
              isSubmitting={isSubmittingTransaction}
              portfolios={dashboardData.portfolios}
              selectedPortfolioId={selectedPortfolioId}
              onSelectPortfolio={setSelectedPortfolioId}
              onSubmit={submitTransaction}
            />
            <TransactionHistory transactions={transactions} />
          </div>
        ) : (
          <div className="rounded-2xl border border-slate-200 bg-white p-6 text-center shadow-sm">
            <h3 className="text-xl font-semibold text-slate-900">Chưa có dữ liệu giao dịch</h3>
            <p className="mt-2 text-slate-600">
              Khi bạn tạo danh mục đầu tư, khu vực thêm giao dịch và lịch sử giao dịch sẽ hiển thị tại đây.
            </p>
            <Link
              href="/portfolio-selection"
              className="mt-5 inline-flex rounded-lg bg-slate-900 px-5 py-3 font-semibold text-white transition hover:bg-slate-800"
            >
              Bắt đầu tạo danh mục
            </Link>
          </div>
        )}
      </div>
    </div>
  );
};

export default DashboardPage;