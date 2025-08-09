package com.backend.backend.controller;

import com.backend.backend.entity.Customer;
import com.backend.backend.entity.Order;
import com.backend.backend.repository.CustomerRepository;
import com.backend.backend.repository.OrderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;

    public OrderController(OrderRepository orderRepository, CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
    }

    @GetMapping
    public List<Order> list() { return orderRepository.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Order> get(@PathVariable Long id) {
        return orderRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Order> create(@RequestBody Order o) {
        if (o.getCustomer() != null && o.getCustomer().getId() != null) {
            Customer customer = customerRepository.findById(o.getCustomer().getId()).orElse(null);
            o.setCustomer(customer);
        }
        Order saved = orderRepository.save(o);
        return ResponseEntity.created(URI.create("/api/orders/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> update(@PathVariable Long id, @RequestBody Order o) {
        return orderRepository.findById(id).map(existing -> {
            if (o.getCustomer() != null && o.getCustomer().getId() != null) {
                Customer customer = customerRepository.findById(o.getCustomer().getId()).orElse(null);
                existing.setCustomer(customer);
            }
            existing.setOrderDate(o.getOrderDate());
            existing.setTotalAmount(o.getTotalAmount());
            existing.getItems().clear();
            if (o.getItems() != null) {
                o.getItems().forEach(item -> {
                    item.setOrder(existing);
                    existing.getItems().add(item);
                });
            }
            return ResponseEntity.ok(orderRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!orderRepository.existsById(id)) return ResponseEntity.notFound().build();
        orderRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

