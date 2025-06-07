package com.lowquality.serverwebm.models.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name ="Comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(columnDefinition = "TEXT",name= "Content")
    private String content;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = true)
    private Chapter chapter;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manga_id", nullable = false)
    private Mangadetail manga;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(name="isDeleted")
    private Boolean isDeleted;
    @PrePersist
    protected void onCreate() {
        this.isDeleted = false;
    }

}
