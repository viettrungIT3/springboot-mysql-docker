package com.backend.backend.repository;

import com.backend.backend.entity.*;
import com.backend.backend.support.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class OrderItemRepositoryIT extends IntegrationTestBase {

    @Autowired
    private OrderItemRepository orderItemRepo;
    @Autowired
    private OrderRepository orderRepo;
    @Autowired
    private CustomerRepository customerRepo;
    @Autowired
    private ProductRepository productRepo;

    @Test
    void save_find_list_shouldWorkWithRealMySQL() {
        // Create dependencies
        Customer customer = Customer.builder()
                .name("Test Customer")
                .contactInfo("test@example.com")
                .build();
        customerRepo.save(customer);

        Product product = Product.builder()
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("50.00"))
                .quantityInStock(100)
                .build();
        productRepo.save(product);

        Order order = Order.builder()
                .customer(customer)
                .orderDate(OffsetDateTime.now())
                .totalAmount(new BigDecimal("100.00"))
                .build();
        orderRepo.save(order);

        // Create order items
        OrderItem item1 = OrderItem.builder()
                .order(order)
                .product(product)
                .quantity(2)
                .price(new BigDecimal("50.00"))
                .build();
        OrderItem item2 = OrderItem.builder()
                .order(order)
                .product(product)
                .quantity(1)
                .price(new BigDecimal("50.00"))
                .build();
        orderItemRepo.save(item1);
        orderItemRepo.save(item2);

        // findById
        OrderItem found = orderItemRepo.findById(item1.getId()).orElseThrow();
        assertThat(found.getQuantity()).isEqualTo(2);
        assertThat(found.getPrice()).isEqualTo(new BigDecimal("50.00"));

        // pagination + sort
        Page<OrderItem> page = orderItemRepo.findAll(PageRequest.of(0, 10, Sort.by("id").descending()));
        assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void relationships_shouldWorkWithRealMySQL() {
        // Create dependencies
        Customer customer = Customer.builder()
                .name("Relationship Customer")
                .contactInfo("relationship@example.com")
                .build();
        customerRepo.save(customer);

        Product product = Product.builder()
                .name("Relationship Product")
                .description("Relationship Description")
                .price(new BigDecimal("75.00"))
                .quantityInStock(50)
                .build();
        productRepo.save(product);

        Order order = Order.builder()
                .customer(customer)
                .orderDate(OffsetDateTime.now())
                .totalAmount(new BigDecimal("150.00"))
                .build();
        orderRepo.save(order);

        // Create order item
        OrderItem item = OrderItem.builder()
                .order(order)
                .product(product)
                .quantity(2)
                .price(new BigDecimal("75.00"))
                .build();
        orderItemRepo.save(item);

        // Verify relationships
        OrderItem found = orderItemRepo.findById(item.getId()).orElseThrow();
        assertThat(found.getOrder().getId()).isEqualTo(order.getId());
        assertThat(found.getProduct().getId()).isEqualTo(product.getId());
        assertThat(found.getOrder().getCustomer().getId()).isEqualTo(customer.getId());
    }

    @Test
    void update_shouldWorkWithRealMySQL() {
        // Create dependencies
        Customer customer = Customer.builder()
                .name("Update Customer")
                .contactInfo("update@example.com")
                .build();
        customerRepo.save(customer);

        Product product = Product.builder()
                .name("Update Product")
                .description("Update Description")
                .price(new BigDecimal("100.00"))
                .quantityInStock(25)
                .build();
        productRepo.save(product);

        Order order = Order.builder()
                .customer(customer)
                .orderDate(OffsetDateTime.now())
                .totalAmount(new BigDecimal("200.00"))
                .build();
        orderRepo.save(order);

        // Create order item
        OrderItem item = OrderItem.builder()
                .order(order)
                .product(product)
                .quantity(1)
                .price(new BigDecimal("100.00"))
                .build();
        orderItemRepo.save(item);

        // Update order item
        item.setQuantity(3);
        item.setPrice(new BigDecimal("90.00"));
        OrderItem updated = orderItemRepo.save(item);

        assertThat(updated.getQuantity()).isEqualTo(3);
        assertThat(updated.getPrice()).isEqualTo(new BigDecimal("90.00"));
    }

    @Test
    void delete_shouldWorkWithRealMySQL() {
        // Create dependencies
        Customer customer = Customer.builder()
                .name("Delete Customer")
                .contactInfo("delete@example.com")
                .build();
        customerRepo.save(customer);

        Product product = Product.builder()
                .name("Delete Product")
                .description("Delete Description")
                .price(new BigDecimal("25.00"))
                .quantityInStock(10)
                .build();
        productRepo.save(product);

        Order order = Order.builder()
                .customer(customer)
                .orderDate(OffsetDateTime.now())
                .totalAmount(new BigDecimal("50.00"))
                .build();
        orderRepo.save(order);

        // Create order item
        OrderItem item = OrderItem.builder()
                .order(order)
                .product(product)
                .quantity(2)
                .price(new BigDecimal("25.00"))
                .build();
        orderItemRepo.save(item);

        Long itemId = item.getId();
        assertThat(orderItemRepo.findById(itemId)).isPresent();

        // Delete order item
        orderItemRepo.delete(item);

        // Verify deletion
        assertThat(orderItemRepo.findById(itemId)).isEmpty();
    }

    @Test
    void cascadeDelete_shouldWorkWithRealMySQL() {
        // Create dependencies
        Customer customer = Customer.builder()
                .name("Cascade Customer")
                .contactInfo("cascade@example.com")
                .build();
        customerRepo.save(customer);

        Product product = Product.builder()
                .name("Cascade Product")
                .description("Cascade Description")
                .price(new BigDecimal("30.00"))
                .quantityInStock(20)
                .build();
        productRepo.save(product);

        Order order = Order.builder()
                .customer(customer)
                .orderDate(OffsetDateTime.now())
                .totalAmount(new BigDecimal("60.00"))
                .build();
        orderRepo.save(order);

        // Create order item
        OrderItem item = OrderItem.builder()
                .order(order)
                .product(product)
                .quantity(2)
                .price(new BigDecimal("30.00"))
                .build();
        orderItemRepo.save(item);

        Long itemId = item.getId();
        Long orderId = order.getId();

        // Delete order (should cascade delete order items)
        orderRepo.delete(order);

        // Verify order item is deleted
        assertThat(orderItemRepo.findById(itemId)).isEmpty();
        assertThat(orderRepo.findById(orderId)).isEmpty();
    }
}
