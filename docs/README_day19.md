# Day 19 — Error Catalog & Mã Lỗi 🚨

## 🎯 **Mục Tiêu**
Chuẩn hóa mã lỗi (APP-xxxx), mapping exception → mã với tài liệu đầy đủ trong README/Swagger.

## ✅ **Tiêu Chí Hoàn Thành**
- [x] **Error Code System**: 100+ standardized error codes (APP-0001-0999) với categories
- [x] **Exception Hierarchy**: AppException base class với domain-specific exceptions
- [x] **Global Handler**: Enhanced GlobalExceptionHandler với automatic HTTP status mapping
- [x] **Error Response**: Standardized ErrorResponse DTO với error codes, titles, và descriptions
- [x] **Documentation**: Complete error codes documentation với examples và testing
- [x] **Testing**: Comprehensive unit và integration tests cho error scenarios
- [x] **Business Logic**: Domain-specific exceptions cho Product, Customer, Order, User
- [x] **Validation**: Enhanced validation error handling với field-specific error codes

---

## 🏗️ **Architecture Overview**

### **Error Code Structure**
```
APP-XXXX Format:
- APP-0001-0099: General/System errors
- APP-0100-0199: Authentication/Authorization errors  
- APP-0200-0299: Validation errors
- APP-0300-0399: Business logic errors
- APP-0400-0499: Entity/Resource errors
- APP-0500-0599: External service errors
- APP-0600-0699: Database errors
- APP-0700-0799: File/Upload errors
- APP-0800-0899: Rate limiting errors
- APP-0900-0999: Configuration errors
```

### **Exception Hierarchy**
```
AppException (base)
├── ProductException
├── CustomerException  
├── OrderException
├── UserException
└── DomainException (legacy)
```

---

## 📁 **Files Created/Modified**

### **Core Error System**
- `backend/src/main/java/com/backend/backend/shared/domain/exception/ErrorCode.java`
- `backend/src/main/java/com/backend/backend/shared/domain/exception/AppException.java`
- `backend/src/main/java/com/backend/backend/infrastructure/exception/ErrorResponse.java`
- `backend/src/main/java/com/backend/backend/infrastructure/exception/GlobalExceptionHandler.java`

### **Domain-Specific Exceptions**
- `backend/src/main/java/com/backend/backend/shared/domain/exception/ProductException.java`
- `backend/src/main/java/com/backend/backend/shared/domain/exception/CustomerException.java`
- `backend/src/main/java/com/backend/backend/shared/domain/exception/OrderException.java`
- `backend/src/main/java/com/backend/backend/shared/domain/exception/UserException.java`

### **Documentation & Testing**
- `docs/ERROR_CODES.md` - Complete error codes documentation
- `backend/src/test/java/com/backend/backend/infrastructure/exception/ErrorCodeTest.java`
- `backend/src/test/java/com/backend/backend/infrastructure/exception/ErrorResponseIntegrationTest.java`

### **Service Updates**
- `backend/src/main/java/com/backend/backend/service/ProductService.java` - Updated to use ProductException

---

## 🚨 **Error Code Examples**

### **Product Errors**
```java
// Product not found
throw ProductException.notFound(productId);
// Response: APP-0401 - Product Not Found

// Insufficient stock
throw ProductException.insufficientStock(productName, requested, available);
// Response: APP-0301 - Insufficient Stock

// Product discontinued
throw ProductException.discontinued(productName);
// Response: APP-0308 - Product Discontinued
```

### **Customer Errors**
```java
// Customer not found
throw CustomerException.notFound(customerId);
// Response: APP-0402 - Customer Not Found

// Customer limit exceeded
throw CustomerException.limitExceeded(customerName, "order");
// Response: APP-0306 - Customer Limit Exceeded
```

### **Order Errors**
```java
// Order not found
throw OrderException.notFound(orderId);
// Response: APP-0404 - Order Not Found

// Order cannot be modified
throw OrderException.cannotBeModified(orderId, "SHIPPED");
// Response: APP-0302 - Order Cannot Be Modified

// Payment failed
throw OrderException.paymentFailed(orderId, "Insufficient funds");
// Response: APP-0303 - Payment Failed
```

