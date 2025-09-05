package com.invoice.backend.repository;

import com.invoice.backend.entity.InvoiceDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InvoiceDetailRepository extends JpaRepository<InvoiceDetail, Long> {
    
    // 특정 인보이스의 상세 내역들
    List<InvoiceDetail> findByInvoiceId(Long invoiceId);
    
    // 특정 제품의 판매 내역들
    List<InvoiceDetail> findByProductId(Long productId);
    
    // 특정 구매자의 모든 구매 상세 내역
    @Query("SELECT id FROM InvoiceDetail id WHERE id.invoice.customer.id = :customerId")
    List<InvoiceDetail> findByCustomerId(@Param("customerId") Long customerId);
    
    // 구매 타입별 조회
    List<InvoiceDetail> findByPurchaseType(InvoiceDetail.PurchaseType purchaseType);
    
    // 할인 타입별 조회
    List<InvoiceDetail> findByDiscountType(InvoiceDetail.DiscountType discountType);
    
    // 특정 기간의 판매 내역
    @Query("SELECT id FROM InvoiceDetail id WHERE id.invoice.invoiceDate BETWEEN :startDate AND :endDate")
    List<InvoiceDetail> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // 특정 구매자의 특정 기간 구매 내역
    @Query("SELECT id FROM InvoiceDetail id WHERE id.invoice.customer.id = :customerId " +
           "AND id.invoice.invoiceDate BETWEEN :startDate AND :endDate")
    List<InvoiceDetail> findByCustomerIdAndDateRange(@Param("customerId") Long customerId,
                                                     @Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);
    
    // 베스트셀러 제품들 (판매량 기준)
    @Query("SELECT id.product, SUM(id.quantity) as totalQuantity FROM InvoiceDetail id " +
           "GROUP BY id.product " +
           "ORDER BY totalQuantity DESC")
    List<Object[]> findBestSellingProducts();
    
    // 할인이 적용된 판매 내역들
    @Query("SELECT id FROM InvoiceDetail id WHERE id.discountType != 'NONE' AND id.discountValue > 0")
    List<InvoiceDetail> findDiscountedSales();
}