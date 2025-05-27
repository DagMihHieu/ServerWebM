package com.lowquality.serverwebm.repository;

import com.lowquality.serverwebm.models.entity.Mangadetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MangadetailRepository extends JpaRepository<Mangadetail, Integer> {
    Mangadetail findByMangaId(Integer mangaId);
    boolean existsByMangaId(Integer mangaId);
    boolean existsByMangaIdAndStatusId(Integer mangaId, Integer statusId);

}
