package com.invoice.backend.service;

import com.invoice.backend.dto.CustomerDTO;
import com.invoice.backend.dto.InvoiceDTO;
import com.invoice.backend.entity.Customer;
import com.invoice.backend.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomerService {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private InvoiceService invoiceService;
    
    // 모든 구매자 조회
    @Transactional(readOnly = true)
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // ID로 구매자 조회
    @Transactional(readOnly = true)
    public Optional<CustomerDTO> getCustomerById(Long id) {
        return customerRepository.findById(id)
                .map(this::convertToDTO);
    }
    
    // 구매자명으로 검색
    @Transactional(readOnly = true)
    public List<CustomerDTO> searchByCustomerName(String customerName) {
        return customerRepository.findByCustomerNameContainingIgnoreCase(customerName)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // 인보이스가 있는 구매자들만 조회 (구매 내역이 있는 고객들)
    @Transactional(readOnly = true)
    public List<CustomerDTO> getCustomersWithPurchaseHistory() {
        return customerRepository.findCustomersWithInvoices()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // 특정 구매자의 구매 내역 조회
    @Transactional(readOnly = true)
    public List<InvoiceDTO> getCustomerPurchaseHistory(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("구매자를 찾을 수 없습니다. ID: " + customerId));
        
        return customer.getInvoices()
                .stream()
                .map(invoiceService::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // 특정 구매자의 특정 기간 구매 내역
    @Transactional(readOnly = true)
    public List<InvoiceDTO> getCustomerPurchaseHistoryByDateRange(Long customerId, LocalDate startDate, LocalDate endDate) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("구매자를 찾을 수 없습니다. ID: " + customerId));
        
        return customer.getInvoices()
                .stream()
                .filter(invoice -> {
                    LocalDate invoiceDate = invoice.getInvoiceDate();
                    return !invoiceDate.isBefore(startDate) && !invoiceDate.isAfter(endDate);
                })
                .map(invoiceService::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // 특정 구매자의 총 구매 금액
    @Transactional(readOnly = true)
    public Double getCustomerTotalPurchaseAmount(Long customerId) {
        Double totalAmount = customerRepository.getTotalPurchaseAmountByCustomerId(customerId);
        return totalAmount != null ? totalAmount : 0.0;
    }
    
    // 구매자 생성
    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        Customer customer = convertToEntity(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return convertToDTO(savedCustomer);
    }
    
    // 구매자 수정
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("구매자를 찾을 수 없습니다. ID: " + id));
        
        updateCustomerFromDTO(existingCustomer, customerDTO);
        Customer savedCustomer = customerRepository.save(existingCustomer);
        return convertToDTO(savedCustomer);
    }
    
    // 구매자 삭제
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("구매자를 찾을 수 없습니다. ID: " + id);
        }
        customerRepository.deleteById(id);
    }
    
    // 입금 완료 처리
    public CustomerDTO markPaymentCompleted(Long customerId, LocalDate paymentDate, String paymentMethod) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("구매자를 찾을 수 없습니다. ID: " + customerId));
        
        customer.setPaymentCompleted(true);
        customer.setPaymentDate(paymentDate);
        customer.setPaymentMethod(paymentMethod);
        
        Customer savedCustomer = customerRepository.save(customer);
        return convertToDTO(savedCustomer);
    }
    
    // 입금 완료된 구매자들 조회
    @Transactional(readOnly = true)
    public List<CustomerDTO> getCustomersWithCompletedPayment() {
        return customerRepository.findByPaymentCompletedTrue()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // 입금 미완료된 구매자들 조회
    @Transactional(readOnly = true)
    public List<CustomerDTO> getCustomersWithPendingPayment() {
        return customerRepository.findByPaymentCompletedFalse()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // Entity to DTO 변환
    public CustomerDTO convertToDTO(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setCustomerName(customer.getCustomerName());
        dto.setPhone(customer.getPhone());
        dto.setEmail(customer.getEmail());
        dto.setAddress(customer.getAddress());
        dto.setPaymentDate(customer.getPaymentDate());
        dto.setPaymentCompleted(customer.getPaymentCompleted());
        dto.setPaymentMethod(customer.getPaymentMethod());
        dto.setNotes(customer.getNotes());
        dto.setCreatedAt(customer.getCreatedAt());
        dto.setUpdatedAt(customer.getUpdatedAt());
        
        // 총 구매 금액 계산
        dto.setTotalPurchaseAmount(getCustomerTotalPurchaseAmount(customer.getId()));
        
        // 구매 횟수 계산
        dto.setPurchaseCount(customer.getInvoices().size());
        
        return dto;
    }
    
    // DTO to Entity 변환
    private Customer convertToEntity(CustomerDTO dto) {
        Customer customer = new Customer();
        customer.setCustomerName(dto.getCustomerName());
        customer.setPhone(dto.getPhone());
        customer.setEmail(dto.getEmail());
        customer.setAddress(dto.getAddress());
        customer.setPaymentDate(dto.getPaymentDate());
        customer.setPaymentCompleted(dto.getPaymentCompleted() != null ? dto.getPaymentCompleted() : false);
        customer.setPaymentMethod(dto.getPaymentMethod());
        customer.setNotes(dto.getNotes());
        return customer;
    }
    
    // 기존 Entity 업데이트
    private void updateCustomerFromDTO(Customer customer, CustomerDTO dto) {
        customer.setCustomerName(dto.getCustomerName());
        customer.setPhone(dto.getPhone());
        customer.setEmail(dto.getEmail());
        customer.setAddress(dto.getAddress());
        customer.setPaymentDate(dto.getPaymentDate());
        if (dto.getPaymentCompleted() != null) {
            customer.setPaymentCompleted(dto.getPaymentCompleted());
        }
        customer.setPaymentMethod(dto.getPaymentMethod());
        customer.setNotes(dto.getNotes());
    }
}