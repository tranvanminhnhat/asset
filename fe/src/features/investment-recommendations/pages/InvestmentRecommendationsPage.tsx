'use client';
import React from 'react';
import { useRouter } from 'next/navigation';
import FundCard from '../components/FundCard';
import StockRecommendationCard from '../components/StockRecommendationCard';

// Mock data
const MOCK_PORTFOLIO = {
  id: '1',
  title: 'Đề xuất Đầu tư Toàn diện',
  description: 'Phân tích Chi tiết Danh mục Đầu tư',
  totalAmount: 75000000,
  currency: 'VND',
  funds: [
    {
      id: '1',
      name: 'Quỹ Khấn cấp',
      type: 'CRYPTO',
      amount: 7500000,
      currency: 'VND',
      percentage: 10,
      description: 'Đầu tư vào quỹ khấn cấp với tỷ suất lợi nhuận 10% năm.',
    },
    {
      id: '2',
      name: 'Quỹ Vàng',
      type: 'GOLD',
      amount: 6000000,
      currency: 'VND',
      percentage: 8,
      description: 'Quỹ đầu tư vào vàng với tỷ suất lợi nhuận 8% năm.',
    },
    {
      id: '3',
      name: 'Quỹ ETF',
      type: 'ETF',
      amount: 27750000,
      currency: 'VND',
      percentage: 37,
      description: 'Quỹ ETF theo dõi chỉ số chính với lợi nhuận 13% năm.',
    },
    {
      id: '4',
      name: 'Quỹ Cổ Phiếu',
      type: 'STOCK',
      amount: 30000000,
      currency: 'VND',
      percentage: 40,
      description: 'Đầu tư lựa chọn các cổ phiếu tốt nhất với lợi nhuận 20% năm.',
    },
  ],
  investmentStrategy: 'Cân bằng - Tăng trưởng',
};

const MOCK_STOCKS = [
  {
    id: '1',
    code: 'VIC',
    name: 'Vingroup',
    currentPrice: 45000,
    estimatedPrice: 55000,
    potentialReturn: 22.22,
    riskLevel: 'MEDIUM' as const,
    advantages: ['Công ty hàng đầu Việt Nam', 'Doanh thu ổn định', 'Mở rộng quốc tế'],
    risks: ['Rủi ro chính trị', 'Cạnh tranh thị trường', 'Phụ thuộc điều kiện kinh tế'],
    sector: 'Bán lẻ & Bất động sản',
  },
  {
    id: '2',
    code: 'VHM',
    name: 'Vinhomes',
    currentPrice: 55000,
    estimatedPrice: 68000,
    potentialReturn: 23.64,
    riskLevel: 'MEDIUM' as const,
    advantages: ['Tăng trưởng mạnh', 'Dự án phát triển tốt', 'Vị trí chiến lược'],
    risks: ['Rủi ro bất động sản', 'Thị trường bão hòa', 'Chính sách quy định'],
    sector: 'Bất động sản',
  },
  {
    id: '3',
    code: 'FPT',
    name: 'FPT Corporation',
    currentPrice: 120000,
    estimatedPrice: 150000,
    potentialReturn: 25.0,
    riskLevel: 'MEDIUM' as const,
    advantages: ['Lĩnh vực công nghệ tăng trưởng', 'Đa dạng hóa doanh thu', 'Tăng trưởng bền vững'],
    risks: ['Cạnh tranh cao công nghệ', 'Thị trường quốc tế', 'Chuyên môn nhân sự'],
    sector: 'Công nghệ & Thông tin',
  },
];

