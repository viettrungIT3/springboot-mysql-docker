package com.backend.backend.dto.stockentry;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class StockEntryCreateRequest {
    @NotNull(message = "ID sản phẩm là bắt buộc")
    private Long productId;

    @NotNull(message = "ID nhà cung cấp là bắt buộc")
    private Long supplierId;

    @NotNull(message = "Số lượng là bắt buộc")
    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    private Integer quantity;

    private OffsetDateTime entryDate;
}
