package com.backend.backend.service;

import com.backend.backend.config.CacheNames;
import com.backend.backend.dto.common.PageResponse;
import com.backend.backend.dto.supplier.SupplierCreateRequest;
import com.backend.backend.dto.supplier.SupplierResponse;
import com.backend.backend.dto.supplier.SupplierUpdateRequest;
import com.backend.backend.entity.Supplier;
import com.backend.backend.exception.ResourceNotFoundException;
import com.backend.backend.mapper.SupplierMapper;
import com.backend.backend.repository.SupplierRepository;
import com.backend.backend.util.PageMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    @Transactional
    @Caching(evict = {
        @CacheEvict(cacheNames = CacheNames.SUPPLIER_LIST, allEntries = true),
        @CacheEvict(cacheNames = CacheNames.SUPPLIER_BY_ID, key = "#result.id", condition = "#result != null")
    })
    public SupplierResponse create(SupplierCreateRequest request) {
        Supplier entity = supplierMapper.toEntity(request);
        Supplier saved = supplierRepository.save(entity);
        return supplierMapper.toResponse(saved);
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(cacheNames = CacheNames.SUPPLIER_LIST, allEntries = true),
        @CacheEvict(cacheNames = CacheNames.SUPPLIER_BY_ID, key = "#id")
    })
    public SupplierResponse update(Long id, SupplierUpdateRequest request) {
        Supplier entity = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhà cung cấp với ID: " + id));
        supplierMapper.updateEntity(entity, request); // partial update
        Supplier saved = supplierRepository.save(entity);
        return supplierMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.SUPPLIER_BY_ID, key = "#id")
    public SupplierResponse getById(Long id) {
        Supplier entity = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhà cung cấp với ID: " + id));
        return supplierMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public List<SupplierResponse> findAll() {
        return supplierRepository.findAll().stream()
                .map(supplierMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(
        cacheNames = CacheNames.SUPPLIER_LIST,
        key = "T(java.util.Objects).hash(#page,#size,#sort)"
    )
    public PageResponse<SupplierResponse> list(int page, int size, String sort) {
        Sort s = (sort == null || sort.isBlank()) ? Sort.by("id").descending() : Sort.by(sort);
        Pageable pageable = PageRequest.of(page, size, s);
        Page<Supplier> result = supplierRepository.findAll(pageable);

        return PageMapper.toPageResponse(result, supplierMapper::toResponse);
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(cacheNames = CacheNames.SUPPLIER_LIST, allEntries = true),
        @CacheEvict(cacheNames = CacheNames.SUPPLIER_BY_ID, key = "#id")
    })
    public void delete(Long id) {
        Supplier entity = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhà cung cấp với ID: " + id));
        entity.delete();
        supplierRepository.save(entity);
    }
    
    // ==================== BUSINESS LOGIC METHODS ====================
    
    /**
     * Check if supplier exists by name
     */
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return supplierRepository.existsByName(name);
    }
    
    /**
     * Find supplier by name (case insensitive)
     */
    @Transactional(readOnly = true)
    public Optional<SupplierResponse> findByName(String name) {
        return supplierRepository.findByNameIgnoreCase(name)
                .map(supplierMapper::toResponse);
    }
    
    /**
     * Find suppliers by name containing (case insensitive)
     */
    @Transactional(readOnly = true)
    public List<SupplierResponse> findByNameContaining(String name) {
        List<Supplier> suppliers = supplierRepository.findByNameContainingIgnoreCase(name);
        return suppliers.stream()
                .map(supplierMapper::toResponse)
                .toList();
    }
    
    /**
     * Find suppliers by contact info containing (case insensitive)
     */
    @Transactional(readOnly = true)
    public List<SupplierResponse> findByContactInfoContaining(String contactInfo) {
        List<Supplier> suppliers = supplierRepository.findByContactInfoContainingIgnoreCase(contactInfo);
        return suppliers.stream()
                .map(supplierMapper::toResponse)
                .toList();
    }
    
    /**
     * Update supplier contact information
     */
    @Transactional
    @Caching(evict = {
        @CacheEvict(cacheNames = CacheNames.SUPPLIER_LIST, allEntries = true),
        @CacheEvict(cacheNames = CacheNames.SUPPLIER_BY_ID, key = "#supplierId")
    })
    public void updateContactInfo(Long supplierId, String contactInfo) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhà cung cấp với ID: " + supplierId));
        
        String oldContactInfo = supplier.getContactInfo();
        supplier.setContactInfo(contactInfo);
        supplierRepository.save(supplier);
        
        log.info("Updated contact info for supplier {} (ID: {}) from '{}' to '{}'", 
                supplier.getName(), supplierId, oldContactInfo, contactInfo);
    }
    
    /**
     * Get supplier statistics
     */
    @Transactional(readOnly = true)
    public SupplierStats getSupplierStats() {
        long totalSuppliers = supplierRepository.count();
        long suppliersWithContactInfo = supplierRepository.countByContactInfoIsNotNull();
        long suppliersWithoutContactInfo = totalSuppliers - suppliersWithContactInfo;
        
        return SupplierStats.builder()
                .totalSuppliers(totalSuppliers)
                .suppliersWithContactInfo(suppliersWithContactInfo)
                .suppliersWithoutContactInfo(suppliersWithoutContactInfo)
                .build();
    }
    
    /**
     * Search suppliers with advanced criteria
     */
    @Transactional(readOnly = true)
    public List<SupplierResponse> searchSuppliers(String name, String contactInfo) {
        List<Supplier> suppliers;
        
        if (name != null && !name.trim().isEmpty() && contactInfo != null && !contactInfo.trim().isEmpty()) {
            // Search by both name and contact info
            suppliers = supplierRepository.findByNameContainingIgnoreCaseAndContactInfoContainingIgnoreCase(name, contactInfo);
        } else if (name != null && !name.trim().isEmpty()) {
            // Search by name only
            suppliers = supplierRepository.findByNameContainingIgnoreCase(name);
        } else if (contactInfo != null && !contactInfo.trim().isEmpty()) {
            // Search by contact info only
            suppliers = supplierRepository.findByContactInfoContainingIgnoreCase(contactInfo);
        } else {
            // Return all suppliers
            suppliers = supplierRepository.findAll();
        }
        
        return suppliers.stream()
                .map(supplierMapper::toResponse)
                .toList();
    }
    
    /**
     * Validate supplier data before creation
     */
    @Transactional(readOnly = true)
    public void validateSupplierData(SupplierCreateRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên nhà cung cấp không được để trống");
        }
        
        if (supplierRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Nhà cung cấp với tên '" + request.getName() + "' đã tồn tại");
        }
    }
    
    /**
     * Get suppliers that have provided products (have stock entries)
     * Note: This would require a custom query or @Query annotation
     * For now, we'll return all suppliers
     */
    @Transactional(readOnly = true)
    public List<SupplierResponse> getActiveSuppliers() {
        // TODO: Implement custom query to find suppliers with stock entries
        List<Supplier> suppliers = supplierRepository.findAll();
        return suppliers.stream()
                .map(supplierMapper::toResponse)
                .toList();
    }
    
    // ==================== INNER CLASSES ====================
    
    @lombok.Data
    @lombok.Builder
    public static class SupplierStats {
        private long totalSuppliers;
        private long suppliersWithContactInfo;
        private long suppliersWithoutContactInfo;
    }
}