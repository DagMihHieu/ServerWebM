package com.lowquality.serverwebm.repository;

import com.lowquality.serverwebm.models.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoriesRepository extends JpaRepository<Category, Integer> {
    Optional<Category> findById(Integer id);
}
