package com.lowquality.serverwebm.repository;

import com.lowquality.serverwebm.models.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
   List<Favorite> findByUser_Id(Integer userId);

    Favorite findByUser_IdAndManga_Id(Integer userId, Integer mangaId);
}
