package com.backend.backend.repository;

import com.backend.backend.entity.Product;
import com.backend.backend.support.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ProductRepositoryIT extends IntegrationTestBase {

    @Autowired
    private ProductRepository repo;

    @Test
    void save_find_list_shouldWorkWithRealMySQL() {
        // save
        Product p1 = Product.builder()
                .name("iPhone 15")
                .price(new BigDecimal("1200.00"))
                .quantityInStock(5)
                .build();
        Product p2 = Product.builder()
                .name("MacBook Air")
                .price(new BigDecimal("1500.00"))
                .quantityInStock(2)
                .build();
        repo.save(p1);
        repo.save(p2);

        // findById
        Product found = repo.findById(p1.getId()).orElseThrow();
        assertThat(found.getName()).isEqualTo("iPhone 15");

        // pagination + sort
        Page<Product> page = repo.findAll(PageRequest.of(0, 10, Sort.by("id").descending()));
        assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(2);

        // search (Day 5)
        Page<Product> search = repo.findByNameContainingIgnoreCase("iphone", PageRequest.of(0, 10));
        assertThat(search.getContent()).extracting("name").anyMatch(n -> n.toString().toLowerCase().contains("iphone"));
    }
}
