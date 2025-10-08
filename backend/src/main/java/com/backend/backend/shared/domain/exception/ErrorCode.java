package com.backend.backend.shared.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Standardized error codes for the application.
 * Format: APP-XXXX where XXXX is a 4-digit number
 * 
 * Categories:
 * - APP-0001-0099: General/System errors
 * - APP-0100-0199: Authentication/Authorization errors
 * - APP-0200-0299: Validation errors
 * - APP-0300-0399: Business logic errors
 * - APP-0400-0499: Entity/Resource errors
 * - APP-0500-0599: External service errors
 * - APP-0600-0699: Database errors
 * - APP-0700-0799: File/Upload errors
 * - APP-0800-0899: Rate limiting errors
 * - APP-0900-0999: Configuration errors
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    
    // === GENERAL/SYSTEM ERRORS (APP-0001-0099) ===
    INTERNAL_SERVER_ERROR("APP-0001", "Internal Server Error", "An unexpected error occurred"),
    SERVICE_UNAVAILABLE("APP-0002", "Service Unavailable", "The service is temporarily unavailable"),
    TIMEOUT_ERROR("APP-0003", "Timeout Error", "The request timed out"),
    CONFIGURATION_ERROR("APP-0004", "Configuration Error", "Application configuration is invalid"),
    
    // === AUTHENTICATION/AUTHORIZATION ERRORS (APP-0100-0199) ===
    AUTHENTICATION_FAILED("APP-0100", "Authentication Failed", "Invalid credentials provided"),
    TOKEN_EXPIRED("APP-0101", "Token Expired", "JWT token has expired"),
    TOKEN_INVALID("APP-0102", "Token Invalid", "JWT token is invalid or malformed"),
    ACCESS_DENIED("APP-0103", "Access Denied", "Insufficient permissions to access this resource"),
    USER_NOT_FOUND("APP-0104", "User Not Found", "User does not exist"),
    USER_DISABLED("APP-0105", "User Disabled", "User account is disabled"),
    PASSWORD_TOO_WEAK("APP-0106", "Password Too Weak", "Password does not meet security requirements"),
    ACCOUNT_LOCKED("APP-0107", "Account Locked", "User account is locked"),
    INVALID_ROLE("APP-0108", "Invalid Role", "User role is invalid or not supported"),
    
    // === VALIDATION ERRORS (APP-0200-0299) ===
    VALIDATION_FAILED("APP-0200", "Validation Failed", "Input validation failed"),
    REQUIRED_FIELD_MISSING("APP-0201", "Required Field Missing", "A required field is missing"),
    INVALID_FORMAT("APP-0202", "Invalid Format", "Field format is invalid"),
    VALUE_OUT_OF_RANGE("APP-0203", "Value Out Of Range", "Value is outside allowed range"),
    DUPLICATE_VALUE("APP-0204", "Duplicate Value", "Value already exists"),
    INVALID_EMAIL_FORMAT("APP-0205", "Invalid Email Format", "Email address format is invalid"),
    INVALID_PHONE_FORMAT("APP-0206", "Invalid Phone Format", "Phone number format is invalid"),
    INVALID_DATE_FORMAT("APP-0207", "Invalid Date Format", "Date format is invalid"),
    INVALID_URL_FORMAT("APP-0208", "Invalid URL Format", "URL format is invalid"),
    
    // === BUSINESS LOGIC ERRORS (APP-0300-0399) ===
    BUSINESS_RULE_VIOLATION("APP-0300", "Business Rule Violation", "Business rule has been violated"),
    INSUFFICIENT_STOCK("APP-0301", "Insufficient Stock", "Not enough stock available"),
    ORDER_CANNOT_BE_MODIFIED("APP-0302", "Order Cannot Be Modified", "Order is in a state that cannot be modified"),
    PAYMENT_FAILED("APP-0303", "Payment Failed", "Payment processing failed"),
    DISCOUNT_EXPIRED("APP-0304", "Discount Expired", "Discount code has expired"),
    DISCOUNT_INVALID("APP-0305", "Discount Invalid", "Discount code is invalid"),
    CUSTOMER_LIMIT_EXCEEDED("APP-0306", "Customer Limit Exceeded", "Customer has exceeded allowed limit"),
    SUPPLIER_INACTIVE("APP-0307", "Supplier Inactive", "Supplier is not active"),
    PRODUCT_DISCONTINUED("APP-0308", "Product Discontinued", "Product is no longer available"),
    
    // === ENTITY/RESOURCE ERRORS (APP-0400-0499) ===
    ENTITY_NOT_FOUND("APP-0400", "Entity Not Found", "Requested entity does not exist"),
    PRODUCT_NOT_FOUND("APP-0401", "Product Not Found", "Product does not exist"),
    CUSTOMER_NOT_FOUND("APP-0402", "Customer Not Found", "Customer does not exist"),
    SUPPLIER_NOT_FOUND("APP-0403", "Supplier Not Found", "Supplier does not exist"),
    ORDER_NOT_FOUND("APP-0404", "Order Not Found", "Order does not exist"),
    ORDER_ITEM_NOT_FOUND("APP-0405", "Order Item Not Found", "Order item does not exist"),
    STOCK_ENTRY_NOT_FOUND("APP-0406", "Stock Entry Not Found", "Stock entry does not exist"),
    CATEGORY_NOT_FOUND("APP-0407", "Category Not Found", "Category does not exist"),
    ENTITY_ALREADY_EXISTS("APP-0408", "Entity Already Exists", "Entity already exists"),
    
    // === EXTERNAL SERVICE ERRORS (APP-0500-0599) ===
    EXTERNAL_SERVICE_ERROR("APP-0500", "External Service Error", "External service returned an error"),
    PAYMENT_SERVICE_ERROR("APP-0501", "Payment Service Error", "Payment service is unavailable"),
    EMAIL_SERVICE_ERROR("APP-0502", "Email Service Error", "Email service is unavailable"),
    SMS_SERVICE_ERROR("APP-0503", "SMS Service Error", "SMS service is unavailable"),
    FILE_STORAGE_ERROR("APP-0504", "File Storage Error", "File storage service is unavailable"),
    
    // === DATABASE ERRORS (APP-0600-0699) ===
    DATABASE_ERROR("APP-0600", "Database Error", "Database operation failed"),
    CONNECTION_TIMEOUT("APP-0601", "Connection Timeout", "Database connection timed out"),
    CONSTRAINT_VIOLATION("APP-0602", "Constraint Violation", "Database constraint violation"),
    DEADLOCK_DETECTED("APP-0603", "Deadlock Detected", "Database deadlock detected"),
    TRANSACTION_FAILED("APP-0604", "Transaction Failed", "Database transaction failed"),
    
    // === FILE/UPLOAD ERRORS (APP-0700-0799) ===
    FILE_TOO_LARGE("APP-0700", "File Too Large", "Uploaded file exceeds size limit"),
    INVALID_FILE_TYPE("APP-0701", "Invalid File Type", "File type is not supported"),
    FILE_UPLOAD_FAILED("APP-0702", "File Upload Failed", "File upload failed"),
    FILE_NOT_FOUND("APP-0703", "File Not Found", "Requested file does not exist"),
    FILE_CORRUPTED("APP-0704", "File Corrupted", "File is corrupted or invalid"),
    
    // === RATE LIMITING ERRORS (APP-0800-0899) ===
    RATE_LIMIT_EXCEEDED("APP-0800", "Rate Limit Exceeded", "Too many requests, please try again later"),
    QUOTA_EXCEEDED("APP-0801", "Quota Exceeded", "API quota has been exceeded"),
    CONCURRENT_REQUEST_LIMIT("APP-0802", "Concurrent Request Limit", "Too many concurrent requests"),
    
    // === CONFIGURATION ERRORS (APP-0900-0999) ===
    MISSING_CONFIGURATION("APP-0900", "Missing Configuration", "Required configuration is missing"),
    INVALID_CONFIGURATION("APP-0901", "Invalid Configuration", "Configuration value is invalid"),
    ENVIRONMENT_MISMATCH("APP-0902", "Environment Mismatch", "Configuration does not match environment");
    
    private final String code;
    private final String title;
    private final String description;
    
    /**
     * Get error code by code string
     */
    public static ErrorCode fromCode(String code) {
        for (ErrorCode errorCode : values()) {
            if (errorCode.getCode().equals(code)) {
                return errorCode;
            }
        }
        throw new IllegalArgumentException("Unknown error code: " + code);
    }
    
    /**
     * Check if error code belongs to a specific category
     */
    public boolean isAuthenticationError() {
        return code.startsWith("APP-01");
    }
    
    public boolean isValidationError() {
        return code.startsWith("APP-02");
    }
    
    public boolean isBusinessError() {
        return code.startsWith("APP-03");
    }
    
    public boolean isEntityError() {
        return code.startsWith("APP-04");
    }
    
    public boolean isSystemError() {
        return code.startsWith("APP-00");
    }
}
