package com.backend.backend.repository;

import com.backend.backend.entity.StockEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockEntryRepository extends JpaRepository<StockEntry, Long> {}

