package com.invoice.backend.repository;

import com.invoice.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // 제품명으로 검색
    List<Product> findByProductNameContainingIgnoreCase(String productName);
    
    // SKU로 제품 검색
    Optional<Product> findBySku(String sku);
    
    // 카테고리별 제품 검색
    List<Product> findByCategory(String category);
    
    // 활성화된 제품들만 조회
    List<Product> findByIsActiveTrue();
    
    // 서비스 상품만 조회
    List<Product> findByIsServiceTrue();
    
    // 물리적 상품만 조회
    List<Product> findByIsServiceFalse();
    
    // 가격 범위로 검색
    List<Product> findBySellingPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    // 재고가 부족한 제품들 (특정 수량 이하)
    List<Product> findByStockQuantityLessThan(Integer quantity);
    
    // 재고가 있는 제품들만
    List<Product> findByStockQuantityGreaterThan(Integer quantity);
    
    // 가격 순으로 정렬
    List<Product> findByIsActiveTrueOrderBySellingPriceAsc();
    List<Product> findByIsActiveTrueOrderBySellingPriceDesc();
    
    // 인기 상품 (판매량 기준)
    @Query("SELECT p, COUNT(id.id) as saleCount FROM Product p " +
           "LEFT JOIN p.invoiceDetails id " +
           "WHERE p.isActive = true " +
           "GROUP BY p " +
           "ORDER BY saleCount DESC")
    List<Object[]> findPopularProducts();
    
    // 특정 제품의 총 판매량
    @Query("SELECT SUM(id.quantity) FROM InvoiceDetail id WHERE id.product.id = :productId")
    Integer getTotalSalesQuantityByProductId(@Param("productId") Long productId);
    
    // 특정 제품의 총 매출액
    @Query("SELECT SUM(id.totalPrice) FROM InvoiceDetail id WHERE id.product.id = :productId")
    BigDecimal getTotalSalesAmountByProductId(@Param("productId") Long productId);
}