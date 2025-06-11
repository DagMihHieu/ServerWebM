package com.lowquality.serverwebm.models.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EditUserDTO {
    private String fullName;
    private String password;
    private Integer roleId;
    private boolean isActive;
}