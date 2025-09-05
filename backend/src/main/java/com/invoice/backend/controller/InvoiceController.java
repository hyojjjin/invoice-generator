package com.invoice.backend.controller;

import com.invoice.backend.dto.InvoiceDTO;
import com.invoice.backend.service.InvoiceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "http://localhost:3000")
public class InvoiceController {
    
    @Autowired
    private InvoiceService invoiceService;
    
    // 모든 인보이스 조회 (페이징 지원)
    @GetMapping
    public ResponseEntity<List<InvoiceDTO>> getAllInvoices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<InvoiceDTO> invoicesPage = invoiceService.getAllInvoices(pageable);
        return ResponseEntity.ok(invoicesPage.getContent());
    }
    
    // 전체 인보이스 조회 (페이징 없음)
    @GetMapping("/all")
    public ResponseEntity<List<InvoiceDTO>> getAllInvoicesWithoutPaging() {
        List<InvoiceDTO> invoices = invoiceService.getAllInvoices();
        return ResponseEntity.ok(invoices);
    }
    
    // ID로 인보이스 조회
    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDTO> getInvoiceById(@PathVariable Long id) {
        Optional<InvoiceDTO> invoice = invoiceService.getInvoiceById(id);
        return invoice.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }
    
    // 인보이스 번호로 조회
    @GetMapping("/number/{invoiceNumber}")
    public ResponseEntity<InvoiceDTO> getInvoiceByNumber(@PathVariable String invoiceNumber) {
        Optional<InvoiceDTO> invoice = invoiceService.getInvoiceByNumber(invoiceNumber);
        return invoice.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }
    
    // 새 인보이스 생성
    @PostMapping
    public ResponseEntity<InvoiceDTO> createInvoice(@Valid @RequestBody InvoiceDTO invoiceDTO) {
        try {
            InvoiceDTO createdInvoice = invoiceService.createInvoice(invoiceDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdInvoice);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 인보이스 수정
    @PutMapping("/{id}")
    public ResponseEntity<InvoiceDTO> updateInvoice(@PathVariable Long id, 
                                                   @Valid @RequestBody InvoiceDTO invoiceDTO) {
        try {
            InvoiceDTO updatedInvoice = invoiceService.updateInvoice(id, invoiceDTO);
            return ResponseEntity.ok(updatedInvoice);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // 인보이스 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        try {
            invoiceService.deleteInvoice(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // 회사명으로 검색
    @GetMapping("/search/company")
    public ResponseEntity<List<InvoiceDTO>> searchByCompanyName(@RequestParam String name) {
        List<InvoiceDTO> invoices = invoiceService.searchByCompanyName(name);
        return ResponseEntity.ok(invoices);
    }
    
    // 고객명으로 검색
    @GetMapping("/search/client")
    public ResponseEntity<List<InvoiceDTO>> searchByClientName(@RequestParam String name) {
        List<InvoiceDTO> invoices = invoiceService.searchByClientName(name);
        return ResponseEntity.ok(invoices);
    }
    
    // 날짜 범위로 검색
    @GetMapping("/search/date-range")
    public ResponseEntity<List<InvoiceDTO>> searchByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<InvoiceDTO> invoices = invoiceService.searchByDateRange(startDate, endDate);
        return ResponseEntity.ok(invoices);
    }
    
    // 최근 인보이스 조회
    @GetMapping("/recent")
    public ResponseEntity<List<InvoiceDTO>> getRecentInvoices() {
        List<InvoiceDTO> invoices = invoiceService.getRecentInvoices();
        return ResponseEntity.ok(invoices);
    }
    
    // 헬스체크 엔드포인트
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Invoice API is running!");
    }
}