import React from 'react';
import { generatePDF } from '../utils/pdfGenerator';

const InvoicePreview = ({ invoiceData }) => {
  const handleDownloadPDF = () => {
    generatePDF(invoiceData);
  };

  return (
    <div className="invoice-preview">
      <div className="preview-header">
        <h2>인보이스 미리보기</h2>
        <button onClick={handleDownloadPDF} className="download-btn">
          PDF 다운로드
        </button>
      </div>
      
      <div className="invoice-document" id="invoice-document">
        {/* 회사 정보 */}
        <div className="invoice-header">
          <div className="company-info">
            <h1>{invoiceData.companyInfo.name || '회사명'}</h1>
            <p>{invoiceData.companyInfo.address}</p>
            <p>전화: {invoiceData.companyInfo.phone}</p>
            <p>이메일: {invoiceData.companyInfo.email}</p>
            {invoiceData.companyInfo.website && (
              <p>웹사이트: {invoiceData.companyInfo.website}</p>
            )}
          </div>
          
          <div className="invoice-title">
            <h2>INVOICE</h2>
            <div className="invoice-details">
              <p><strong>인보이스 번호:</strong> {invoiceData.invoiceDetails.number || 'INV-001'}</p>
              <p><strong>발행일:</strong> {invoiceData.invoiceDetails.date}</p>
              <p><strong>만료일:</strong> {invoiceData.invoiceDetails.dueDate}</p>
            </div>
          </div>
        </div>

        {/* 고객 정보 */}
        <div className="client-info">
          <h3>청구 대상</h3>
          <div className="client-details">
            <p><strong>{invoiceData.clientInfo.name || '고객명'}</strong></p>
            <p>{invoiceData.clientInfo.address}</p>
            <p>전화: {invoiceData.clientInfo.phone}</p>
            <p>이메일: {invoiceData.clientInfo.email}</p>
          </div>
        </div>

        {/* 상품/서비스 목록 */}
        <div className="items-section">
          <table className="items-table">
            <thead>
              <tr>
                <th>설명</th>
                <th>수량</th>
                <th>단가</th>
                <th>총액</th>
              </tr>
            </thead>
            <tbody>
              {invoiceData.items.map((item, index) => (
                <tr key={item.id}>
                  <td>{item.description || `상품 ${index + 1}`}</td>
                  <td>{item.quantity}</td>
                  <td>₩{item.price.toLocaleString()}</td>
                  <td>₩{item.total.toLocaleString()}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* 요약 */}
        <div className="invoice-summary">
          <div className="summary-table">
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

        {/* 메모 */}
        {invoiceData.invoiceDetails.notes && (
          <div className="invoice-notes">
            <h3>메모</h3>
            <p>{invoiceData.invoiceDetails.notes}</p>
          </div>
        )}

        {/* 푸터 */}
        <div className="invoice-footer">
          <p>감사합니다!</p>
          <p>문의사항이 있으시면 언제든지 연락주세요.</p>
        </div>
      </div>
    </div>
  );
};

export default InvoicePreview;
