package com.invoice.backend.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public class CustomerDTO {
    
    private Long id;
    
    @NotBlank(message = "고객 고유코드는 필수입니다")
    private String customerCode;
    
    @NotBlank(message = "고객명은 필수입니다")
    private String customerName;
    
    private LocalDate createdAt;
    
    private LocalDate updatedAt;
    
    // 추가 정보 (계산된 값들)
    private Double totalPurchaseAmount = 0.0; // 총 구매 금액
    
    private Integer purchaseCount = 0; // 구매 횟수
    
    // Constructors
    public CustomerDTO() {}
    
    public CustomerDTO(String customerCode, String customerName) {
        this.customerCode = customerCode;
        this.customerName = customerName;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCustomerCode() { return customerCode; }
    public void setCustomerCode(String customerCode) { this.customerCode = customerCode; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }
    
    public LocalDate getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDate updatedAt) { this.updatedAt = updatedAt; }
    
    public Double getTotalPurchaseAmount() { return totalPurchaseAmount; }
    public void setTotalPurchaseAmount(Double totalPurchaseAmount) { this.totalPurchaseAmount = totalPurchaseAmount; }
    
    public Integer getPurchaseCount() { return purchaseCount; }
    public void setPurchaseCount(Integer purchaseCount) { this.purchaseCount = purchaseCount; }
}