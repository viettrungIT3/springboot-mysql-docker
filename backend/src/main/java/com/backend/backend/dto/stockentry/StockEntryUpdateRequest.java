package com.backend.backend.dto.stockentry;

import jakarta.validation.constraints.Min;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class StockEntryUpdateRequest {
    private Long productId;

    private Long supplierId;

    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    private Integer quantity;

    private OffsetDateTime entryDate;
}
