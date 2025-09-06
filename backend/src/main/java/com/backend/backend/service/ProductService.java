package com.backend.backend.service;

import com.backend.backend.dto.common.PageResponse;
import com.backend.backend.dto.product.ProductCreateRequest;
import com.backend.backend.dto.product.ProductResponse;
import com.backend.backend.dto.product.ProductUpdateRequest;
import com.backend.backend.entity.Product;
import com.backend.backend.exception.ResourceNotFoundException;
import com.backend.backend.mapper.ProductMapper;
import com.backend.backend.repository.ProductRepository;
import com.backend.backend.util.PageMapper;
import com.backend.backend.util.SlugUtil;
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
        entity.setSlug(generateUniqueSlug(request.getName()));
        Product saved = productRepository.save(entity);
        return productMapper.toResponse(saved);
    }

    @Transactional
    public ProductResponse update(Long id, ProductUpdateRequest request) {
        Product entity = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + id));
        productMapper.updateEntity(entity, request); // partial update
        
        // Update slug if name is being updated
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            entity.setSlug(generateUniqueSlug(request.getName()));
        }
        
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
    public ProductResponse getBySlug(String slug) {
        Product entity = productRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với slug: " + slug));
        return productMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> list(int page, int size, String sort, String search) {
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

        Page<Product> result;
        if (search != null && !search.isBlank()) {
            result = productRepository.findByNameContainingIgnoreCase(search, pageable);
        } else {
            result = productRepository.findAll(pageable);
        }

        return PageMapper.toPageResponse(result, productMapper::toResponse);
    }

    @Transactional
    public void delete(Long id) {
        Product entity = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + id));
        entity.markAsDeleted();
        productRepository.save(entity);
    }

    /**
     * Generates a unique slug from the given name.
     * If the base slug already exists, appends a counter to make it unique.
     */
    private String generateUniqueSlug(String name) {
        String baseSlug = SlugUtil.toSlug(name);
        String slug = baseSlug;
        int counter = 1;
        
        while (productRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter++;
        }
        
        return slug;
    }
}
