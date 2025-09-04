package com.backend.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SupplierDTO {
    private Long id;

    @NotBlank(message = "Tên nhà cung cấp là bắt buộc")
    @Size(max = 100, message = "Tên nhà cung cấp không được vượt quá 100 ký tự")
    private String name;

    @Size(max = 200, message = "Thông tin liên hệ không được vượt quá 200 ký tự")
    private String contactInfo;

    // Constructors
    public SupplierDTO() {
    }

    public SupplierDTO(String name, String contactInfo) {
        this.name = name;
        this.contactInfo = contactInfo;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }
}
