package com.backend.backend.controller;

import com.backend.backend.dto.common.PageResponse;
import com.backend.backend.dto.orderitem.OrderItemCreateRequest;
import com.backend.backend.dto.orderitem.OrderItemResponse;
import com.backend.backend.dto.orderitem.OrderItemUpdateRequest;
import com.backend.backend.service.OrderItemService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Order Items")
@RestController
@RequestMapping("/api/v1/order-items")
public class OrderItemController {

    private final OrderItemService orderItemService;

    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @PostMapping
    public ResponseEntity<OrderItemResponse> create(@Valid @RequestBody OrderItemCreateRequest request) {
        return ResponseEntity.ok(orderItemService.create(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OrderItemResponse> update(@PathVariable Long id,
            @Valid @RequestBody OrderItemUpdateRequest request) {
        return ResponseEntity.ok(orderItemService.update(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderItemResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderItemService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<OrderItemResponse>> list() {
        return ResponseEntity.ok(orderItemService.findAll());
    }

    @GetMapping("/page")
    public ResponseEntity<PageResponse<OrderItemResponse>> listWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort) {
        return ResponseEntity.ok(orderItemService.list(page, size, sort));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
