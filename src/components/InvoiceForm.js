import React from 'react';

const InvoiceForm = ({ 
  invoiceData, 
  updateInvoiceData, 
  addItem, 
  updateItem, 
  removeItem, 
  updateTaxRate 
}) => {
  const handleInputChange = (section, field, value) => {
    updateInvoiceData({
      [section]: {
        ...invoiceData[section],
        [field]: value
      }
    });
  };

  const handleItemChange = (id, field, value) => {
    updateItem(id, field, value);
  };

  return (
    <div className="invoice-form">
      <h2>인보이스 정보 입력</h2>
      
      {/* 회사 정보 */}
      <div className="form-section">
        <h3>회사 정보</h3>
        <div className="form-row">
          <div className="form-group">
            <label>회사명</label>
            <input
              type="text"
              value={invoiceData.companyInfo.name}
              onChange={(e) => handleInputChange('companyInfo', 'name', e.target.value)}
              placeholder="회사명을 입력하세요"
            />
          </div>
          <div className="form-group">
            <label>전화번호</label>
            <input
              type="text"
              value={invoiceData.companyInfo.phone}
              onChange={(e) => handleInputChange('companyInfo', 'phone', e.target.value)}
              placeholder="전화번호를 입력하세요"
            />
          </div>
        </div>
        <div className="form-group">
          <label>주소</label>
          <input
            type="text"
            value={invoiceData.companyInfo.address}
            onChange={(e) => handleInputChange('companyInfo', 'address', e.target.value)}
            placeholder="회사 주소를 입력하세요"
          />
        </div>
        <div className="form-row">
          <div className="form-group">
            <label>이메일</label>
            <input
              type="email"
              value={invoiceData.companyInfo.email}
              onChange={(e) => handleInputChange('companyInfo', 'email', e.target.value)}
              placeholder="이메일을 입력하세요"
            />
          </div>
          <div className="form-group">
            <label>웹사이트</label>
            <input
              type="text"
              value={invoiceData.companyInfo.website}
              onChange={(e) => handleInputChange('companyInfo', 'website', e.target.value)}
              placeholder="웹사이트를 입력하세요"
            />
          </div>
        </div>
      </div>

      {/* 고객 정보 */}
      <div className="form-section">
        <h3>고객 정보</h3>
        <div className="form-row">
          <div className="form-group">
            <label>고객명</label>
            <input
              type="text"
              value={invoiceData.clientInfo.name}
              onChange={(e) => handleInputChange('clientInfo', 'name', e.target.value)}
              placeholder="고객명을 입력하세요"
            />
          </div>
          <div className="form-group">
            <label>전화번호</label>
            <input
              type="text"
              value={invoiceData.clientInfo.phone}
              onChange={(e) => handleInputChange('clientInfo', 'phone', e.target.value)}
              placeholder="전화번호를 입력하세요"
            />
          </div>
        </div>
        <div className="form-group">
          <label>주소</label>
          <input
            type="text"
            value={invoiceData.clientInfo.address}
            onChange={(e) => handleInputChange('clientInfo', 'address', e.target.value)}
            placeholder="고객 주소를 입력하세요"
          />
        </div>
        <div className="form-group">
          <label>이메일</label>
          <input
            type="email"
            value={invoiceData.clientInfo.email}
            onChange={(e) => handleInputChange('clientInfo', 'email', e.target.value)}
            placeholder="이메일을 입력하세요"
          />
        </div>
      </div>

      {/* 인보이스 세부사항 */}
      <div className="form-section">
        <h3>인보이스 세부사항</h3>
        <div className="form-row">
          <div className="form-group">
            <label>인보이스 번호</label>
            <input
              type="text"
              value={invoiceData.invoiceDetails.number}
              onChange={(e) => handleInputChange('invoiceDetails', 'number', e.target.value)}
              placeholder="인보이스 번호를 입력하세요"
            />
          </div>
          <div className="form-group">
            <label>발행일</label>
            <input
              type="date"
              value={invoiceData.invoiceDetails.date}
              onChange={(e) => handleInputChange('invoiceDetails', 'date', e.target.value)}
            />
          </div>
          <div className="form-group">
            <label>만료일</label>
            <input
              type="date"
              value={invoiceData.invoiceDetails.dueDate}
              onChange={(e) => handleInputChange('invoiceDetails', 'dueDate', e.target.value)}
            />
          </div>
        </div>
        <div className="form-group">
          <label>메모</label>
          <textarea
            value={invoiceData.invoiceDetails.notes}
            onChange={(e) => handleInputChange('invoiceDetails', 'notes', e.target.value)}
            placeholder="추가 메모를 입력하세요"
            rows="3"
          />
        </div>
      </div>

      {/* 상품/서비스 목록 */}
      <div className="form-section">
        <h3>상품/서비스 목록</h3>
        <div className="items-header">
          <div className="item-description">설명</div>
          <div className="item-quantity">수량</div>
          <div className="item-price">단가</div>
          <div className="item-total">총액</div>
          <div className="item-actions">작업</div>
        </div>
        
        {invoiceData.items.map((item, index) => (
          <div key={item.id} className="item-row">
            <div className="item-description">
              <input
                type="text"
                value={item.description}
                onChange={(e) => handleItemChange(item.id, 'description', e.target.value)}
                placeholder="상품/서비스 설명"
              />
            </div>
            <div className="item-quantity">
              <input
                type="number"
                min="0"
                step="0.01"
                value={item.quantity}
                onChange={(e) => handleItemChange(item.id, 'quantity', parseFloat(e.target.value) || 0)}
              />
            </div>
            <div className="item-price">
              <input
                type="number"
                min="0"
                step="0.01"
                value={item.price}
                onChange={(e) => handleItemChange(item.id, 'price', parseFloat(e.target.value) || 0)}
              />
            </div>
            <div className="item-total">
              ₩{item.total.toLocaleString()}
            </div>
            <div className="item-actions">
              <button 
                type="button" 
                onClick={() => removeItem(item.id)}
                className="remove-btn"
                disabled={invoiceData.items.length === 1}
              >
                삭제
              </button>
            </div>
          </div>
        ))}
        
        <button type="button" onClick={addItem} className="add-item-btn">
          + 상품 추가
        </button>
      </div>

      {/* 세금 설정 */}
      <div className="form-section">
        <h3>세금 설정</h3>
        <div className="form-group">
          <label>세율 (%)</label>
          <input
            type="number"
            min="0"
            max="100"
            step="0.01"
            value={invoiceData.taxRate}
            onChange={(e) => updateTaxRate(parseFloat(e.target.value) || 0)}
          />
        </div>
      </div>

      {/* 요약 */}
      <div className="form-section summary">
        <h3>요약</h3>
        <div className="summary-row">
          <span>소계:</span>
          <span>₩{invoiceData.subtotal.toLocaleString()}</span>
        </div>
        <div className="summary-row">
          <span>세금 ({invoiceData.taxRate}%):</span>
          <span>₩{invoiceData.taxAmount.toLocaleString()}</span>
        </div>
        <div className="summary-row total">
          <span>총액:</span>
          <span>₩{invoiceData.total.toLocaleString()}</span>
        </div>
      </div>
    </div>
  );
};

export default InvoiceForm;
