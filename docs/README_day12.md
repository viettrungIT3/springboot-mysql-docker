# Day 12 — Global ID/Slug Feature 🏷️

## 📋 Tổng quan

Tính năng **Global ID/Slug** cho phép truy cập entities thông qua URL-friendly slugs thay vì chỉ dựa vào ID số. Điều này giúp:

- **SEO-friendly URLs**: `/api/v1/products/slug/iphone-15-pro` thay vì `/api/v1/products/123`
- **Bookmarkable**: URLs dễ đọc và chia sẻ
- **User-friendly**: Người dùng có thể đoán được nội dung từ URL

## 🎯 Mục tiêu đã đạt được

✅ **Thêm slug field** cho `Product` và `Customer` entities  
✅ **Tự động generate slug** từ tên entity  
✅ **Unique constraints** đảm bảo không trùng lặp  
✅ **Database migration** an toàn cho existing data  
✅ **API endpoints** hỗ trợ truy cập bằng slug  
✅ **Backward compatibility** - ID-based endpoints vẫn hoạt động  

## 🏗️ Kiến trúc Implementation

### 1. Database Schema

```sql
-- Products table
ALTER TABLE products ADD COLUMN slug VARCHAR(150) NOT NULL UNIQUE;

-- Customers table  
ALTER TABLE customers ADD COLUMN slug VARCHAR(180) NOT NULL UNIQUE;

-- Indexes for performance
CREATE INDEX idx_products_slug ON products(slug);
CREATE INDEX idx_customers_slug ON customers(slug);
```

### 2. Entity Layer

```java
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, columnDefinition = "TEXT")
    private String name;
    
    @Column(name = "slug", length = 150, nullable = false, unique = true)
    private String slug;
    
    // ... other fields
}
```

### 3. Repository Layer

```java
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySlug(String slug);
    boolean existsBySlug(String slug);
}
```

### 4. Service Layer

```java
@Service
public class ProductService {
    
    @Transactional
    public ProductResponse create(ProductCreateRequest request) {
        Product entity = productMapper.toEntity(request);
        entity.setSlug(generateUniqueSlug(request.getName()));
        Product saved = productRepository.save(entity);
        return productMapper.toResponse(saved);
    }
    
    @Transactional(readOnly = true)
    public ProductResponse getBySlug(String slug) {
        Product entity = productRepository.findBySlug(slug)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với slug: " + slug));
        return productMapper.toResponse(entity);
    }
    
    private String generateUniqueSlug(String name) {
        String baseSlug = SlugUtil.toSlug(name);
        String slug = baseSlug;
        int counter = 1;
        while (productRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter++;
        }
        return slug;
    }
}
```

### 5. Controller Layer

```java
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    
    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get product by slug", description = "Lấy thông tin chi tiết của một sản phẩm bằng slug (SEO-friendly)")
    public ResponseEntity<ProductResponse> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(productService.getBySlug(slug));
    }
}
```

## 🛠️ Utility Classes

### SlugUtil.java

```java
public class SlugUtil {
    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    public static String toSlug(String input) {
        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH).replaceAll("-{2,}", "-");
    }
}
```

**Ví dụ chuyển đổi:**
- `"iPhone 15 Pro"` → `"iphone-15-pro"`
- `"Samsung Galaxy S24"` → `"samsung-galaxy-s24"`
- `"Nguyễn Văn An"` → `"nguyen-van-an"`

## 📊 Database Migration

### V3__add_slug_products_customers.sql

```sql
-- Migration V3: Add slug columns for products and customers
-- Adding slug columns with unique constraints for SEO-friendly URLs

-- Add slug column to products table (nullable first)
ALTER TABLE products ADD COLUMN slug VARCHAR(150) AFTER name;

-- Add slug column to customers table (nullable first)
ALTER TABLE customers ADD COLUMN slug VARCHAR(180) AFTER name;

-- Create indexes for better performance on slug lookups
CREATE INDEX idx_products_slug ON products(slug);
CREATE INDEX idx_customers_slug ON customers(slug);

-- Populate slug values for existing records
-- For products: simple slug generation
UPDATE products 
SET slug = LOWER(REPLACE(name, ' ', '-'))
WHERE slug IS NULL;

-- For customers: simple slug generation  
UPDATE customers 
SET slug = LOWER(REPLACE(name, ' ', '-'))
WHERE slug IS NULL;

-- Handle duplicates by appending ID
UPDATE products 
SET slug = CONCAT(slug, '-', id)
WHERE id > 1;

UPDATE customers 
SET slug = CONCAT(slug, '-', id)
WHERE id > 1;

-- Now add NOT NULL UNIQUE constraints after all slugs are populated
ALTER TABLE products MODIFY COLUMN slug VARCHAR(150) NOT NULL UNIQUE;
ALTER TABLE customers MODIFY COLUMN slug VARCHAR(180) NOT NULL UNIQUE;
```

## 🚀 API Endpoints

### Products

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/v1/products/slug/{slug}` | Lấy product bằng slug |
| `POST` | `/api/v1/products` | Tạo product mới (tự động generate slug) |
| `PATCH` | `/api/v1/products/{id}` | Cập nhật product (update slug nếu đổi tên) |

### Customers

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/v1/customers/slug/{slug}` | Lấy customer bằng slug |
| `POST` | `/api/v1/customers` | Tạo customer mới (tự động generate slug) |
| `PATCH` | `/api/v1/customers/{id}` | Cập nhật customer (update slug nếu đổi tên) |

## 🧪 Testing

### 1. Tạo Product mới

```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "iPhone 15 Pro",
    "description": "Latest iPhone with advanced features",
    "price": 1200.00,
    "quantityInStock": 10
  }'
```

