# Day 3 — Validation & Global Error Handling

## ✅ Đã hoàn thành

### 1. Dependencies
- ✅ `spring-boot-starter-validation` đã có sẵn trong `backend/build.gradle`
- ✅ Hibernate Validator được tự động include

### 2. DTO Classes với Validation
Đã tạo các DTO classes với validation annotations:

#### ProductDTO
- `@NotBlank` cho name (bắt buộc)
- `@Size(max = 100)` cho name (giới hạn độ dài)
- `@DecimalMin(value = "0.0")` cho price (giá >= 0)
- `@Min(value = 0)` cho quantityInStock (số lượng >= 0)

#### CustomerDTO & SupplierDTO
- `@NotBlank` cho name (bắt buộc)
- `@Size(max = 100)` cho name
- `@Size(max = 200)` cho contactInfo

#### OrderDTO & OrderItemDTO
- Validation cho nested objects với `@Valid`
- Validation cho các trường bắt buộc và giá trị tối thiểu

### 3. Service Layer
Đã tạo service classes để xử lý business logic:
- `ProductService`
- `CustomerService` 
- `SupplierService`

### 4. Updated Controllers
Đã cập nhật tất cả controllers:
- Sử dụng `@Valid` annotation
- Thay đổi endpoint từ `/api/*` thành `/api/v1/*`
- Sử dụng DTO thay vì Entity
- Proper HTTP status codes (201 cho CREATE, 200 cho GET/PUT, 204 cho DELETE)

### 5. Global Exception Handler
Đã tạo `GlobalExceptionHandler` với:
- `@RestControllerAdvice` để handle exceptions globally
- Xử lý `MethodArgumentNotValidException` cho validation errors
- Xử lý `ResourceNotFoundException` cho 404 errors
- Xử lý `Exception` cho 500 errors
- Response format chuẩn với timestamp, status, error message

### 6. Custom Exception
Đã tạo `ResourceNotFoundException` để handle trường hợp không tìm thấy resource.

## 🧪 Test Results

### Validation Error (400)
```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{"name": "", "price": -5, "quantityInStock": -1}'
```

**Response:**
```json
{
  "fieldErrors": {
    "quantityInStock": "Số lượng tồn kho phải lớn hơn hoặc bằng 0",
    "price": "Giá phải lớn hơn hoặc bằng 0", 
    "name": "Tên sản phẩm là bắt buộc"
  },
  "error": "Validation Failed",
  "timestamp": "2025-09-04T13:28:02.214214088",
  "status": 400
}
```

### Resource Not Found (404)
```bash
curl -X GET http://localhost:8080/api/v1/products/999
```

**Response:**
```json
{
  "error": "Resource Not Found",
  "message": "Không tìm thấy sản phẩm với ID: 999",
  "timestamp": "2025-09-04T13:28:18.435115846",
  "status": 404
}
```

### Successful Creation (201)
```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Test Product", "description": "Test Description", "price": 99.99, "quantityInStock": 10}'
```

**Response:**
```json
{
  "id": 3,
  "name": "Test Product", 
  "description": "Test Description",
  "price": 99.99,
  "quantityInStock": 10
}
```

## 📋 API Endpoints

### Products
- `GET /api/v1/products` - Lấy danh sách sản phẩm
- `GET /api/v1/products/{id}` - Lấy sản phẩm theo ID
- `POST /api/v1/products` - Tạo sản phẩm mới (with validation)
- `PUT /api/v1/products/{id}` - Cập nhật sản phẩm (with validation)
- `DELETE /api/v1/products/{id}` - Xóa sản phẩm

### Customers
- `GET /api/v1/customers` - Lấy danh sách khách hàng
- `GET /api/v1/customers/{id}` - Lấy khách hàng theo ID
- `POST /api/v1/customers` - Tạo khách hàng mới (with validation)
- `PUT /api/v1/customers/{id}` - Cập nhật khách hàng (with validation)
- `DELETE /api/v1/customers/{id}` - Xóa khách hàng

### Suppliers
- `GET /api/v1/suppliers` - Lấy danh sách nhà cung cấp
- `GET /api/v1/suppliers/{id}` - Lấy nhà cung cấp theo ID
- `POST /api/v1/suppliers` - Tạo nhà cung cấp mới (with validation)
- `PUT /api/v1/suppliers/{id}` - Cập nhật nhà cung cấp (with validation)
- `DELETE /api/v1/suppliers/{id}` - Xóa nhà cung cấp

## 🔍 Swagger UI
Truy cập Swagger UI tại: http://localhost:8080/swagger-ui/index.html

## 🎯 Kết quả đạt được

1. ✅ **Bean Validation**: Tất cả input được validate với Jakarta Validation
2. ✅ **Global Error Handling**: Response JSON chuẩn cho tất cả lỗi
3. ✅ **HTTP Status Codes**: 
   - 200 (OK) cho GET/PUT thành công
   - 201 (CREATED) cho POST thành công
   - 204 (NO_CONTENT) cho DELETE thành công
   - 400 (BAD_REQUEST) cho validation errors
   - 404 (NOT_FOUND) cho resource không tồn tại
   - 500 (INTERNAL_SERVER_ERROR) cho lỗi hệ thống
4. ✅ **DTO Pattern**: Tách biệt Entity và DTO để bảo mật và validate
5. ✅ **Service Layer**: Business logic được tách riêng khỏi Controller
6. ✅ **Localized Messages**: Error messages bằng tiếng Việt
