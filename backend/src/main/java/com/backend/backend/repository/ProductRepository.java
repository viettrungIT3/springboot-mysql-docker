package com.backend.backend.repository;

import com.backend.backend.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    boolean existsByName(String name);
    Optional<Product> findByName(String name);
    Optional<Product> findBySlug(String slug);
    boolean existsBySlug(String slug);
}

