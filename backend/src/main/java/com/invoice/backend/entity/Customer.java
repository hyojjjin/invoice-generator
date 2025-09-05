package com.invoice.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
public class Customer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "customer_name")
    @NotBlank(message = "구매자명은 필수입니다")
    private String customerName;
    
    @Column(name = "phone")
    private String phone;
    
    @Column(name = "email")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;
    
    @Column(name = "address")
    private String address;
    
    @Column(name = "payment_date")
    private LocalDate paymentDate;
    
    @Column(name = "payment_completed")
    @NotNull(message = "입금 완료 여부는 필수입니다")
    private Boolean paymentCompleted = false;
    
    @Column(name = "payment_method")
    private String paymentMethod; // 계좌이체, 카드결제, 현금 등
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes; // 구매자 관련 메모
    
    // 구매자의 인보이스 목록 (일대다 관계)
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Invoice> invoices = new ArrayList<>();
    
    // 생성일, 수정일
    @Column(name = "created_at")
    private LocalDate createdAt;
    
    @Column(name = "updated_at")
    private LocalDate updatedAt;
    
    // Constructors
    public Customer() {
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
    }
    
    public Customer(String customerName, String email) {
        this();
        this.customerName = customerName;
        this.email = email;
    }
    
    // JPA 생명주기 콜백
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDate.now();
    }
    
    // Helper methods
    public void addInvoice(Invoice invoice) {
        invoices.add(invoice);
        invoice.setCustomer(this);
    }
    
    public void removeInvoice(Invoice invoice) {
        invoices.remove(invoice);
        invoice.setCustomer(null);
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }
    
    public Boolean getPaymentCompleted() { return paymentCompleted; }
    public void setPaymentCompleted(Boolean paymentCompleted) { this.paymentCompleted = paymentCompleted; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public List<Invoice> getInvoices() { return invoices; }
    public void setInvoices(List<Invoice> invoices) { this.invoices = invoices; }
    
    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }
    
    public LocalDate getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDate updatedAt) { this.updatedAt = updatedAt; }
}