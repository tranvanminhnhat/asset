import { Allocation } from '../../investment-amount/type/investment-amount.type';
import { FundAllocation } from '../type/investment-recommendations.type';

const toNumber = (value: number | string | null | undefined) => {
  if (typeof value === 'number' && Number.isFinite(value)) {
    return value;
  }

  if (typeof value === 'string') {
    const parsedValue = Number(value);
    return Number.isFinite(parsedValue) ? parsedValue : 0;
  }

  return 0;
};

const getFundName = (type: string) => {
  switch (type) {
    case 'EMERGENCY': return 'Quỹ Khẩn cấp';
    case 'GOLD': return 'Quỹ Vàng';
    case 'ETF': return 'Quỹ ETF';
    case 'STOCK': return 'Quỹ Cổ Phiếu';
    default: return type;
  }
};

const getFundDescription = (type: string) => {
  switch (type) {
    case 'EMERGENCY': return 'Đầu tư vào quỹ khẩn cấp với tỷ suất lợi nhuận ổn định.';
    case 'GOLD': return 'Quỹ đầu tư vào vàng với tỷ suất lợi nhuận 8% năm.';
    case 'ETF': return 'Quỹ ETF theo dõi chỉ số chính với lợi nhuận 13% năm.';
    case 'STOCK': return 'Đầu tư lựa chọn các cổ phiếu tốt nhất với lợi nhuận 20% năm.';
    default: return '';
  }
};

export const mapAllocationsToFunds = (allocations: Allocation[]): FundAllocation[] => {
  return allocations.map((alloc, index) => {
    const quantity = typeof alloc.quantity === 'number' && alloc.quantity > 0 ? alloc.quantity : 1;

    return {
      id: (index + 1).toString(),
      name: getFundName(alloc.type),
      type: alloc.type,
      unitAmount: toNumber(alloc.amount),
      carryOverAmount: toNumber(alloc.carryOverAmount),
      quantity,
      totalAmount: toNumber(alloc.amount) * quantity,
      currency: 'VND',
      percentage: toNumber(alloc.percent) * 100,
      description: getFundDescription(alloc.type),
    };
  });
};
