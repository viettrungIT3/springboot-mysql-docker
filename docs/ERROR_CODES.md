# Error Codes Documentation

## Overview

This application uses standardized error codes following the format `APP-XXXX` where XXXX is a 4-digit number. Each error code is categorized by its prefix to make it easier to understand and handle.

## Error Code Categories

### General/System Errors (APP-0001-0099)
| Code | Title | Description | HTTP Status |
|------|-------|-------------|-------------|
| APP-0001 | Internal Server Error | An unexpected error occurred | 500 |
| APP-0002 | Service Unavailable | The service is temporarily unavailable | 503 |
| APP-0003 | Timeout Error | The request timed out | 408 |
| APP-0004 | Configuration Error | Application configuration is invalid | 500 |

### Authentication/Authorization Errors (APP-0100-0199)
| Code | Title | Description | HTTP Status |
|------|-------|-------------|-------------|
| APP-0100 | Authentication Failed | Invalid credentials provided | 401 |
| APP-0101 | Token Expired | JWT token has expired | 401 |
| APP-0102 | Token Invalid | JWT token is invalid or malformed | 401 |
| APP-0103 | Access Denied | Insufficient permissions to access this resource | 403 |
| APP-0104 | User Not Found | User does not exist | 404 |
| APP-0105 | User Disabled | User account is disabled | 403 |
| APP-0106 | Password Too Weak | Password does not meet security requirements | 400 |
| APP-0107 | Account Locked | User account is locked | 403 |
| APP-0108 | Invalid Role | User role is invalid or not supported | 400 |

### Validation Errors (APP-0200-0299)
| Code | Title | Description | HTTP Status |
|------|-------|-------------|-------------|
| APP-0200 | Validation Failed | Input validation failed | 400 |
| APP-0201 | Required Field Missing | A required field is missing | 400 |
| APP-0202 | Invalid Format | Field format is invalid | 400 |
| APP-0203 | Value Out Of Range | Value is outside allowed range | 400 |
| APP-0204 | Duplicate Value | Value already exists | 400 |
| APP-0205 | Invalid Email Format | Email address format is invalid | 400 |
| APP-0206 | Invalid Phone Format | Phone number format is invalid | 400 |
| APP-0207 | Invalid Date Format | Date format is invalid | 400 |
| APP-0208 | Invalid URL Format | URL format is invalid | 400 |

### Business Logic Errors (APP-0300-0399)
| Code | Title | Description | HTTP Status |
|------|-------|-------------|-------------|
| APP-0300 | Business Rule Violation | Business rule has been violated | 400 |
| APP-0301 | Insufficient Stock | Not enough stock available | 400 |
| APP-0302 | Order Cannot Be Modified | Order is in a state that cannot be modified | 400 |
| APP-0303 | Payment Failed | Payment processing failed | 400 |
| APP-0304 | Discount Expired | Discount code has expired | 400 |
| APP-0305 | Discount Invalid | Discount code is invalid | 400 |
| APP-0306 | Customer Limit Exceeded | Customer has exceeded allowed limit | 400 |
| APP-0307 | Supplier Inactive | Supplier is not active | 400 |
| APP-0308 | Product Discontinued | Product is no longer available | 400 |

### Entity/Resource Errors (APP-0400-0499)
| Code | Title | Description | HTTP Status |
|------|-------|-------------|-------------|
| APP-0400 | Entity Not Found | Requested entity does not exist | 404 |
| APP-0401 | Product Not Found | Product does not exist | 404 |
| APP-0402 | Customer Not Found | Customer does not exist | 404 |
| APP-0403 | Supplier Not Found | Supplier does not exist | 404 |
| APP-0404 | Order Not Found | Order does not exist | 404 |
| APP-0405 | Order Item Not Found | Order item does not exist | 404 |
| APP-0406 | Stock Entry Not Found | Stock entry does not exist | 404 |
| APP-0407 | Category Not Found | Category does not exist | 404 |
| APP-0408 | Entity Already Exists | Entity already exists | 400 |

