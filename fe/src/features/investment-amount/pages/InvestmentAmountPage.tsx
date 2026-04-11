'use client';
import React, { useState, useRef } from 'react';
import { useRouter } from 'next/navigation';
import RiskOption from '../components/RiskOption';

const InvestmentAmountPage: React.FC = () => {
  const router = useRouter();
  const [riskLevel, setRiskLevel] = useState<'LOW' | 'MEDIUM' | 'HIGH'>('MEDIUM');
  const [monthlyAmount, setMonthlyAmount] = useState(500);
  const [investmentPeriod, setInvestmentPeriod] = useState(150);
  const containerRef = useRef<HTMLDivElement>(null);

  const getRiskReturns = (risk: string) => {
    switch (risk) {
      case 'LOW':
        return { color: 'text-green-600', return: 8.11 };
      case 'MEDIUM':
        return { color: 'text-blue-600', return: 13.68 };
      case 'HIGH':
        return { color: 'text-orange-600', return: 53.47 };
      default:
        return { color: 'text-slate-600', return: 0 };
    }
  };

  const calculateProjection = () => {
    const returns = getRiskReturns(riskLevel).return / 100;
    const totalInvested = monthlyAmount * investmentPeriod;
    const projectedValue = totalInvested * (1 + returns);
    return {
      totalInvested,
      projectedValue,
      profit: projectedValue - totalInvested,
    };
  };

  const projection = calculateProjection();

  const handleContinue = () => {
    const investmentData = {
      riskLevel,
      monthlyAmount,
      investmentPeriod,
      totalAmount: projection.totalInvested,
      projectedValue: projection.projectedValue,
    };
    localStorage.setItem('investmentAmount', JSON.stringify(investmentData));
    router.push('/investment-recommendations');
  };

  const scrollToResults = () => {
    const resultsSection = document.getElementById('results-section');
    if (resultsSection) {
      resultsSection.scrollIntoView({ behavior: 'smooth' });
    }
  };

  return (
    <div className="min-h-screen bg-white px-4 py-10">
      <div className="max-w-4xl mx-auto">
        {/* Hero Section */}
        <div className="mb-16 flex flex-col md:flex-row gap-8 items-center">
          <div className="flex-1">
            <h1 className="text-4xl font-bold text-slate-900 mb-4">
              Hành trình tích lũy tài sản của bạn bắt đầu từ đây
            </h1>
            <p className="text-slate-600 text-lg mb-6">
              Khám phá cơn gió và sông đé được mục tiêu tài chính của bạn. Công cụ của chúng tôi cung cấp các dự kiến với hiển thị quy hoạch.
            </p>
            <button
              onClick={scrollToResults}
              className="bg-emerald-600 hover:bg-emerald-700 text-white font-semibold py-3 px-8 rounded-lg transition flex items-center gap-2"
            >
              Bắt đầu lập kế hoạch ngay →
            </button>
          </div>
          <div className="flex-1 hidden md:block">
            <div className="w-full h-64 bg-gradient-to-br from-slate-300 to-slate-400 rounded-lg"></div>
          </div>
        </div>

        <div ref={containerRef} id="results-section" className="bg-slate-50 rounded-lg p-8 mb-8">
          {/* Risk Selection */}
          <div className="mb-10">
            <h2 className="text-2xl font-bold text-slate-900 mb-2">Xác định hộ số tài chính của bạn</h2>
            <p className="text-slate-600 mb-6">
              Hãy cho chúng tôi biết về chi phí và thích rủi ro của bạn để chúng tôi có thể đưa ra các đề xuất phù hợp.
            </p>
            <div className="space-y-3">
              <RiskOption
                label="An Toàn"
                description="Bảo toàn vốn, tích lũy chậm chạp đơn giản từ đầu tư kho."
                isSelected={riskLevel === 'LOW'}
                onClick={() => setRiskLevel('LOW')}
              />
              <RiskOption
                label="Cân Bằng"
                description="Tăng trưởng cân bằng giữa đầu tư tổ chức và hạt nhân dài hạn."
                isSelected={riskLevel === 'MEDIUM'}
                onClick={() => setRiskLevel('MEDIUM')}
              />
              <RiskOption
                label="Tăng Trưởng"
                description="Hướng tới lợi nhuận tối đa với rủi ro cao hơn trong đầu tư dài hạn."
                isSelected={riskLevel === 'HIGH'}
                onClick={() => setRiskLevel('HIGH')}
              />
            </div>
          </div>

          <hr className="my-8" />

          {/* Monthly Investment */}
          <div className="mb-8">
            <label className="block text-sm font-semibold text-slate-900 mb-3">
              💰 Thử nhập dạng gần dây (mục/tháng)
            </label>
            <div className="relative">
              <input
                type="number"
                value={monthlyAmount}
                onChange={(e) => setMonthlyAmount(Math.max(0, parseInt(e.target.value) || 0))}
                className="w-full px-4 py-3 border-2 border-slate-300 rounded-lg focus:outline-none focus:border-blue-500 text-lg font-semibold"
              />
              <span className="absolute right-4 top-1/2 transform -translate-y-1/2 text-slate-600">₫</span>
            </div>
          </div>

          {/* Investment Period */}
          <div className="mb-8">
            <label className="block text-sm font-semibold text-slate-900 mb-3">
              ⏱️ Giai đoạn lập kế hoạch (tháng)
            </label>
            <input
              type="number"
              value={investmentPeriod}
              onChange={(e) => setInvestmentPeriod(Math.max(0, parseInt(e.target.value) || 0))}
              className="w-full px-4 py-3 border-2 border-slate-300 rounded-lg focus:outline-none focus:border-blue-500 text-lg font-semibold"
            />
          </div>

          <hr className="my-8" />

          {/* Projection Results */}
          <div>
            <h3 className="text-xl font-bold text-slate-900 mb-6">Lộ trình tài chính có nhân hóa</h3>
            <p className="text-slate-600 mb-6">
              Đây là các chi chi được dự kiến từ trên thông tin của bạn. Bạn có thể thay đổi các chi chi để nhìn.
            </p>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-8">
              <div className="bg-white p-6 rounded-lg border border-slate-200">
                <p className="text-sm text-slate-600 mb-2">Tổng số tiền đầu tư</p>
                <p className="text-2xl font-bold text-slate-900">
                  {projection.totalInvested.toLocaleString('vi-VN')}₫
                </p>
              </div>
              <div className="bg-white p-6 rounded-lg border border-slate-200">
                <p className="text-sm text-slate-600 mb-2">Dự kiến giá trị</p>
                <p className="text-2xl font-bold text-emerald-600">
                  {projection.projectedValue.toLocaleString('vi-VN')}₫
                </p>
              </div>
              <div className="bg-white p-6 rounded-lg border border-slate-200">
                <p className="text-sm text-slate-600 mb-2">Lợi nhuận dự kiến</p>
                <p className={`text-2xl font-bold ${getRiskReturns(riskLevel).color}`}>
                  +{projection.profit.toLocaleString('vi-VN')}₫
                </p>
              </div>
            </div>
          </div>
        </div>

        <div className="flex gap-4 justify-between">
          <button
            onClick={() => router.back()}
            className="px-6 py-3 border border-slate-300 text-slate-900 font-semibold rounded-lg hover:bg-slate-50 transition"
          >
            Đặt lại
          </button>
          <button
            onClick={handleContinue}
            className="px-6 py-3 bg-emerald-600 hover:bg-emerald-700 text-white font-semibold rounded-lg transition"
          >
            Tính toán kế hoạch tài chính →
          </button>
        </div>
      </div>
    </div>
  );
};

export default InvestmentAmountPage;
