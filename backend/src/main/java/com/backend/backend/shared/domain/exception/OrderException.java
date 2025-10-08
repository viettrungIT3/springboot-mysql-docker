package com.backend.backend.shared.domain.exception;

import java.math.BigDecimal;

/**
 * Order-specific exceptions with error codes
 */
public class OrderException extends AppException {
    
    public OrderException(ErrorCode errorCode) {
        super(errorCode);
    }
    
    public OrderException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
    
    public OrderException(ErrorCode errorCode, String message, Object... args) {
        super(errorCode, message, args);
    }
    
    // Static factory methods for common order errors
    
    public static OrderException notFound(Long orderId) {
        return new OrderException(ErrorCode.ORDER_NOT_FOUND, 
                "Order with ID %d not found", orderId);
    }
    
    public static OrderException cannotBeModified(Long orderId, String currentStatus) {
        return new OrderException(ErrorCode.ORDER_CANNOT_BE_MODIFIED, 
                "Order %d cannot be modified in current status: %s", orderId, currentStatus);
    }
    
    public static OrderException itemNotFound(Long orderId, Long itemId) {
        return new OrderException(ErrorCode.ORDER_ITEM_NOT_FOUND, 
                "Order item %d not found in order %d", itemId, orderId);
    }
    
    public static OrderException paymentFailed(Long orderId, String reason) {
        return new OrderException(ErrorCode.PAYMENT_FAILED, 
                "Payment failed for order %d: %s", orderId, reason);
    }
    
    public static OrderException invalidAmount(BigDecimal amount) {
        return new OrderException(ErrorCode.VALUE_OUT_OF_RANGE, 
                "Invalid order amount: %s", amount);
    }
    
    public static OrderException emptyOrder() {
        return new OrderException(ErrorCode.BUSINESS_RULE_VIOLATION, 
                "Order cannot be empty - must contain at least one item");
    }
}
