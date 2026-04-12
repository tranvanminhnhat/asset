'use client';
import React from 'react';
import { useRouter } from 'next/navigation';

const HeroBanner: React.FC = () => {
  const router = useRouter();

  return (
    <div className="bg-white rounded-lg shadow-md p-8 mb-12">
      <div className="flex flex-col md:flex-row items-center justify-between gap-8">
        <div className="flex-1">
          <h2 className="text-4xl font-bold text-emerald-950 mb-4">
            Nâng tầm đầu tư của bạn
          </h2>
          <p className="text-gray-600 text-l font-light leading-relaxed mb-6">
            Quản lý danh mục đầu tư một cách dễ dàng. Theo dõi hiệu suất, dự phòng và nâng cao trưởng tài sản trên một nền tảng sang trọng.
          </p>
          <button
            onClick={() => router.push('/investment-amount')}
            className="bg-blue-600 hover:bg-blue-700 text-white font-semibold py-3 px-6 rounded-lg transition"
          >
            Bắt đầu ngay
          </button>
        </div>
        <div className="flex-1">
          <div className="bg-linear-to-br from-slate-600 to-slate-800 rounded-lg p-8 h-64 flex items-center justify-center">
            <svg className="w-full h-full" viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg">
              {/* Bar chart illustration */}
              <defs>
                <linearGradient id="barGradient" x1="0%" y1="0%" x2="0%" y2="100%">
                  <stop offset="0%" style={{ stopColor: '#fbbf24', stopOpacity: 1 }} />
                  <stop offset="100%" style={{ stopColor: '#f59e0b', stopOpacity: 1 }} />
                </linearGradient>
              </defs>
              {/* Bars */}
              <rect x="30" y="120" width="25" height="60" fill="url(#barGradient)" />
              <rect x="65" y="90" width="25" height="90" fill="url(#barGradient)" />
              <rect x="100" y="60" width="25" height="120" fill="url(#barGradient)" />
              <rect x="135" y="30" width="25" height="150" fill="url(#barGradient)" />
              {/* Trend line */}
              <polyline points="42,120 77,90 112,60 147,30" fill="none" stroke="#fbbf24" strokeWidth="2" />
            </svg>
          </div>
        </div>
      </div>
    </div>
  );
};

export default HeroBanner;