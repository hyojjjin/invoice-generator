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
    
    // 구매자 정보와의 관계 (다대일)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    // Company Information (발행 회사 정보)
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
    
    // Invoice Details (인보이스 내역들)
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceDetail> invoiceDetails = new ArrayList<>();
    
    // 생성일, 수정일
    @Column(name = "created_at")
    private LocalDate createdAt;
    
    @Column(name = "updated_at")
    private LocalDate updatedAt;
    
    // Constructors
    public Invoice() {
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
    }
    
    public Invoice(Customer customer, String companyName, String invoiceNumber, LocalDate invoiceDate) {
        this();
        this.customer = customer;
        this.companyName = companyName;
        this.invoiceNumber = invoiceNumber;
        this.invoiceDate = invoiceDate;
    }
    
    // JPA 생명주기 콜백
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDate.now();
    }
    
    // Helper methods
    public void addInvoiceDetail(InvoiceDetail invoiceDetail) {
        invoiceDetails.add(invoiceDetail);
        invoiceDetail.setInvoice(this);
        calculateTotals();
    }
    
    public void removeInvoiceDetail(InvoiceDetail invoiceDetail) {
        invoiceDetails.remove(invoiceDetail);
        invoiceDetail.setInvoice(null);
        calculateTotals();
    }
    
    public void calculateTotals() {
        this.subtotal = invoiceDetails.stream()
                .map(InvoiceDetail::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (this.taxRate != null) {
            this.taxAmount = this.subtotal.multiply(this.taxRate).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
        } else {
            this.taxAmount = BigDecimal.ZERO;
        }
        
        this.total = this.subtotal.add(this.taxAmount);
    }
    
    // 총 할인 금액 계산
    public BigDecimal getTotalDiscountAmount() {
        return invoiceDetails.stream()
                .map(InvoiceDetail::getDiscountAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    // 총 수익 계산
    public BigDecimal getTotalProfit() {
        return invoiceDetails.stream()
                .map(InvoiceDetail::calculateProfit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    
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
    
    public List<InvoiceDetail> getInvoiceDetails() { return invoiceDetails; }
    public void setInvoiceDetails(List<InvoiceDetail> invoiceDetails) { 
        this.invoiceDetails = invoiceDetails;
        calculateTotals();
    }
    
    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }
    
    public LocalDate getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDate updatedAt) { this.updatedAt = updatedAt; }
}