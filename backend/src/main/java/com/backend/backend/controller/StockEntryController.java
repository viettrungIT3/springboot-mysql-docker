package com.backend.backend.controller;

import com.backend.backend.dto.common.PageResponse;
import com.backend.backend.dto.stockentry.StockEntryCreateRequest;
import com.backend.backend.dto.stockentry.StockEntryResponse;
import com.backend.backend.dto.stockentry.StockEntryUpdateRequest;
import com.backend.backend.service.StockEntryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stock-entries")
public class StockEntryController {

    private final StockEntryService stockEntryService;

    public StockEntryController(StockEntryService stockEntryService) {
        this.stockEntryService = stockEntryService;
    }

    @PostMapping
    public ResponseEntity<StockEntryResponse> create(@Valid @RequestBody StockEntryCreateRequest request) {
        return ResponseEntity.ok(stockEntryService.create(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<StockEntryResponse> update(@PathVariable Long id,
            @Valid @RequestBody StockEntryUpdateRequest request) {
        return ResponseEntity.ok(stockEntryService.update(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockEntryResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(stockEntryService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<StockEntryResponse>> list() {
        return ResponseEntity.ok(stockEntryService.findAll());
    }

    @GetMapping("/page")
    public ResponseEntity<PageResponse<StockEntryResponse>> listWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort) {
        return ResponseEntity.ok(stockEntryService.list(page, size, sort));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        stockEntryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
