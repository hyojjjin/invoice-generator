package com.invoice.backend.repository;

import com.invoice.backend.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    
    // 인보이스 번호로 검색
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    
    // 인보이스 번호 중복 확인
    boolean existsByInvoiceNumber(String invoiceNumber);
    
    // 회사명으로 검색
    List<Invoice> findByCompanyNameContainingIgnoreCase(String companyName);
    
    // 고객명으로 검색
    List<Invoice> findByClientNameContainingIgnoreCase(String clientName);
    
    // 날짜 범위로 검색
    List<Invoice> findByInvoiceDateBetween(LocalDate startDate, LocalDate endDate);
    
    // 만료일이 지난 인보이스 검색
    @Query("SELECT i FROM Invoice i WHERE i.dueDate < :currentDate")
    List<Invoice> findOverdueInvoices(@Param("currentDate") LocalDate currentDate);
    
    // 특정 월의 인보이스 검색
    @Query("SELECT i FROM Invoice i WHERE YEAR(i.invoiceDate) = :year AND MONTH(i.invoiceDate) = :month")
    List<Invoice> findByYearAndMonth(@Param("year") int year, @Param("month") int month);
    
    // 총액 기준으로 정렬하여 검색
    List<Invoice> findAllByOrderByTotalDesc();
    
    // 최근 생성된 인보이스들 (상위 N개)
    List<Invoice> findTop10ByOrderByIdDesc();
}