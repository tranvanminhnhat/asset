'use client';
import React, { useState } from 'react';
import { useRouter } from 'next/navigation';
import PortfolioSelector from '../components/PortfolioSelector';
import StockSelector from '../components/StockSelector';

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

const STOCK_OPTIONS = [
  {
    id: 'vic',
    code: 'VIC',
    name: 'Vingroup',
    currentPrice: 45000,
    change: 1500,
    changePercent: 3.45,
    volume: 500000,
    advantages: ['Công ty hàng đầu', 'Doanh thu ổn định'],
    risks: ['Rủi ro thị trường', 'Phụ thuộc điều kiện kinh tế'],
  },
  {
    id: 'vhm',
    code: 'VHM',
    name: 'Vinhomes',
    currentPrice: 55000,
    change: 2000,
    changePercent: 3.77,
    volume: 400000,
    advantages: ['Tăng trưởng mạnh', 'Dự án phát triển tốt'],
    risks: ['Rủi ro bất động sản', 'Phụ thuộc chính sách'],
  },
  {
    id: 'fpt',
    code: 'FPT',
    name: 'FPT Corporation',
    currentPrice: 120000,
    change: 5000,
    changePercent: 4.35,
    volume: 300000,
    advantages: ['Lĩnh vực công nghệ', 'Tăng trưởng bền vững'],
    risks: ['Cạnh tranh cao', 'Thị trường quốc tế'],
  },
];

const PortfolioSelectionPage: React.FC = () => {
  const router = useRouter();
  const [selectedPortfolio, setSelectedPortfolio] = useState<string | null>('medium');
  const [selectedStock, setSelectedStock] = useState<string | null>(null);

  const handleContinue = () => {
    if (selectedPortfolio && selectedStock) {
      // Save selection to state or local storage
      const selection = {
        portfolio: selectedPortfolio,
        stock: selectedStock,
      };
      localStorage.setItem('investmentSelection', JSON.stringify(selection));
      router.push('/investment-amount');
    }
  };

  const allocationAmount = 1000000; // Mock amount

  return (
    <div className="min-h-screen bg-[#eff8f1] px-4 py-10">
      <div className="max-w-4xl mx-auto">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-slate-900 mb-3">Phân Bổ Danh Mục Đầu Tư</h1>
          <p className="text-slate-600">
            Dựa trên thập của bạn, chúng tôi đề xuất phân bổ tài sản cho bạn
          </p>
        </div>

        <div className="bg-white rounded-lg shadow-md p-8 mb-8">
          {/* Portfolio Selection */}
          <div className="mb-10">
            <h2 className="text-xl font-semibold text-slate-900 mb-2">Chọn 1 Mã Chứng Khoán</h2>
            <p className="text-sm text-slate-600 mb-4">
              Lựa chọn thích hợp cho hồ sơ của bạn
            </p>
            <PortfolioSelector
              options={PORTFOLIO_OPTIONS}
              selectedId={selectedPortfolio}
              onSelect={setSelectedPortfolio}
            />
          </div>

          <hr className="my-8" />

          {/* Stock Selection */}
          <div>
            <h2 className="text-xl font-semibold text-slate-900 mb-2">Hoạch Tư Nhập 1 Mã Có Phiếu</h2>
            <p className="text-sm text-slate-600 mb-4">
              Mặc số cổ phiếu lựa chọn nhiều mục cần có chuyển đổi với vốn.
            </p>
            <StockSelector
              stocks={STOCK_OPTIONS}
              selectedId={selectedStock}
              onSelect={setSelectedStock}
              allocationAmount={allocationAmount}
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
            disabled={!selectedPortfolio || !selectedStock}
            className={`px-6 py-3 rounded-lg font-semibold transition ${
              selectedPortfolio && selectedStock
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
