export const formatCurrency = (
  value: number,
  _currency = 'VND',
  locale = 'vi-VN',
) => {
  return new Intl.NumberFormat(locale, {
    style: 'currency',
    currency: 'VND',
    minimumFractionDigits: 0,
    maximumFractionDigits: 0,
  }).format(value);
};

export const formatUnitPrice = (
  value: number,
  locale = 'vi-VN',
  fractionDigits = 6,
) => {
  return new Intl.NumberFormat(locale, {
    style: 'currency',
    currency: 'VND',
    minimumFractionDigits: fractionDigits,
    maximumFractionDigits: fractionDigits,
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