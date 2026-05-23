import React from 'react';
import { formatCurrency, formatDateTime } from '@/lib/formatters';
import { TransactionResponse } from '../type/dashboard.type';
import Badge from '@/common/components/ui/Badge';
import { BadgeVariant } from '@/common/components/ui/Badge';

interface TransactionHistoryProps {
  transactions: TransactionResponse[];
}

const txTypeVariant: Record<string, BadgeVariant> = {
  BUY: 'success',
  SELL: 'error',
  DIVIDEND: 'info',
  ADJUSTMENT: 'warning',
};

const TransactionHistory: React.FC<TransactionHistoryProps> = ({ transactions }) => {
  return (
    <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
      <div className="px-6 py-5 border-b border-slate-100">
        <h2 className="text-base font-semibold text-slate-900">Lịch sử giao dịch</h2>
        <p className="text-xs text-slate-500 mt-0.5">Danh sách giao dịch mới nhất</p>
      </div>

      {transactions.length === 0 ? (
        <div className="px-6 py-12 text-center">
          <div className="mx-auto mb-3 w-12 h-12 rounded-xl bg-slate-100 flex items-center justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" className="w-6 h-6 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
            </svg>
          </div>
          <p className="text-sm font-medium text-slate-600">Chưa có giao dịch nào</p>
          <p className="text-xs text-slate-400 mt-0.5">Thêm giao dịch đầu tiên để theo dõi tài sản</p>
        </div>
      ) : (
        <div className="overflow-x-auto">
          <table className="min-w-full">
            <thead className="bg-slate-50 border-b border-slate-100">
              <tr>
                {['Ngày', 'Loại GD', 'Tài sản', 'Số lượng', 'Đơn giá', 'Tổng tiền'].map((h) => (
                  <th key={h} className="px-5 py-3 text-left text-[11px] font-semibold text-slate-500 uppercase tracking-wider whitespace-nowrap">
                    {h}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-50">
              {transactions.map((tx) => (
                <tr key={tx.transactionId} className="hover:bg-slate-50/50 transition-colors">
                  <td className="px-5 py-3.5 text-xs text-slate-500 whitespace-nowrap">
                    {formatDateTime(tx.transactionDate)}
                  </td>
                  <td className="px-5 py-3.5">
                    <Badge variant={txTypeVariant[tx.transactionType] ?? 'neutral'}>
                      {tx.transactionType}
                    </Badge>
                  </td>
                  <td className="px-5 py-3.5 text-xs font-medium text-slate-700">
                    {tx.assetType}
                  </td>
                  <td className="px-5 py-3.5 text-xs text-slate-700 font-mono">
                    {tx.quantity}
                  </td>
                  <td className="px-5 py-3.5 text-xs text-slate-700">
                    {formatCurrency(tx.unitPrice)}
                  </td>
                  <td className="px-5 py-3.5 text-xs font-semibold text-slate-900">
                    {formatCurrency(tx.totalAmount)}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

export default TransactionHistory;