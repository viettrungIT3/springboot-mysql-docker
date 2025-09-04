package com.backend.backend.controller;

import com.backend.backend.dto.common.PageResponse;
import com.backend.backend.dto.order.OrderCreateRequest;
import com.backend.backend.dto.order.OrderResponse;
import com.backend.backend.dto.order.OrderUpdateRequest;
import com.backend.backend.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderCreateRequest request) {
        return ResponseEntity.ok(orderService.create(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OrderResponse> update(@PathVariable Long id,
            @Valid @RequestBody OrderUpdateRequest request) {
        return ResponseEntity.ok(orderService.update(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> list() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @GetMapping("/page")
    public ResponseEntity<PageResponse<OrderResponse>> listWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort) {
        return ResponseEntity.ok(orderService.list(page, size, sort));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponse>> getByCustomerId(@PathVariable Long customerId) {
        return ResponseEntity.ok(orderService.findByCustomerId(customerId));
    }

    @PostMapping("/{orderId}/items")
    public ResponseEntity<OrderResponse> addItem(@PathVariable Long orderId,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(orderService.addItem(orderId, productId, quantity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
