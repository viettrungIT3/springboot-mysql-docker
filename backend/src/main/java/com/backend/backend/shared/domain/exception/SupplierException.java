package com.backend.backend.shared.domain.exception;

/**
 * Supplier-specific exceptions with error codes
 */
public class SupplierException extends AppException {
    
    public SupplierException(ErrorCode errorCode) {
        super(errorCode);
    }
    
    public SupplierException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
    
    public SupplierException(ErrorCode errorCode, String message, Object... args) {
        super(errorCode, message, args);
    }
    
    // Static factory methods for common supplier errors
    
    public static SupplierException notFound(Long supplierId) {
        return new SupplierException(ErrorCode.SUPPLIER_NOT_FOUND, 
                "Supplier with ID %d not found", supplierId);
    }
    
    public static SupplierException notFound(String supplierName) {
        return new SupplierException(ErrorCode.SUPPLIER_NOT_FOUND, 
                "Supplier with name '%s' not found", supplierName);
    }
    
    public static SupplierException alreadyExists(String supplierName) {
        return new SupplierException(ErrorCode.ENTITY_ALREADY_EXISTS, 
                "Supplier with name '%s' already exists", supplierName);
    }
    
    public static SupplierException inactive(String supplierName) {
        return new SupplierException(ErrorCode.SUPPLIER_INACTIVE, 
                "Supplier '%s' is not active", supplierName);
    }
    
    public static SupplierException invalidContactInfo(String contactInfo) {
        return new SupplierException(ErrorCode.INVALID_FORMAT, 
                "Invalid contact information format: %s", contactInfo);
    }
}
