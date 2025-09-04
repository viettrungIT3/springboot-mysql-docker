package com.backend.backend.dto.supplier;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SupplierCreateRequest {
    @NotBlank(message = "Tên nhà cung cấp là bắt buộc")
    @Size(max = 255, message = "Tên nhà cung cấp không được vượt quá 255 ký tự")
    private String name;

    @Size(max = 500, message = "Thông tin liên hệ không được vượt quá 500 ký tự")
    private String contactInfo;
}
