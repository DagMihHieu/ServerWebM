package com.lowquality.serverwebm.repository;

import com.lowquality.serverwebm.models.entity.Mangadetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MangadetailRepository extends JpaRepository<Mangadetail, Integer> {
    List<Mangadetail> findByNameContaining(String name);
    List<Mangadetail>    findByIdOrderByUpdatedAtDesc(Integer id);
    List<Mangadetail> findByUploaderId(Integer uploaderId);
    @Query("""
    SELECT m FROM Mangadetail m
    LEFT JOIN m.categories c
    WHERE (:search IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', :search, '%')))
      AND (:statusId IS NULL OR m.status_id.id = :statusId)
      AND (:authorId IS NULL OR m.author_id.id = :authorId)
      AND (:uploaderId IS NULL OR m.uploader.id = :uploaderId)
      AND (:categorySize = 0 OR c.id IN :categoryIds)
    GROUP BY m.id
HAVING (:categorySize = 0 OR COUNT(DISTINCT c.id) = :categorySize)
""")
    Page<Mangadetail> filterMangaJPQL(
            @Param("search") String search,
            @Param("categoryIds") List<Integer> categoryIds,
            @Param("categorySize") Long categorySize, // truyền thủ công categoryIds.size()
            @Param("statusId") Integer statusId,
            @Param("authorId") Integer authorId,
            @Param("uploaderId") Integer uploaderId,
            Pageable pageable
    );

}
