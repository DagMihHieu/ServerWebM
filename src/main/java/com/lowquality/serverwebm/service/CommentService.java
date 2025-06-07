package com.lowquality.serverwebm.service;

import com.lowquality.serverwebm.models.DTO.CommentDTO;

import com.lowquality.serverwebm.models.entity.Chapter;
import com.lowquality.serverwebm.models.entity.Comment;
import com.lowquality.serverwebm.models.entity.Mangadetail;
import com.lowquality.serverwebm.models.entity.User;

import com.lowquality.serverwebm.repository.CommentRepository;
import com.lowquality.serverwebm.repository.UserRepository;
import com.lowquality.serverwebm.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

import java.util.stream.Collectors;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ChapterService chapterService;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private MangaService mangaService;

    public List<CommentDTO> getAllCommentInChapter(int chapterId) {
        return commentRepository.findByChapter_Id(chapterId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    public List<CommentDTO> getAllCommentInManga(int mangaId) {
        return commentRepository.findByManga_Id(mangaId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    public CommentDTO getCommentById(int id) {
        return convertToDTO(findCommentById(id));
    }
//    public void deleteCommentById(int id) {
//        Comment comment = findCommentById(id);
//        User currentUser = SecurityUtils.getCurrentUser();
//        permissionService.checkCommentPermission(currentUser, comment.getUser().getId(), "xóa");
//        commentRepository.delete(comment);
//    }
    public CommentDTO deleteCommentById(int id) {
        Comment comment = findCommentById(id);
        User currentUser = SecurityUtils.getCurrentUser();
        permissionService.checkCommentPermission(currentUser, comment.getUser().getId(), "xóa");
        comment.setIsDeleted(true);
        return convertToDTO(commentRepository.save(comment));
    }
    public CommentDTO createComment(CommentDTO commentDTO) {
        Comment comment = new Comment();
        User user = SecurityUtils.getCurrentUser();
        Mangadetail manga = mangaService.getMangaEntityById(commentDTO.getMangaId());
        comment.setManga(manga);
        comment.setContent(commentDTO.getComment());
        if (commentDTO.getChapId() != null) {
            Chapter chapter = chapterService.findById(commentDTO.getChapId());
            comment.setChapter(chapter);
        } else {
            comment.setChapter(null);
        }
        comment.setUser(user);
        comment.setIsDeleted(false);
        return convertToDTO(commentRepository.save(comment));
    }
    public Comment findCommentById(int id) {
      return commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
    }
    public CommentDTO editComment(CommentDTO commentDTO) {
        Comment comment = findCommentById(commentDTO.getId());
        User currentUser = SecurityUtils.getCurrentUser();

        permissionService.checkCommentPermission(currentUser, comment.getUser().getId(), "chỉnh sửa");
        comment.setContent(commentDTO.getComment());
        return convertToDTO(commentRepository.save(comment));
    }
    private CommentDTO convertToDTO(Comment comment) {
        if (comment.getIsDeleted()){
            return CommentDTO.builder()
                    .id(comment.getId())
                    .chapId(comment.getChapter() != null ? comment.getChapter().getId() : null)
                    .mangaId(comment.getManga().getId())
                    .comment("Comment này đã bị xóa")
                    .isDeleted(comment.getIsDeleted())
                    .build();
        }

        return CommentDTO.builder()
                .id(comment.getId())
                .chapId(comment.getChapter() != null ? comment.getChapter().getId() : null)
                .mangaId(comment.getManga().getId())
                .comment(comment.getContent())
                .isDeleted(comment.getIsDeleted())
                .build();
    }

}
