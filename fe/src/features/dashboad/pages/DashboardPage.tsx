'use client';
import React from 'react';
import { useDashboard } from '../hooks/useDashboard';
import HeroBanner from '../components/HeroBanner';
import DashboardSummary from '../components/DashboardSummary';
import PortfolioList from '../components/PortfolioList';
import SummaryBox from '../components/SummaryBox';

const DashboardPage: React.FC = () => {
  const { dashboardData, isLoading, error } = useDashboard();

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
        <div className="max-w-6xl mx-auto">
          <div className="text-center">
            <p className="text-slate-600">Không có dữ liệu dashboard.</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-[#eff8f1] px-4 py-10">
      <div className="max-w-6xl mx-auto">
        <HeroBanner />

        <PortfolioList portfolios={dashboardData.portfolios} />

        <SummaryBox summary={dashboardData.summary} />
      </div>
    </div>
  );
};

export default DashboardPage;