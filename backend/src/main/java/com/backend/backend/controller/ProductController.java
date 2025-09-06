package com.backend.backend.controller;

import com.backend.backend.api.ApiError;
import com.backend.backend.dto.common.PageResponse;
import com.backend.backend.dto.product.ProductCreateRequest;
import com.backend.backend.dto.product.ProductResponse;
import com.backend.backend.dto.product.ProductUpdateRequest;
import com.backend.backend.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Products")
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Create product", description = "Tạo sản phẩm mới trong hệ thống", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductCreateRequest.class), examples = @ExampleObject(name = "Basic Product", value = """
            {
              "name": "iPhone 15 Pro",
              "description": "Latest iPhone model with Pro features",
              "price": 1299.99,
              "quantityInStock": 50
            }
            """))), responses = {
            @ApiResponse(responseCode = "200", description = "Tạo sản phẩm thành công", content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductCreateRequest request) {
        return ResponseEntity.ok(productService.create(request));
    }

    @Operation(summary = "Update product", description = "Cập nhật thông tin sản phẩm", responses = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công", content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy sản phẩm", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponse> update(
            @Parameter(description = "ID của sản phẩm", example = "1") @PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequest request) {
        return ResponseEntity.ok(productService.update(id, request));
    }

    @Operation(summary = "Get product by ID", description = "Lấy thông tin chi tiết của một sản phẩm", responses = {
            @ApiResponse(responseCode = "200", description = "Lấy thông tin thành công", content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy sản phẩm", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(
            @Parameter(description = "ID của sản phẩm", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @Operation(summary = "Get product by slug", description = "Lấy thông tin chi tiết của một sản phẩm bằng slug (SEO-friendly)", responses = {
            @ApiResponse(responseCode = "200", description = "Lấy thông tin thành công", content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy sản phẩm", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ProductResponse> getBySlug(
            @Parameter(description = "Slug của sản phẩm", example = "iphone-15-pro") @PathVariable String slug) {
        return ResponseEntity.ok(productService.getBySlug(slug));
    }

    @Operation(summary = "List products with pagination, sorting, search", description = "Lấy danh sách sản phẩm với hỗ trợ phân trang, sắp xếp và tìm kiếm", parameters = {
            @Parameter(name = "page", description = "Số trang (bắt đầu từ 0)", example = "0"),
            @Parameter(name = "size", description = "Kích thước trang", example = "10"),
            @Parameter(name = "sort", description = "Định dạng sort: field,asc|desc", example = "name,asc"),
            @Parameter(name = "search", description = "Từ khóa tìm kiếm theo tên sản phẩm", example = "iphone")
    }, responses = {
            @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công", content = @Content(schema = @Schema(implementation = PageResponse.class)))
    })
    @GetMapping
    public ResponseEntity<PageResponse<ProductResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String sort,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(productService.list(page, size, sort, search));
    }

    @Operation(summary = "Delete product", description = "Xóa một sản phẩm khỏi hệ thống", security = {
            @SecurityRequirement(name = "bearer-jwt") }, responses = {
                    @ApiResponse(responseCode = "204", description = "Xóa thành công"),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy sản phẩm", content = @Content(schema = @Schema(implementation = ApiError.class)))
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID của sản phẩm", example = "1") @PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
