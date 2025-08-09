package com.backend.backend.controller;

import com.backend.backend.entity.StockEntry;
import com.backend.backend.repository.StockEntryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/stock-entries")
public class StockEntryController {
    private final StockEntryRepository repository;

    public StockEntryController(StockEntryRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<StockEntry> list() { return repository.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<StockEntry> get(@PathVariable Long id) {
        return repository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<StockEntry> create(@RequestBody StockEntry e) {
        StockEntry saved = repository.save(e);
        return ResponseEntity.created(URI.create("/api/stock-entries/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StockEntry> update(@PathVariable Long id, @RequestBody StockEntry e) {
        return repository.findById(id).map(existing -> {
            existing.setProduct(e.getProduct());
            existing.setSupplier(e.getSupplier());
            existing.setQuantity(e.getQuantity());
            existing.setEntryDate(e.getEntryDate());
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

