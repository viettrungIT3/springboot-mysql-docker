package com.backend.backend.repository;

import com.backend.backend.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Page<Customer> findByNameContainingIgnoreCase(String name, Pageable pageable);
    boolean existsByName(String name);
    Optional<Customer> findByName(String name);
    Optional<Customer> findBySlug(String slug);
    boolean existsBySlug(String slug);
}
