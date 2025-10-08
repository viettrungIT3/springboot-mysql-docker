package com.backend.backend.shared.domain.exception;

/**
 * Customer-specific exceptions with error codes
 */
public class CustomerException extends AppException {
    
    public CustomerException(ErrorCode errorCode) {
        super(errorCode);
    }
    
    public CustomerException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
    
    public CustomerException(ErrorCode errorCode, String message, Object... args) {
        super(errorCode, message, args);
    }
    
    // Static factory methods for common customer errors
    
    public static CustomerException notFound(Long customerId) {
        return new CustomerException(ErrorCode.CUSTOMER_NOT_FOUND, 
                "Customer with ID %d not found", customerId);
    }
    
    public static CustomerException notFound(String customerName) {
        return new CustomerException(ErrorCode.CUSTOMER_NOT_FOUND, 
                "Customer with name '%s' not found", customerName);
    }
    
    public static CustomerException alreadyExists(String customerName) {
        return new CustomerException(ErrorCode.ENTITY_ALREADY_EXISTS, 
                "Customer with name '%s' already exists", customerName);
    }
    
    public static CustomerException limitExceeded(String customerName, String limitType) {
        return new CustomerException(ErrorCode.CUSTOMER_LIMIT_EXCEEDED, 
                "Customer '%s' has exceeded %s limit", customerName, limitType);
    }
    
    public static CustomerException invalidContactInfo(String contactInfo) {
        return new CustomerException(ErrorCode.INVALID_FORMAT, 
                "Invalid contact information format: %s", contactInfo);
    }
}
