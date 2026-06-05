'use client';
import React from 'react';
import RegisterForm from '../components/RegisterForm';

const RegisterPage: React.FC = () => {
  return (
    <div className="min-h-screen bg-slate-50 flex">
      {/* Left branding panel */}
      <div className="hidden lg:flex lg:w-1/2 bg-slate-900 relative items-center justify-center p-12 overflow-hidden">
        <div className="absolute inset-0 opacity-10"
          style={{ backgroundImage: 'linear-gradient(rgba(255,255,255,.1) 1px,transparent 1px),linear-gradient(90deg,rgba(255,255,255,.1) 1px,transparent 1px)', backgroundSize: '32px 32px' }} />
        <div className="relative z-10 max-w-sm">
          <div className="flex items-center gap-3 mb-8">
            <div className="w-10 h-10 rounded-xl bg-emerald-500 flex items-center justify-center shadow-lg shadow-emerald-500/40">
              <svg className="w-5 h-5 text-white" fill="currentColor" viewBox="0 0 20 20">
                <path d="M2 10a8 8 0 1116 0A8 8 0 012 10zm6.293-4.707a1 1 0 011.414 0L14 9.586l-1.414 1.414L11 9.414V14a1 1 0 11-2 0V9.414l-1.586 1.586L5.999 9.586l2.294-4.293z" />
              </svg>
            </div>
            <span className="text-white font-bold text-lg tracking-tight">MoneyPilot</span>
          </div>
          <h2 className="text-3xl font-bold text-white mb-4 leading-snug">
            Bắt đầu hành trình<br />đầu tư của bạn
          </h2>
          <p className="text-slate-400 text-sm leading-relaxed">
            Đăng ký miễn phí và trải nghiệm nền tảng quản lý danh mục đầu tư thông minh, đơn giản, hiệu quả.
          </p>
          <ul className="mt-8 space-y-3">
            {[
              'Quản lý nhiều loại tài sản',
              'Phân tích hiệu suất real-time',
              'Gợi ý danh mục từ AI',
              'Bảo mật tuyệt đối',
            ].map((item) => (
              <li key={item} className="flex items-center gap-3 text-sm text-slate-300">
                <span className="w-5 h-5 rounded-full bg-emerald-500/20 border border-emerald-500/40 flex items-center justify-center shrink-0">
                  <svg className="w-3 h-3 text-emerald-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={3}>
                    <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" />
                  </svg>
                </span>
                {item}
              </li>
            ))}
          </ul>
        </div>
      </div>

      {/* Right form panel */}
      <div className="flex-1 flex items-center justify-center px-6 py-12 animate-fade-up">
        <RegisterForm />
      </div>
    </div>
  );
};

export default RegisterPage;

