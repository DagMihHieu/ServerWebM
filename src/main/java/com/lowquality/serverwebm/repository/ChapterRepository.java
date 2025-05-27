package com.lowquality.serverwebm.repository;

import com.lowquality.serverwebm.models.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ChapterRepository extends JpaRepository<Chapter, Integer> {
    List<Chapter> findByManga_Id(Integer mangaId);
    boolean existsByManga_id(Integer mangaId);

    Collection<Object> findById_manga(Integer mangaId);
}
