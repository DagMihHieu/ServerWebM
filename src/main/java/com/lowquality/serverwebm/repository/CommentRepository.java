package com.lowquality.serverwebm.repository;

import com.lowquality.serverwebm.models.DTO.CommentDTO;
import com.lowquality.serverwebm.models.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Collection;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByChapter_IdOrderByUpdatedAtDesc(Integer chapterId);
    List<Comment> findByReplyOrderByUpdatedAtDesc(Comment parent);
//    void deleteByChapter_Id(Integer chapterId);
    List<Comment> findByManga_IdOrderByUpdatedAtDesc(int mangaId);
}
