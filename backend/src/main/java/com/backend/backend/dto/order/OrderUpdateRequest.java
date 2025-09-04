package com.backend.backend.dto.order;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class OrderUpdateRequest {
    private Long customerId;
    private OffsetDateTime orderDate;
    // Note: Không cho phép update items qua order update, phải dùng separate endpoints
}
