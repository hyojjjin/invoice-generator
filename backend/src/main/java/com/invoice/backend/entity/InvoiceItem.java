package com.invoice.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@Entity
@Table(name = "invoice_items")
public class InvoiceItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "description")
    @NotBlank(message = "상품 설명은 필수입니다")
    private String description;
    
    @Column(name = "quantity")
    @NotNull(message = "수량은 필수입니다")
    @Positive(message = "수량은 0보다 커야 합니다")
    private Integer quantity;
    
    @Column(name = "price", precision = 12, scale = 2)
    @NotNull(message = "단가는 필수입니다")
    @PositiveOrZero(message = "단가는 0 이상이어야 합니다")
    private BigDecimal price;
    
    @Column(name = "total", precision = 12, scale = 2)
    @PositiveOrZero(message = "총액은 0 이상이어야 합니다")
    private BigDecimal total;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    @JsonIgnore
    private Invoice invoice;
    
    // Constructors
    public InvoiceItem() {}
    
    public InvoiceItem(String description, Integer quantity, BigDecimal price) {
        this.description = description;
        this.quantity = quantity;
        this.price = price;
        calculateTotal();
    }
    
    // Helper method
    public void calculateTotal() {
        if (this.quantity != null && this.price != null) {
            this.total = this.price.multiply(new BigDecimal(this.quantity));
        } else {
            this.total = BigDecimal.ZERO;
        }
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { 
        this.quantity = quantity;
        calculateTotal();
    }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { 
        this.price = price;
        calculateTotal();
    }
    
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    
    public Invoice getInvoice() { return invoice; }
    public void setInvoice(Invoice invoice) { this.invoice = invoice; }
}