package com.lowquality.serverwebm.service;

import com.lowquality.serverwebm.models.DTO.CommentDTO;

import com.lowquality.serverwebm.models.entity.Chapter;
import com.lowquality.serverwebm.models.entity.Comment;
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
    private UserRepository userRepository;
    @Autowired
    private ChapterService chapterService;
    @Autowired
    private PermissionService permissionService;

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
    public void deleteCommentById(int id) {
        Comment comment = findCommentById(id);
        User currentUser = SecurityUtils.getCurrentUser();
        permissionService.checkCommentPermission(currentUser, comment.getUser().getId(), "xóa");
        commentRepository.delete(comment);


    }
    public CommentDTO createComment(CommentDTO commentDTO) {
        Comment comment = new Comment();
        User user = SecurityUtils.getCurrentUser();
        Chapter chapter = chapterService.findById(commentDTO.getChapId());
        comment.setManga(chapter.getManga());
        comment.setContent(commentDTO.getComment());
        comment.setChapter(chapter);
        comment.setUser(user);
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
        // hàm này chưa dùng
//        if (comment.isDelete){
//            return CommentDTO.builder()
//                    .id(comment.getId())
//                    .chapId(comment.getChapter().getId())
//                    .mangaId(comment.getManga().getId())
//                    .userId(comment.getUser().getId())
//                    .comment("Comment này đã bị xóa")
//                    .build();
//        }

        return CommentDTO.builder()
                .id(comment.getId())
                .chapId(comment.getChapter().getId())
                .mangaId(comment.getManga().getId())
                .userId(comment.getUser().getId())
                .comment(comment.getContent())
                .build();
    }

}
