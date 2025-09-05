package com.invoice.backend.controller;

import com.invoice.backend.dto.CustomerDTO;
import com.invoice.backend.dto.InvoiceDTO;
import com.invoice.backend.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "http://localhost:3000")
public class CustomerController {
    
    @Autowired
    private CustomerService customerService;
    
    // 모든 구매자 조회
    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        List<CustomerDTO> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }
    
    // 구매 내역이 있는 구매자들만 조회
    @GetMapping("/with-purchase-history")
    public ResponseEntity<List<CustomerDTO>> getCustomersWithPurchaseHistory() {
        List<CustomerDTO> customers = customerService.getCustomersWithPurchaseHistory();
        return ResponseEntity.ok(customers);
    }
    
    // ID로 구매자 조회
    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long id) {
        Optional<CustomerDTO> customer = customerService.getCustomerById(id);
        return customer.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
    
    // 구매자명으로 검색
    @GetMapping("/search")
    public ResponseEntity<List<CustomerDTO>> searchCustomers(@RequestParam String name) {
        List<CustomerDTO> customers = customerService.searchByCustomerName(name);
        return ResponseEntity.ok(customers);
    }
    
    // 특정 구매자의 구매 내역 조회
    @GetMapping("/{id}/purchase-history")
    public ResponseEntity<List<InvoiceDTO>> getCustomerPurchaseHistory(@PathVariable Long id) {
        try {
            List<InvoiceDTO> invoices = customerService.getCustomerPurchaseHistory(id);
            return ResponseEntity.ok(invoices);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // 특정 구매자의 특정 기간 구매 내역 조회
    @GetMapping("/{id}/purchase-history/date-range")
    public ResponseEntity<List<InvoiceDTO>> getCustomerPurchaseHistoryByDateRange(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<InvoiceDTO> invoices = customerService.getCustomerPurchaseHistoryByDateRange(id, startDate, endDate);
            return ResponseEntity.ok(invoices);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // 특정 구매자의 총 구매 금액 조회
    @GetMapping("/{id}/total-purchase-amount")
    public ResponseEntity<Double> getCustomerTotalPurchaseAmount(@PathVariable Long id) {
        try {
            Double totalAmount = customerService.getCustomerTotalPurchaseAmount(id);
            return ResponseEntity.ok(totalAmount);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // 새 구매자 생성
    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(@Valid @RequestBody CustomerDTO customerDTO) {
        try {
            CustomerDTO createdCustomer = customerService.createCustomer(customerDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 구매자 정보 수정
    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable Long id, 
                                                     @Valid @RequestBody CustomerDTO customerDTO) {
        try {
            CustomerDTO updatedCustomer = customerService.updateCustomer(id, customerDTO);
            return ResponseEntity.ok(updatedCustomer);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // 구매자 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        try {
            customerService.deleteCustomer(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // 입금 완료 처리
    @PostMapping("/{id}/complete-payment")
    public ResponseEntity<CustomerDTO> markPaymentCompleted(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate paymentDate,
            @RequestParam String paymentMethod) {
        try {
            CustomerDTO updatedCustomer = customerService.markPaymentCompleted(id, paymentDate, paymentMethod);
            return ResponseEntity.ok(updatedCustomer);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // 입금 완료된 구매자들 조회
    @GetMapping("/payment-completed")
    public ResponseEntity<List<CustomerDTO>> getCustomersWithCompletedPayment() {
        List<CustomerDTO> customers = customerService.getCustomersWithCompletedPayment();
        return ResponseEntity.ok(customers);
    }
    
    // 입금 미완료된 구매자들 조회
    @GetMapping("/payment-pending")
    public ResponseEntity<List<CustomerDTO>> getCustomersWithPendingPayment() {
        List<CustomerDTO> customers = customerService.getCustomersWithPendingPayment();
        return ResponseEntity.ok(customers);
    }
    
    // 헬스체크
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Customer API is running!");
    }
}