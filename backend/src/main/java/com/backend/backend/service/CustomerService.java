package com.backend.backend.service;

import com.backend.backend.dto.common.PageResponse;
import com.backend.backend.dto.customer.CustomerCreateRequest;
import com.backend.backend.dto.customer.CustomerResponse;
import com.backend.backend.dto.customer.CustomerUpdateRequest;
import com.backend.backend.entity.Customer;
import com.backend.backend.exception.ResourceNotFoundException;
import com.backend.backend.mapper.CustomerMapper;
import com.backend.backend.repository.CustomerRepository;
import com.backend.backend.util.PageMapper;
import com.backend.backend.util.SlugUtil;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @Transactional
    public CustomerResponse create(CustomerCreateRequest request) {
        Customer entity = customerMapper.toEntity(request);
        entity.setSlug(generateUniqueSlug(request.getName()));
        Customer saved = customerRepository.save(entity);
        return customerMapper.toResponse(saved);
    }

    @Transactional
    public CustomerResponse update(Long id, CustomerUpdateRequest request) {
        Customer entity = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng với ID: " + id));
        customerMapper.updateEntity(entity, request); // partial update
        
        // Update slug if name is being updated
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            entity.setSlug(generateUniqueSlug(request.getName()));
        }
        
        Customer saved = customerRepository.save(entity);
        return customerMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public CustomerResponse getById(Long id) {
        Customer entity = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng với ID: " + id));
        return customerMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public CustomerResponse getBySlug(String slug) {
        Customer entity = customerRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng với slug: " + slug));
        return customerMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> findAll() {
        return customerRepository.findAll().stream()
                .map(customerMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
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
    public void delete(Long id) {
        Customer entity = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng với ID: " + id));
        entity.markAsDeleted();
        customerRepository.save(entity);
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
}