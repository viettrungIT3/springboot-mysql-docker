package com.backend.backend.shared.domain.exception;

/**
 * StockEntry-specific exceptions with error codes
 */
public class StockEntryException extends AppException {
    
    public StockEntryException(ErrorCode errorCode) {
        super(errorCode);
    }
    
    public StockEntryException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
    
    public StockEntryException(ErrorCode errorCode, String message, Object... args) {
        super(errorCode, message, args);
    }
    
    // Static factory methods for common stock entry errors
    
    public static StockEntryException notFound(Long stockEntryId) {
        return new StockEntryException(ErrorCode.STOCK_ENTRY_NOT_FOUND, 
                "Stock entry with ID %d not found", stockEntryId);
    }
    
    public static StockEntryException invalidQuantity(Integer quantity) {
        return new StockEntryException(ErrorCode.VALUE_OUT_OF_RANGE, 
                "Invalid stock quantity: %d", quantity);
    }
    
    public static StockEntryException invalidDate(String dateInfo) {
        return new StockEntryException(ErrorCode.INVALID_DATE_FORMAT, 
                "Invalid date format: %s", dateInfo);
    }
}