**Response:**
```json
{
  "id": 18,
  "name": "iPhone 15 Pro",
  "slug": "iphone-15-pro",
  "description": "Latest iPhone with advanced features",
  "price": 1200.00,
  "quantityInStock": 10
}
```

### 2. Truy cập bằng Slug

```bash
curl -X GET http://localhost:8080/api/v1/products/slug/iphone-15-pro
```

**Response:**
```json
{
  "id": 18,
  "name": "iPhone 15 Pro",
  "slug": "iphone-15-pro",
  "description": "Latest iPhone with advanced features",
  "price": 1200.00,
  "quantityInStock": 10
}
```

### 3. Test Unique Slug Generation

```bash
# Tạo product thứ 2 với tên tương tự
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "iPhone 15 Pro",
    "description": "Another iPhone 15 Pro",
    "price": 1100.00,
    "quantityInStock": 5
  }'
```

**Response:**
```json
{
  "id": 19,
  "name": "iPhone 15 Pro",
  "slug": "iphone-15-pro-1",
  "description": "Another iPhone 15 Pro",
  "price": 1100.00,
  "quantityInStock": 5
}
```

## 🔧 Development Commands

### Sử dụng Makefile

```bash
# Start development environment
make dev-start

# Test API endpoints
make test-api

# Check development status
make dev-status

# View logs
make logs

# Clean and rebuild
make clean
make dev-rebuild
```

### Manual Testing

```bash
# Test slug endpoint
curl -X GET http://localhost:8080/api/v1/products/slug/test-product

# Test customer slug endpoint  
curl -X GET http://localhost:8080/api/v1/customers/slug/alice-smith

# Create new product with slug
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Samsung Galaxy S24", "price": 999.99, "quantityInStock": 25}'
```

## 📈 Performance Considerations

### Database Indexes

- **Primary Index**: `idx_products_slug` và `idx_customers_slug`
- **Unique Constraint**: Đảm bảo slug uniqueness
- **Query Performance**: Slug lookup nhanh như ID lookup

### Caching Strategy

```java
@Cacheable(value = "products", key = "#slug")
public ProductResponse getBySlug(String slug) {
    // Implementation
}
```

## 🔒 Security & Validation

### Input Validation

```java
@GetMapping("/slug/{slug}")
public ResponseEntity<ProductResponse> getBySlug(
    @PathVariable 
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug chỉ được chứa chữ thường, số và dấu gạch ngang")
    String slug) {
    return ResponseEntity.ok(productService.getBySlug(slug));
}
```

### SQL Injection Prevention

- Sử dụng JPA Repository methods
- Parameterized queries tự động
- Input sanitization trong SlugUtil

## 🚨 Error Handling

### Resource Not Found

```json
{
  "error": "Resource Not Found",
  "message": "Không tìm thấy sản phẩm với slug: invalid-slug",
  "timestamp": "2025-09-06T08:45:01.207997554",
  "status": 404
}
```

### Duplicate Slug Handling

- Tự động append số: `iphone-15-pro-1`, `iphone-15-pro-2`
- Đảm bảo uniqueness trong database
- Graceful fallback cho edge cases

## 📚 Swagger Documentation

Truy cập Swagger UI để xem documentation đầy đủ:

```bash
make swagger
# Hoặc mở: http://localhost:8080/swagger-ui/index.html
```

**Endpoints được document:**
- `GET /api/v1/products/slug/{slug}` - Get product by slug
- `GET /api/v1/customers/slug/{slug}` - Get customer by slug

## 🔄 Migration Strategy

### Production Deployment

1. **Backup database** trước khi deploy
2. **Test migration** trên staging environment
3. **Monitor performance** sau khi deploy
4. **Rollback plan** nếu có vấn đề

### Rollback Procedure

```sql
-- Rollback migration (nếu cần)
ALTER TABLE products DROP COLUMN slug;
ALTER TABLE customers DROP COLUMN slug;
DROP INDEX idx_products_slug;
DROP INDEX idx_customers_slug;
```

## 🎯 Best Practices

### 1. Slug Generation

- **Consistent**: Luôn sử dụng SlugUtil
- **Unique**: Kiểm tra uniqueness trước khi save
- **Readable**: Giữ nguyên ý nghĩa từ tên gốc

### 2. API Design

- **RESTful**: Tuân thủ REST conventions
- **Consistent**: Cùng pattern cho tất cả entities
- **Backward Compatible**: Không break existing APIs

### 3. Database Design

- **Indexes**: Tạo indexes cho performance
- **Constraints**: Đảm bảo data integrity
- **Migration**: Safe migration cho existing data

## 🚀 Future Enhancements

### Planned Features

- [ ] **Slug History**: Track slug changes over time
- [ ] **Custom Slugs**: Allow manual slug override
- [ ] **Slug Validation**: More sophisticated validation rules
- [ ] **Bulk Slug Update**: Update multiple entities at once
- [ ] **Slug Analytics**: Track slug usage patterns

### Performance Optimizations

- [ ] **Slug Caching**: Cache frequently accessed slugs
- [ ] **Async Slug Generation**: Background slug processing
- [ ] **Slug Precomputation**: Generate slugs in advance

## 📝 Summary

Day 12 đã thành công implement tính năng **Global ID/Slug** với:

✅ **Complete Implementation**: Entity, Repository, Service, Controller  
✅ **Database Migration**: Safe migration với existing data  
✅ **API Endpoints**: Slug-based access cho Products và Customers  
✅ **Unique Constraints**: Đảm bảo slug uniqueness  
✅ **Error Handling**: Proper exception handling  
✅ **Documentation**: Swagger integration  
✅ **Testing**: Comprehensive test coverage  

Tính năng này giúp ứng dụng có URLs thân thiện với SEO và người dùng, đồng thời duy trì backward compatibility với ID-based access. 🎯
