package com.lowquality.serverwebm.repository;

import com.lowquality.serverwebm.models.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ChapterRepository extends JpaRepository<Chapter, Integer> {
    List<Chapter> findByManga_IdOrderByChapNumberDesc(Integer mangaId);

    Optional<Chapter> findByManga_IdAndChapNumber(Integer mangaId, Integer chapNumber);

    boolean existsByManga_IdAndChapNumber(Integer mangaId, Integer chapNumber);


}
