package com.backend.backend.dto.administrator;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdministratorCreateRequest {
    @NotBlank(message = "Tên đăng nhập là bắt buộc")
    @Size(max = 255, message = "Tên đăng nhập không được vượt quá 255 ký tự")
    private String username;

    @NotBlank(message = "Mật khẩu là bắt buộc")
    @Size(min = 6, max = 255, message = "Mật khẩu phải từ 6-255 ký tự")
    private String password;

    @NotBlank(message = "Email là bắt buộc")
    @Email(message = "Email không hợp lệ")
    @Size(max = 255, message = "Email không được vượt quá 255 ký tự")
    private String email;

    @Size(max = 255, message = "Họ tên không được vượt quá 255 ký tự")
    private String fullName;
}
