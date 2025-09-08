package com.backend.backend.dto.administrator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    private String token;
    private String username;
    private String email;
    private String fullName;
    private String message;
    
    public static LoginResponse success(String token, String username, String email, String fullName) {
        return LoginResponse.builder()
                .token(token)
                .username(username)
                .email(email)
                .fullName(fullName)
                .message("Đăng nhập thành công")
                .build();
    }
    
    public static LoginResponse error(String message) {
        return LoginResponse.builder()
                .message(message)
                .build();
    }
}
