package com.lowquality.serverwebm.service;

import com.lowquality.serverwebm.models.DTO.CommentDTO;

import com.lowquality.serverwebm.models.entity.Chapter;
import com.lowquality.serverwebm.models.entity.Comment;
import com.lowquality.serverwebm.models.entity.User;

import com.lowquality.serverwebm.repository.CommentRepository;
import com.lowquality.serverwebm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import java.util.stream.Collectors;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChapterService chapterService;

    public List<CommentDTO> getAllCommentInChapter(int chapterId) {
        return commentRepository.findByChapter_Id(chapterId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    public List<CommentDTO> getAllCommentInManga(int mangaId) {
        return commentRepository.findByChapter_Id(mangaId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    public CommentDTO getCommentById(int id) {
        return convertToDTO(findCommentById(id));
    }
    public void deleteCommentById(int id) {
        commentRepository.deleteById(id);
    }
    public CommentDTO createComment(CommentDTO commentDTO) {
        Comment comment = new Comment();
        User user =  userRepository.findById(commentDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Chapter chapter = chapterService.findById(commentDTO.getChapId());
        comment.setManga(chapter.getManga());
        comment.setContent(commentDTO.getComment());
        comment.setChapter(chapter);
        comment.setUser(user);
        return convertToDTO(commentRepository.save(comment));
    }
    public Comment findCommentById(int id) {
      return commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
    }
    public CommentDTO editComment(CommentDTO commentDTO) {
        Comment comment = findCommentById(commentDTO.getId());
        User user =  userRepository.findById(commentDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Chapter chapter = chapterService.findById(commentDTO.getChapId());

        comment.setContent(commentDTO.getComment());
        comment.setChapter(chapter);
        comment.setManga(chapter.getManga());
        comment.setUser(user);
        return convertToDTO(commentRepository.save(comment));
    }
    private CommentDTO convertToDTO(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .chapId(comment.getChapter().getId())
                .mangaId(comment.getManga().getId())
                .userId(comment.getUser().getId())
                .comment(comment.getContent())
                .build();
    }
}
