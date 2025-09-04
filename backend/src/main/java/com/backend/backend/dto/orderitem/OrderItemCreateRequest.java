package com.backend.backend.dto.orderitem;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemCreateRequest {
    @NotNull(message = "ID đơn hàng là bắt buộc")
    private Long orderId;

    @NotNull(message = "ID sản phẩm là bắt buộc")
    private Long productId;

    @NotNull(message = "Số lượng là bắt buộc")
    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    private Integer quantity;

    @NotNull(message = "Giá là bắt buộc")
    @Min(value = 0, message = "Giá phải lớn hơn hoặc bằng 0")
    private BigDecimal price;
}
