package com.backend.backend.controller;

import com.backend.backend.dto.SupplierDTO;
import com.backend.backend.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/suppliers")
public class SupplierController {
    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    public ResponseEntity<List<SupplierDTO>> list() {
        List<SupplierDTO> suppliers = supplierService.findAll();
        return ResponseEntity.ok(suppliers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierDTO> get(@PathVariable Long id) {
        SupplierDTO supplier = supplierService.findById(id);
        return ResponseEntity.ok(supplier);
    }

    @PostMapping
    public ResponseEntity<SupplierDTO> create(@Valid @RequestBody SupplierDTO dto) {
        SupplierDTO savedSupplier = supplierService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(URI.create("/api/v1/suppliers/" + savedSupplier.getId()))
                .body(savedSupplier);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupplierDTO> update(@PathVariable Long id, @Valid @RequestBody SupplierDTO dto) {
        SupplierDTO updatedSupplier = supplierService.update(id, dto);
        return ResponseEntity.ok(updatedSupplier);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        supplierService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

