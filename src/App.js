import React, { useState, useEffect } from 'react';
import './App.css';
import InvoiceForm from './components/InvoiceForm';
import InvoicePreview from './components/InvoicePreview';

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

  // 로컬 스토리지에서 데이터 로드
  useEffect(() => {
    const savedData = localStorage.getItem('invoiceData');
    if (savedData) {
      setInvoiceData(JSON.parse(savedData));
    }
  }, []);

  // 데이터가 변경될 때마다 로컬 스토리지에 저장
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

  return (
    <div className="App">
      <header className="App-header">
        <h1>인보이스 생성기</h1>
        <p>자동으로 인보이스를 생성하고 PDF로 다운로드하세요</p>
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
