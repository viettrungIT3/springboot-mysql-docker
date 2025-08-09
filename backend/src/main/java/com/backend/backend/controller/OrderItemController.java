package com.backend.backend.controller;

import com.backend.backend.entity.OrderItem;
import com.backend.backend.repository.OrderItemRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/order-items")
public class OrderItemController {
    private final OrderItemRepository repository;

    public OrderItemController(OrderItemRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<OrderItem> list() { return repository.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<OrderItem> get(@PathVariable Long id) {
        return repository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<OrderItem> create(@RequestBody OrderItem e) {
        OrderItem saved = repository.save(e);
        return ResponseEntity.created(URI.create("/api/order-items/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderItem> update(@PathVariable Long id, @RequestBody OrderItem e) {
        return repository.findById(id).map(existing -> {
            existing.setOrder(e.getOrder());
            existing.setProduct(e.getProduct());
            existing.setQuantity(e.getQuantity());
            existing.setPrice(e.getPrice());
            return ResponseEntity.ok(repository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) return ResponseEntity.notFound().build();
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

