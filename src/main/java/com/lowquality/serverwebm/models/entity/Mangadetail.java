package com.lowquality.serverwebm.models.entity;

import java.util.*;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;

@Data
@Entity
@Table(name = "manga_detail")
public class Mangadetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "name", length = 100)
    private String name;
    @ManyToOne
    @JoinColumn(name ="user_id")
    private User uploader;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id")
    private Author author_id;
    @Column(name = "cover_img")
    private String cover_img;
    @Column(columnDefinition = "TEXT", name = "description")
    private String description;
    @ManyToMany
    @JoinTable(
            name = "manga_category",
            joinColumns = @JoinColumn(name = "manga_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories = new ArrayList<>();
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "status_id")
    private Status status_id;
    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;
    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt;
    @OneToMany(mappedBy = "manga", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chapter> chapters = new ArrayList<>();
    @OneToMany(mappedBy = "manga", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();
    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
        updatedAt = java.time.LocalDateTime.now();
    }
    @PreUpdate
    protected void onUpdate() {}


}
