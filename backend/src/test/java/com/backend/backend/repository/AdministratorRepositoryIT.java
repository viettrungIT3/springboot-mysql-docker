package com.backend.backend.repository;

import com.backend.backend.entity.Administrator;
import com.backend.backend.support.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;

import static org.assertj.core.api.Assertions.assertThat;

class AdministratorRepositoryIT extends IntegrationTestBase {

    @Autowired
    private AdministratorRepository repo;

    @Test
    void save_find_list_shouldWorkWithRealMySQL() {
        // save
        Administrator admin1 = Administrator.builder()
                .username("admin1")
                .password("password123")
                .email("admin1@example.com")
                .fullName("Administrator One")
                .build();
        Administrator admin2 = Administrator.builder()
                .username("admin2")
                .password("password456")
                .email("admin2@example.com")
                .fullName("Administrator Two")
                .build();
        repo.save(admin1);
        repo.save(admin2);

        // findById
        Administrator found = repo.findById(admin1.getId()).orElseThrow();
        assertThat(found.getUsername()).isEqualTo("admin1");

        // pagination + sort
        Page<Administrator> page = repo.findAll(PageRequest.of(0, 10, Sort.by("id").descending()));
        assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void findByUsername_shouldWorkWithRealMySQL() {
        // Create administrator
        Administrator admin = Administrator.builder()
                .username("testuser")
                .password("password123")
                .email("testuser@example.com")
                .fullName("Test User")
                .build();
        repo.save(admin);

        // Find by username
        var found = repo.findByUsername("testuser");
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("testuser@example.com");

        // Find non-existent username
        var notFound = repo.findByUsername("nonexistent");
        assertThat(notFound).isEmpty();
    }

    @Test
    void existsByUsername_shouldWorkWithRealMySQL() {
        // Create administrator
        Administrator admin = Administrator.builder()
                .username("existinguser")
                .password("password123")
                .email("existinguser@example.com")
                .fullName("Existing User")
                .build();
        repo.save(admin);

        // Check existence
        assertThat(repo.existsByUsername("existinguser")).isTrue();
        assertThat(repo.existsByUsername("nonexistent")).isFalse();
    }

    @Test
    void existsByEmail_shouldWorkWithRealMySQL() {
        // Create administrator
        Administrator admin = Administrator.builder()
                .username("emailtest")
                .password("password123")
                .email("emailtest@example.com")
                .fullName("Email Test User")
                .build();
        repo.save(admin);

        // Check existence
        assertThat(repo.existsByEmail("emailtest@example.com")).isTrue();
        assertThat(repo.existsByEmail("nonexistent@example.com")).isFalse();
    }

    @Test
    void uniqueConstraints_shouldWorkWithRealMySQL() {
        // Create first administrator
        Administrator admin1 = Administrator.builder()
                .username("uniqueuser")
                .password("password123")
                .email("unique@example.com")
                .fullName("Unique User")
                .build();
        repo.save(admin1);

        // Try to create second administrator with same username
        Administrator admin2 = Administrator.builder()
                .username("uniqueuser") // Same username
                .password("password456")
                .email("unique2@example.com")
                .fullName("Unique User 2")
                .build();

        // This should fail due to unique constraint
        try {
            repo.save(admin2);
            // If we get here, the constraint didn't work
            assertThat(false).as("Unique constraint should have prevented duplicate username").isTrue();
        } catch (Exception e) {
            // Expected - unique constraint violation
            assertThat(e.getMessage()).contains("Duplicate entry");
        }
    }
}
