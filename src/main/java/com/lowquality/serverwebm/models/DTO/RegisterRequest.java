package com.lowquality.serverwebm.models.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 8,max = 20, message = "Mật khẩu phải có ít nhất 8 ký tự và tối đa 20 ký tự")
    private String password;

    private String fullName;
} 