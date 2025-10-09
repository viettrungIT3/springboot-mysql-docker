# Day 23 — CSV Import/Export (Products)

## Mục tiêu
Thêm chức năng import/export sản phẩm từ CSV file để hỗ trợ bulk operations.

## Thay đổi chính

### 1. Dependencies
- **File**: `backend/build.gradle`
- **Thêm**:
  ```gradle
  implementation 'org.apache.commons:commons-csv:1.10.0'
  ```

### 2. Controller Endpoints
- **File**: `backend/src/main/java/com/backend/backend/controller/ProductController.java`
- **Endpoints**:
  - `POST /api/v1/products/import-csv` - Import từ CSV
  - `GET /api/v1/products/export-csv` - Export ra CSV

### 3. Service Implementation
- **File**: `backend/src/main/java/com/backend/backend/service/ProductService.java`
- **Methods**:
  - `importProductsFromCsv(MultipartFile file)` - Import logic
  - `exportProductsToCsv(PrintWriter writer)` - Export logic

### 4. Makefile Commands
- **File**: `makefile`
- **Commands**:
  - `make product-import-csv FILE=./path/to/file.csv`
  - `make product-export-csv FILE=./output.csv`

## CSV Format

### 1. Import Format
```csv
name,description,price,quantityInStock
Laptop,Gaming laptop,1299.99,10
Mouse,Wireless mouse,29.99,50
```

### 2. Export Format
```csv
id,name,description,price,quantityInStock,slug,createdAt,updatedAt
1,Laptop,Gaming laptop,1299.99,10,gaming-laptop,2025-10-08T14:30:00Z,2025-10-08T14:30:00Z
```

## Cách sử dụng

### 1. Import CSV
```bash
# Tạo file CSV mẫu
cat > products.csv << EOF
name,description,price,quantityInStock
Laptop,Gaming laptop,1299.99,10
Mouse,Wireless mouse,29.99,50
EOF

# Import qua makefile
make product-import-csv FILE=./products.csv

# Hoặc import trực tiếp
curl -X POST http://localhost:8080/api/v1/products/import-csv \
  -F "file=@products.csv"
```

### 2. Export CSV
```bash
# Export qua makefile
make product-export-csv FILE=./products_export.csv

# Hoặc export trực tiếp
curl -X GET http://localhost:8080/api/v1/products/export-csv \
  -o products_export.csv
```

## Business Logic

### 1. Import Logic
- **Validation**: Kiểm tra file không rỗng
- **Parsing**: Sử dụng Apache Commons CSV
- **Upsert**: Update nếu tồn tại, tạo mới nếu chưa có
- **Error handling**: Skip records lỗi, log errors
- **Cache eviction**: Clear cache sau import

### 2. Export Logic
- **Format**: CSV với header
- **Encoding**: UTF-8
- **Fields**: Tất cả fields của Product entity
- **Streaming**: Sử dụng PrintWriter để tránh memory issues

## Error Handling

### 1. Import Errors
- **File empty**: 400 Bad Request
- **Invalid format**: 400 Bad Request
- **Parse errors**: Log và skip record
- **Validation errors**: Log và skip record

### 2. Export Errors
- **IO errors**: 500 Internal Server Error
- **Encoding issues**: 500 Internal Server Error

## Performance Considerations

### 1. Import
- **Batch processing**: Sử dụng `saveAll()` thay vì `save()` từng record
- **Memory efficient**: Đọc file streaming
- **Transaction**: Wrap trong `@Transactional`

### 2. Export
- **Streaming**: Sử dụng `PrintWriter` thay vì load toàn bộ vào memory
- **Large datasets**: Có thể cần pagination cho datasets lớn

## Lợi ích
- **Bulk operations**: Import/export hàng loạt
- **Data migration**: Dễ dàng migrate data
- **Backup/restore**: Backup data dưới dạng CSV
- **Integration**: Tích hợp với external systems

## Lưu ý cho dev
- CSV format phải match với expected columns
- Import sẽ update existing products (theo name)
- Export bao gồm tất cả products (không filter)
- Có thể extend để support custom CSV formats
