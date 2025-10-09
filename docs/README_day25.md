# Day 25 — Business Rules Xác nhận Đơn

## Mục tiêu
Thêm endpoint xác nhận đơn hàng với business rules: tính lại totalAmount và ghi StockEntry xuất kho.

## Thay đổi chính

### 1. Controller Endpoint
- **File**: `backend/src/main/java/com/backend/backend/controller/OrderController.java`
- **Endpoint**: `POST /api/v1/orders/{orderId}/confirm`

### 2. Service Implementation
- **File**: `backend/src/main/java/com/backend/backend/service/OrderService.java`
- **Method**: `confirm(Long orderId)`

### 3. Business Logic
- **Recalculate totalAmount**: Tính lại tổng tiền từ items
- **Stock Movement**: Ghi StockEntry với quantity âm (xuất kho)
- **Cache Eviction**: Clear cache sau khi confirm

## Business Rules

### 1. Order Confirmation
```java
@Transactional
public OrderResponse confirm(Long orderId) {
    Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> OrderException.notFound(orderId));
    
    // Recalculate total to ensure consistency
    BigDecimal total = order.getItems().stream()
            .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    order.setTotalAmount(total);
    
    // Record stock movement entries (negative quantities)
    OffsetDateTime now = OffsetDateTime.now();
    for (OrderItem item : order.getItems()) {
        StockEntry movement = StockEntry.builder()
                .product(item.getProduct())
                .supplier(null)
                .quantity(-item.getQuantity()) // Negative for outflow
                .entryDate(now)
                .build();
        stockEntryRepository.save(movement);
    }
    
    Order saved = orderRepository.save(order);
    return orderMapper.toResponse(saved);
}
```

### 2. Stock Movement Logic
- **Quantity**: Âm (-) để thể hiện xuất kho
- **Supplier**: null vì đây là xuất kho, không phải nhập
- **Timestamp**: Thời điểm confirm order
- **Product**: Link đến product được xuất

## Cách sử dụng

### 1. Tạo order trước
```bash
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: test-key-123" \
  -d '{
    "customerId": 1,
    "items": [
      {"productId": 1, "quantity": 2},
      {"productId": 2, "quantity": 1}
    ]
  }'
```

### 2. Confirm order
```bash
curl -X POST http://localhost:8080/api/v1/orders/1/confirm
```

### 3. Kiểm tra kết quả
```bash
# Xem order đã confirm
curl http://localhost:8080/api/v1/orders/1

# Xem stock entries (xuất kho)
curl http://localhost:8080/api/v1/stock-entries
```

## Response Example

### 1. Order Response
```json
{
  "id": 1,
  "customerId": 1,
  "totalAmount": 2599.98,
  "orderDate": "2025-10-08T14:30:00Z",
  "items": [
    {
      "productId": 1,
      "quantity": 2,
      "price": 1299.99,
      "totalPrice": 2599.98
    }
  ]
}
```

### 2. Stock Entries Created
```json
[
  {
    "id": 1,
    "productId": 1,
    "supplierId": null,
    "quantity": -2,
    "entryDate": "2025-10-08T14:30:00Z"
  }
]
```

## Business Benefits

### 1. Data Consistency
- **Recalculate**: Đảm bảo totalAmount chính xác
- **Audit Trail**: Ghi lại mọi thay đổi stock
- **Transaction**: Tất cả trong một transaction

### 2. Inventory Management
- **Stock Tracking**: Theo dõi xuất kho
- **Historical Data**: Lịch sử giao dịch
- **Reporting**: Báo cáo tồn kho

### 3. Order Lifecycle
- **Draft → Confirmed**: Rõ ràng trạng thái order
- **Business Rules**: Áp dụng rules khi confirm
- **Validation**: Kiểm tra trước khi confirm

## Error Handling

### 1. Order Not Found
- **Error**: 404 Not Found
- **Message**: "Order not found with id: {orderId}"

### 2. Transaction Rollback
- **Database Error**: Rollback toàn bộ transaction
- **Stock Entry Error**: Rollback order update
- **Consistency**: Đảm bảo data consistency

## Cache Management

### 1. Cache Eviction
```java
@Caching(evict = {
    @CacheEvict(cacheNames = CacheNames.ORDER_LIST, allEntries = true),
    @CacheEvict(cacheNames = CacheNames.ORDER_BY_ID, key = "#orderId"),
    @CacheEvict(cacheNames = CacheNames.ORDER_BY_CUSTOMER, allEntries = true)
})
```

### 2. Cache Invalidation
- **Order List**: Clear tất cả order lists
- **Order Detail**: Clear specific order
- **Customer Orders**: Clear customer order lists

## Lợi ích

### 1. Business Logic
- **Consistency**: Đảm bảo data consistency
- **Audit**: Ghi lại mọi thay đổi
- **Rules**: Áp dụng business rules

### 2. Inventory Management
- **Stock Tracking**: Theo dõi xuất kho
- **Reporting**: Báo cáo tồn kho
- **Analytics**: Phân tích xuất kho

## Lưu ý cho dev

### 1. Transaction Management
- **@Transactional**: Đảm bảo ACID properties
- **Rollback**: Handle rollback scenarios
- **Isolation**: Cân nhắc isolation level

### 2. Performance
- **Batch Operations**: Có thể optimize với batch insert
- **Indexing**: Đảm bảo indexes cho queries
- **Caching**: Cache invalidation strategy

### 3. Business Rules
- **Validation**: Kiểm tra business rules trước confirm
- **Notifications**: Có thể thêm notifications
- **Workflow**: Có thể extend thành workflow
