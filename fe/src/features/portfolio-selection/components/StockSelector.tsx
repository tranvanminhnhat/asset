import React, { useState } from 'react';
import { StockOption } from '../type/portfolio-selection.type';

interface StockSelectorProps {
  stocks: StockOption[];
  selectedId: string | null;
  onSelect: (stockId: string) => void;
  allocationAmount: number;
}

const StockSelector: React.FC<StockSelectorProps> = ({ stocks, selectedId, onSelect, allocationAmount }) => {
  const [searchTerm, setSearchTerm] = useState('');

  const filteredStocks = stocks.filter((stock) =>
    stock.code.toLowerCase().includes(searchTerm.toLowerCase()) ||
    stock.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND',
      minimumFractionDigits: 0,
    }).format(value);
  };

  return (
    <div className="space-y-4">
      <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
        <p className="text-sm text-slate-600">📌 Số tiền cho chứng khoán: <span className="font-semibold text-slate-900">{formatCurrency(allocationAmount)}</span></p>
        <p className="text-xs text-red-600 mt-2">* Bạn chỉ được chọn 1 mã chứng khoán để đủ hoặc nhập</p>
      </div>

      <div>
        <label className="block text-sm font-medium text-slate-900 mb-3">
          🔍 Hoạch Tư Nhập 1 Mã Có Phiếu
        </label>
        <input
          type="text"
          placeholder="VIC, VPR, FPT Nhập..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="w-full px-4 py-3 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
      </div>

      <div className="space-y-3">
        {filteredStocks.map((stock) => (
          <div
            key={stock.id}
            onClick={() => onSelect(stock.id)}
            className={`p-4 rounded-lg border-2 cursor-pointer transition ${
              selectedId === stock.id
                ? 'border-blue-500 bg-blue-50'
                : 'border-slate-200 hover:border-slate-300'
            }`}
          >
            <div className="flex items-start justify-between mb-3">
              <div>
                <h4 className="font-semibold text-slate-900">{stock.code}</h4>
                <p className="text-sm text-slate-600">{stock.name}</p>
              </div>
              <input
                type="radio"
                name="stock"
                value={stock.id}
                checked={selectedId === stock.id}
                onChange={(e) => onSelect(e.target.value)}
                className="w-4 h-4 accent-blue-600"
              />
            </div>

            <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-3 text-sm">
              <div>
                <p className="text-slate-600">Giá hiện tại</p>
                <p className="font-semibold text-slate-900">{formatCurrency(stock.currentPrice)}</p>
              </div>
              <div>
                <p className="text-slate-600">Thay đổi</p>
                <p className={`font-semibold ${stock.change >= 0 ? 'text-green-600' : 'text-red-600'}`}>
                  {stock.change >= 0 ? '+' : ''}{formatCurrency(stock.change)}
                </p>
              </div>
              <div>
                <p className="text-slate-600">%</p>
                <p className={`font-semibold ${stock.changePercent >= 0 ? 'text-green-600' : 'text-red-600'}`}>
                  {stock.changePercent >= 0 ? '+' : ''}{stock.changePercent.toFixed(2)}%
                </p>
              </div>
              <div>
                <p className="text-slate-600">Khối lượng</p>
                <p className="font-semibold text-slate-900">{stock.volume.toLocaleString()}</p>
              </div>
            </div>

            <div className="border-t pt-3 space-y-2 text-xs">
              <div>
                <p className="text-slate-600 mb-1">Ưu điểm:</p>
                <ul className="list-disc list-inside text-slate-600 space-y-1">
                  {stock.advantages.slice(0, 2).map((adv, idx) => (
                    <li key={idx}>{adv}</li>
                  ))}
                </ul>
              </div>
              <div>
                <p className="text-slate-600 mb-1">Rủi ro:</p>
                <ul className="list-disc list-inside text-slate-600 space-y-1">
                  {stock.risks.slice(0, 2).map((risk, idx) => (
                    <li key={idx}>{risk}</li>
                  ))}
                </ul>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default StockSelector;
