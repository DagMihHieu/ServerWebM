package com.lowquality.serverwebm.models.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "roles")
public class Role {
    public static final Role USER = new Role(2);
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id",nullable = false, unique = true)
    private Integer role_Id;

    
    @Column(name="role_name",length = 10,nullable = false)
    private String name;

    public Role() {}

    public Role(int role_Id) {
        this.role_Id = role_Id;
    }

    public Role(int role_Id, String name) {
        this.role_Id = role_Id;
        this.name = name;

    }
} 