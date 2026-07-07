'use client';
import React, { useState } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import { formatCurrency, formatUnitPrice } from '@/lib/formatters';
import LogoutButton from '@/features/logout/components/LogoutButton';
import AssetOption from '../components/AssetOption';
import { useInvestmentProcessing } from '../hooks/useInvestmentProcessing';
import { AssetPrice } from '../type/investment-processing.type';
import { useI18n } from '@/common/i18n/I18nProvider';

interface InvestmentProcessingPageProps {
  sessionId: number;
  allocationId: number;
  assetType: string;
  recommendedAmount: number;
}

const InvestmentProcessingContent: React.FC<InvestmentProcessingPageProps> = ({
  sessionId,
  allocationId,
  assetType,
  recommendedAmount,
}) => {
  const router = useRouter();
  const { t, locale } = useI18n();
  const [selectedAsset, setSelectedAsset] = useState<AssetPrice | null>(null);
  const [quantity, setQuantity] = useState<string>('');
  const [submitError, setSubmitError] = useState<string | null>(null);
  const [step, setStep] = useState<'select' | 'confirm' | 'done'>('select');

  const {
    assets,
    isLoadingAssets,
    isAssetsError,
    selectResult,
    isSelecting,
    isSelectError,
    isCompleting,
    isCompleteError,
    submitSelect,
    submitComplete,
  } = useInvestmentProcessing({ sessionId, allocationId, assetType });

  const parsedQuantity = Number(quantity.replace(/[^\d.]/g, ''));
  const totalCost = selectedAsset ? selectedAsset.currentPrice * parsedQuantity : 0;
  const maxBuyableQuantity = selectedAsset && selectedAsset.currentPrice > 0
    ? Math.floor(recommendedAmount / selectedAsset.currentPrice)
    : 0;
  const isValidQuantity = parsedQuantity > 0 && totalCost <= recommendedAmount;
  const residual = recommendedAmount - totalCost;
  const assetTypeName = t(`assettype.${assetType.toLowerCase()}`, { fallback: assetType });

  const handleSelectAsset = async () => {
    if (!selectedAsset || !isValidQuantity) return;
    setSubmitError(null);

    try {
      await submitSelect({
        productId: selectedAsset.productId,
        assetType: selectedAsset.assetType ?? assetType,
        assetCode: selectedAsset.assetCode ?? `PRODUCT_${selectedAsset.productId}`,
        quantity: parsedQuantity,
      });
      setStep('confirm');
    } catch {
      setSubmitError(t('processing.selectError'));
    }
  };

  const handleComplete = async () => {
    setSubmitError(null);
    try {
      await submitComplete();
      setStep('done');
      // Navigate back to recommendations after short delay
      setTimeout(() => {
        router.push('/investment-recommendations');
      }, 1500);
    } catch {
      setSubmitError(t('processing.completeError'));
    }
  };

  if (isLoadingAssets) {
    return (
      <div className="min-h-screen bg-slate-50 flex items-center justify-center">
        <div className="text-center">
          <svg className="w-8 h-8 animate-spin text-emerald-500 mx-auto mb-3" fill="none" viewBox="0 0 24 24">
            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
          </svg>
          <p className="text-sm text-slate-500">{t('common.loading')}</p>
        </div>
      </div>
    );
  }

  if (step === 'done') {
    return (
      <div className="min-h-screen bg-slate-50 flex items-center justify-center px-4">
        <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-8 max-w-md w-full text-center">
          <div className="w-16 h-16 rounded-full bg-emerald-100 flex items-center justify-center mx-auto mb-4">
            <svg xmlns="http://www.w3.org/2000/svg" className="w-8 h-8 text-emerald-600" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.5}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" />
            </svg>
          </div>
          <h2 className="text-xl font-bold text-slate-900 mb-2">{t('processing.doneTitle')}</h2>
          <p className="text-sm text-slate-500">{t('processing.doneSubtitle')}</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-slate-50 px-4 py-10 animate-fade-up">
      <div className="max-w-2xl mx-auto">
        {/* Header */}
        <div className="flex items-center justify-between mb-8">
          <div>
            <h1 className="text-2xl font-bold text-slate-900 tracking-tight">
              {t('processing.pageTitle', { params: { typeName: assetTypeName } })}
            </h1>
            <p className="text-sm text-slate-500 mt-1">
              {t('processing.sessionInfo', { params: { sessionId, allocationId } })}
            </p>
          </div>
          <LogoutButton />
        </div>

        {/* Steps */}
        <div className="flex items-center gap-2 mb-8">
          {[t('processing.step.money'), t('processing.step.processing'), t('processing.step.review'), t('processing.step.complete')].map((s, i) => (
            <React.Fragment key={s}>
              <div className={`flex items-center gap-2 text-xs font-medium ${i === 1 ? 'text-emerald-700' : i === 0 ? 'text-emerald-700' : 'text-slate-400'}`}>
                <span className={`w-6 h-6 rounded-full flex items-center justify-center text-xs font-bold ${
                  i <= 1 ? 'bg-emerald-600 text-white' : 'bg-slate-200 text-slate-500'
                }`}>{i + 1}</span>
                <span className="hidden sm:inline">{s}</span>
              </div>
              {i < 3 && <div className="flex-1 h-px bg-slate-200" />}
            </React.Fragment>
          ))}
        </div>

        {/* Budget info */}
        <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-5 mb-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-xs text-slate-500">{t('processing.budget')}</p>
              <p className="text-xl font-bold text-slate-900 mt-0.5">{formatCurrency(recommendedAmount, 'VND', locale)}</p>
            </div>
            {selectedAsset && parsedQuantity > 0 && (
              <div className="text-right">
                <p className="text-xs text-slate-500">{t('processing.estimatedCost')}</p>
                <p className={`text-xl font-bold mt-0.5 ${isValidQuantity ? 'text-emerald-700' : 'text-red-600'}`}>
                  {formatCurrency(totalCost, 'VND', locale)}
                </p>
              </div>
            )}
          </div>
          {selectedAsset && parsedQuantity > 0 && (
            <div className="mt-3 pt-3 border-t border-slate-100">
              <div className="flex justify-between text-xs">
                <span className="text-slate-500">{t('processing.residual')}</span>
                <span className={`font-semibold ${residual >= 0 ? 'text-slate-700' : 'text-red-600'}`}>
                  {formatCurrency(Math.max(residual, 0), 'VND', locale)}
                </span>
              </div>
              {!isValidQuantity && parsedQuantity > 0 && (
                <p className="mt-2 text-xs text-red-600 font-medium">
                  ⚠ {t('processing.overBudget')}
                </p>
              )}
            </div>
          )}
        </div>

        {/* Step: Select */}
        {step === 'select' && (
          <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-6 space-y-6">
            {/* Asset list */}
            <div>
              <h2 className="text-base font-semibold text-slate-900 mb-1">{t('processing.assetTitle')}</h2>
              <p className="text-xs text-slate-500 mb-4">
                {t('processing.assetCount', { params: { count: assets.length } })}
              </p>

              {assets.length === 0 ? (
                <div className="rounded-xl border border-amber-200 bg-amber-50 px-4 py-3 text-sm text-amber-700">
                  {t('processing.noAsset', { params: { assetType: assetTypeName } })}
                </div>
              ) : (
                <div className="space-y-2.5 max-h-64 overflow-y-auto">
                  {assets.map((asset) => (
                    <AssetOption
                      key={asset.id !== 0 ? asset.id : asset.assetCode}
                      asset={asset}
                      isSelected={selectedAsset != null && (
                        asset.id !== 0
                          ? selectedAsset.id === asset.id
                          : selectedAsset.assetCode === asset.assetCode
                      )}
                      onClick={() => {
                        setSelectedAsset(asset);
                        setQuantity('');
                      }}
                    />
                  ))}
                </div>
              )}
            </div>

            <div className="border-t border-slate-100" />

            {/* Quantity input */}
            <div>
              <h2 className="text-base font-semibold text-slate-900 mb-1">{t('processing.quantityTitle')}</h2>
              <p className="text-xs text-slate-500 mb-4">
                {selectedAsset
                  ? t('processing.currentPrice', { params: { price: formatUnitPrice(selectedAsset.currentPrice, locale) } })
                  : t('processing.chooseAssetFirst')}
              </p>
              <div className="flex items-center gap-2">
                <input
                  type="number"
                  min="1"
                  step="1"
                  value={quantity}
                  onChange={(e) => setQuantity(e.target.value)}
                  disabled={!selectedAsset}
                  placeholder={t('processing.quantityPlaceholder')}
                  className="w-full border border-slate-300 rounded-xl px-4 py-3 text-base font-semibold text-slate-900 focus:outline-none focus:ring-2 focus:ring-emerald-500/30 focus:border-emerald-500 transition hover:border-slate-400 disabled:opacity-50 disabled:bg-slate-50"
                />
                <button
                  type="button"
                  onClick={() => setQuantity(String(maxBuyableQuantity))}
                  disabled={!selectedAsset || maxBuyableQuantity <= 0}
                  className="shrink-0 px-4 py-3 border border-emerald-200 bg-emerald-50 text-emerald-700 font-semibold text-sm rounded-xl hover:bg-emerald-100 transition disabled:opacity-40 disabled:cursor-not-allowed"
                >
                  {t('processing.maxButton')}
                </button>
              </div>
              {selectedAsset && maxBuyableQuantity > 0 && (
                <p className="mt-2 text-xs text-slate-500">
                  {t('processing.maxHint', { params: { max: maxBuyableQuantity } })}
                </p>
              )}
            </div>

            {(isSelectError || submitError) && (
              <div className="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
                {submitError ?? t('processing.selectError')}
              </div>
            )}

            <div className="flex items-center justify-between pt-2">
              <button
                type="button"
                onClick={() => router.back()}
                className="px-5 py-2.5 border border-slate-300 text-slate-700 font-semibold text-sm rounded-xl hover:bg-slate-50 transition"
              >
                {t('common.back')}
              </button>
              <button
                type="button"
                onClick={handleSelectAsset}
                disabled={!selectedAsset || !isValidQuantity || isSelecting}
                className="inline-flex items-center gap-2 px-6 py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white font-semibold text-sm rounded-xl transition disabled:opacity-40 disabled:cursor-not-allowed"
              >
                {isSelecting ? (
                  <>
                    <svg className="w-4 h-4 animate-spin" fill="none" viewBox="0 0 24 24">
                      <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                      <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
                    </svg>
                    {t('processing.submitting')}
                  </>
                ) : (
                  <>
                    {t('processing.submit')}
                    <svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                      <path strokeLinecap="round" strokeLinejoin="round" d="M9 5l7 7-7 7" />
                    </svg>
                  </>
                )}
              </button>
            </div>
          </div>
        )}

        {/* Step: Confirm */}
        {step === 'confirm' && selectResult && (
          <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-6 space-y-6">
            <div className="text-center mb-2">
              <div className="w-12 h-12 rounded-full bg-emerald-100 flex items-center justify-center mx-auto mb-3">
                <svg xmlns="http://www.w3.org/2000/svg" className="w-6 h-6 text-emerald-600" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                </svg>
              </div>
              <h2 className="text-base font-semibold text-slate-900">{t('processing.confirmTitle')}</h2>
              <p className="text-xs text-slate-500 mt-1">{t('processing.confirmSubtitle')}</p>
            </div>

            <div className="rounded-xl bg-slate-50 border border-slate-100 divide-y divide-slate-100">
              {[
                { label: t('processing.field.assetCode'), value: selectResult.assetCode },
                { label: t('processing.field.assetName'), value: selectResult.assetName },
                { label: t('processing.field.quantity'), value: String(selectResult.quantity) },
                { label: t('processing.field.currentPrice'), value: formatUnitPrice(selectResult.currentPrice, locale) },
                { label: t('processing.field.invested'), value: formatCurrency(selectResult.investedAmount, 'VND', locale) },
                { label: t('processing.field.residual'), value: formatCurrency(selectResult.residualAmount, 'VND', locale) },
              ].map(({ label, value }) => (
                <div key={label} className="flex justify-between px-4 py-3">
                  <span className="text-xs text-slate-500">{label}</span>
                  <span className="text-xs font-semibold text-slate-900">{value}</span>
                </div>
              ))}
            </div>

            {(isCompleteError || submitError) && (
              <div className="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
                {submitError ?? t('processing.completeError')}
              </div>
            )}

            <div className="flex items-center justify-between pt-2">
              <button
                type="button"
                onClick={() => setStep('select')}
                className="px-5 py-2.5 border border-slate-300 text-slate-700 font-semibold text-sm rounded-xl hover:bg-slate-50 transition"
              >
                {t('processing.edit')}
              </button>
              <button
                type="button"
                onClick={handleComplete}
                disabled={isCompleting}
                className="inline-flex items-center gap-2 px-6 py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white font-semibold text-sm rounded-xl transition disabled:opacity-40 disabled:cursor-not-allowed"
              >
                {isCompleting ? (
                  <>
                    <svg className="w-4 h-4 animate-spin" fill="none" viewBox="0 0 24 24">
                      <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                      <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
                    </svg>
                    {t('processing.completing')}
                  </>
                ) : (
                  <>
                    {t('processing.complete')}
                    <svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                      <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" />
                    </svg>
                  </>
                )}
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

const InvestmentProcessingPage: React.FC = () => {
  const router = useRouter();
  const searchParams = useSearchParams();
  const { t } = useI18n();

  const sessionIdRaw = searchParams.get('sessionId');
  const allocationIdRaw = searchParams.get('allocationId');
  const assetType = searchParams.get('type') ?? '';
  const recommendedAmountRaw = searchParams.get('recommendedAmount');

  const sessionId = sessionIdRaw ? Number(sessionIdRaw) : null;
  const allocationId = allocationIdRaw ? Number(allocationIdRaw) : null;
  // recommendedAmount might be passed as param or fall back to 0 (page will show budget from state)
  const recommendedAmount = recommendedAmountRaw ? Number(recommendedAmountRaw) : 0;

  if (!sessionId || !allocationId || !assetType) {
    return (
      <div className="min-h-screen bg-slate-50 flex items-center justify-center px-4">
        <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-8 max-w-md w-full text-center">
          <div className="w-14 h-14 rounded-2xl bg-slate-100 flex items-center justify-center mx-auto mb-4">
            <svg xmlns="http://www.w3.org/2000/svg" className="w-7 h-7 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
            </svg>
          </div>
          <h1 className="text-xl font-bold text-slate-900 mb-2">{t('processing.missingTitle')}</h1>
          <p className="text-sm text-slate-500 mb-6">{t('processing.missingSubtitle')}</p>
          <button
            onClick={() => router.push('/investment-recommendations')}
            className="inline-flex items-center gap-2 rounded-xl bg-emerald-600 px-6 py-2.5 text-sm font-semibold text-white transition hover:bg-emerald-700"
          >
            {t('processing.backToRecommendations')}
          </button>
        </div>
      </div>
    );
  }

  return (
    <InvestmentProcessingContent
      sessionId={sessionId}
      allocationId={allocationId}
      assetType={assetType}
      recommendedAmount={recommendedAmount}
    />
  );
};

export default InvestmentProcessingPage;