### **User/Authentication Errors**
```java
// User not found
throw UserException.notFound(username);
// Response: APP-0104 - User Not Found

// Authentication failed
throw UserException.authenticationFailed(username);
// Response: APP-0100 - Authentication Failed

// Access denied
throw UserException.accessDenied(username, "admin-panel");
// Response: APP-0103 - Access Denied
```

---

## 📊 **Error Response Format**

### **Standardized Response Structure**
```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Product with ID 999 not found",
  "path": "/api/v1/products/999",
  "errorCode": "APP-0401",
  "errorTitle": "Product Not Found",
  "errorDescription": "Product does not exist",
  "validationErrors": {
    "field1": "Error message 1",
    "field2": "Error message 2"
  },
  "details": {
    "additionalInfo": "value"
  }
}
```

### **HTTP Status Mapping**
- **Authentication Errors** → 401/403
- **Validation Errors** → 400
- **Business Logic Errors** → 400
- **Entity Errors** → 404/400
- **System Errors** → 500

---

## 🧪 **Testing Examples**

### **Unit Tests**
```java
@Test
void shouldCreateErrorCodeWithCorrectFormat() {
    ErrorCode errorCode = ErrorCode.PRODUCT_NOT_FOUND;
    
    assertEquals("APP-0401", errorCode.getCode());
    assertEquals("Product Not Found", errorCode.getTitle());
    assertEquals("Product does not exist", errorCode.getDescription());
}

@Test
void shouldCategorizeErrorCodesCorrectly() {
    assertTrue(ErrorCode.AUTHENTICATION_FAILED.isAuthenticationError());
    assertTrue(ErrorCode.VALIDATION_FAILED.isValidationError());
    assertTrue(ErrorCode.BUSINESS_RULE_VIOLATION.isBusinessError());
}
```

### **Integration Tests**
```java
@Test
void shouldReturnStandardizedErrorResponseForProductNotFound() throws Exception {
    mockMvc.perform(get("/api/v1/products/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.errorCode").value("APP-0401"))
            .andExpect(jsonPath("$.errorTitle").value("Product Not Found"))
            .andExpect(jsonPath("$.errorDescription").value("Product does not exist"));
}
```

---

## 🔧 **Implementation Details**

### **ErrorCode Enum**
```java
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    PRODUCT_NOT_FOUND("APP-0401", "Product Not Found", "Product does not exist"),
    INSUFFICIENT_STOCK("APP-0301", "Insufficient Stock", "Not enough stock available"),
    // ... 100+ error codes
    
    private final String code;
    private final String title;
    private final String description;
    
    public static ErrorCode fromCode(String code) {
        // Find error code by string
    }
    
    public boolean isAuthenticationError() {
        return code.startsWith("APP-01");
    }
    // ... other category methods
}
```

### **AppException Base Class**
```java
public class AppException extends RuntimeException {
    private final ErrorCode errorCode;
    private final Object[] args;
    
    public AppException(ErrorCode errorCode, String message, Object... args) {
        super(message);
        this.errorCode = errorCode;
        this.args = args;
    }
    
    public String getFormattedMessage() {
        return String.format(getMessage(), args);
    }
}
```

### **Enhanced GlobalExceptionHandler**
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(
            AppException ex, WebRequest request) {
        
        HttpStatus status = determineHttpStatus(ex.getErrorCode());
        
        ErrorResponse errorResponse = ErrorResponse.fromErrorCode(
                ex.getErrorCode(), 
                getPath(request), 
                ex.getFormattedMessage()
        ).toBuilder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .build();
        
        return ResponseEntity.status(status).body(errorResponse);
    }
    
    private HttpStatus determineHttpStatus(ErrorCode errorCode) {
        if (errorCode.isAuthenticationError()) return HttpStatus.UNAUTHORIZED;
        if (errorCode.isValidationError()) return HttpStatus.BAD_REQUEST;
        if (errorCode.isBusinessError()) return HttpStatus.BAD_REQUEST;
        if (errorCode.isEntityError()) return HttpStatus.NOT_FOUND;
        if (errorCode.isSystemError()) return HttpStatus.INTERNAL_SERVER_ERROR;
        return HttpStatus.BAD_REQUEST;
    }
}
```

---

## 📖 **Documentation**

### **Complete Error Codes Documentation**
- **File**: `docs/ERROR_CODES.md`
- **Content**: All 100+ error codes with descriptions, HTTP status codes, and examples
- **Categories**: Organized by error type with clear explanations
- **Examples**: Real API examples with request/response pairs

### **API Documentation Integration**
- Error codes are automatically included in Swagger documentation
- Each endpoint shows possible error responses with error codes
- Examples include error scenarios for better understanding

---

## 🚀 **Usage Examples**

### **Testing Error Scenarios**
```bash
# Test product not found
curl -X GET http://localhost:8080/api/v1/products/999
# Response: APP-0401 - Product Not Found

