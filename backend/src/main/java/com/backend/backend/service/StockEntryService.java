package com.backend.backend.service;

import com.backend.backend.dto.common.PageResponse;
import com.backend.backend.dto.stockentry.StockEntryCreateRequest;
import com.backend.backend.dto.stockentry.StockEntryResponse;
import com.backend.backend.dto.stockentry.StockEntryUpdateRequest;
import com.backend.backend.entity.Product;
import com.backend.backend.entity.StockEntry;
import com.backend.backend.shared.domain.exception.StockEntryException;
import com.backend.backend.shared.domain.exception.ProductException;
import com.backend.backend.shared.domain.exception.SupplierException;
import com.backend.backend.mapper.StockEntryMapper;
import com.backend.backend.repository.ProductRepository;
import com.backend.backend.repository.StockEntryRepository;
import com.backend.backend.repository.SupplierRepository;
import com.backend.backend.util.PageMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockEntryService {

    private final StockEntryRepository stockEntryRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final StockEntryMapper stockEntryMapper;

    @Transactional
    public StockEntryResponse create(StockEntryCreateRequest request) {
        // Validate product exists
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> ProductException.notFound(request.getProductId()));

        // Validate supplier exists
        // Supplier supplier = supplierRepository.findById(request.getSupplierId())
        //         .orElseThrow(() -> SupplierException.notFound(request.getSupplierId()));

        StockEntry entity = stockEntryMapper.toEntity(request);

        // Set entry date if not provided
        if (entity.getEntryDate() == null) {
            entity.setEntryDate(OffsetDateTime.now());
        }

        StockEntry saved = stockEntryRepository.save(entity);

        // Update product stock
        product.setQuantityInStock(product.getQuantityInStock() + request.getQuantity());
        productRepository.save(product);

        return stockEntryMapper.toResponse(saved);
    }

    @Transactional
    public StockEntryResponse update(Long id, StockEntryUpdateRequest request) {
        StockEntry entity = stockEntryRepository.findById(id)
                .orElseThrow(() -> StockEntryException.notFound(id));

        // Store old quantity for stock adjustment
        Integer oldQuantity = entity.getQuantity();
        Product product = entity.getProduct();

        // Validate new product if being changed
        if (request.getProductId() != null && !request.getProductId().equals(entity.getProduct().getId())) {
            Product newProduct = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> ProductException.notFound(request.getProductId()));

            // Revert old product stock
            product.setQuantityInStock(product.getQuantityInStock() - oldQuantity);
            productRepository.save(product);

            product = newProduct;
        }

        // Validate new supplier if being changed
        if (request.getSupplierId() != null && !request.getSupplierId().equals(entity.getSupplier().getId())) {
            supplierRepository.findById(request.getSupplierId())
                    .orElseThrow(() -> SupplierException.notFound(request.getSupplierId()));
        }

        stockEntryMapper.updateEntity(entity, request); // partial update
        StockEntry saved = stockEntryRepository.save(entity);

        // Update product stock with new quantity
        Integer newQuantity = saved.getQuantity();
        if (!oldQuantity.equals(newQuantity) || !product.getId().equals(saved.getProduct().getId())) {
            if (product.getId().equals(saved.getProduct().getId())) {
                // Same product, adjust difference
                product.setQuantityInStock(product.getQuantityInStock() - oldQuantity + newQuantity);
            } else {
                // Different product, add new quantity
                product.setQuantityInStock(product.getQuantityInStock() + newQuantity);
            }
            productRepository.save(product);
        }

        return stockEntryMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public StockEntryResponse getById(Long id) {
        StockEntry entity = stockEntryRepository.findById(id)
                .orElseThrow(() -> StockEntryException.notFound(id));
        return stockEntryMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public List<StockEntryResponse> findAll() {
        return stockEntryRepository.findAll().stream()
                .map(stockEntryMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<StockEntryResponse> list(int page, int size, String sort) {
        Sort s = (sort == null || sort.isBlank()) ? Sort.by("entryDate").descending() : Sort.by(sort);
        Pageable pageable = PageRequest.of(page, size, s);
        Page<StockEntry> result = stockEntryRepository.findAll(pageable);

        return PageMapper.toPageResponse(result, stockEntryMapper::toResponse);
    }

    @Transactional
    public void delete(Long id) {
        StockEntry entity = stockEntryRepository.findById(id)
                .orElseThrow(() -> StockEntryException.notFound(id));

        // Revert product stock
        Product product = entity.getProduct();
        product.setQuantityInStock(product.getQuantityInStock() - entity.getQuantity());
        productRepository.save(product);

        entity.delete();
        stockEntryRepository.save(entity);
    }
    
    // ==================== BUSINESS LOGIC METHODS ====================
    
    /**
     * Get stock entries by product
     */
    @Transactional(readOnly = true)
    public List<StockEntryResponse> findByProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + productId);
        }
        
        List<StockEntry> entries = stockEntryRepository.findByProductId(productId);
        return entries.stream()
                .map(stockEntryMapper::toResponse)
                .toList();
    }
    
    /**
     * Get stock entries by supplier
     */
    @Transactional(readOnly = true)
    public List<StockEntryResponse> findBySupplier(Long supplierId) {
        if (!supplierRepository.existsById(supplierId)) {
            throw new ResourceNotFoundException("Không tìm thấy nhà cung cấp với ID: " + supplierId);
        }
        
        List<StockEntry> entries = stockEntryRepository.findBySupplierId(supplierId);
        return entries.stream()
                .map(stockEntryMapper::toResponse)
                .toList();
    }
    
    /**
     * Get stock entries by date range
     */
    @Transactional(readOnly = true)
    public List<StockEntryResponse> findByDateRange(OffsetDateTime startDate, OffsetDateTime endDate) {
        List<StockEntry> entries = stockEntryRepository.findByEntryDateBetween(startDate, endDate);
        return entries.stream()
                .map(stockEntryMapper::toResponse)
                .toList();
    }
    
    /**
     * Get stock entries by quantity range
     */
    @Transactional(readOnly = true)
    public List<StockEntryResponse> findByQuantityRange(Integer minQuantity, Integer maxQuantity) {
        List<StockEntry> entries = stockEntryRepository.findByQuantityBetween(minQuantity, maxQuantity);
        return entries.stream()
                .map(stockEntryMapper::toResponse)
                .toList();
    }
    
    /**
     * Get stock statistics
     */
    @Transactional(readOnly = true)
    public StockStats getStockStats() {
        List<StockEntry> allEntries = stockEntryRepository.findAll();
        
        long totalEntries = allEntries.size();
        long totalQuantity = allEntries.stream()
                .mapToInt(StockEntry::getQuantity)
                .sum();
        
        // Count entries by date range (last 30 days)
        OffsetDateTime thirtyDaysAgo = OffsetDateTime.now().minusDays(30);
        long recentEntries = allEntries.stream()
                .filter(entry -> entry.getEntryDate() != null && entry.getEntryDate().isAfter(thirtyDaysAgo))
                .count();
        
        // Count unique products and suppliers
        long uniqueProducts = allEntries.stream()
                .map(entry -> entry.getProduct().getId())
                .distinct()
                .count();
        
        long uniqueSuppliers = allEntries.stream()
                .map(entry -> entry.getSupplier().getId())
                .distinct()
                .count();
        
        return StockStats.builder()
                .totalEntries(totalEntries)
                .totalQuantity(totalQuantity)
                .recentEntries(recentEntries)
                .uniqueProducts(uniqueProducts)
                .uniqueSuppliers(uniqueSuppliers)
                .build();
    }
    
    /**
     * Get stock statistics for a specific product
     */
    @Transactional(readOnly = true)
    public ProductStockStats getProductStockStats(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + productId));
        
        List<StockEntry> entries = stockEntryRepository.findByProductId(productId);
        
        long totalEntries = entries.size();
        long totalQuantityReceived = entries.stream()
                .mapToInt(StockEntry::getQuantity)
                .sum();
        
        long uniqueSuppliers = entries.stream()
                .map(entry -> entry.getSupplier().getId())
                .distinct()
                .count();
        
        // Find most recent entry
        Optional<StockEntry> mostRecentEntry = entries.stream()
                .filter(entry -> entry.getEntryDate() != null)
                .max((e1, e2) -> e1.getEntryDate().compareTo(e2.getEntryDate()));
        
        return ProductStockStats.builder()
                .productId(productId)
                .productName(product.getName())
                .totalEntries(totalEntries)
                .totalQuantityReceived(totalQuantityReceived)
                .uniqueSuppliers(uniqueSuppliers)
                .mostRecentEntryDate(mostRecentEntry.map(StockEntry::getEntryDate).orElse(null))
                .build();
    }
    
    /**
     * Validate stock entry data before creation
     */
    @Transactional(readOnly = true)
    public void validateStockEntryData(StockEntryCreateRequest request) {
        if (request.getProductId() == null || !productRepository.existsById(request.getProductId())) {
            throw new IllegalArgumentException("Không tìm thấy sản phẩm với ID: " + request.getProductId());
        }
        
        if (request.getSupplierId() != null && !supplierRepository.existsById(request.getSupplierId())) {
            throw new IllegalArgumentException("Không tìm thấy nhà cung cấp với ID: " + request.getSupplierId());
        }
        
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Số lượng nhập kho phải lớn hơn 0");
        }
    }
    
    /**
     * Get stock entries with advanced search
     */
    @Transactional(readOnly = true)
    public List<StockEntryResponse> searchStockEntries(Long productId, Long supplierId, 
                                                      OffsetDateTime startDate, OffsetDateTime endDate) {
        List<StockEntry> entries;
        
        if (productId != null && supplierId != null) {
            // Search by both product and supplier
            entries = stockEntryRepository.findByProductIdAndSupplierId(productId, supplierId);
        } else if (productId != null) {
            // Search by product only
            entries = stockEntryRepository.findByProductId(productId);
        } else if (supplierId != null) {
            // Search by supplier only
            entries = stockEntryRepository.findBySupplierId(supplierId);
        } else {
            // Search by date range or return all
            if (startDate != null && endDate != null) {
                entries = stockEntryRepository.findByEntryDateBetween(startDate, endDate);
            } else {
                entries = stockEntryRepository.findAll();
            }
        }
        
        return entries.stream()
                .map(stockEntryMapper::toResponse)
                .toList();
    }
    
    // ==================== INNER CLASSES ====================
    
    @lombok.Data
    @lombok.Builder
    public static class StockStats {
        private long totalEntries;
        private long totalQuantity;
        private long recentEntries;
        private long uniqueProducts;
        private long uniqueSuppliers;
    }
    
    @lombok.Data
    @lombok.Builder
    public static class ProductStockStats {
        private Long productId;
        private String productName;
        private long totalEntries;
        private long totalQuantityReceived;
        private long uniqueSuppliers;
        private OffsetDateTime mostRecentEntryDate;
    }
}