const InvestmentRecommendationsPage: React.FC = () => {
  const router = useRouter();

  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND',
      minimumFractionDigits: 0,
    }).format(value);
  };

  return (
    <div className="min-h-screen bg-white px-4 py-10">
      <div className="max-w-6xl mx-auto">
        {/* Header Section */}
        <div className="mb-12">
          <h1 className="text-4xl font-bold text-slate-900 mb-3">{MOCK_PORTFOLIO.title}</h1>
          <div>
            <p className="text-lg text-slate-600 mb-2">{MOCK_PORTFOLIO.description}</p>
            <p className="text-slate-600 leading-relaxed">
              Đây là đề xuất dựa trên hồ sơ rủi ro của bạn, được chứa đó là cung cấp cho bạn một cấu trúc tài chính rõ ràng, đa dạng hóa, và phù hợp với mục tiêu đầu tư dài hạn của bạn.
            </p>
          </div>
        </div>

        {/* Fund Allocation Section */}
        <div className="mb-12">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-2xl font-bold text-slate-900">Tổng quan các Quỹ Đầu tư</h2>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-6">
            {MOCK_PORTFOLIO.funds.map((fund) => (
              <FundCard key={fund.id} fund={fund} />
            ))}
          </div>
          <button className="w-full bg-emerald-600 hover:bg-emerald-700 text-white font-semibold py-3 px-6 rounded-lg transition">
            Chọn Mã Cổ Phiếu
          </button>
        </div>

        <hr className="my-12" />

        {/* Stock Recommendations Section */}
        <div className="mb-12">
          <h2 className="text-2xl font-bold text-slate-900 mb-3">Quản lý Quỹ Cổ Phiếu của bạn</h2>
          <p className="text-slate-600 mb-6">
            Quỹ cổ phiếu của bạn dòng giải các cổ phiếu lựa chọn sẽ sông doanh mục đầu tư. Việc lựa chọn các cổ phiếu phù hợp chính là yếu tố hàng đầu để giúp bạn đạt được mục tiêu tài chính của bạn.
          </p>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
            {MOCK_STOCKS.map((stock) => (
              <StockRecommendationCard key={stock.id} stock={stock} />
            ))}
          </div>
          <button className="flex items-center justify-center gap-2 w-full bg-emerald-600 hover:bg-emerald-700 text-white font-semibold py-3 px-6 rounded-lg transition">
            Chọn Mã Cổ Phiếu →
          </button>
        </div>

        {/* Summary Section */}
        <div className="bg-slate-50 rounded-lg p-8 mb-8">
          <h3 className="text-2xl font-bold text-slate-900 mb-6">Kế hoạch đầu tư của bạn</h3>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div className="bg-white p-6 rounded-lg border border-slate-200">
              <p className="text-sm text-slate-600 mb-2">Tổng số tiền đầu tư</p>
              <p className="text-3xl font-bold text-slate-900">{formatCurrency(MOCK_PORTFOLIO.totalAmount)}</p>
            </div>
            <div className="bg-white p-6 rounded-lg border border-slate-200">
              <p className="text-sm text-slate-600 mb-2">Chiến lược đầu tư</p>
              <p className="text-2xl font-bold text-emerald-600">{MOCK_PORTFOLIO.investmentStrategy}</p>
            </div>
            <div className="bg-white p-6 rounded-lg border border-slate-200">
              <p className="text-sm text-slate-600 mb-2">Số quỹ</p>
              <p className="text-3xl font-bold text-slate-900">{MOCK_PORTFOLIO.funds.length}</p>
            </div>
          </div>
        </div>

        {/* Action Buttons */}
        <div className="flex gap-4 justify-between">
          <button
            onClick={() => router.back()}
            className="px-6 py-3 border border-slate-300 text-slate-900 font-semibold rounded-lg hover:bg-slate-50 transition"
          >
            Quay lại
          </button>
          <button
            onClick={() => router.push('/dashboard')}
            className="px-6 py-3 bg-emerald-600 hover:bg-emerald-700 text-white font-semibold rounded-lg transition"
          >
            Hoàn thành đầu tư →
          </button>
        </div>
      </div>
    </div>
  );
};

export default InvestmentRecommendationsPage;
