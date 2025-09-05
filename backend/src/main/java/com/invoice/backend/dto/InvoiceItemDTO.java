package com.invoice.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public class InvoiceItemDTO {
    
    private Long id;
    
    @NotBlank(message = "상품 설명은 필수입니다")
    private String description;
    
    @NotNull(message = "수량은 필수입니다")
    @Positive(message = "수량은 0보다 커야 합니다")
    private Integer quantity;
    
    @NotNull(message = "단가는 필수입니다")
    @PositiveOrZero(message = "단가는 0 이상이어야 합니다")
    private BigDecimal price;
    
    @PositiveOrZero(message = "총액은 0 이상이어야 합니다")
    private BigDecimal total;
    
    // Constructors
    public InvoiceItemDTO() {}
    
    public InvoiceItemDTO(String description, Integer quantity, BigDecimal price) {
        this.description = description;
        this.quantity = quantity;
        this.price = price;
        this.total = price.multiply(new BigDecimal(quantity));
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
}