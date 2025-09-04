package com.backend.backend.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
public class OrderCreateRequest {
    @NotNull(message = "ID khách hàng là bắt buộc")
    private Long customerId;

    private OffsetDateTime orderDate;

    @Valid
    private List<OrderItemCreateRequest> items;
    
    @Data
    public static class OrderItemCreateRequest {
        @NotNull(message = "ID sản phẩm là bắt buộc")
        private Long productId;

        @NotNull(message = "Số lượng là bắt buộc")
        private Integer quantity;
    }
}
