'use client';
import React from 'react';
import LoginForm from '../components/LoginForm';

const LoginPage: React.FC = () => {
  return (
    <div className="min-h-screen bg-slate-50 flex">
      {/* Left branding panel */}
      <div className="hidden lg:flex lg:w-1/2 bg-slate-900 relative items-center justify-center p-12 overflow-hidden">
        {/* Grid pattern */}
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
            Quản lý tài chính<br />thông minh hơn
          </h2>
          <p className="text-slate-400 text-sm leading-relaxed">
            Theo dõi danh mục, phân tích hiệu suất và đưa ra quyết định đầu tư chính xác với nền tảng dữ liệu thực.
          </p>
          <div className="mt-8 grid grid-cols-2 gap-4">
            {[
              { label: 'Danh mục', value: '4 loại tài sản' },
              { label: 'Bảo mật', value: 'JWT + Middleware' },
              { label: 'Giao dịch', value: 'Real-time' },
              { label: 'Báo cáo', value: 'Chi tiết' },
            ].map((item) => (
              <div key={item.label} className="rounded-xl bg-white/5 border border-white/10 p-4">
                <p className="text-xs text-slate-400 mb-1">{item.label}</p>
                <p className="text-sm font-semibold text-white">{item.value}</p>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Right form panel */}
      <div className="flex-1 flex items-center justify-center px-6 py-12 animate-fade-up">
        <LoginForm />
      </div>
    </div>
  );
};

export default LoginPage;
