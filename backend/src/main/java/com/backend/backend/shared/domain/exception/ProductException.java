package com.backend.backend.shared.domain.exception;

/**
 * Product-specific exceptions with error codes
 */
public class ProductException extends AppException {
    
    public ProductException(ErrorCode errorCode) {
        super(errorCode);
    }
    
    public ProductException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
    
    public ProductException(ErrorCode errorCode, String message, Object... args) {
        super(errorCode, message, args);
    }
    
    // Static factory methods for common product errors
    
    public static ProductException notFound(Long productId) {
        return new ProductException(ErrorCode.PRODUCT_NOT_FOUND, 
                "Product with ID %d not found", productId);
    }
    
    public static ProductException notFound(String identifier) {
        return new ProductException(ErrorCode.PRODUCT_NOT_FOUND, 
                "Product with identifier '%s' not found", identifier);
    }
    
    public static ProductException insufficientStock(String productName, int requested, int available) {
        return new ProductException(ErrorCode.INSUFFICIENT_STOCK, 
                "Insufficient stock for product '%s'. Requested: %d, Available: %d", 
                productName, requested, available);
    }
    
    public static ProductException discontinued(String productName) {
        return new ProductException(ErrorCode.PRODUCT_DISCONTINUED, 
                "Product '%s' is discontinued and no longer available", productName);
    }
    
    public static ProductException alreadyExists(String productName) {
        return new ProductException(ErrorCode.ENTITY_ALREADY_EXISTS, 
                "Product with name '%s' already exists", productName);
    }
}
