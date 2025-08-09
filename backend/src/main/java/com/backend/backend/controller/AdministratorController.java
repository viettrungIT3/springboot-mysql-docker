package com.backend.backend.controller;

import com.backend.backend.entity.Administrator;
import com.backend.backend.repository.AdministratorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/administrators")
public class AdministratorController {
    private final AdministratorRepository repository;

    public AdministratorController(AdministratorRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Administrator> list() { return repository.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Administrator> get(@PathVariable Long id) {
        return repository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Administrator> create(@RequestBody Administrator a) {
        Administrator saved = repository.save(a);
        return ResponseEntity.created(URI.create("/api/administrators/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Administrator> update(@PathVariable Long id, @RequestBody Administrator a) {
        return repository.findById(id).map(existing -> {
            existing.setUsername(a.getUsername());
            existing.setPassword(a.getPassword());
            existing.setEmail(a.getEmail());
            existing.setFullName(a.getFullName());
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

