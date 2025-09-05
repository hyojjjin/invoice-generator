import React, { useState, useEffect } from 'react';
import CustomerDetail from './CustomerDetail';
import './CustomerList.css';

const CustomerList = () => {
  const [customers, setCustomers] = useState([]);
  const [selectedCustomer, setSelectedCustomer] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterType, setFilterType] = useState('all'); // all, completed, pending

  // 구매자 목록 조회
  const fetchCustomers = async () => {
    try {
      setLoading(true);
      let url = 'http://localhost:8080/api/customers';
      
      // 필터 타입에 따라 URL 변경
      if (filterType === 'completed') {
        url += '/payment-completed';
      } else if (filterType === 'pending') {
        url += '/payment-pending';
      } else if (filterType === 'with-history') {
        url += '/with-purchase-history';
      }

      const response = await fetch(url);
      if (!response.ok) {
        throw new Error('구매자 목록을 가져오는데 실패했습니다.');
      }
      
      const data = await response.json();
      setCustomers(data);
      setError(null);
    } catch (err) {
      setError(err.message);
      console.error('구매자 목록 조회 실패:', err);
    } finally {
      setLoading(false);
    }
  };

  // 구매자 검색
  const searchCustomers = async () => {
    if (!searchTerm.trim()) {
      fetchCustomers();
      return;
    }

    try {
      setLoading(true);
      const response = await fetch(`http://localhost:8080/api/customers/search?name=${encodeURIComponent(searchTerm)}`);
      if (!response.ok) {
        throw new Error('검색에 실패했습니다.');
      }
      
      const data = await response.json();
      setCustomers(data);
      setError(null);
    } catch (err) {
      setError(err.message);
      console.error('구매자 검색 실패:', err);
    } finally {
      setLoading(false);
    }
  };

  // 컴포넌트 마운트 시 및 필터 변경 시 데이터 로드
  useEffect(() => {
    fetchCustomers();
  }, [filterType]);

  // 검색어 변경 시 검색 실행
  useEffect(() => {
    const timeoutId = setTimeout(() => {
      searchCustomers();
    }, 300); // 300ms 디바운스

    return () => clearTimeout(timeoutId);
  }, [searchTerm]);

  // 구매자 선택 핸들러
  const handleCustomerSelect = (customer) => {
    setSelectedCustomer(customer);
  };

  // 뒤로가기 핸들러
  const handleBackToList = () => {
    setSelectedCustomer(null);
  };

  // 입금 완료 처리
  const handleMarkPaymentCompleted = async (customerId) => {
    try {
      const paymentDate = new Date().toISOString().split('T')[0];
      const paymentMethod = prompt('결제 방법을 입력하세요 (예: 계좌이체, 카드결제):', '계좌이체');
      
      if (!paymentMethod) return;

      const response = await fetch(`http://localhost:8080/api/customers/${customerId}/complete-payment?paymentDate=${paymentDate}&paymentMethod=${encodeURIComponent(paymentMethod)}`, {
        method: 'POST'
      });

      if (!response.ok) {
        throw new Error('입금 완료 처리에 실패했습니다.');
      }

      alert('입금 완료 처리되었습니다.');
      fetchCustomers(); // 목록 새로고침
    } catch (err) {
      alert('입금 완료 처리 실패: ' + err.message);
    }
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

  if (selectedCustomer) {
    return (
      <CustomerDetail 
        customer={selectedCustomer} 
        onBack={handleBackToList}
      />
    );
  }

  return (
    <div className="customer-list-container">
      <div className="customer-list-header">
        <h2>구매자 관리</h2>
        <div className="customer-controls">
          {/* 검색 */}
          <div className="search-container">
            <input
              type="text"
              placeholder="구매자명으로 검색..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="search-input"
            />
          </div>

          {/* 필터 */}
          <div className="filter-container">
            <select 
              value={filterType} 
              onChange={(e) => setFilterType(e.target.value)}
              className="filter-select"
            >
              <option value="all">전체 구매자</option>
              <option value="with-history">구매 내역 있음</option>
              <option value="completed">입금 완료</option>
              <option value="pending">입금 대기</option>
            </select>
          </div>

          {/* 새로고침 */}
          <button onClick={fetchCustomers} className="refresh-btn">
            🔄 새로고침
          </button>
        </div>
      </div>

      {loading && (
        <div className="loading">
          <div className="loading-spinner"></div>
          <p>구매자 목록을 불러오는 중...</p>
        </div>
      )}

      {error && (
        <div className="error-message">
          <p>❌ {error}</p>
          <button onClick={fetchCustomers} className="retry-btn">다시 시도</button>
        </div>
      )}

      {!loading && !error && (
        <>
          <div className="customer-summary">
            <p>총 <strong>{customers.length}명</strong>의 구매자</p>
          </div>

          <div className="customer-grid">
            {customers.length === 0 ? (
              <div className="no-customers">
                <p>구매자가 없습니다.</p>
                {searchTerm && (
                  <p>검색어: "{searchTerm}"에 해당하는 구매자가 없습니다.</p>
                )}
              </div>
            ) : (
              customers.map((customer) => (
                <div key={customer.id} className="customer-card">
                  <div className="customer-info">
                    <div className="customer-header">
                      <h3 className="customer-name">{customer.customerName}</h3>
                      <div className={`payment-status ${customer.paymentCompleted ? 'completed' : 'pending'}`}>
                        {customer.paymentCompleted ? '입금완료' : '입금대기'}
                      </div>
                    </div>

                    <div className="customer-details">
                      <p><strong>이메일:</strong> {customer.email || '-'}</p>
                      <p><strong>전화번호:</strong> {customer.phone || '-'}</p>
                      <p><strong>총 구매금액:</strong> {formatCurrency(customer.totalPurchaseAmount)}</p>
                      <p><strong>구매횟수:</strong> {customer.purchaseCount}회</p>
                      {customer.paymentDate && (
                        <p><strong>입금일:</strong> {formatDate(customer.paymentDate)}</p>
                      )}
                    </div>
                  </div>

                  <div className="customer-actions">
                    <button 
                      onClick={() => handleCustomerSelect(customer)}
                      className="view-detail-btn"
                    >
                      구매 내역 보기
                    </button>
                    
                    {!customer.paymentCompleted && (
                      <button 
                        onClick={() => handleMarkPaymentCompleted(customer.id)}
                        className="complete-payment-btn"
                      >
                        입금 완료
                      </button>
                    )}
                  </div>
                </div>
              ))
            )}
          </div>
        </>
      )}
    </div>
  );
};

export default CustomerList;