package com.backend.backend.controller;

import com.backend.backend.dto.administrator.AdministratorCreateRequest;
import com.backend.backend.dto.administrator.AdministratorResponse;
import com.backend.backend.dto.administrator.AdministratorUpdateRequest;
import com.backend.backend.dto.common.PageResponse;
import com.backend.backend.service.AdministratorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Administrators")
@RestController
@RequestMapping("/api/v1/administrators")
public class AdministratorController {

    private final AdministratorService administratorService;

    public AdministratorController(AdministratorService administratorService) {
        this.administratorService = administratorService;
    }

    @PostMapping
    public ResponseEntity<AdministratorResponse> create(@Valid @RequestBody AdministratorCreateRequest request) {
        return ResponseEntity.ok(administratorService.create(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AdministratorResponse> update(@PathVariable Long id,
            @Valid @RequestBody AdministratorUpdateRequest request) {
        return ResponseEntity.ok(administratorService.update(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdministratorResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(administratorService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<AdministratorResponse>> list() {
        return ResponseEntity.ok(administratorService.findAll());
    }

    @GetMapping("/page")
    public ResponseEntity<PageResponse<AdministratorResponse>> listWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort) {
        return ResponseEntity.ok(administratorService.list(page, size, sort));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        administratorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
