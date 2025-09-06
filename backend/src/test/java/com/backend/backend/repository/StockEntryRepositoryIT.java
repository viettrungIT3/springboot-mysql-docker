package com.backend.backend.repository;

import com.backend.backend.entity.*;
import com.backend.backend.support.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class StockEntryRepositoryIT extends IntegrationTestBase {

    @Autowired
    private StockEntryRepository stockEntryRepo;
    @Autowired
    private ProductRepository productRepo;
    @Autowired
    private SupplierRepository supplierRepo;

    @Test
    void save_find_list_shouldWorkWithRealMySQL() {
        // Create dependencies
        Product product = Product.builder()
                .name("Stock Product")
                .description("Stock Description")
                .price(new java.math.BigDecimal("100.00"))
                .quantityInStock(50)
                .build();
        productRepo.save(product);

        Supplier supplier = Supplier.builder()
                .name("Stock Supplier")
                .contactInfo("stock@supplier.com")
                .build();
        supplierRepo.save(supplier);

        // Create stock entries
        StockEntry entry1 = StockEntry.builder()
                .product(product)
                .supplier(supplier)
                .quantity(10)
                .entryDate(OffsetDateTime.now())
                .build();
        StockEntry entry2 = StockEntry.builder()
                .product(product)
                .supplier(supplier)
                .quantity(20)
                .entryDate(OffsetDateTime.now())
                .build();
        stockEntryRepo.save(entry1);
        stockEntryRepo.save(entry2);

        // findById
        StockEntry found = stockEntryRepo.findById(entry1.getId()).orElseThrow();
        assertThat(found.getQuantity()).isEqualTo(10);

        // pagination + sort
        Page<StockEntry> page = stockEntryRepo.findAll(PageRequest.of(0, 10, Sort.by("id").descending()));
        assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void relationships_shouldWorkWithRealMySQL() {
        // Create dependencies
        Product product = Product.builder()
                .name("Relationship Product")
                .description("Relationship Description")
                .price(new java.math.BigDecimal("75.00"))
                .quantityInStock(25)
                .build();
        productRepo.save(product);

        Supplier supplier = Supplier.builder()
                .name("Relationship Supplier")
                .contactInfo("relationship@supplier.com")
                .build();
        supplierRepo.save(supplier);

        // Create stock entry
        StockEntry entry = StockEntry.builder()
                .product(product)
                .supplier(supplier)
                .quantity(15)
                .entryDate(OffsetDateTime.now())
                .build();
        stockEntryRepo.save(entry);

        // Verify relationships
        StockEntry found = stockEntryRepo.findById(entry.getId()).orElseThrow();
        assertThat(found.getProduct().getId()).isEqualTo(product.getId());
        assertThat(found.getSupplier().getId()).isEqualTo(supplier.getId());
        assertThat(found.getProduct().getName()).isEqualTo("Relationship Product");
        assertThat(found.getSupplier().getName()).isEqualTo("Relationship Supplier");
    }

    @Test
    void update_shouldWorkWithRealMySQL() {
        // Create dependencies
        Product product = Product.builder()
                .name("Update Product")
                .description("Update Description")
                .price(new java.math.BigDecimal("50.00"))
                .quantityInStock(30)
                .build();
        productRepo.save(product);

        Supplier supplier = Supplier.builder()
                .name("Update Supplier")
                .contactInfo("update@supplier.com")
                .build();
        supplierRepo.save(supplier);

        // Create stock entry
        StockEntry entry = StockEntry.builder()
                .product(product)
                .supplier(supplier)
                .quantity(5)
                .entryDate(OffsetDateTime.now())
                .build();
        stockEntryRepo.save(entry);

        // Update stock entry
        entry.setQuantity(25);
        entry.setEntryDate(OffsetDateTime.now().plusDays(1));
        StockEntry updated = stockEntryRepo.save(entry);

        assertThat(updated.getQuantity()).isEqualTo(25);
        assertThat(updated.getEntryDate()).isAfter(OffsetDateTime.now());
    }

    @Test
    void delete_shouldWorkWithRealMySQL() {
        // Create dependencies
        Product product = Product.builder()
                .name("Delete Product")
                .description("Delete Description")
                .price(new java.math.BigDecimal("30.00"))
                .quantityInStock(15)
                .build();
        productRepo.save(product);

        Supplier supplier = Supplier.builder()
                .name("Delete Supplier")
                .contactInfo("delete@supplier.com")
                .build();
        supplierRepo.save(supplier);

        // Create stock entry
        StockEntry entry = StockEntry.builder()
                .product(product)
                .supplier(supplier)
                .quantity(8)
                .entryDate(OffsetDateTime.now())
                .build();
        stockEntryRepo.save(entry);

        Long entryId = entry.getId();
        assertThat(stockEntryRepo.findById(entryId)).isPresent();

        // Delete stock entry
        stockEntryRepo.delete(entry);

        // Verify deletion
        assertThat(stockEntryRepo.findById(entryId)).isEmpty();
    }

    @Test
    void findAll_shouldWorkWithRealMySQL() {
        // Create dependencies
        Product product1 = Product.builder()
                .name("Product One")
                .description("Product One Description")
                .price(new java.math.BigDecimal("40.00"))
                .quantityInStock(20)
                .build();
        Product product2 = Product.builder()
                .name("Product Two")
                .description("Product Two Description")
                .price(new java.math.BigDecimal("60.00"))
                .quantityInStock(30)
                .build();
        productRepo.save(product1);
        productRepo.save(product2);

        Supplier supplier = Supplier.builder()
                .name("Multi Supplier")
                .contactInfo("multi@supplier.com")
                .build();
        supplierRepo.save(supplier);

        // Create multiple stock entries
        for (int i = 1; i <= 5; i++) {
            StockEntry entry = StockEntry.builder()
                    .product(i % 2 == 0 ? product2 : product1)
                    .supplier(supplier)
                    .quantity(i * 5)
                    .entryDate(OffsetDateTime.now().plusDays(i))
                    .build();
            stockEntryRepo.save(entry);
        }

        // Find all stock entries
        var allEntries = stockEntryRepo.findAll();
        assertThat(allEntries).hasSize(5);

        // Verify all entries have relationships
        for (StockEntry entry : allEntries) {
            assertThat(entry.getProduct()).isNotNull();
            assertThat(entry.getSupplier()).isNotNull();
            assertThat(entry.getQuantity()).isPositive();
            assertThat(entry.getEntryDate()).isNotNull();
        }
    }
}
