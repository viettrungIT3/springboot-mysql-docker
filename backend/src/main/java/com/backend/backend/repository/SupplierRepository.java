package com.backend.backend.repository;

import com.backend.backend.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    // Basic methods
    boolean existsByName(String name);
    Optional<Supplier> findByName(String name);
    
    // Business logic methods
    Optional<Supplier> findByNameIgnoreCase(String name);
    List<Supplier> findByNameContainingIgnoreCase(String name);
    List<Supplier> findByContactInfoContainingIgnoreCase(String contactInfo);
    List<Supplier> findByNameContainingIgnoreCaseAndContactInfoContainingIgnoreCase(String name, String contactInfo);
    long countByContactInfoIsNotNull();
}

