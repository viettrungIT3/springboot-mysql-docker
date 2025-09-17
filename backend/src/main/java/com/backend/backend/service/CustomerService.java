package com.backend.backend.service;

import com.backend.backend.dto.common.PageResponse;
import com.backend.backend.dto.customer.CustomerCreateRequest;
import com.backend.backend.dto.customer.CustomerResponse;
import com.backend.backend.dto.customer.CustomerUpdateRequest;
import com.backend.backend.entity.Customer;
import com.backend.backend.shared.domain.exception.EntityNotFoundException;
import com.backend.backend.mapper.CustomerMapper;
import com.backend.backend.repository.CustomerRepository;
import com.backend.backend.util.PageMapper;
import com.backend.backend.util.SlugUtil;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.backend.backend.config.CacheNames;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Transactional
    @Caching(evict = {
        @CacheEvict(cacheNames = CacheNames.CUSTOMER_LIST, allEntries = true),
        @CacheEvict(cacheNames = CacheNames.CUSTOMER_BY_ID, key = "#result.id", condition = "#result != null"),
        @CacheEvict(cacheNames = CacheNames.CUSTOMER_BY_SLUG, key = "#result.slug", condition = "#result != null")
    })
    public CustomerResponse create(CustomerCreateRequest request) {
        Customer entity = customerMapper.toEntity(request);
        entity.setSlug(generateUniqueSlug(request.getName()));
        Customer saved = customerRepository.save(entity);
        return customerMapper.toResponse(saved);
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(cacheNames = CacheNames.CUSTOMER_LIST, allEntries = true),
        @CacheEvict(cacheNames = CacheNames.CUSTOMER_BY_ID, key = "#id"),
        @CacheEvict(cacheNames = CacheNames.CUSTOMER_BY_SLUG, allEntries = true) // Evict by slug if slug changes
    })
    public CustomerResponse update(Long id, CustomerUpdateRequest request) {
        Customer entity = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer", id));
        customerMapper.updateEntity(entity, request); // partial update
        
        // Update slug if name is being updated
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            entity.setSlug(generateUniqueSlug(request.getName()));
        }
        
        Customer saved = customerRepository.save(entity);
        return customerMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.CUSTOMER_BY_ID, key = "#id")
    public CustomerResponse getById(Long id) {
        Customer entity = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer", id));
        return customerMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.CUSTOMER_BY_SLUG, key = "#slug")
    public CustomerResponse getBySlug(String slug) {
        Customer entity = customerRepository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Customer", slug));
        return customerMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> findAll() {
        return customerRepository.findAll().stream()
                .map(customerMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    // @Cacheable(
    //     cacheNames = CacheNames.CUSTOMER_LIST,
    //     key = "T(java.util.Objects).hash(#page,#size,#sort,#search)"
    // )
    public PageResponse<CustomerResponse> list(int page, int size, String sort, String search) {
        Sort s = (sort == null || sort.isBlank())
                ? Sort.by("id").descending()
                : Sort.by(sort.split(",")[0])
                        .ascending();

        // Xử lý sort direction nếu có
        if (sort != null && !sort.isBlank() && sort.contains(",")) {
            String[] sortParts = sort.split(",");
            if (sortParts.length > 1 && "desc".equalsIgnoreCase(sortParts[1])) {
                s = Sort.by(sortParts[0]).descending();
            }
        }

        Pageable pageable = PageRequest.of(page, size, s);

        Page<Customer> result;
        if (search != null && !search.isBlank()) {
            result = customerRepository.findByNameContainingIgnoreCase(search, pageable);
        } else {
            result = customerRepository.findAll(pageable);
        }

        return PageMapper.toPageResponse(result, customerMapper::toResponse);
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(cacheNames = CacheNames.CUSTOMER_LIST, allEntries = true),
        @CacheEvict(cacheNames = CacheNames.CUSTOMER_BY_ID, key = "#id"),
        @CacheEvict(cacheNames = CacheNames.CUSTOMER_BY_SLUG, allEntries = true) // Evict by slug if slug changes
    })
    public void delete(Long id) {
        Customer entity = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer", id));
        entity.delete();
        customerRepository.save(entity);
    }

    // ==================== BUSINESS LOGIC METHODS ====================
    
    /**
     * Check if customer exists by name
     */
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return customerRepository.existsByName(name);
    }
    
    /**
     * Check if customer exists by slug
     */
    @Transactional(readOnly = true)
    public boolean existsBySlug(String slug) {
        return customerRepository.existsBySlug(slug);
    }
    
    /**
     * Find customer by name (case insensitive)
     */
    @Transactional(readOnly = true)
    public Optional<CustomerResponse> findByName(String name) {
        return customerRepository.findByNameIgnoreCase(name)
                .map(customerMapper::toResponse);
    }
    
    /**
     * Find customers by name containing (case insensitive)
     */
    @Transactional(readOnly = true)
    public List<CustomerResponse> findByNameContaining(String name) {
        List<Customer> customers = customerRepository.findByNameContainingIgnoreCase(name);
        return customers.stream()
                .map(customerMapper::toResponse)
                .toList();
    }
    
    /**
     * Update customer contact information
     */
    @Transactional
    @Caching(evict = {
        @CacheEvict(cacheNames = CacheNames.CUSTOMER_LIST, allEntries = true),
        @CacheEvict(cacheNames = CacheNames.CUSTOMER_BY_ID, key = "#customerId"),
        @CacheEvict(cacheNames = CacheNames.CUSTOMER_BY_SLUG, allEntries = true)
    })
    public void updateContactInfo(Long customerId, String contactInfo) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer", customerId));
        
        String oldContactInfo = customer.getContactInfo();
        customer.setContactInfo(contactInfo);
        customerRepository.save(customer);
        
        log.info("Updated contact info for customer {} (ID: {}) from '{}' to '{}'", 
                customer.getName(), customerId, oldContactInfo, contactInfo);
    }
    
    /**
     * Get customer statistics
     */
    @Transactional(readOnly = true)
    public CustomerStats getCustomerStats() {
        long totalCustomers = customerRepository.count();
        long customersWithContactInfo = customerRepository.countByContactInfoIsNotNull();
        long customersWithoutContactInfo = totalCustomers - customersWithContactInfo;
        
        return CustomerStats.builder()
                .totalCustomers(totalCustomers)
                .customersWithContactInfo(customersWithContactInfo)
                .customersWithoutContactInfo(customersWithoutContactInfo)
                .build();
    }
    
    /**
     * Search customers with advanced criteria
     */
    @Transactional(readOnly = true)
    public List<CustomerResponse> searchCustomers(String name, String contactInfo) {
        List<Customer> customers;
        
        if (name != null && !name.trim().isEmpty() && contactInfo != null && !contactInfo.trim().isEmpty()) {
            // Search by both name and contact info
            customers = customerRepository.findByNameContainingIgnoreCaseAndContactInfoContainingIgnoreCase(name, contactInfo);
        } else if (name != null && !name.trim().isEmpty()) {
            // Search by name only
            customers = customerRepository.findByNameContainingIgnoreCase(name);
        } else if (contactInfo != null && !contactInfo.trim().isEmpty()) {
            // Search by contact info only
            customers = customerRepository.findByContactInfoContainingIgnoreCase(contactInfo);
        } else {
            // Return all customers
            customers = customerRepository.findAll();
        }
        
        return customers.stream()
                .map(customerMapper::toResponse)
                .toList();
    }
    
    /**
     * Validate customer data before creation
     */
    @Transactional(readOnly = true)
    public void validateCustomerData(CustomerCreateRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên khách hàng không được để trống");
        }
        
        if (customerRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Khách hàng với tên '" + request.getName() + "' đã tồn tại");
        }
    }
    
    /**
     * Generates a unique slug from the given name.
     * If the base slug already exists, appends a counter to make it unique.
     */
    private String generateUniqueSlug(String name) {
        String baseSlug = SlugUtil.toSlug(name);
        String slug = baseSlug;
        int counter = 1;
        
        while (customerRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter++;
        }
        
        return slug;
    }
    
    // ==================== INNER CLASSES ====================
    
    @lombok.Data
    @lombok.Builder
    public static class CustomerStats {
        private long totalCustomers;
        private long customersWithContactInfo;
        private long customersWithoutContactInfo;
    }
}