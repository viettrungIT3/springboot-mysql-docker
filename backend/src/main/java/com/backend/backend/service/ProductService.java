package com.backend.backend.service;

import com.backend.backend.dto.common.PageResponse;
import com.backend.backend.dto.product.ProductCreateRequest;
import com.backend.backend.dto.product.ProductResponse;
import com.backend.backend.dto.product.ProductUpdateRequest;
import com.backend.backend.entity.Product;
import com.backend.backend.exception.ResourceNotFoundException;
import com.backend.backend.mapper.ProductMapper;
import com.backend.backend.repository.ProductRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Transactional
    public ProductResponse create(ProductCreateRequest request) {
        Product entity = productMapper.toEntity(request);
        Product saved = productRepository.save(entity);
        return productMapper.toResponse(saved);
    }

    @Transactional
    public ProductResponse update(Long id, ProductUpdateRequest request) {
        Product entity = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + id));
        productMapper.updateEntity(entity, request); // partial update
        Product saved = productRepository.save(entity);
        return productMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        Product entity = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + id));
        return productMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> list(int page, int size, String sort) {
        Sort s = (sort == null || sort.isBlank()) ? Sort.by("id").descending() : Sort.by(sort);
        Pageable pageable = PageRequest.of(page, size, s);
        Page<Product> result = productRepository.findAll(pageable);

        return PageResponse.<ProductResponse>builder()
                .items(result.getContent().stream().map(productMapper::toResponse).toList())
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
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + id);
        }
        productRepository.deleteById(id);
    }
}
