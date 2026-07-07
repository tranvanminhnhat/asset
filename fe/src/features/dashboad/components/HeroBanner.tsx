'use client';
import React from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import LogoutButton from '@/features/logout/components/LogoutButton';
import { useI18n } from '@/common/i18n/I18nProvider';

const HeroBanner: React.FC = () => {
  const router = useRouter();
  const { pick } = useI18n();

  return (
    <div className="relative rounded-2xl overflow-hidden bg-slate-900 mb-8 shadow-xl">
      {/* Background pattern */}
      <div className="absolute inset-0 opacity-10">
        <svg className="w-full h-full" viewBox="0 0 400 200" preserveAspectRatio="xMidYMid slice">
          <defs>
            <pattern id="grid" width="40" height="40" patternUnits="userSpaceOnUse">
              <path d="M 40 0 L 0 0 0 40" fill="none" stroke="white" strokeWidth="0.5"/>
            </pattern>
          </defs>
          <rect width="100%" height="100%" fill="url(#grid)" />
        </svg>
      </div>

      <div className="relative px-8 py-8">
        <div className="flex justify-between items-start mb-6">
          <div className="flex items-center gap-2.5">
            <div className="w-8 h-8 rounded-lg bg-emerald-500 flex items-center justify-center">
              <svg className="w-4.5 h-4.5 text-white" fill="currentColor" viewBox="0 0 20 20">
                <path d="M2 10a8 8 0 1116 0A8 8 0 012 10zm6.293-4.707a1 1 0 011.414 0L14 9.586l-1.414 1.414L11 9.414V14a1 1 0 11-2 0V9.414l-1.586 1.586L5.999 9.586l2.294-4.293z" />
              </svg>
            </div>
            <span className="text-white font-bold text-base tracking-tight">MoneyPilot</span>
          </div>
          <LogoutButton />
        </div>

        <div className="flex flex-col md:flex-row items-start md:items-center justify-between gap-6">
          <div className="flex-1">
            <p className="text-emerald-400 text-xs font-semibold uppercase tracking-widest mb-2">
              {pick({ vi: 'Nen tang quan ly tai san', en: 'Asset management platform', ja: '資産管理プラットフォーム' })}
            </p>
            <h1 className="text-3xl font-bold text-white leading-tight tracking-tight mb-3">
              {pick({ vi: 'Nang tam danh muc dau tu', en: 'Elevate your portfolio', ja: 'ポートフォリオを強化' })}
              <br className="hidden md:block" /> {pick({ vi: 'cua ban', en: 'today', ja: 'しましょう' })}
            </h1>
            <p className="text-slate-400 text-sm leading-relaxed max-w-md">
              {pick({ vi: 'Theo doi hieu suat, toi uu phan bo va tang truong tai san tren mot nen tang chuyen nghiep.', en: 'Track performance, optimize allocations, and grow your assets on one professional platform.', ja: 'パフォーマンス追跡、配分最適化、資産成長を1つのプラットフォームで実現します。' })}
            </p>
            <div className="mt-5 flex items-center gap-3">
              <button
                onClick={() => router.push('/investment-amount')}
                className="inline-flex items-center gap-2 bg-emerald-500 hover:bg-emerald-400 text-white text-sm font-semibold px-5 py-2.5 rounded-xl transition-all duration-200 shadow-lg shadow-emerald-500/25 hover:shadow-emerald-500/40"
              >
                {pick({ vi: 'Dau tu ngay', en: 'Invest now', ja: '今すぐ投資' })}
                <svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M17 8l4 4m0 0l-4 4m4-4H3" />
                </svg>
              </button>
              <Link
                href="/investment-plans"
                className="inline-flex items-center gap-2 bg-white/10 hover:bg-white/20 text-white text-sm font-semibold px-5 py-2.5 rounded-xl transition-all duration-200 border border-white/20"
              >
                {pick({ vi: 'Ke hoach cua toi', en: 'My plans', ja: 'マイプラン' })}
              </Link>
            </div>
          </div>

          {/* Chart visual */}
          <div className="hidden md:flex items-end gap-1.5 h-24 shrink-0">
            {[40, 60, 45, 75, 55, 85, 70, 95].map((h, i) => (
              <div
                key={i}
                className="w-6 rounded-t-md bg-linear-to-t from-emerald-600 to-emerald-400 opacity-80"
                style={{ height: `${h}%` }}
              />
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default HeroBanner;