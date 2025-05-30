package com.lowquality.serverwebm.models.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "pages")
public class Pages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_chapter", nullable = false)
    private Chapter chapter;
    @Column(name = "page_number", nullable = false)
    private Integer page_number;
    @Column(name = "page_img_url")
    private String page_img_url;
}
