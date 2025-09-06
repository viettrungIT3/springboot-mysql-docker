package com.backend.backend.service;

import com.backend.backend.dto.common.PageResponse;
import com.backend.backend.dto.stockentry.StockEntryCreateRequest;
import com.backend.backend.dto.stockentry.StockEntryResponse;
import com.backend.backend.dto.stockentry.StockEntryUpdateRequest;
import com.backend.backend.entity.Product;
import com.backend.backend.entity.StockEntry;
import com.backend.backend.exception.ResourceNotFoundException;
import com.backend.backend.mapper.StockEntryMapper;
import com.backend.backend.repository.ProductRepository;
import com.backend.backend.repository.StockEntryRepository;
import com.backend.backend.repository.SupplierRepository;
import com.backend.backend.util.PageMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class StockEntryService {

    private final StockEntryRepository stockEntryRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final StockEntryMapper stockEntryMapper;

    public StockEntryService(StockEntryRepository stockEntryRepository,
            ProductRepository productRepository,
            SupplierRepository supplierRepository,
            StockEntryMapper stockEntryMapper) {
        this.stockEntryRepository = stockEntryRepository;
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
        this.stockEntryMapper = stockEntryMapper;
    }

    @Transactional
    public StockEntryResponse create(StockEntryCreateRequest request) {
        // Validate product exists
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy sản phẩm với ID: " + request.getProductId()));

        // Validate supplier exists
        // Supplier supplier = supplierRepository.findById(request.getSupplierId())
        // .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhà cung cấp
        // với ID: " + request.getSupplierId()));

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
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu nhập kho với ID: " + id));

        // Store old quantity for stock adjustment
        Integer oldQuantity = entity.getQuantity();
        Product product = entity.getProduct();

        // Validate new product if being changed
        if (request.getProductId() != null && !request.getProductId().equals(entity.getProduct().getId())) {
            Product newProduct = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy sản phẩm với ID: " + request.getProductId()));

            // Revert old product stock
            product.setQuantityInStock(product.getQuantityInStock() - oldQuantity);
            productRepository.save(product);

            product = newProduct;
        }

        // Validate new supplier if being changed
        if (request.getSupplierId() != null && !request.getSupplierId().equals(entity.getSupplier().getId())) {
            supplierRepository.findById(request.getSupplierId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy nhà cung cấp với ID: " + request.getSupplierId()));
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
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu nhập kho với ID: " + id));
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
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu nhập kho với ID: " + id));

        // Revert product stock
        Product product = entity.getProduct();
        product.setQuantityInStock(product.getQuantityInStock() - entity.getQuantity());
        productRepository.save(product);

        entity.markAsDeleted();
        stockEntryRepository.save(entity);
    }
}
