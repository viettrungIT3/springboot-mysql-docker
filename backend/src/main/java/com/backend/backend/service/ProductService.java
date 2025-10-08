package com.backend.backend.service;

import com.backend.backend.config.CacheNames;
import com.backend.backend.dto.common.PageResponse;
import com.backend.backend.dto.product.ProductCreateRequest;
import com.backend.backend.dto.product.ProductResponse;
import com.backend.backend.dto.product.ProductUpdateRequest;
import com.backend.backend.entity.Product;
import com.backend.backend.shared.domain.exception.ProductException;
import com.backend.backend.mapper.ProductMapper;
import com.backend.backend.repository.ProductRepository;
import com.backend.backend.util.PageMapper;
import com.backend.backend.util.SlugUtil;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional
    @Caching(evict = {
        @CacheEvict(cacheNames = CacheNames.PRODUCT_LIST, allEntries = true),
        @CacheEvict(cacheNames = CacheNames.PRODUCT_BY_ID,   key = "#result.id", condition = "#result != null"),
        @CacheEvict(cacheNames = CacheNames.PRODUCT_BY_SLUG, key = "#result.slug", condition = "#result != null")
    })
    public ProductResponse create(ProductCreateRequest request) {
        Product entity = productMapper.toEntity(request);
        entity.setSlug(generateUniqueSlug(request.getName()));
        Product saved = productRepository.save(entity);
        return productMapper.toResponse(saved);
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(cacheNames = CacheNames.PRODUCT_LIST, allEntries = true),
        @CacheEvict(cacheNames = CacheNames.PRODUCT_BY_ID,   key = "#id"),
        @CacheEvict(cacheNames = CacheNames.PRODUCT_BY_SLUG, key = "#result.slug", condition = "#result != null")
    })
    public ProductResponse update(Long id, ProductUpdateRequest request) {
        Product entity = productRepository.findById(id)
                .orElseThrow(() -> ProductException.notFound(id));
        productMapper.updateEntity(entity, request); // partial update
        
        // Update slug if name is being updated
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            entity.setSlug(generateUniqueSlug(request.getName()));
        }
        
        Product saved = productRepository.save(entity);
        return productMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.PRODUCT_BY_ID, key = "#id")
    public ProductResponse getById(Long id) {
        Product entity = productRepository.findById(id)
                .orElseThrow(() -> ProductException.notFound(id));
        return productMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.PRODUCT_BY_SLUG, key = "#slug")
    public ProductResponse getBySlug(String slug) {
        Product entity = productRepository.findBySlug(slug)
                .orElseThrow(() -> ProductException.notFound(slug));
        return productMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(
        cacheNames = CacheNames.PRODUCT_LIST,
        key = "T(java.util.Objects).hash(#page,#size,#sort,#search)"
    )
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
    @Caching(evict = {
        @CacheEvict(cacheNames = CacheNames.PRODUCT_LIST, allEntries = true),
        @CacheEvict(cacheNames = CacheNames.PRODUCT_BY_ID,   key = "#id"),
        @CacheEvict(cacheNames = CacheNames.PRODUCT_BY_SLUG, allEntries = true)
    })
    public void delete(Long id) {
        Product entity = productRepository.findById(id)
                .orElseThrow(() -> ProductException.notFound(id));
        entity.delete();
        productRepository.save(entity);
    }

    // ==================== BUSINESS LOGIC METHODS ====================
    
    /**
     * Check if product is in stock
     */
    @Transactional(readOnly = true)
    public boolean isInStock(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> ProductException.notFound(productId));
        return product.getQuantityInStock() > 0;
    }
    
    /**
     * Check if product has sufficient stock
     */
    @Transactional(readOnly = true)
    public boolean hasSufficientStock(Long productId, Integer requiredQuantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> ProductException.notFound(productId));
        return product.getQuantityInStock() >= requiredQuantity;
    }
    
    /**
     * Reserve stock for an order
     */
    @Transactional
    @Caching(evict = {
        @CacheEvict(cacheNames = CacheNames.PRODUCT_LIST, allEntries = true),
        @CacheEvict(cacheNames = CacheNames.PRODUCT_BY_ID, key = "#productId"),
        @CacheEvict(cacheNames = CacheNames.PRODUCT_BY_SLUG, allEntries = true)
    })
    public void reserveStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> ProductException.notFound(productId));
        
        if (product.getQuantityInStock() < quantity) {
            throw ProductException.insufficientStock(product.getName(), quantity, product.getQuantityInStock());
        }
        
        product.setQuantityInStock(product.getQuantityInStock() - quantity);
        productRepository.save(product);
        log.info("Reserved {} units of product {} (ID: {})", quantity, product.getName(), productId);
    }
    
    /**
     * Release reserved stock (for cancelled orders)
     */
    @Transactional
    @Caching(evict = {
        @CacheEvict(cacheNames = CacheNames.PRODUCT_LIST, allEntries = true),
        @CacheEvict(cacheNames = CacheNames.PRODUCT_BY_ID, key = "#productId"),
        @CacheEvict(cacheNames = CacheNames.PRODUCT_BY_SLUG, allEntries = true)
    })
    public void releaseStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> ProductException.notFound(productId));
        
        product.setQuantityInStock(product.getQuantityInStock() + quantity);
        productRepository.save(product);
        log.info("Released {} units of product {} (ID: {})", quantity, product.getName(), productId);
    }
    
    /**
     * Add stock to product
     */
    @Transactional
    @Caching(evict = {
        @CacheEvict(cacheNames = CacheNames.PRODUCT_LIST, allEntries = true),
        @CacheEvict(cacheNames = CacheNames.PRODUCT_BY_ID, key = "#productId"),
        @CacheEvict(cacheNames = CacheNames.PRODUCT_BY_SLUG, allEntries = true)
    })
    public void addStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> ProductException.notFound(productId));
        
        product.setQuantityInStock(product.getQuantityInStock() + quantity);
        productRepository.save(product);
        log.info("Added {} units to product {} (ID: {})", quantity, product.getName(), productId);
    }
    
    /**
     * Update product price
     */
    @Transactional
    @Caching(evict = {
        @CacheEvict(cacheNames = CacheNames.PRODUCT_LIST, allEntries = true),
        @CacheEvict(cacheNames = CacheNames.PRODUCT_BY_ID, key = "#productId"),
        @CacheEvict(cacheNames = CacheNames.PRODUCT_BY_SLUG, allEntries = true)
    })
    public void updatePrice(Long productId, BigDecimal newPrice) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> ProductException.notFound(productId));
        
        BigDecimal oldPrice = product.getPrice();
        product.setPrice(newPrice);
        productRepository.save(product);
        log.info("Updated price for product {} (ID: {}) from {} to {}", 
                product.getName(), productId, oldPrice, newPrice);
    }
    
    /**
     * Find products by price range
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        List<Product> products = productRepository.findByPriceBetween(minPrice, maxPrice);
        return products.stream()
                .map(productMapper::toResponse)
                .toList();
    }
    
    /**
     * Find low stock products (below threshold)
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> findLowStockProducts(Integer threshold) {
        List<Product> products = productRepository.findByQuantityInStockLessThan(threshold);
        return products.stream()
                .map(productMapper::toResponse)
                .toList();
    }
    
    /**
     * Find products by name containing (case insensitive)
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> findByNameContaining(String name) {
        List<Product> products = productRepository.findByNameContainingIgnoreCase(name);
        return products.stream()
                .map(productMapper::toResponse)
                .toList();
    }
    
    /**
     * Get product statistics
     */
    @Transactional(readOnly = true)
    public ProductStats getProductStats() {
        long totalProducts = productRepository.count();
        long outOfStockProducts = productRepository.countByQuantityInStock(0);
        long lowStockProducts = productRepository.countByQuantityInStockLessThan(10);
        
        Optional<BigDecimal> avgPrice = productRepository.findAll().stream()
                .map(Product::getPrice)
                .reduce(BigDecimal::add)
                .map(sum -> sum.divide(BigDecimal.valueOf(totalProducts), 2, BigDecimal.ROUND_HALF_UP));
        
        return ProductStats.builder()
                .totalProducts(totalProducts)
                .outOfStockProducts(outOfStockProducts)
                .lowStockProducts(lowStockProducts)
                .averagePrice(avgPrice.orElse(BigDecimal.ZERO))
                .build();
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
    
    // ==================== INNER CLASSES ====================
    
    @lombok.Data
    @lombok.Builder
    public static class ProductStats {
        private long totalProducts;
        private long outOfStockProducts;
        private long lowStockProducts;
        private BigDecimal averagePrice;
    }
}
