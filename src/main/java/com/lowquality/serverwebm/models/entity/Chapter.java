package com.lowquality.serverwebm.models.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "chapter")
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "chapter_title", length = 100)
    private String name;
    @Column(name = "chap_number")
    private Integer chapNumber;
    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL,orphanRemoval = true)
    List<Pages> pages;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manga_id")
    private Mangadetail manga;
    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;
    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt;
    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
        updatedAt = java.time.LocalDateTime.now();
    }
    @PreUpdate
    protected void onUpdate() {}



}
