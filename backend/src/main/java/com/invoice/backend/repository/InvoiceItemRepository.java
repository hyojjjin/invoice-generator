package com.invoice.backend.repository;

import com.invoice.backend.entity.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {
    
    // 특정 인보이스의 아이템들 조회
    List<InvoiceItem> findByInvoiceId(Long invoiceId);
    
    // 상품 설명으로 검색
    List<InvoiceItem> findByDescriptionContainingIgnoreCase(String description);
    
    // 특정 인보이스의 아이템 수 조회
    @Query("SELECT COUNT(i) FROM InvoiceItem i WHERE i.invoice.id = :invoiceId")
    Long countByInvoiceId(@Param("invoiceId") Long invoiceId);
}