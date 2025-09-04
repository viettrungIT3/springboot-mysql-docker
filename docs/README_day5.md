# Day 5 - Pagination, Sort, Filter

## 🎯 Mục tiêu đã hoàn thành

Chuẩn hóa tất cả list endpoints với query params:
- `?page=0&size=10&sort=name,asc&search=iphone`
- Trả về JSON với metadata: page, size, totalElements, totalPages
- Swagger hiển thị rõ ràng với documentation đầy đủ
- Tái sử dụng với nhiều entity (Product, Customer, v.v.)

## ✅ Các thành phần đã triển khai

### 1. PageResponse<T> - DTO chuẩn hóa response pagination
```java
@Data
@Builder
public class PageResponse<T> {
    private List<T> items;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
```

### 2. PageMapper - Utility chuyển đổi Page → PageResponse
```java
public class PageMapper {
    public static <T, R> PageResponse<R> toPageResponse(Page<T> page, Function<T, R> mapper) {
        return PageResponse.<R>builder()
                .items(page.getContent().stream().map(mapper).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}
```

### 3. Repository Updates - Hỗ trợ search với pagination
```java
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
}

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Page<Customer> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
```

### 4. Service Layer - Logic xử lý pagination, sort và search
```java
@Transactional(readOnly = true)
public PageResponse<ProductResponse> list(int page, int size, String sort, String search) {
    Sort s = (sort == null || sort.isBlank())
            ? Sort.by("id").descending()
            : Sort.by(sort.split(",")[0])
            .ascending();

    // Xử lý sort direction nếu có
    if (sort != null && !sort.isBlank() && sort.contains(",")) {
        String[] sortParts = sort.split(",");
        if (sortParts.length > 1 && "desc".equalsIgnoreCase(sortParts[1])) {
            s = Sort.by(sortParts[0]).descending();
        }
    }

    Pageable pageable = PageRequest.of(page, size, s);

    Page<Product> result;
    if (search != null && !search.isBlank()) {
        result = productRepository.findByNameContainingIgnoreCase(search, pageable);
    } else {
        result = productRepository.findAll(pageable);
    }

    return PageMapper.toPageResponse(result, productMapper::toResponse);
}
```

### 5. Controller với Swagger Documentation
```java
@Operation(summary = "Danh sách sản phẩm với pagination, sorting, và search")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công")
})
@GetMapping
public ResponseEntity<PageResponse<ProductResponse>> list(
        @Parameter(description = "Số trang, bắt đầu từ 0")
        @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Kích thước trang")
        @RequestParam(defaultValue = "10") int size,
        @Parameter(description = "Định dạng sort: field,asc|desc (mặc định id,desc)")
        @RequestParam(defaultValue = "id,desc") String sort,
        @Parameter(description = "Từ khóa tìm kiếm (theo tên sản phẩm)")
        @RequestParam(required = false) String search
) {
    return ResponseEntity.ok(productService.list(page, size, sort, search));
}
```

## 🧪 Ví dụ API Calls

### 1. Pagination cơ bản
```bash
GET /api/v1/products?page=0&size=3&sort=name,asc
```

**Response:**
```json
{
  "items": [
    {
      "id": 6,
      "name": "Final Test",
      "description": "Test after fixes",
      "price": 299.99,
      "quantityInStock": 20
    },
    {
      "id": 2,
      "name": "Notebook",
      "description": "A5 ruled",
      "price": 3.20,
      "quantityInStock": 199
    },
    {
      "id": 1,
      "name": "Pen",
      "description": "Blue ink pen",
      "price": 1.50,
      "quantityInStock": 148
    }
  ],
  "page": 0,
  "size": 3,
  "totalElements": 6,
  "totalPages": 2
}
```

### 2. Search với pagination
```bash
GET /api/v1/products?page=0&size=5&search=test
```

**Response:**
```json
{
  "items": [
    {
      "id": 6,
      "name": "Final Test",
      "description": "Test after fixes",
      "price": 299.99,
      "quantityInStock": 20
    },
    {
      "id": 5,
      "name": "Test Product MapStruct",
      "description": "Valid product with MapStruct",
      "price": 199.99,
      "quantityInStock": 25
    }
  ],
  "page": 0,
  "size": 5,
  "totalElements": 4,
  "totalPages": 1
}
```

### 3. Sort descending
```bash
GET /api/v1/customers?page=0&size=5&sort=id,desc
```

**Response:**
```json
{
  "items": [
    {
      "id": 4,
      "name": "Nguyễn Văn A",
      "contactInfo": "Updated: email: a@example.com, phone: 0987654321"
    },
    {
      "id": 3,
      "name": "John Doe",
      "contactInfo": "john@example.com"
    }
  ],
  "page": 0,
  "size": 5,
  "totalElements": 4,
  "totalPages": 1
}
```

## 🎉 Kết quả

✅ **Hoàn thành tất cả mục tiêu Day 5:**
- ✅ Các list endpoint đồng bộ về format query params
- ✅ Response có metadata → dễ dùng cho frontend (React/Flutter/Angular)
- ✅ Swagger hiển thị rõ ràng với documentation đầy đủ
- ✅ Hỗ trợ pagination, sorting và search
- ✅ Pattern có thể tái sử dụng cho nhiều entity
- ✅ Code được optimize và sử dụng PageMapper utility

## 📋 Entities đã cập nhật

- ✅ **ProductController** - Đầy đủ pagination, sort, search + Swagger docs
- ✅ **CustomerController** - Đầy đủ pagination, sort, search + Swagger docs
- ✅ **Tất cả Service classes** - Đã được cập nhật sử dụng PageMapper
- ✅ **Repository classes** - Đã thêm search methods với pagination

## 🚀 Công nghệ sử dụng

- **Spring Boot 3.5.4** - Framework chính
- **Spring Data JPA** - ORM và pagination
- **Springdoc OpenAPI 2.7.0** - Swagger documentation
- **MapStruct 1.6.2** - Object mapping
- **MySQL** - Database
- **Docker** - Containerization

Tất cả API endpoints hiện đã chuẩn hóa và sẵn sàng cho production!