### External Service Errors (APP-0500-0599)
| Code | Title | Description | HTTP Status |
|------|-------|-------------|-------------|
| APP-0500 | External Service Error | External service returned an error | 502 |
| APP-0501 | Payment Service Error | Payment service is unavailable | 502 |
| APP-0502 | Email Service Error | Email service is unavailable | 502 |
| APP-0503 | SMS Service Error | SMS service is unavailable | 502 |
| APP-0504 | File Storage Error | File storage service is unavailable | 502 |

### Database Errors (APP-0600-0699)
| Code | Title | Description | HTTP Status |
|------|-------|-------------|-------------|
| APP-0600 | Database Error | Database operation failed | 500 |
| APP-0601 | Connection Timeout | Database connection timed out | 500 |
| APP-0602 | Constraint Violation | Database constraint violation | 400 |
| APP-0603 | Deadlock Detected | Database deadlock detected | 500 |
| APP-0604 | Transaction Failed | Database transaction failed | 500 |

### File/Upload Errors (APP-0700-0799)
| Code | Title | Description | HTTP Status |
|------|-------|-------------|-------------|
| APP-0700 | File Too Large | Uploaded file exceeds size limit | 413 |
| APP-0701 | Invalid File Type | File type is not supported | 400 |
| APP-0702 | File Upload Failed | File upload failed | 500 |
| APP-0703 | File Not Found | Requested file does not exist | 404 |
| APP-0704 | File Corrupted | File is corrupted or invalid | 400 |

### Rate Limiting Errors (APP-0800-0899)
| Code | Title | Description | HTTP Status |
|------|-------|-------------|-------------|
| APP-0800 | Rate Limit Exceeded | Too many requests, please try again later | 429 |
| APP-0801 | Quota Exceeded | API quota has been exceeded | 429 |
| APP-0802 | Concurrent Request Limit | Too many concurrent requests | 429 |

### Configuration Errors (APP-0900-0999)
| Code | Title | Description | HTTP Status |
|------|-------|-------------|-------------|
| APP-0900 | Missing Configuration | Required configuration is missing | 500 |
| APP-0901 | Invalid Configuration | Configuration value is invalid | 500 |
| APP-0902 | Environment Mismatch | Configuration does not match environment | 500 |

## Error Response Format

All error responses follow this standardized format:

```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Custom error message",
  "path": "/api/v1/products/123",
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

## Usage Examples

### Product Not Found
```bash
curl -X GET http://localhost:8080/api/v1/products/999
```

Response:
```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Product with ID 999 not found",
  "path": "/api/v1/products/999",
  "errorCode": "APP-0401",
  "errorTitle": "Product Not Found",
  "errorDescription": "Product does not exist"
}
```

### Validation Error
```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{"name": "", "price": -5}'
```

Response:
```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Input validation failed",
  "path": "/api/v1/products",
  "errorCode": "APP-0200",
  "errorTitle": "Validation Failed",
  "errorDescription": "Input validation failed",
  "validationErrors": {
    "name": "Product name is required",
    "price": "Price must be positive"
  }
}
```

### Insufficient Stock
```bash
curl -X POST http://localhost:8080/api/v1/products/1/reserve \
  -H "Content-Type: application/json" \
  -d '{"quantity": 100}'
```

Response:
```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Insufficient stock for product 'iPhone 15'. Requested: 100, Available: 5",
  "path": "/api/v1/products/1/reserve",
  "errorCode": "APP-0301",
  "errorTitle": "Insufficient Stock",
  "errorDescription": "Not enough stock available"
}
```

## Implementation Notes

1. **Error Code Consistency**: All error codes follow the `APP-XXXX` format
2. **HTTP Status Mapping**: Error codes are automatically mapped to appropriate HTTP status codes
3. **Localization**: Error messages can be localized based on the client's locale
4. **Logging**: All errors are logged with their error codes for better debugging
5. **Monitoring**: Error codes can be used for monitoring and alerting systems

## Adding New Error Codes

When adding new error codes:

1. Add the error code to the `ErrorCode` enum
2. Update this documentation
3. Add appropriate exception handling
4. Update Swagger documentation
5. Add tests for the new error scenarios

## Testing Error Codes

Use the following commands to test error scenarios:

```bash
# Test product not found
make test-api

# Test validation errors
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{"name": "", "price": -5}'

# Test authentication errors
curl -X GET http://localhost:8080/api/v1/users/profile
```
