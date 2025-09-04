package com.backend.backend.dto.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductUpdateRequest {
    @Size(max = 100, message = "Tên sản phẩm không được vượt quá 100 ký tự")
    private String name;

    @Size(max = 500, message = "Mô tả không được vượt quá 500 ký tự")
    private String description;

    @DecimalMin(value = "0.0", inclusive = true, message = "Giá phải lớn hơn hoặc bằng 0")
    private BigDecimal price;

    @Min(value = 0, message = "Số lượng tồn kho phải lớn hơn hoặc bằng 0")
    private Integer quantityInStock;
}
