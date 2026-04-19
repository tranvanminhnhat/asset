import React from 'react';
import Link from 'next/link';
import { DashboardPortfolioItem } from '../type/dashboard.type';
import PortfolioCard from './PortfolioCard';

interface PortfolioListProps {
  portfolios: DashboardPortfolioItem[];
}

const PortfolioList: React.FC<PortfolioListProps> = ({ portfolios }) => {
  if (portfolios.length === 0) {
    return (
      <div className="mb-10 rounded-2xl border border-dashed border-emerald-300 bg-emerald-50/70 p-8 text-center">
        <h2 className="text-2xl font-semibold text-slate-900 mb-2">Danh mục đầu tư của tôi</h2>
        <p className="mx-auto max-w-2xl text-slate-600">
          Bạn chưa có danh mục nào. Hãy tạo danh mục đầu tiên để bắt đầu theo dõi tài sản,
          giao dịch và hiệu suất đầu tư.
        </p>
        <div className="mt-6 flex items-center justify-center gap-3">
          <Link
            href="/portfolio-selection"
            className="rounded-lg bg-emerald-600 px-5 py-3 font-semibold text-white transition hover:bg-emerald-700"
          >
            Tạo danh mục đầu tiên
          </Link>
          <Link
            href="/investment-recommendations"
            className="rounded-lg border border-emerald-300 bg-white px-5 py-3 font-semibold text-emerald-700 transition hover:bg-emerald-50"
          >
            Xem gợi ý đầu tư
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="mb-12">
      <h2 className="text-2xl font-semibold text-slate-900 mb-6">Danh mục đầu tư của tôi</h2>
      <div className="grid grid-cols-[repeat(auto-fit,minmax(320px,1fr))] gap-6">
        {portfolios.map((portfolio) => (
          <PortfolioCard key={portfolio.id} portfolio={portfolio} />
        ))}
      </div>
    </div>
  );
};

export default PortfolioList;