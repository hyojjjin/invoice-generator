package com.invoice.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public class CustomerDTO {
    
    private Long id;
    
    @NotBlank(message = "구매자명은 필수입니다")
    private String customerName;
    
    private String phone;
    
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;
    
    private String address;
    
    private LocalDate paymentDate;
    
    private Boolean paymentCompleted = false;
    
    private String paymentMethod;
    
    private String notes;
    
    private LocalDate createdAt;
    
    private LocalDate updatedAt;
    
    // 추가 정보 (계산된 값들)
    private Double totalPurchaseAmount = 0.0; // 총 구매 금액
    
    private Integer purchaseCount = 0; // 구매 횟수
    
    // Constructors
    public CustomerDTO() {}
    
    public CustomerDTO(String customerName, String email) {
        this.customerName = customerName;
        this.email = email;
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
    
    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }
    
    public LocalDate getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDate updatedAt) { this.updatedAt = updatedAt; }
    
    public Double getTotalPurchaseAmount() { return totalPurchaseAmount; }
    public void setTotalPurchaseAmount(Double totalPurchaseAmount) { this.totalPurchaseAmount = totalPurchaseAmount; }
    
    public Integer getPurchaseCount() { return purchaseCount; }
    public void setPurchaseCount(Integer purchaseCount) { this.purchaseCount = purchaseCount; }
}