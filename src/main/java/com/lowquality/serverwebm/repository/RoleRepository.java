package com.lowquality.serverwebm.repository;

import com.lowquality.serverwebm.models.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
}
