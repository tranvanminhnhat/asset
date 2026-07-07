'use client';

import React, { useEffect, useState } from 'react';
import Modal from '@/common/components/ui/Modal';
import Input from '@/common/components/ui/Input';
import Button from '@/common/components/ui/Button';
import { useI18n } from '@/common/i18n/I18nProvider';
import { ProfileSettingsResponse } from '@/features/profile/type/profile.type';

interface AutoInvestModalProps {
  open: boolean;
  profile: ProfileSettingsResponse | null;
  onClose: () => void;
  onSubmit: (amount: number) => Promise<void>;
}

const AutoInvestModal: React.FC<AutoInvestModalProps> = ({ open, profile, onClose, onSubmit }) => {
  const { t, pick, locale } = useI18n();
  const [amount, setAmount] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    if (!open) {
      return;
    }

    setAmount('');
    setError(null);
    setIsSubmitting(false);
  }, [open]);

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const parsedAmount = Number(amount.replace(/[^\d]/g, ''));

    if (!parsedAmount || Number.isNaN(parsedAmount)) {
      setError(pick({ vi: 'Vui long nhap so tien hop le.', en: 'Please enter a valid amount.', ja: '有効な金額を入力してください。' }));
      return;
    }

    try {
      setError(null);
      setIsSubmitting(true);
      await onSubmit(parsedAmount);
    } catch {
      setError(t('autoinvest.error'));
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Modal open={open} onClose={onClose} title={t('autoinvest.modalTitle')} maxWidth="max-w-lg">
      <form onSubmit={handleSubmit} className="px-6 py-5 space-y-5">
        <div className="rounded-2xl bg-emerald-50 p-4 border border-emerald-100">
          <p className="text-xs font-semibold uppercase tracking-wide text-emerald-700">{t('autoinvest.strategy')}</p>
          <p className="mt-1 text-sm font-semibold text-slate-900">
            {profile?.investmentStyle ?? 'BALANCED'}
            <span className="mx-2 text-slate-300">•</span>
            {profile?.autoInvestEnabled ? 'Auto' : 'Manual'}
          </p>
          <p className="mt-2 text-xs text-slate-600">{t('autoinvest.targetFlow')}</p>
        </div>

        <Input
          label={t('autoinvest.amount')}
          value={amount}
          onChange={(event) => setAmount(event.target.value)}
          placeholder={pick({ vi: 'Vi du: 10,000,000', en: 'Example: 10,000,000', ja: '例: 10,000,000' })}
          suffix={locale === 'ja' ? '¥' : '₫'}
          inputMode="numeric"
          error={error ?? undefined}
        />

        <div className="flex items-center justify-end gap-3 pt-2">
          <Button type="button" variant="outline" onClick={onClose} disabled={isSubmitting}>
            {pick({ vi: 'Dong', en: 'Close', ja: '閉じる' })}
          </Button>
          <Button type="submit" loading={isSubmitting}>
            {isSubmitting ? t('autoinvest.running') : t('autoinvest.submit')}
          </Button>
        </div>
      </form>
    </Modal>
  );
};

export default AutoInvestModal;
