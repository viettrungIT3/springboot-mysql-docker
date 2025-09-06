package com.backend.backend.controller;

import com.backend.backend.dto.common.PageResponse;
import com.backend.backend.dto.customer.CustomerCreateRequest;
import com.backend.backend.dto.customer.CustomerResponse;
import com.backend.backend.dto.customer.CustomerUpdateRequest;
import com.backend.backend.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Customers")
@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerCreateRequest request) {
        return ResponseEntity.ok(customerService.create(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CustomerResponse> update(@PathVariable Long id,
            @Valid @RequestBody CustomerUpdateRequest request) {
        return ResponseEntity.ok(customerService.update(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getById(id));
    }

    @Operation(summary = "Get customer by slug", description = "Lấy thông tin chi tiết của một khách hàng bằng slug (SEO-friendly)", responses = {
            @ApiResponse(responseCode = "200", description = "Lấy thông tin thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy khách hàng")
    })
    @GetMapping("/slug/{slug}")
    public ResponseEntity<CustomerResponse> getBySlug(
            @Parameter(description = "Slug của khách hàng", example = "alice-smith") @PathVariable String slug) {
        return ResponseEntity.ok(customerService.getBySlug(slug));
    }

    @Operation(summary = "Danh sách khách hàng với pagination, sorting, và search")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công")
    })
    @GetMapping
    public ResponseEntity<PageResponse<CustomerResponse>> list(
            @Parameter(description = "Số trang, bắt đầu từ 0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Kích thước trang") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Định dạng sort: field,asc|desc (mặc định id,desc)") @RequestParam(defaultValue = "id,desc") String sort,
            @Parameter(description = "Từ khóa tìm kiếm (theo tên khách hàng)") @RequestParam(required = false) String search) {
        return ResponseEntity.ok(customerService.list(page, size, sort, search));
    }

    @Operation(summary = "Delete customer", description = "Xóa một khách hàng khỏi hệ thống (soft delete - đánh dấu deleted_at, dữ liệu vẫn còn trong DB)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Xóa thành công (soft delete)"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy khách hàng")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
