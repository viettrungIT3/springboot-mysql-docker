# Day 13 — Soft Delete & Auditing 🗑️⏱️

## Mục tiêu
Thêm các cột audit (created_at, updated_at) cho tất cả entity và implement soft delete (deleted_at) thay vì xóa hẳn dữ liệu.

## Tính năng chính
- ✅ **Audit Fields**: Tự động fill `created_at`, `updated_at` cho tất cả entities
- ✅ **Soft Delete**: Đánh dấu `deleted_at` thay vì xóa thật dữ liệu
- ✅ **Auto Filtering**: Tất cả queries tự động bỏ qua records đã soft delete
- ✅ **Swagger Documentation**: Rõ ràng về behavior của DELETE endpoints

## Các thay đổi thực hiện

### 1. Migration Database (V4)
```sql
-- Thêm các cột audit cho tất cả bảng
ALTER TABLE products 
  ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  ADD COLUMN updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
  ADD COLUMN deleted_at TIMESTAMP NULL;

-- Tương tự cho: customers, orders, order_items, stock_entries, suppliers, administrators
```

### 2. Base Entity - AuditableEntity
```java
@MappedSuperclass
public abstract class AuditableEntity {
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Helper methods
    public boolean isDeleted() { return deletedAt != null; }
    public void markAsDeleted() { this.deletedAt = LocalDateTime.now(); }
    public void restore() { this.deletedAt = null; }
}
```

### 3. Cập nhật Entities
Tất cả entities giờ extend `AuditableEntity` và có `@SQLRestriction`:
```java
@Entity
@Table(name = "products")
@SQLRestriction("deleted_at IS NULL")
public class Product extends AuditableEntity {
    // ... existing fields
}
```

**Entities được cập nhật:**
- Product
- Customer  
- Order
- OrderItem
- StockEntry
- Supplier
- Administrator

### 4. Cập nhật Services
Thay đổi từ hard delete sang soft delete:
```java
@Transactional
public void delete(Long id) {
    Product entity = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + id));
    entity.markAsDeleted();
    productRepository.save(entity);
}
```

### 5. Cập nhật Controllers
Thêm Swagger documentation cho DELETE endpoints:
```java
@Operation(summary = "Delete product", 
           description = "Xóa một sản phẩm khỏi hệ thống (soft delete - đánh dấu deleted_at, dữ liệu vẫn còn trong DB)")
@ApiResponses(value = {
    @ApiResponse(responseCode = "204", description = "Xóa thành công (soft delete)"),
    @ApiResponse(responseCode = "404", description = "Không tìm thấy sản phẩm")
})
@DeleteMapping("/{id}")
public ResponseEntity<Void> delete(@PathVariable Long id) {
    productService.delete(id);
    return ResponseEntity.noContent().build();
}
```

## Cách hoạt động

### Audit Fields
- **created_at**: Tự động set khi tạo record (không thể update)
- **updated_at**: Tự động update khi có thay đổi
- **deleted_at**: NULL = active, có giá trị = soft deleted

### Soft Delete Flow
1. **DELETE API call** → Service method
2. **Service** → `entity.markAsDeleted()` → `repository.save(entity)`
3. **Database** → Record vẫn còn, `deleted_at` được set
4. **API queries** → `@SQLRestriction` tự động filter ra deleted records

### API Behavior
- **DELETE /api/v1/products/{id}** → 204 No Content (như trước)
- **GET /api/v1/products/{id}** (đã soft delete) → 404 Not Found
- **GET /api/v1/products** → Chỉ trả về active records

## Testing

### Test Case 1: Tạo và Soft Delete Product
```bash
# 1. Tạo product mới
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Soft Delete Test", "description": "Test", "price": 50.00, "quantityInStock": 5}'
# Response: {"id": 34, "name": "Soft Delete Test", ...}

# 2. Soft delete
curl -X DELETE http://localhost:8080/api/v1/products/34
# Response: 204 No Content

# 3. Kiểm tra không còn xuất hiện trong API
curl -X GET http://localhost:8080/api/v1/products/34
# Response: 404 Not Found

# 4. Kiểm tra database - record vẫn còn với deleted_at
mysql> SELECT * FROM products WHERE id = 34;
# deleted_at: 2025-09-06 09:50:48 (không NULL)
```

### Test Case 2: Audit Fields
```bash
# Tạo product mới và kiểm tra audit fields
mysql> SELECT id, name, created_at, updated_at, deleted_at FROM products WHERE id = 35;
# created_at: 2025-09-06 09:51:00
# updated_at: 2025-09-06 09:51:00  
# deleted_at: NULL
```

## Lợi ích

### 1. Data Integrity
- ✅ Không mất dữ liệu khi "xóa"
- ✅ Có thể restore dữ liệu nếu cần
- ✅ Audit trail đầy đủ

### 2. Business Logic
- ✅ Phù hợp với yêu cầu business (không xóa thật)
- ✅ Có thể phân tích dữ liệu đã "xóa"
- ✅ Compliance và audit requirements

### 3. Performance
- ✅ Không cần backup/restore phức tạp
- ✅ Queries vẫn nhanh với index trên deleted_at
- ✅ Ít overhead hơn hard delete + restore

## Các lưu ý kỹ thuật

### 1. @SQLRestriction vs @Where
- ✅ Sử dụng `@SQLRestriction` (Hibernate 6.3+)
- ❌ Tránh `@Where` (deprecated từ Hibernate 6.3)

### 2. Migration Strategy
- ✅ Thêm cột với DEFAULT CURRENT_TIMESTAMP
- ✅ Existing records được fill created_at tự động
- ✅ Không ảnh hưởng đến dữ liệu hiện có

### 3. MapStruct Warnings
- ⚠️ Có warnings về unmapped audit fields trong mappers
- ✅ Không ảnh hưởng functionality
- 🔧 Có thể ignore hoặc update mappers sau

## Commit Message
```
feat(audit): add audit fields and soft delete support to all entities

- added created_at, updated_at, deleted_at columns via Flyway (V4)
- introduced AuditableEntity base class with auto timestamps
- updated Product, Customer, Order, OrderItem, StockEntry, Supplier, Administrator to extend AuditableEntity
- applied @SQLRestriction clause to exclude soft deleted rows by default
- modified service delete methods to set deleted_at instead of hard delete
- updated DELETE endpoints to perform soft delete (204 response, row remains in DB)
- added Swagger documentation for soft delete behavior
```

## Kết luận
Day 13 đã thành công implement soft delete và auditing cho toàn bộ hệ thống. Tất cả entities giờ có đầy đủ audit trail và soft delete functionality, đảm bảo data integrity và business requirements.

**Next Steps**: Có thể thêm tính năng restore deleted records hoặc bulk operations cho soft delete.
