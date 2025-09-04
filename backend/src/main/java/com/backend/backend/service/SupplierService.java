package com.backend.backend.service;

import com.backend.backend.dto.common.PageResponse;
import com.backend.backend.dto.supplier.SupplierCreateRequest;
import com.backend.backend.dto.supplier.SupplierResponse;
import com.backend.backend.dto.supplier.SupplierUpdateRequest;
import com.backend.backend.entity.Supplier;
import com.backend.backend.exception.ResourceNotFoundException;
import com.backend.backend.mapper.SupplierMapper;
import com.backend.backend.repository.SupplierRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    public SupplierService(SupplierRepository supplierRepository, SupplierMapper supplierMapper) {
        this.supplierRepository = supplierRepository;
        this.supplierMapper = supplierMapper;
    }

    @Transactional
    public SupplierResponse create(SupplierCreateRequest request) {
        Supplier entity = supplierMapper.toEntity(request);
        Supplier saved = supplierRepository.save(entity);
        return supplierMapper.toResponse(saved);
    }

    @Transactional
    public SupplierResponse update(Long id, SupplierUpdateRequest request) {
        Supplier entity = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhà cung cấp với ID: " + id));
        supplierMapper.updateEntity(entity, request); // partial update
        Supplier saved = supplierRepository.save(entity);
        return supplierMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
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
    public PageResponse<SupplierResponse> list(int page, int size, String sort) {
        Sort s = (sort == null || sort.isBlank()) ? Sort.by("id").descending() : Sort.by(sort);
        Pageable pageable = PageRequest.of(page, size, s);
        Page<Supplier> result = supplierRepository.findAll(pageable);

        return PageResponse.<SupplierResponse>builder()
                .items(result.getContent().stream().map(supplierMapper::toResponse).toList())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .first(result.isFirst())
                .last(result.isLast())
                .build();
    }

    @Transactional
    public void delete(Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy nhà cung cấp với ID: " + id);
        }
        supplierRepository.deleteById(id);
    }
}