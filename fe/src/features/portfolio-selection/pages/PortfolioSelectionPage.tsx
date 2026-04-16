'use client';
import React, { useState } from 'react';
import { useRouter } from 'next/navigation';
import PortfolioSelector from '../components/PortfolioSelector';

// Mock data - in real app, this would come from API
const PORTFOLIO_OPTIONS = [
  {
    id: 'low',
    name: 'An Toàn',
    description: 'Đầu tư bảo thủ',
    riskLevel: 'LOW' as const,
    expectedReturn: 8.11,
  },
  {
    id: 'medium',
    name: 'Cân Bằng',
    description: 'Đầu tư cân bằng',
    riskLevel: 'MEDIUM' as const,
    expectedReturn: 13.68,
  },
  {
    id: 'high',
    name: 'Tăng Trưởng',
    description: 'Đầu tư tăng trưởng',
    riskLevel: 'HIGH' as const,
    expectedReturn: 53.47,
  },
];

const PortfolioSelectionPage: React.FC = () => {
  const router = useRouter();
  const [selectedPortfolio, setSelectedPortfolio] = useState<string | null>('medium');

  const handleContinue = () => {
    if (selectedPortfolio) {
      // Save selection to state or local storage
      const selection = {
        portfolio: selectedPortfolio,
      };
      localStorage.setItem('investmentSelection', JSON.stringify(selection));
      router.push('/investment-amount');
    }
  };

  return (
    <div className="min-h-screen bg-[#eff8f1] px-4 py-10">
      <div className="max-w-4xl mx-auto">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-slate-900 mb-3">Phân Bổ Danh Mục Đầu Tư</h1>
          <p className="text-slate-600">
            Dựa trên hồ sơ của bạn, chúng tôi đề xuất phân bổ tài sản phù hợp.
          </p>
        </div>

        <div className="bg-white rounded-lg shadow-md p-8 mb-8">
          <div className="mb-10">
            <h2 className="text-xl font-semibold text-slate-900 mb-2">Chọn danh mục đầu tư</h2>
            <p className="text-sm text-slate-600 mb-4">
              Lựa chọn phân bổ phù hợp với mục tiêu tài chính của bạn.
            </p>
            <PortfolioSelector
              options={PORTFOLIO_OPTIONS}
              selectedId={selectedPortfolio}
              onSelect={setSelectedPortfolio}
            />
          </div>
        </div>

        <div className="flex gap-4 justify-between">
          <button
            onClick={() => router.back()}
            className="px-6 py-3 border border-slate-300 text-slate-900 font-semibold rounded-lg hover:bg-slate-50 transition"
          >
            Quay lại
          </button>
          <button
            onClick={handleContinue}
            disabled={!selectedPortfolio}
            className={`px-6 py-3 rounded-lg font-semibold transition ${
              selectedPortfolio
                ? 'bg-blue-600 text-white hover:bg-blue-700'
                : 'bg-slate-300 text-slate-500 cursor-not-allowed'
            }`}
          >
            Tiếp tục
          </button>
        </div>
      </div>
    </div>
  );
};

export default PortfolioSelectionPage;
