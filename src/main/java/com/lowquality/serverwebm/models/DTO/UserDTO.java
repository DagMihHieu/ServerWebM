package com.lowquality.serverwebm.models.DTO;

import com.lowquality.serverwebm.models.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Integer id;
    

    private String email;
  
    private String fullName;
    private String avatarUrl;
    private String googleId;
    private Set<Role> roles = new HashSet<>();
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 