package com.backend.backend.dto.administrator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdministratorResponse {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    // Không bao gồm password trong response vì lý do bảo mật
}
