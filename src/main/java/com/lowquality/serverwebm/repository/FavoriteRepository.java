package com.lowquality.serverwebm.repository;

import com.lowquality.serverwebm.models.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
}
