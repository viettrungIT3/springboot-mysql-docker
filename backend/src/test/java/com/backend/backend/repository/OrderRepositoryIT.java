package com.backend.backend.repository;

import com.backend.backend.entity.Customer;
import com.backend.backend.entity.Order;
import com.backend.backend.support.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class OrderRepositoryIT extends IntegrationTestBase {

    @Autowired
    private OrderRepository orderRepo;
    @Autowired
    private CustomerRepository customerRepo;

    @Test
    void save_find_list_shouldWorkWithRealMySQL() {
        // Create customer first
        Customer customer = Customer.builder()
                .name("Test Customer")
                .contactInfo("test@example.com")
                .build();
        customerRepo.save(customer);

        // Create orders
        Order order1 = Order.builder()
                .customer(customer)
                .orderDate(OffsetDateTime.now())
                .totalAmount(new BigDecimal("100.00"))
                .build();
        Order order2 = Order.builder()
                .customer(customer)
                .orderDate(OffsetDateTime.now())
                .totalAmount(new BigDecimal("200.00"))
                .build();
        orderRepo.save(order1);
        orderRepo.save(order2);

        // findById
        Order found = orderRepo.findById(order1.getId()).orElseThrow();
        assertThat(found.getTotalAmount()).isEqualTo(new BigDecimal("100.00"));

        // pagination + sort
        Page<Order> page = orderRepo.findAll(PageRequest.of(0, 10, Sort.by("id").descending()));
        assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void findByCustomerId_shouldWorkWithRealMySQL() {
        // Create customers
        Customer customer1 = Customer.builder()
                .name("Customer One")
                .contactInfo("customer1@example.com")
                .build();
        Customer customer2 = Customer.builder()
                .name("Customer Two")
                .contactInfo("customer2@example.com")
                .build();
        customerRepo.save(customer1);
        customerRepo.save(customer2);

        // Create orders for customer1
        Order order1 = Order.builder()
                .customer(customer1)
                .orderDate(OffsetDateTime.now())
                .totalAmount(new BigDecimal("100.00"))
                .build();
        Order order2 = Order.builder()
                .customer(customer1)
                .orderDate(OffsetDateTime.now())
                .totalAmount(new BigDecimal("150.00"))
                .build();
        orderRepo.save(order1);
        orderRepo.save(order2);

        // Create order for customer2
        Order order3 = Order.builder()
                .customer(customer2)
                .orderDate(OffsetDateTime.now())
                .totalAmount(new BigDecimal("200.00"))
                .build();
        orderRepo.save(order3);

        // Find orders by customer1
        var customer1Orders = orderRepo.findByCustomerId(customer1.getId());
        assertThat(customer1Orders).hasSize(2);
        assertThat(customer1Orders).extracting("totalAmount")
                .containsExactlyInAnyOrder(new BigDecimal("100.00"), new BigDecimal("150.00"));

        // Find orders by customer2
        var customer2Orders = orderRepo.findByCustomerId(customer2.getId());
        assertThat(customer2Orders).hasSize(1);
        assertThat(customer2Orders.get(0).getTotalAmount()).isEqualTo(new BigDecimal("200.00"));

        // Find orders for non-existent customer
        var nonExistentOrders = orderRepo.findByCustomerId(999L);
        assertThat(nonExistentOrders).isEmpty();
    }

    @Test
    void update_shouldWorkWithRealMySQL() {
        // Create customer and order
        Customer customer = Customer.builder()
                .name("Update Customer")
                .contactInfo("update@example.com")
                .build();
        customerRepo.save(customer);

        Order order = Order.builder()
                .customer(customer)
                .orderDate(OffsetDateTime.now())
                .totalAmount(new BigDecimal("100.00"))
                .build();
        orderRepo.save(order);

        // Update order
        order.setTotalAmount(new BigDecimal("150.00"));
        Order updated = orderRepo.save(order);

        assertThat(updated.getTotalAmount()).isEqualTo(new BigDecimal("150.00"));
    }

    @Test
    void delete_shouldWorkWithRealMySQL() {
        // Create customer and order
        Customer customer = Customer.builder()
                .name("Delete Customer")
                .contactInfo("delete@example.com")
                .build();
        customerRepo.save(customer);

        Order order = Order.builder()
                .customer(customer)
                .orderDate(OffsetDateTime.now())
                .totalAmount(new BigDecimal("100.00"))
                .build();
        orderRepo.save(order);

        Long orderId = order.getId();
        assertThat(orderRepo.findById(orderId)).isPresent();

        // Delete order
        orderRepo.delete(order);

        // Verify deletion
        assertThat(orderRepo.findById(orderId)).isEmpty();
    }
}
