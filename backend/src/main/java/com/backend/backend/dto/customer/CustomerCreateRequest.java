package com.backend.backend.dto.customer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CustomerCreateRequest {
    @NotBlank(message = "Tên khách hàng là bắt buộc")
    @Size(max = 255, message = "Tên khách hàng không được vượt quá 255 ký tự")
    private String name;

    @Size(max = 500, message = "Thông tin liên hệ không được vượt quá 500 ký tự")
    private String contactInfo;
}
