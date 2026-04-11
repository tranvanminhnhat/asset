import React from 'react';
import { Portfolio } from '../type/dashboard.type';
import PortfolioCard from './PortfolioCard';

interface PortfolioListProps {
  portfolios: Portfolio[];
}

const PortfolioList: React.FC<PortfolioListProps> = ({ portfolios }) => {
  return (
    <div className="mb-12">
      <h2 className="text-2xl font-semibold text-slate-900 mb-6">Danh mục đầu tư của tôi</h2>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {portfolios.map((portfolio) => (
          <PortfolioCard key={portfolio.id} portfolio={portfolio} />
        ))}
      </div>
    </div>
  );
};

export default PortfolioList;