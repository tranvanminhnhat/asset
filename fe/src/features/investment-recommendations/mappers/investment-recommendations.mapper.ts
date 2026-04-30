import { Allocation } from '../../investment-amount/type/investment-amount.type';
import { FundAllocation } from '../type/investment-recommendations.type';

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
  return allocations.map((alloc, index) => ({
    id: (index + 1).toString(),
    name: getFundName(alloc.type),
    type: alloc.type,
    unitAmount: alloc.amount,
    quantity: alloc.quantity > 0 ? alloc.quantity : 1,
    totalAmount: alloc.amount * (alloc.quantity > 0 ? alloc.quantity : 1),
    currency: 'VND',
    percentage: alloc.percent * 100,
    description: getFundDescription(alloc.type),
  }));
};
