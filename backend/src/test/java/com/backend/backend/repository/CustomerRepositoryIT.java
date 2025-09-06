package com.backend.backend.repository;

import com.backend.backend.entity.Customer;
import com.backend.backend.support.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerRepositoryIT extends IntegrationTestBase {

    @Autowired
    private CustomerRepository repo;

    @Test
    void save_find_list_shouldWorkWithRealMySQL() {
        // save
        Customer c1 = Customer.builder()
                .name("Nguyễn Văn A")
                .contactInfo("email: nguyenvana@example.com, phone: 0123456789")
                .build();
        Customer c2 = Customer.builder()
                .name("Trần Thị B")
                .contactInfo("email: tranthib@example.com, phone: 0987654321")
                .build();
        repo.save(c1);
        repo.save(c2);

        // findById
        Customer found = repo.findById(c1.getId()).orElseThrow();
        assertThat(found.getName()).isEqualTo("Nguyễn Văn A");

        // pagination + sort
        Page<Customer> page = repo.findAll(PageRequest.of(0, 10, Sort.by("id").descending()));
        assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(2);

        // search (findByNameContainingIgnoreCase)
        Page<Customer> search = repo.findByNameContainingIgnoreCase("nguyễn", PageRequest.of(0, 10));
        assertThat(search.getContent()).extracting("name").anyMatch(n -> n.toString().toLowerCase().contains("nguyễn"));
    }

    @Test
    void search_shouldWorkWithCaseInsensitive() {
        // Create test data
        Customer customer1 = Customer.builder()
                .name("JOHN DOE")
                .contactInfo("john@example.com")
                .build();
        Customer customer2 = Customer.builder()
                .name("jane smith")
                .contactInfo("jane@example.com")
                .build();
        Customer customer3 = Customer.builder()
                .name("Bob Johnson")
                .contactInfo("bob@example.com")
                .build();

        repo.save(customer1);
        repo.save(customer2);
        repo.save(customer3);

        // Test case insensitive search
        Page<Customer> search1 = repo.findByNameContainingIgnoreCase("john", PageRequest.of(0, 10));
        assertThat(search1.getContent()).hasSize(2); // JOHN DOE and Bob Johnson

        Page<Customer> search2 = repo.findByNameContainingIgnoreCase("JANE", PageRequest.of(0, 10));
        assertThat(search2.getContent()).hasSize(1); // jane smith
    }
}
