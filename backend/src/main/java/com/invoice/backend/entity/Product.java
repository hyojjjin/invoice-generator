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
@Table(name = "products")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "product_name")
    @NotBlank(message = "제품명은 필수입니다")
    private String productName;
    
    @Column(name = "product_option")
    private String productOption; // 색상, 사이즈, 모델 등
    
    @Column(name = "capacity")
    private String capacity; // 용량 (예: 500ml, 1GB, 10개입 등)
    
    @Column(name = "selling_price", precision = 12, scale = 2)
    @NotNull(message = "판매가는 필수입니다")
    @PositiveOrZero(message = "판매가는 0 이상이어야 합니다")
    private BigDecimal sellingPrice;
    
    @Column(name = "cost_price", precision = 12, scale = 2)
    @PositiveOrZero(message = "원가는 0 이상이어야 합니다")
    private BigDecimal costPrice; // 원가 (수익 계산용)
    
    @Column(name = "category")
    private String category; // 제품 카테고리
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description; // 제품 설명
    
    @Column(name = "sku")
    private String sku; // 제품 코드 (Stock Keeping Unit)
    
    @Column(name = "stock_quantity")
    @PositiveOrZero(message = "재고 수량은 0 이상이어야 합니다")
    private Integer stockQuantity = 0; // 재고 수량
    
    @Column(name = "is_active")
    @NotNull(message = "활성화 상태는 필수입니다")
    private Boolean isActive = true; // 판매 중인지 여부
    
    @Column(name = "is_service")
    @NotNull(message = "서비스 여부는 필수입니다")
    private Boolean isService = false; // 서비스 상품인지 물리적 상품인지
    
    // 제품의 인보이스 아이템 목록
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InvoiceDetail> invoiceDetails = new ArrayList<>();
    
    // 생성일, 수정일
    @Column(name = "created_at")
    private LocalDate createdAt;
    
    @Column(name = "updated_at")
    private LocalDate updatedAt;
    
    // Constructors
    public Product() {
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
    }
    
    public Product(String productName, BigDecimal sellingPrice) {
        this();
        this.productName = productName;
        this.sellingPrice = sellingPrice;
    }
    
    public Product(String productName, String productOption, String capacity, BigDecimal sellingPrice, Boolean isService) {
        this(productName, sellingPrice);
        this.productOption = productOption;
        this.capacity = capacity;
        this.isService = isService;
    }
    
    // JPA 생명주기 콜백
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDate.now();
    }
    
    // Helper methods
    public void addInvoiceDetail(InvoiceDetail invoiceDetail) {
        invoiceDetails.add(invoiceDetail);
        invoiceDetail.setProduct(this);
    }
    
    public void removeInvoiceDetail(InvoiceDetail invoiceDetail) {
        invoiceDetails.remove(invoiceDetail);
        invoiceDetail.setProduct(null);
    }
    
    // 수익 계산
    public BigDecimal calculateProfit() {
        if (costPrice != null && sellingPrice != null) {
            return sellingPrice.subtract(costPrice);
        }
        return BigDecimal.ZERO;
    }
    
    // 수익률 계산
    public BigDecimal calculateProfitMargin() {
        if (costPrice != null && sellingPrice != null && costPrice.compareTo(BigDecimal.ZERO) > 0) {
            return calculateProfit().divide(costPrice, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));
        }
        return BigDecimal.ZERO;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    public String getProductOption() { return productOption; }
    public void setProductOption(String productOption) { this.productOption = productOption; }
    
    public String getCapacity() { return capacity; }
    public void setCapacity(String capacity) { this.capacity = capacity; }
    
    public BigDecimal getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; }
    
    public BigDecimal getCostPrice() { return costPrice; }
    public void setCostPrice(BigDecimal costPrice) { this.costPrice = costPrice; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Boolean getIsService() { return isService; }
    public void setIsService(Boolean isService) { this.isService = isService; }
    
    public List<InvoiceDetail> getInvoiceDetails() { return invoiceDetails; }
    public void setInvoiceDetails(List<InvoiceDetail> invoiceDetails) { this.invoiceDetails = invoiceDetails; }
    
    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }
    
    public LocalDate getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDate updatedAt) { this.updatedAt = updatedAt; }
}