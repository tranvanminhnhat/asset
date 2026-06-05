export const formatCurrency = (
  value: number,
  currency = 'VND',
  locale = 'vi-VN',
) => {
  const usesMinorUnits = currency !== 'VND';

  return new Intl.NumberFormat(locale, {
    style: 'currency',
    currency,
    minimumFractionDigits: usesMinorUnits ? 2 : 0,
    maximumFractionDigits: usesMinorUnits ? 2 : 0,
  }).format(value);
};

export const formatPercentage = (value: number) => `${value.toFixed(2)}%`;

export const formatDateTime = (value: string, locale = 'vi-VN') => {
  const date = new Date(value);

  if (Number.isNaN(date.getTime())) {
    return value;
  }

  return new Intl.DateTimeFormat(locale, {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(date);
};