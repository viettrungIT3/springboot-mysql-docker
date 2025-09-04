package com.backend.backend.dto.order;

import com.backend.backend.dto.customer.CustomerResponse;
import com.backend.backend.dto.orderitem.OrderItemResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private CustomerResponse customer;
    private OffsetDateTime orderDate;
    private BigDecimal totalAmount;
    private List<OrderItemResponse> items;
}
