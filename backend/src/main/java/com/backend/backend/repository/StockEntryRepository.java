package com.backend.backend.repository;

import com.backend.backend.entity.StockEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface StockEntryRepository extends JpaRepository<StockEntry, Long> {
    // Business logic methods
    List<StockEntry> findByProductId(Long productId);
    List<StockEntry> findBySupplierId(Long supplierId);
    List<StockEntry> findByProductIdAndSupplierId(Long productId, Long supplierId);
    List<StockEntry> findByEntryDateBetween(OffsetDateTime startDate, OffsetDateTime endDate);
    List<StockEntry> findByQuantityBetween(Integer minQuantity, Integer maxQuantity);
}
