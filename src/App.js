import React, { useState, useEffect } from 'react';
import './App.css';
import InvoiceForm from './components/InvoiceForm';
import InvoicePreview from './components/InvoicePreview';
import apiService from './services/apiService';

function App() {
  const [invoiceData, setInvoiceData] = useState({
    companyInfo: {
      name: '',
      address: '',
      phone: '',
      email: '',
      website: ''
    },
    clientInfo: {
      name: '',
      address: '',
      phone: '',
      email: ''
    },
    invoiceDetails: {
      number: '',
      date: new Date().toISOString().split('T')[0],
      dueDate: '',
      notes: ''
    },
    items: [
      {
        id: 1,
        description: '',
        quantity: 1,
        price: 0,
        total: 0
      }
    ],
    taxRate: 10,
    subtotal: 0,
    taxAmount: 0,
    total: 0
  });

  // 컴포넌트 마운트 시 백엔드 연결 확인 및 데이터 로드
  useEffect(() => {
    const initializeApp = async () => {
      try {
        // 백엔드 연결 확인
        await apiService.healthCheck();
        console.log('✅ 백엔드 서버와 연결되었습니다.');
        
        // 최근 인보이스 데이터 로드 시도
        const recentInvoices = await apiService.getRecentInvoices();
        if (recentInvoices && recentInvoices.length > 0) {
          // 가장 최근 인보이스를 기본값으로 설정
          const latestInvoice = apiService.transformFromApiFormat(recentInvoices[0]);
          setInvoiceData(latestInvoice);
          console.log('📄 최근 인보이스 데이터를 로드했습니다.');
        }
      } catch (error) {
        console.warn('⚠️ 백엔드 서버에 연결할 수 없습니다. 로컬 스토리지를 사용합니다.');
        
        // 백엔드 연결 실패 시 로컬 스토리지에서 데이터 로드
        const savedData = localStorage.getItem('invoiceData');
        if (savedData) {
          setInvoiceData(JSON.parse(savedData));
          console.log('💾 로컬 스토리지에서 데이터를 로드했습니다.');
        }
      }
    };

    initializeApp();
  }, []);

  // 데이터가 변경될 때마다 로컬 스토리지에 백업 저장
  useEffect(() => {
    localStorage.setItem('invoiceData', JSON.stringify(invoiceData));
  }, [invoiceData]);

  const updateInvoiceData = (newData) => {
    setInvoiceData(prevData => ({
      ...prevData,
      ...newData
    }));
  };

  const calculateTotals = (items, taxRate) => {
    const subtotal = items.reduce((sum, item) => sum + (item.quantity * item.price), 0);
    const taxAmount = (subtotal * taxRate) / 100;
    const total = subtotal + taxAmount;
    
    return { subtotal, taxAmount, total };
  };

  const addItem = () => {
    const newItem = {
      id: Date.now(),
      description: '',
      quantity: 1,
      price: 0,
      total: 0
    };
    
    const updatedItems = [...invoiceData.items, newItem];
    const totals = calculateTotals(updatedItems, invoiceData.taxRate);
    
    updateInvoiceData({
      items: updatedItems,
      ...totals
    });
  };

  const updateItem = (id, field, value) => {
    const updatedItems = invoiceData.items.map(item => {
      if (item.id === id) {
        const updatedItem = { ...item, [field]: value };
        if (field === 'quantity' || field === 'price') {
          updatedItem.total = updatedItem.quantity * updatedItem.price;
        }
        return updatedItem;
      }
      return item;
    });
    
    const totals = calculateTotals(updatedItems, invoiceData.taxRate);
    
    updateInvoiceData({
      items: updatedItems,
      ...totals
    });
  };

  const removeItem = (id) => {
    const updatedItems = invoiceData.items.filter(item => item.id !== id);
    const totals = calculateTotals(updatedItems, invoiceData.taxRate);
    
    updateInvoiceData({
      items: updatedItems,
      ...totals
    });
  };

  const updateTaxRate = (rate) => {
    const totals = calculateTotals(invoiceData.items, rate);
    
    updateInvoiceData({
      taxRate: rate,
      ...totals
    });
  };

  // 인보이스 저장 함수
  const saveInvoice = async () => {
    try {
      // 인보이스 번호가 없으면 자동 생성
      if (!invoiceData.invoiceDetails.number) {
        const timestamp = Date.now();
        const invoiceNumber = `INV-${timestamp}`;
        updateInvoiceData({
          invoiceDetails: {
            ...invoiceData.invoiceDetails,
            number: invoiceNumber
          }
        });
      }

      const savedInvoice = await apiService.createInvoice(invoiceData);
      console.log('✅ 인보이스가 성공적으로 저장되었습니다:', savedInvoice);
      alert('인보이스가 성공적으로 저장되었습니다!');
      
      // 저장된 인보이스 데이터로 업데이트
      const transformedData = apiService.transformFromApiFormat(savedInvoice);
      setInvoiceData(transformedData);
      
    } catch (error) {
      console.error('❌ 인보이스 저장 실패:', error);
      alert('인보이스 저장에 실패했습니다. 백엔드 서버가 실행 중인지 확인해주세요.');
    }
  };

  // 새 인보이스 생성
  const createNewInvoice = () => {
    setInvoiceData({
      companyInfo: {
        name: '',
        address: '',
        phone: '',
        email: '',
        website: ''
      },
      clientInfo: {
        name: '',
        address: '',
        phone: '',
        email: ''
      },
      invoiceDetails: {
        number: '',
        date: new Date().toISOString().split('T')[0],
        dueDate: '',
        notes: ''
      },
      items: [
        {
          id: 1,
          description: '',
          quantity: 1,
          price: 0,
          total: 0
        }
      ],
      taxRate: 10,
      subtotal: 0,
      taxAmount: 0,
      total: 0
    });
  };

  return (
    <div className="App">
      <header className="App-header">
        <div className="header-content">
          <div className="header-text">
            <h1>인보이스 생성기</h1>
            <p>자동으로 인보이스를 생성하고 PDF로 다운로드하세요</p>
          </div>
          <div className="header-buttons">
            <button onClick={createNewInvoice} className="new-invoice-btn">
              새 인보이스
            </button>
            <button onClick={saveInvoice} className="save-invoice-btn">
              인보이스 저장
            </button>
          </div>
        </div>
      </header>
      
      <div className="main-content">
        <div className="form-section">
          <InvoiceForm
            invoiceData={invoiceData}
            updateInvoiceData={updateInvoiceData}
            addItem={addItem}
            updateItem={updateItem}
            removeItem={removeItem}
            updateTaxRate={updateTaxRate}
            saveInvoice={saveInvoice}
          />
        </div>
        
        <div className="preview-section">
          <InvoicePreview invoiceData={invoiceData} />
        </div>
      </div>
    </div>
  );
}

export default App;
