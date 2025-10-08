package com.backend.backend.shared.domain.exception;

/**
 * OrderItem-specific exceptions with error codes
 */
public class OrderItemException extends AppException {
    
    public OrderItemException(ErrorCode errorCode) {
        super(errorCode);
    }
    
    public OrderItemException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
    
    public OrderItemException(ErrorCode errorCode, String message, Object... args) {
        super(errorCode, message, args);
    }
    
    // Static factory methods for common order item errors
    
    public static OrderItemException notFound(Long orderItemId) {
        return new OrderItemException(ErrorCode.ORDER_ITEM_NOT_FOUND, 
                "Order item with ID %d not found", orderItemId);
    }
    
    public static OrderItemException notFoundInOrder(Long orderId, Long itemId) {
        return new OrderItemException(ErrorCode.ORDER_ITEM_NOT_FOUND, 
                "Order item %d not found in order %d", itemId, orderId);
    }
    
    public static OrderItemException invalidQuantity(Integer quantity) {
        return new OrderItemException(ErrorCode.VALUE_OUT_OF_RANGE, 
                "Invalid order item quantity: %d", quantity);
    }
    
    public static OrderItemException invalidPrice(java.math.BigDecimal price) {
        return new OrderItemException(ErrorCode.VALUE_OUT_OF_RANGE, 
                "Invalid order item price: %s", price);
    }
}
