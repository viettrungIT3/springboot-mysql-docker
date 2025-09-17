package com.backend.backend.controller;

import com.backend.backend.dto.user.UserCreateRequest;
import com.backend.backend.dto.user.UserResponse;
import com.backend.backend.dto.user.UserUpdateRequest;
import com.backend.backend.dto.common.PageResponse;
import com.backend.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Users")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @Operation(summary = "Create administrator", security = @SecurityRequirement(name = "bearer-jwt"))
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.ok(userService.create(request));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update administrator", security = @SecurityRequirement(name = "bearer-jwt"))
    public ResponseEntity<UserResponse> update(@PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userService.update(id, request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get administrator by ID", security = @SecurityRequirement(name = "bearer-jwt"))
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @GetMapping
    @Operation(summary = "List all administrators", security = @SecurityRequirement(name = "bearer-jwt"))
    public ResponseEntity<List<UserResponse>> list() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/page")
    @Operation(summary = "List administrators with pagination", security = @SecurityRequirement(name = "bearer-jwt"))
    public ResponseEntity<PageResponse<UserResponse>> listWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort) {
        return ResponseEntity.ok(userService.list(page, size, sort));
    }

    @Operation(summary = "Delete administrator", description = "Xóa một quản trị viên khỏi hệ thống (soft delete - đánh dấu deleted_at, dữ liệu vẫn còn trong DB)", security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Xóa thành công (soft delete)"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy quản trị viên")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
