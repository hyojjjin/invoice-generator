package com.invoice.backend.service;

import com.invoice.backend.dto.InvoiceDTO;
import com.invoice.backend.dto.InvoiceItemDTO;
import com.invoice.backend.entity.Invoice;
import com.invoice.backend.entity.InvoiceItem;
import com.invoice.backend.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class InvoiceService {
    
    @Autowired
    private InvoiceRepository invoiceRepository;
    
    // 모든 인보이스 조회
    @Transactional(readOnly = true)
    public List<InvoiceDTO> getAllInvoices() {
        return invoiceRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // 페이징된 인보이스 조회
    @Transactional(readOnly = true)
    public Page<InvoiceDTO> getAllInvoices(Pageable pageable) {
        return invoiceRepository.findAll(pageable)
                .map(this::convertToDTO);
    }
    
    // ID로 인보이스 조회
    @Transactional(readOnly = true)
    public Optional<InvoiceDTO> getInvoiceById(Long id) {
        return invoiceRepository.findById(id)
                .map(this::convertToDTO);
    }
    
    // 인보이스 번호로 조회
    @Transactional(readOnly = true)
    public Optional<InvoiceDTO> getInvoiceByNumber(String invoiceNumber) {
        return invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .map(this::convertToDTO);
    }
    
    // 인보이스 생성
    public InvoiceDTO createInvoice(InvoiceDTO invoiceDTO) {
        // 인보이스 번호 중복 확인
        if (invoiceRepository.existsByInvoiceNumber(invoiceDTO.getInvoiceNumber())) {
            throw new RuntimeException("이미 존재하는 인보이스 번호입니다: " + invoiceDTO.getInvoiceNumber());
        }
        
        Invoice invoice = convertToEntity(invoiceDTO);
        Invoice savedInvoice = invoiceRepository.save(invoice);
        return convertToDTO(savedInvoice);
    }
    
    // 인보이스 수정
    public InvoiceDTO updateInvoice(Long id, InvoiceDTO invoiceDTO) {
        Invoice existingInvoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("인보이스를 찾을 수 없습니다. ID: " + id));
        
        // 인보이스 번호가 변경되었고 중복되는 경우 확인
        if (!existingInvoice.getInvoiceNumber().equals(invoiceDTO.getInvoiceNumber()) &&
            invoiceRepository.existsByInvoiceNumber(invoiceDTO.getInvoiceNumber())) {
            throw new RuntimeException("이미 존재하는 인보이스 번호입니다: " + invoiceDTO.getInvoiceNumber());
        }
        
        updateInvoiceFromDTO(existingInvoice, invoiceDTO);
        Invoice savedInvoice = invoiceRepository.save(existingInvoice);
        return convertToDTO(savedInvoice);
    }
    
    // 인보이스 삭제
    public void deleteInvoice(Long id) {
        if (!invoiceRepository.existsById(id)) {
            throw new RuntimeException("인보이스를 찾을 수 없습니다. ID: " + id);
        }
        invoiceRepository.deleteById(id);
    }
    
    // 회사명으로 검색
    @Transactional(readOnly = true)
    public List<InvoiceDTO> searchByCompanyName(String companyName) {
        return invoiceRepository.findByCompanyNameContainingIgnoreCase(companyName)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // 고객명으로 검색
    @Transactional(readOnly = true)
    public List<InvoiceDTO> searchByClientName(String clientName) {
        return invoiceRepository.findByClientNameContainingIgnoreCase(clientName)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // 날짜 범위로 검색
    @Transactional(readOnly = true)
    public List<InvoiceDTO> searchByDateRange(LocalDate startDate, LocalDate endDate) {
        return invoiceRepository.findByInvoiceDateBetween(startDate, endDate)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // 최근 인보이스 조회
    @Transactional(readOnly = true)
    public List<InvoiceDTO> getRecentInvoices() {
        return invoiceRepository.findTop10ByOrderByIdDesc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // Entity to DTO 변환
    private InvoiceDTO convertToDTO(Invoice invoice) {
        InvoiceDTO dto = new InvoiceDTO();
        dto.setId(invoice.getId());
        dto.setCompanyName(invoice.getCompanyName());
        dto.setCompanyAddress(invoice.getCompanyAddress());
        dto.setCompanyPhone(invoice.getCompanyPhone());
        dto.setCompanyEmail(invoice.getCompanyEmail());
        dto.setCompanyWebsite(invoice.getCompanyWebsite());
        dto.setClientName(invoice.getClientName());
        dto.setClientAddress(invoice.getClientAddress());
        dto.setClientPhone(invoice.getClientPhone());
        dto.setClientEmail(invoice.getClientEmail());
        dto.setInvoiceNumber(invoice.getInvoiceNumber());
        dto.setInvoiceDate(invoice.getInvoiceDate());
        dto.setDueDate(invoice.getDueDate());
        dto.setNotes(invoice.getNotes());
        dto.setTaxRate(invoice.getTaxRate());
        dto.setSubtotal(invoice.getSubtotal());
        dto.setTaxAmount(invoice.getTaxAmount());
        dto.setTotal(invoice.getTotal());
        
        List<InvoiceItemDTO> itemDTOs = invoice.getItems().stream()
                .map(this::convertItemToDTO)
                .collect(Collectors.toList());
        dto.setItems(itemDTOs);
        
        return dto;
    }
    
    // DTO to Entity 변환
    private Invoice convertToEntity(InvoiceDTO dto) {
        Invoice invoice = new Invoice();
        invoice.setCompanyName(dto.getCompanyName());
        invoice.setCompanyAddress(dto.getCompanyAddress());
        invoice.setCompanyPhone(dto.getCompanyPhone());
        invoice.setCompanyEmail(dto.getCompanyEmail());
        invoice.setCompanyWebsite(dto.getCompanyWebsite());
        invoice.setClientName(dto.getClientName());
        invoice.setClientAddress(dto.getClientAddress());
        invoice.setClientPhone(dto.getClientPhone());
        invoice.setClientEmail(dto.getClientEmail());
        invoice.setInvoiceNumber(dto.getInvoiceNumber());
        invoice.setInvoiceDate(dto.getInvoiceDate());
        invoice.setDueDate(dto.getDueDate());
        invoice.setNotes(dto.getNotes());
        invoice.setTaxRate(dto.getTaxRate());
        
        if (dto.getItems() != null) {
            List<InvoiceItem> items = dto.getItems().stream()
                    .map(itemDTO -> convertItemToEntity(itemDTO, invoice))
                    .collect(Collectors.toList());
            invoice.setItems(items);
        }
        
        invoice.calculateTotals();
        return invoice;
    }
    
    // 기존 Entity 업데이트
    private void updateInvoiceFromDTO(Invoice invoice, InvoiceDTO dto) {
        invoice.setCompanyName(dto.getCompanyName());
        invoice.setCompanyAddress(dto.getCompanyAddress());
        invoice.setCompanyPhone(dto.getCompanyPhone());
        invoice.setCompanyEmail(dto.getCompanyEmail());
        invoice.setCompanyWebsite(dto.getCompanyWebsite());
        invoice.setClientName(dto.getClientName());
        invoice.setClientAddress(dto.getClientAddress());
        invoice.setClientPhone(dto.getClientPhone());
        invoice.setClientEmail(dto.getClientEmail());
        invoice.setInvoiceNumber(dto.getInvoiceNumber());
        invoice.setInvoiceDate(dto.getInvoiceDate());
        invoice.setDueDate(dto.getDueDate());
        invoice.setNotes(dto.getNotes());
        invoice.setTaxRate(dto.getTaxRate());
        
        // 기존 아이템들 제거
        invoice.getItems().clear();
        
        // 새 아이템들 추가
        if (dto.getItems() != null) {
            List<InvoiceItem> items = dto.getItems().stream()
                    .map(itemDTO -> convertItemToEntity(itemDTO, invoice))
                    .collect(Collectors.toList());
            invoice.getItems().addAll(items);
        }
        
        invoice.calculateTotals();
    }
    
    // InvoiceItem Entity to DTO
    private InvoiceItemDTO convertItemToDTO(InvoiceItem item) {
        InvoiceItemDTO dto = new InvoiceItemDTO();
        dto.setId(item.getId());
        dto.setDescription(item.getDescription());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        dto.setTotal(item.getTotal());
        return dto;
    }
    
    // InvoiceItem DTO to Entity
    private InvoiceItem convertItemToEntity(InvoiceItemDTO dto, Invoice invoice) {
        InvoiceItem item = new InvoiceItem();
        item.setDescription(dto.getDescription());
        item.setQuantity(dto.getQuantity());
        item.setPrice(dto.getPrice());
        item.setInvoice(invoice);
        item.calculateTotal();
        return item;
    }
}