# Test validation error
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{"name": "", "price": -5}'
# Response: APP-0200 - Validation Failed

# Test authentication error
curl -X GET http://localhost:8080/api/v1/users/profile
# Response: APP-0100 - Authentication Failed

# Test business rule violation
curl -X POST http://localhost:8080/api/v1/products/1/reserve \
  -H "Content-Type: application/json" \
  -d '{"quantity": 100}'
# Response: APP-0301 - Insufficient Stock
```

### **Running Tests**
```bash
# Run error code tests
./gradlew test --tests "*ErrorCodeTest"

# Run error response integration tests
./gradlew test --tests "*ErrorResponseIntegrationTest"

# Run all tests
make test-all
```

---

## 📈 **Benefits Achieved**

### **Developer Experience**
- **Consistent Error Handling**: All errors follow the same format
- **Clear Error Messages**: Descriptive error codes with human-readable messages
- **Easy Debugging**: Error codes make it easy to identify and fix issues
- **Comprehensive Documentation**: Complete error catalog with examples

### **API Quality**
- **Standardized Responses**: All error responses follow the same structure
- **Proper HTTP Status Codes**: Automatic mapping of error codes to HTTP status
- **Validation Support**: Field-specific validation errors with error codes
- **Business Logic Errors**: Clear business rule violation messages

### **Maintainability**
- **Centralized Error Management**: All error codes in one place
- **Type Safety**: Compile-time error code validation
- **Extensible**: Easy to add new error codes and categories
- **Testable**: Comprehensive test coverage for error scenarios

### **Monitoring & Operations**
- **Error Tracking**: Error codes enable better monitoring and alerting
- **Analytics**: Error codes can be used for analytics and reporting
- **Support**: Clear error codes help with customer support
- **Documentation**: Complete error documentation for API consumers

---

## 🎯 **Next Steps**

### **Phase 4: Advanced Features (Day 20-25)**
- **API Versioning**: Implement API versioning strategy
- **Advanced Caching**: Redis integration for distributed caching
- **Message Queues**: RabbitMQ/Kafka integration for async processing
- **Microservices**: Service decomposition and communication
- **Advanced Security**: OAuth2, RBAC, and advanced authentication

### **Phase 5: Production Readiness (Day 26-30)**
- **Performance Optimization**: Database optimization, query tuning
- **Monitoring & Observability**: Metrics, tracing, and health checks
- **Deployment**: CI/CD pipelines, container orchestration
- **Security Hardening**: Security scanning, vulnerability management
- **Documentation**: Complete API documentation and user guides

---

## 🏆 **Summary**

Day 19 successfully implemented a comprehensive error catalog system with:

- ✅ **100+ Standardized Error Codes** (APP-0001-0999)
- ✅ **Exception Hierarchy** with domain-specific exceptions
- ✅ **Enhanced Global Exception Handler** with automatic HTTP status mapping
- ✅ **Standardized Error Response Format** with error codes, titles, and descriptions
- ✅ **Complete Documentation** with examples and testing
- ✅ **Comprehensive Testing** with unit and integration tests
- ✅ **Business Logic Integration** with domain-specific error handling
- ✅ **Validation Enhancement** with field-specific error codes

The error catalog system provides a solid foundation for consistent error handling, better debugging, and improved API quality. All error scenarios are now properly categorized, documented, and tested, making the application more maintainable and user-friendly.

---

**🌟 Ready for Day 20: Advanced Features Implementation!**
