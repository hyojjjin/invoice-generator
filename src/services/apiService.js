// API 서비스 - 백엔드와 통신하는 함수들
const API_BASE_URL = 'http://localhost:8080/api';

class ApiService {
  // 기본 fetch 설정
  async request(endpoint, options = {}) {
    const url = `${API_BASE_URL}${endpoint}`;
    const config = {
      headers: {
        'Content-Type': 'application/json',
        ...options.headers,
      },
      ...options,
    };

    try {
      const response = await fetch(url, config);
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      
      // 204 No Content 응답의 경우 JSON 파싱하지 않음
      if (response.status === 204) {
        return null;
      }
      
      return await response.json();
    } catch (error) {
      console.error('API request failed:', error);
      throw error;
    }
  }

  // 인보이스 관련 API
  async getAllInvoices() {
    return this.request('/invoices/all');
  }

  async getInvoiceById(id) {
    return this.request(`/invoices/${id}`);
  }

  async getInvoiceByNumber(invoiceNumber) {
    return this.request(`/invoices/number/${invoiceNumber}`);
  }

  async createInvoice(invoiceData) {
    // React 데이터 구조를 백엔드 API 형식으로 변환
    const apiData = this.transformToApiFormat(invoiceData);
    
    return this.request('/invoices', {
      method: 'POST',
      body: JSON.stringify(apiData),
    });
  }

  async updateInvoice(id, invoiceData) {
    const apiData = this.transformToApiFormat(invoiceData);
    
    return this.request(`/invoices/${id}`, {
      method: 'PUT',
      body: JSON.stringify(apiData),
    });
  }

  async deleteInvoice(id) {
    return this.request(`/invoices/${id}`, {
      method: 'DELETE',
    });
  }

  // 검색 API
  async searchByCompanyName(companyName) {
    return this.request(`/invoices/search/company?name=${encodeURIComponent(companyName)}`);
  }

  async searchByClientName(clientName) {
    return this.request(`/invoices/search/client?name=${encodeURIComponent(clientName)}`);
  }

  async searchByDateRange(startDate, endDate) {
    return this.request(`/invoices/search/date-range?startDate=${startDate}&endDate=${endDate}`);
  }

  async getRecentInvoices() {
    return this.request('/invoices/recent');
  }

  // 헬스체크
  async healthCheck() {
    return this.request('/invoices/health');
  }

  // React 데이터 구조를 백엔드 API 형식으로 변환
  transformToApiFormat(reactData) {
    return {
      // 회사 정보
      companyName: reactData.companyInfo?.name || '',
      companyAddress: reactData.companyInfo?.address || '',
      companyPhone: reactData.companyInfo?.phone || '',
      companyEmail: reactData.companyInfo?.email || '',
      companyWebsite: reactData.companyInfo?.website || '',
      
      // 고객 정보
      clientName: reactData.clientInfo?.name || '',
      clientAddress: reactData.clientInfo?.address || '',
      clientPhone: reactData.clientInfo?.phone || '',
      clientEmail: reactData.clientInfo?.email || '',
      
      // 인보이스 상세 정보
      invoiceNumber: reactData.invoiceDetails?.number || '',
      invoiceDate: reactData.invoiceDetails?.date || new Date().toISOString().split('T')[0],
      dueDate: reactData.invoiceDetails?.dueDate || '',
      notes: reactData.invoiceDetails?.notes || '',
      
      // 금액 정보
      taxRate: reactData.taxRate || 0,
      subtotal: reactData.subtotal || 0,
      taxAmount: reactData.taxAmount || 0,
      total: reactData.total || 0,
      
      // 아이템 목록
      items: reactData.items?.map(item => ({
        description: item.description || '',
        quantity: item.quantity || 1,
        price: item.price || 0,
        total: item.total || 0
      })) || []
    };
  }

  // 백엔드 API 응답을 React 데이터 구조로 변환
  transformFromApiFormat(apiData) {
    return {
      id: apiData.id,
      companyInfo: {
        name: apiData.companyName || '',
        address: apiData.companyAddress || '',
        phone: apiData.companyPhone || '',
        email: apiData.companyEmail || '',
        website: apiData.companyWebsite || ''
      },
      clientInfo: {
        name: apiData.clientName || '',
        address: apiData.clientAddress || '',
        phone: apiData.clientPhone || '',
        email: apiData.clientEmail || ''
      },
      invoiceDetails: {
        number: apiData.invoiceNumber || '',
        date: apiData.invoiceDate || '',
        dueDate: apiData.dueDate || '',
        notes: apiData.notes || ''
      },
      taxRate: apiData.taxRate || 0,
      subtotal: apiData.subtotal || 0,
      taxAmount: apiData.taxAmount || 0,
      total: apiData.total || 0,
      items: apiData.items?.map((item, index) => ({
        id: item.id || Date.now() + index,
        description: item.description || '',
        quantity: item.quantity || 1,
        price: item.price || 0,
        total: item.total || 0
      })) || []
    };
  }
}

// 싱글톤 인스턴스 생성
const apiService = new ApiService();

export default apiService;