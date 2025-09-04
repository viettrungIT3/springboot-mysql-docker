package com.backend.backend.dto.stockentry;

import com.backend.backend.dto.product.ProductResponse;
import com.backend.backend.dto.supplier.SupplierResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockEntryResponse {
    private Long id;
    private ProductResponse product;
    private SupplierResponse supplier;
    private Integer quantity;
    private OffsetDateTime entryDate;
}
