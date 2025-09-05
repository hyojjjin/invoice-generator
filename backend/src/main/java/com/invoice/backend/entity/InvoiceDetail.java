package com.invoice.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@Entity
@Table(name = "invoice_details")
public class InvoiceDetail {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 인보이스와의 관계 (다대일)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    @JsonIgnore
    private Invoice invoice;
    
    // 제품과의 관계 (다대일)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(name = "quantity")
    @NotNull(message = "수량은 필수입니다")
    @Positive(message = "수량은 0보다 커야 합니다")
    private Integer quantity;
    
    // 판매 당시의 제품 정보 (제품 정보가 변경되어도 인보이스는 그대로 유지)
    @Column(name = "product_name_at_sale")
    @NotNull(message = "판매 당시 제품명은 필수입니다")
    private String productNameAtSale;
    
    @Column(name = "product_option_at_sale")
    private String productOptionAtSale;
    
    @Column(name = "capacity_at_sale")
    private String capacityAtSale;
    
    @Column(name = "unit_price", precision = 12, scale = 2)
    @NotNull(message = "단가는 필수입니다")
    @PositiveOrZero(message = "단가는 0 이상이어야 합니다")
    private BigDecimal unitPrice; // 판매 당시의 단가
    
    // 구매 옵션 정보
    @Enumerated(EnumType.STRING)
    @Column(name = "purchase_type")
    @NotNull(message = "구매 타입은 필수입니다")
    private PurchaseType purchaseType = PurchaseType.GENERAL; // 일반구매 or 서비스제공
    
    // 할인 옵션
    @Column(name = "discount_type")
    @Enumerated(EnumType.STRING)
    private DiscountType discountType = DiscountType.NONE; // 할인 타입
    
    @Column(name = "discount_value", precision = 12, scale = 2)
    @PositiveOrZero(message = "할인값은 0 이상이어야 합니다")
    private BigDecimal discountValue = BigDecimal.ZERO; // 할인 금액 또는 할인율
    
    @Column(name = "discounted_price", precision = 12, scale = 2)
    @PositiveOrZero(message = "할인 적용가는 0 이상이어야 합니다")
    private BigDecimal discountedPrice; // 제품별 할인 적용가
    
    @Column(name = "total_price", precision = 12, scale = 2)
    @PositiveOrZero(message = "총액은 0 이상이어야 합니다")
    private BigDecimal totalPrice; // 최종 총액 (할인 적용 후)
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes; // 해당 제품 관련 메모
    
    // 구매 타입 열거형
    public enum PurchaseType {
        GENERAL("일반구매"),
        SERVICE("서비스제공");
        
        private final String description;
        
        PurchaseType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    // 할인 타입 열거형
    public enum DiscountType {
        NONE("할인없음"),
        FIXED_AMOUNT("정액할인"),
        PERCENTAGE("정률할인"),
        BULK_DISCOUNT("대량구매할인"),
        MEMBER_DISCOUNT("회원할인"),
        SEASONAL_DISCOUNT("시즌할인");
        
        private final String description;
        
        DiscountType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    // Constructors
    public InvoiceDetail() {}
    
    public InvoiceDetail(Product product, Integer quantity, PurchaseType purchaseType) {
        this.product = product;
        this.quantity = quantity;
        this.purchaseType = purchaseType;
        
        // 판매 당시 제품 정보 복사
        if (product != null) {
            this.productNameAtSale = product.getProductName();
            this.productOptionAtSale = product.getProductOption();
            this.capacityAtSale = product.getCapacity();
            this.unitPrice = product.getSellingPrice();
        }
        
        calculatePrices();
    }
    
    // Helper methods
    public void calculatePrices() {
        if (unitPrice == null || quantity == null) {
            this.discountedPrice = BigDecimal.ZERO;
            this.totalPrice = BigDecimal.ZERO;
            return;
        }
        
        // 기본 가격 계산
        BigDecimal basePrice = unitPrice.multiply(new BigDecimal(quantity));
        
        // 할인 적용
        if (discountType == DiscountType.FIXED_AMOUNT && discountValue != null) {
            // 정액 할인
            this.discountedPrice = unitPrice.subtract(discountValue.divide(new BigDecimal(quantity), 2, BigDecimal.ROUND_HALF_UP));
        } else if (discountType == DiscountType.PERCENTAGE && discountValue != null) {
            // 정률 할인
            BigDecimal discountAmount = unitPrice.multiply(discountValue).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
            this.discountedPrice = unitPrice.subtract(discountAmount);
        } else {
            // 할인 없음
            this.discountedPrice = unitPrice;
        }
        
        // 최종 총액 계산
        this.totalPrice = this.discountedPrice.multiply(new BigDecimal(quantity));
    }
    
    // 할인 금액 계산
    public BigDecimal getDiscountAmount() {
        if (unitPrice != null && discountedPrice != null && quantity != null) {
            return unitPrice.subtract(discountedPrice).multiply(new BigDecimal(quantity));
        }
        return BigDecimal.ZERO;
    }
    
    // 수익 계산 (원가 정보가 있는 경우)
    public BigDecimal calculateProfit() {
        if (product != null && product.getCostPrice() != null && quantity != null) {
            BigDecimal totalCost = product.getCostPrice().multiply(new BigDecimal(quantity));
            return totalPrice.subtract(totalCost);
        }
        return BigDecimal.ZERO;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Invoice getInvoice() { return invoice; }
    public void setInvoice(Invoice invoice) { this.invoice = invoice; }
    
    public Product getProduct() { return product; }
    public void setProduct(Product product) { 
        this.product = product;
        // 제품 정보 변경 시 판매 당시 정보도 업데이트
        if (product != null) {
            this.productNameAtSale = product.getProductName();
            this.productOptionAtSale = product.getProductOption();
            this.capacityAtSale = product.getCapacity();
            this.unitPrice = product.getSellingPrice();
        }
    }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { 
        this.quantity = quantity;
        calculatePrices();
    }
    
    public String getProductNameAtSale() { return productNameAtSale; }
    public void setProductNameAtSale(String productNameAtSale) { this.productNameAtSale = productNameAtSale; }
    
    public String getProductOptionAtSale() { return productOptionAtSale; }
    public void setProductOptionAtSale(String productOptionAtSale) { this.productOptionAtSale = productOptionAtSale; }
    
    public String getCapacityAtSale() { return capacityAtSale; }
    public void setCapacityAtSale(String capacityAtSale) { this.capacityAtSale = capacityAtSale; }
    
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { 
        this.unitPrice = unitPrice;
        calculatePrices();
    }
    
    public PurchaseType getPurchaseType() { return purchaseType; }
    public void setPurchaseType(PurchaseType purchaseType) { this.purchaseType = purchaseType; }
    
    public DiscountType getDiscountType() { return discountType; }
    public void setDiscountType(DiscountType discountType) { 
        this.discountType = discountType;
        calculatePrices();
    }
    
    public BigDecimal getDiscountValue() { return discountValue; }
    public void setDiscountValue(BigDecimal discountValue) { 
        this.discountValue = discountValue;
        calculatePrices();
    }
    
    public BigDecimal getDiscountedPrice() { return discountedPrice; }
    public void setDiscountedPrice(BigDecimal discountedPrice) { this.discountedPrice = discountedPrice; }
    
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}