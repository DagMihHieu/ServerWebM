package com.lowquality.serverwebm.repository;

import com.lowquality.serverwebm.models.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Integer> {
}
