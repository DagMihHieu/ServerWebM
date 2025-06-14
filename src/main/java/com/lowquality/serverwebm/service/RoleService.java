package com.lowquality.serverwebm.service;

import com.lowquality.serverwebm.models.DTO.RoleDTO;
import com.lowquality.serverwebm.models.entity.Role;
import com.lowquality.serverwebm.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {
 @Autowired
    private RoleRepository roleRepository;

    public List<RoleDTO> getAllRole() {
        List<Role> roles = roleRepository.findAll();
        roles = roles.stream().filter(role -> role.getRole_Id() !=1 ).toList();
        return roles.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    Role findByRoleId(Integer roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role ID " + roleId + " không tồn tại"));
    }
    public RoleDTO getRoleById(Integer id) {
        return convertToDTO(findByRoleId(id));
    }
    private RoleDTO convertToDTO(Role role) {
        return RoleDTO.builder()
                .id(role.getRole_Id())
                .role_name(role.getName())
                .build();
    }

}
