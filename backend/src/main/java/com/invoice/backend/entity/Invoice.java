package com.invoice.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices")
public class Invoice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Company Information
    @Column(name = "company_name")
    @NotBlank(message = "회사명은 필수입니다")
    private String companyName;
    
    @Column(name = "company_address")
    private String companyAddress;
    
    @Column(name = "company_phone")
    private String companyPhone;
    
    @Column(name = "company_email")
    private String companyEmail;
    
    @Column(name = "company_website")
    private String companyWebsite;
    
    // Client Information
    @Column(name = "client_name")
    @NotBlank(message = "고객명은 필수입니다")
    private String clientName;
    
    @Column(name = "client_address")
    private String clientAddress;
    
    @Column(name = "client_phone")
    private String clientPhone;
    
    @Column(name = "client_email")
    private String clientEmail;
    
    // Invoice Details
    @Column(name = "invoice_number", unique = true)
    @NotBlank(message = "인보이스 번호는 필수입니다")
    private String invoiceNumber;
    
    @Column(name = "invoice_date")
    @NotNull(message = "발행일은 필수입니다")
    private LocalDate invoiceDate;
    
    @Column(name = "due_date")
    private LocalDate dueDate;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    // Financial Information
    @Column(name = "tax_rate", precision = 5, scale = 2)
    @PositiveOrZero(message = "세율은 0 이상이어야 합니다")
    private BigDecimal taxRate;
    
    @Column(name = "subtotal", precision = 12, scale = 2)
    @PositiveOrZero(message = "소계는 0 이상이어야 합니다")
    private BigDecimal subtotal;
    
    @Column(name = "tax_amount", precision = 12, scale = 2)
    @PositiveOrZero(message = "세액은 0 이상이어야 합니다")
    private BigDecimal taxAmount;
    
    @Column(name = "total", precision = 12, scale = 2)
    @PositiveOrZero(message = "총액은 0 이상이어야 합니다")
    private BigDecimal total;
    
    // Invoice Items
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceItem> items = new ArrayList<>();
    
    // Constructors
    public Invoice() {}
    
    public Invoice(String companyName, String clientName, String invoiceNumber, LocalDate invoiceDate) {
        this.companyName = companyName;
        this.clientName = clientName;
        this.invoiceNumber = invoiceNumber;
        this.invoiceDate = invoiceDate;
    }
    
    // Helper methods
    public void addItem(InvoiceItem item) {
        items.add(item);
        item.setInvoice(this);
        calculateTotals();
    }
    
    public void removeItem(InvoiceItem item) {
        items.remove(item);
        item.setInvoice(null);
        calculateTotals();
    }
    
    public void calculateTotals() {
        this.subtotal = items.stream()
                .map(InvoiceItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (this.taxRate != null) {
            this.taxAmount = this.subtotal.multiply(this.taxRate).divide(new BigDecimal("100"));
        } else {
            this.taxAmount = BigDecimal.ZERO;
        }
        
        this.total = this.subtotal.add(this.taxAmount);
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    
    public String getCompanyAddress() { return companyAddress; }
    public void setCompanyAddress(String companyAddress) { this.companyAddress = companyAddress; }
    
    public String getCompanyPhone() { return companyPhone; }
    public void setCompanyPhone(String companyPhone) { this.companyPhone = companyPhone; }
    
    public String getCompanyEmail() { return companyEmail; }
    public void setCompanyEmail(String companyEmail) { this.companyEmail = companyEmail; }
    
    public String getCompanyWebsite() { return companyWebsite; }
    public void setCompanyWebsite(String companyWebsite) { this.companyWebsite = companyWebsite; }
    
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    
    public String getClientAddress() { return clientAddress; }
    public void setClientAddress(String clientAddress) { this.clientAddress = clientAddress; }
    
    public String getClientPhone() { return clientPhone; }
    public void setClientPhone(String clientPhone) { this.clientPhone = clientPhone; }
    
    public String getClientEmail() { return clientEmail; }
    public void setClientEmail(String clientEmail) { this.clientEmail = clientEmail; }
    
    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    
    public LocalDate getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(LocalDate invoiceDate) { this.invoiceDate = invoiceDate; }
    
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public BigDecimal getTaxRate() { return taxRate; }
    public void setTaxRate(BigDecimal taxRate) { 
        this.taxRate = taxRate;
        calculateTotals();
    }
    
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    
    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }
    
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    
    public List<InvoiceItem> getItems() { return items; }
    public void setItems(List<InvoiceItem> items) { 
        this.items = items;
        calculateTotals();
    }
}