'use client';
import React from 'react';
import LoginForm from '../components/LoginForm';
import { useI18n } from '@/common/i18n/I18nProvider';

const LoginPage: React.FC = () => {
  const { pick } = useI18n();

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
            {pick({ vi: 'Quan ly tai chinh', en: 'Manage finances', ja: '資産を管理' })}<br />
            {pick({ vi: 'thong minh hon', en: 'more intelligently', ja: 'よりスマートに' })}
          </h2>
          <p className="text-slate-400 text-sm leading-relaxed">
            {pick({
              vi: 'Theo doi danh muc, phan tich hieu suat va dua ra quyet dinh dau tu chinh xac voi nen tang du lieu thuc.',
              en: 'Track portfolios, analyze performance, and make better investment decisions with reliable data.',
              ja: 'ポートフォリオの追跡、パフォーマンス分析、信頼できるデータに基づく投資判断を支援します。',
            })}
          </p>
          <div className="mt-8 grid grid-cols-2 gap-4">
            {[
              {
                label: pick({ vi: 'Danh muc', en: 'Portfolios', ja: 'ポートフォリオ' }),
                value: pick({ vi: '4 loai tai san', en: '4 asset classes', ja: '4つの資産クラス' }),
              },
              {
                label: pick({ vi: 'Bao mat', en: 'Security', ja: 'セキュリティ' }),
                value: 'JWT + Middleware',
              },
              {
                label: pick({ vi: 'Giao dich', en: 'Transactions', ja: '取引' }),
                value: 'Real-time',
              },
              {
                label: pick({ vi: 'Bao cao', en: 'Reports', ja: 'レポート' }),
                value: pick({ vi: 'Chi tiet', en: 'Detailed', ja: '詳細' }),
              },
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
