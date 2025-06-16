package com.lowquality.serverwebm.models.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @NotBlank
    private String email;
    @Size(min = 8,max = 20, message = "Mật khẩu phải có ít nhất 8 ký tự và tối đa 20 ký tự")
    private String oldPassword;
    @Size(min = 8,max = 20, message = "Mật khẩu phải có ít nhất 8 ký tự và tối đa 20 ký tự")
    private String newPassword;
}
