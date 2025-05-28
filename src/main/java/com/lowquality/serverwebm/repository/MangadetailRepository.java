package com.lowquality.serverwebm.repository;

import com.lowquality.serverwebm.models.entity.Mangadetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MangadetailRepository extends JpaRepository<Mangadetail, Integer> {
    List<Mangadetail> findByNameContaining(String name);
}
