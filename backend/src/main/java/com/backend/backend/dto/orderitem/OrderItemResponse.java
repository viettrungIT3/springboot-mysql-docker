package com.backend.backend.dto.orderitem;

import com.backend.backend.dto.product.ProductResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
    private Long id;
    private Long orderId; // Chỉ trả về ID để tránh circular reference
    private ProductResponse product;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal totalPrice; // quantity * price
}
