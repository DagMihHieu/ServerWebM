package com.lowquality.serverwebm.repository;

import com.lowquality.serverwebm.models.entity.Pages;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PagesRepository extends JpaRepository<Pages, Integer> {
    List<Pages> findByChapter_id(Integer chapterId);
}
