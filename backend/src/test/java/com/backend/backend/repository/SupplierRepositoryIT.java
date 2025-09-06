package com.backend.backend.repository;

import com.backend.backend.entity.Supplier;
import com.backend.backend.support.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;

import static org.assertj.core.api.Assertions.assertThat;

class SupplierRepositoryIT extends IntegrationTestBase {

    @Autowired
    private SupplierRepository repo;

    @Test
    void save_find_list_shouldWorkWithRealMySQL() {
        // save
        Supplier s1 = Supplier.builder()
                .name("Công ty TNHH ABC")
                .contactInfo("email: contact@abc.com, phone: 0123456789, address: 123 Đường ABC, TP.HCM")
                .build();
        Supplier s2 = Supplier.builder()
                .name("Nhà cung cấp XYZ")
                .contactInfo("email: info@xyz.com, phone: 0987654321, address: 456 Đường XYZ, Hà Nội")
                .build();
        repo.save(s1);
        repo.save(s2);

        // findById
        Supplier found = repo.findById(s1.getId()).orElseThrow();
        assertThat(found.getName()).isEqualTo("Công ty TNHH ABC");

        // pagination + sort
        Page<Supplier> page = repo.findAll(PageRequest.of(0, 10, Sort.by("id").descending()));
        assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(2);

        // findAll
        var allSuppliers = repo.findAll();
        assertThat(allSuppliers).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void update_shouldWorkWithRealMySQL() {
        // Create supplier
        Supplier supplier = Supplier.builder()
                .name("Supplier Original")
                .contactInfo("original@example.com")
                .build();
        repo.save(supplier);

        // Update supplier
        supplier.setName("Supplier Updated");
        supplier.setContactInfo("updated@example.com");
        Supplier updated = repo.save(supplier);

        assertThat(updated.getName()).isEqualTo("Supplier Updated");
        assertThat(updated.getContactInfo()).isEqualTo("updated@example.com");
    }

    @Test
    void delete_shouldWorkWithRealMySQL() {
        // Create supplier
        Supplier supplier = Supplier.builder()
                .name("Supplier To Delete")
                .contactInfo("delete@example.com")
                .build();
        repo.save(supplier);

        Long supplierId = supplier.getId();
        assertThat(repo.findById(supplierId)).isPresent();

        // Delete supplier
        repo.delete(supplier);

        // Verify deletion
        assertThat(repo.findById(supplierId)).isEmpty();
    }
}
