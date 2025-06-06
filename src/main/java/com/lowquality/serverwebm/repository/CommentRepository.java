package com.lowquality.serverwebm.repository;

import com.lowquality.serverwebm.models.DTO.CommentDTO;
import com.lowquality.serverwebm.models.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByChapter_Id(Integer chapterId);
}
