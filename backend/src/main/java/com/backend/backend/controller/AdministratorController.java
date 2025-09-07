package com.backend.backend.controller;

import com.backend.backend.dto.administrator.AdministratorCreateRequest;
import com.backend.backend.dto.administrator.AdministratorResponse;
import com.backend.backend.dto.administrator.AdministratorUpdateRequest;
import com.backend.backend.dto.common.PageResponse;
import com.backend.backend.service.AdministratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
    @Operation(summary = "Create administrator", security = @SecurityRequirement(name = "bearer-jwt"))
    public ResponseEntity<AdministratorResponse> create(@Valid @RequestBody AdministratorCreateRequest request) {
        return ResponseEntity.ok(administratorService.create(request));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update administrator", security = @SecurityRequirement(name = "bearer-jwt"))
    public ResponseEntity<AdministratorResponse> update(@PathVariable Long id,
            @Valid @RequestBody AdministratorUpdateRequest request) {
        return ResponseEntity.ok(administratorService.update(id, request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get administrator by ID", security = @SecurityRequirement(name = "bearer-jwt"))
    public ResponseEntity<AdministratorResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(administratorService.getById(id));
    }

    @GetMapping
    @Operation(summary = "List all administrators", security = @SecurityRequirement(name = "bearer-jwt"))
    public ResponseEntity<List<AdministratorResponse>> list() {
        return ResponseEntity.ok(administratorService.findAll());
    }

    @GetMapping("/page")
    @Operation(summary = "List administrators with pagination", security = @SecurityRequirement(name = "bearer-jwt"))
    public ResponseEntity<PageResponse<AdministratorResponse>> listWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort) {
        return ResponseEntity.ok(administratorService.list(page, size, sort));
    }

    @Operation(summary = "Delete administrator", description = "Xóa một quản trị viên khỏi hệ thống (soft delete - đánh dấu deleted_at, dữ liệu vẫn còn trong DB)", security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Xóa thành công (soft delete)"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy quản trị viên")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        administratorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
