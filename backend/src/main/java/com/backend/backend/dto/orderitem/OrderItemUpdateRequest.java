package com.backend.backend.dto.orderitem;

import jakarta.validation.constraints.Min;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemUpdateRequest {
    private Long orderId;

    private Long productId;

    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    private Integer quantity;

    @Min(value = 0, message = "Giá phải lớn hơn hoặc bằng 0")
    private BigDecimal price;
}
