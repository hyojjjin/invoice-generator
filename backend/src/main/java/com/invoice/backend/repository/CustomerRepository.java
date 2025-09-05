package com.invoice.backend.repository;

import com.invoice.backend.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    // 구매자명으로 검색
    List<Customer> findByCustomerNameContainingIgnoreCase(String customerName);
    
    // 이메일로 구매자 검색
    Optional<Customer> findByEmail(String email);
    
    // 전화번호로 구매자 검색
    Optional<Customer> findByPhone(String phone);
    
    // 입금 완료된 구매자들
    List<Customer> findByPaymentCompletedTrue();
    
    // 입금 미완료된 구매자들
    List<Customer> findByPaymentCompletedFalse();
    
    // 특정 기간에 입금한 구매자들
    List<Customer> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate);
    
    // 결제 방법별 구매자 검색
    List<Customer> findByPaymentMethod(String paymentMethod);
    
    // 최근 가입한 구매자들 (상위 N개)
    List<Customer> findTop10ByOrderByCreatedAtDesc();
    
    // 인보이스가 있는 구매자들만 조회
    @Query("SELECT DISTINCT c FROM Customer c WHERE SIZE(c.invoices) > 0")
    List<Customer> findCustomersWithInvoices();
    
    // 구매자별 총 구매 금액 조회
    @Query("SELECT c, SUM(i.total) as totalAmount FROM Customer c " +
           "LEFT JOIN c.invoices i " +
           "GROUP BY c " +
           "ORDER BY totalAmount DESC")
    List<Object[]> findCustomersWithTotalPurchaseAmount();
    
    // 특정 구매자의 총 구매 금액
    @Query("SELECT SUM(i.total) FROM Invoice i WHERE i.customer.id = :customerId")
    Double getTotalPurchaseAmountByCustomerId(@Param("customerId") Long customerId);
}