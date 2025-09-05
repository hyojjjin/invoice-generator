import React, { useState, useEffect } from 'react';
import './CustomerDetail.css';

const CustomerDetail = ({ customer, onBack }) => {
  const [purchaseHistory, setPurchaseHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedInvoice, setSelectedInvoice] = useState(null);
  const [dateFilter, setDateFilter] = useState({
    startDate: '',
    endDate: ''
  });

  // 구매 내역 조회
  const fetchPurchaseHistory = async () => {
    try {
      setLoading(true);
      let url = `http://localhost:8080/api/customers/${customer.id}/purchase-history`;
      
      // 날짜 필터가 있으면 날짜 범위 조회
      if (dateFilter.startDate && dateFilter.endDate) {
        url += `/date-range?startDate=${dateFilter.startDate}&endDate=${dateFilter.endDate}`;
      }

      const response = await fetch(url);
      if (!response.ok) {
        throw new Error('구매 내역을 가져오는데 실패했습니다.');
      }
      
      const data = await response.json();
      setPurchaseHistory(data);
      setError(null);
    } catch (err) {
      setError(err.message);
      console.error('구매 내역 조회 실패:', err);
    } finally {
      setLoading(false);
    }
  };

  // 컴포넌트 마운트 시 데이터 로드
  useEffect(() => {
    fetchPurchaseHistory();
  }, [customer.id, dateFilter]);

  // 날짜 필터 변경 핸들러
  const handleDateFilterChange = (field, value) => {
    setDateFilter(prev => ({
      ...prev,
      [field]: value
    }));
  };

  // 날짜 필터 초기화
  const clearDateFilter = () => {
    setDateFilter({
      startDate: '',
      endDate: ''
    });
  };

  // 인보이스 상세 보기
  const handleInvoiceSelect = (invoice) => {
    setSelectedInvoice(selectedInvoice?.id === invoice.id ? null : invoice);
  };

  // 금액 포맷팅
  const formatCurrency = (amount) => {
    if (!amount) return '₩0';
    return `₩${Math.round(amount).toLocaleString()}`;
  };

  // 날짜 포맷팅
  const formatDate = (dateString) => {
    if (!dateString) return '-';
    return new Date(dateString).toLocaleDateString('ko-KR');
  };

  // 총 구매 금액 계산
  const totalPurchaseAmount = purchaseHistory.reduce((sum, invoice) => sum + (invoice.total || 0), 0);

  return (
    <div className="customer-detail-container">
      {/* 헤더 */}
      <div className="customer-detail-header">
        <button onClick={onBack} className="back-btn">
          ← 목록으로 돌아가기
        </button>
        <h2>{customer.customerName} 구매 내역</h2>
      </div>

      {/* 구매자 정보 요약 */}
      <div className="customer-summary-card">
        <div className="summary-grid">
          <div className="summary-item">
            <label>구매자명</label>
            <span>{customer.customerName}</span>
          </div>
          <div className="summary-item">
            <label>이메일</label>
            <span>{customer.email || '-'}</span>
          </div>
          <div className="summary-item">
            <label>전화번호</label>
            <span>{customer.phone || '-'}</span>
          </div>
          <div className="summary-item">
            <label>입금 상태</label>
            <span className={`payment-status ${customer.paymentCompleted ? 'completed' : 'pending'}`}>
              {customer.paymentCompleted ? '입금완료' : '입금대기'}
            </span>
          </div>
          <div className="summary-item">
            <label>총 구매 횟수</label>
            <span>{purchaseHistory.length}회</span>
          </div>
          <div className="summary-item">
            <label>총 구매 금액</label>
            <span className="total-amount">{formatCurrency(totalPurchaseAmount)}</span>
          </div>
        </div>
      </div>

      {/* 날짜 필터 */}
      <div className="date-filter-container">
        <h3>기간별 조회</h3>
        <div className="date-filter">
          <input
            type="date"
            value={dateFilter.startDate}
            onChange={(e) => handleDateFilterChange('startDate', e.target.value)}
            className="date-input"
          />
          <span>~</span>
          <input
            type="date"
            value={dateFilter.endDate}
            onChange={(e) => handleDateFilterChange('endDate', e.target.value)}
            className="date-input"
          />
          <button onClick={clearDateFilter} className="clear-filter-btn">
            전체 기간
          </button>
        </div>
      </div>

      {/* 구매 내역 목록 */}
      <div className="purchase-history-section">
        <h3>구매 내역</h3>
        
        {loading && (
          <div className="loading">
            <div className="loading-spinner"></div>
            <p>구매 내역을 불러오는 중...</p>
          </div>
        )}

        {error && (
          <div className="error-message">
            <p>❌ {error}</p>
            <button onClick={fetchPurchaseHistory} className="retry-btn">다시 시도</button>
          </div>
        )}

        {!loading && !error && (
          <>
            {purchaseHistory.length === 0 ? (
              <div className="no-history">
                <p>구매 내역이 없습니다.</p>
                {(dateFilter.startDate || dateFilter.endDate) && (
                  <p>선택한 기간에 구매 내역이 없습니다.</p>
                )}
              </div>
            ) : (
              <div className="invoice-list">
                {purchaseHistory.map((invoice) => (
                  <div key={invoice.id} className="invoice-card">
                    <div 
                      className="invoice-header"
                      onClick={() => handleInvoiceSelect(invoice)}
                    >
                      <div className="invoice-basic-info">
                        <h4>인보이스 #{invoice.invoiceNumber}</h4>
                        <p className="invoice-date">발행일: {formatDate(invoice.invoiceDate)}</p>
                        {invoice.dueDate && (
                          <p className="due-date">만료일: {formatDate(invoice.dueDate)}</p>
                        )}
                      </div>
                      <div className="invoice-amount">
                        <span className="total-amount">{formatCurrency(invoice.total)}</span>
                        <span className="expand-icon">
                          {selectedInvoice?.id === invoice.id ? '▼' : '▶'}
                        </span>
                      </div>
                    </div>

                    {/* 인보이스 상세 정보 (펼침/접힘) */}
                    {selectedInvoice?.id === invoice.id && (
                      <div className="invoice-details">
                        <div className="invoice-summary">
                          <div className="summary-row">
                            <span>소계:</span>
                            <span>{formatCurrency(invoice.subtotal)}</span>
                          </div>
                          <div className="summary-row">
                            <span>세금 ({invoice.taxRate}%):</span>
                            <span>{formatCurrency(invoice.taxAmount)}</span>
                          </div>
                          <div className="summary-row total">
                            <span>총액:</span>
                            <span>{formatCurrency(invoice.total)}</span>
                          </div>
                        </div>

                        {/* 구매 상품 목록 */}
                        {invoice.items && invoice.items.length > 0 && (
                          <div className="invoice-items">
                            <h5>구매 상품</h5>
                            <table className="items-table">
                              <thead>
                                <tr>
                                  <th>상품명</th>
                                  <th>수량</th>
                                  <th>단가</th>
                                  <th>총액</th>
                                </tr>
                              </thead>
                              <tbody>
                                {invoice.items.map((item, index) => (
                                  <tr key={item.id || index}>
                                    <td>{item.description}</td>
                                    <td>{item.quantity}</td>
                                    <td>{formatCurrency(item.price)}</td>
                                    <td>{formatCurrency(item.total)}</td>
                                  </tr>
                                ))}
                              </tbody>
                            </table>
                          </div>
                        )}

                        {/* 메모 */}
                        {invoice.notes && (
                          <div className="invoice-notes">
                            <h5>메모</h5>
                            <p>{invoice.notes}</p>
                          </div>
                        )}
                      </div>
                    )}
                  </div>
                ))}
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
};

export default CustomerDetail;