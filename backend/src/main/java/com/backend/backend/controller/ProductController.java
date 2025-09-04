package com.backend.backend.controller;

import com.backend.backend.dto.common.PageResponse;
import com.backend.backend.dto.product.ProductCreateRequest;
import com.backend.backend.dto.product.ProductResponse;
import com.backend.backend.dto.product.ProductUpdateRequest;
import com.backend.backend.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product", description = "API quản lý sản phẩm")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductCreateRequest request) {
        return ResponseEntity.ok(productService.create(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequest request) {
        return ResponseEntity.ok(productService.update(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @Operation(summary = "Danh sách sản phẩm với pagination, sorting, và search")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công")
    })
    @GetMapping
    public ResponseEntity<PageResponse<ProductResponse>> list(
            @Parameter(description = "Số trang, bắt đầu từ 0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Kích thước trang") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Định dạng sort: field,asc|desc (mặc định id,desc)") @RequestParam(defaultValue = "id,desc") String sort,
            @Parameter(description = "Từ khóa tìm kiếm (theo tên sản phẩm)") @RequestParam(required = false) String search) {
        return ResponseEntity.ok(productService.list(page, size, sort, search));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
