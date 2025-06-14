package com.lowquality.serverwebm.service;

import com.lowquality.serverwebm.models.DTO.CommentDTO;

import com.lowquality.serverwebm.models.entity.Chapter;
import com.lowquality.serverwebm.models.entity.Comment;
import com.lowquality.serverwebm.models.entity.Mangadetail;
import com.lowquality.serverwebm.models.entity.User;

import com.lowquality.serverwebm.repository.CommentRepository;
import com.lowquality.serverwebm.repository.UserRepository;
import com.lowquality.serverwebm.util.SecurityUtils;
import com.lowquality.serverwebm.util.UrlUtils;
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
        return commentRepository.findByChapter_IdOrderByUpdatedAtDesc(chapterId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    public List<CommentDTO> getAllCommentInManga(int mangaId) {
        return commentRepository.findByManga_IdOrderByUpdatedAtDesc(mangaId).stream()
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
        permissionService.checkCommentPermission( comment.getUser().getId(), "xóa");
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
        permissionService.checkCommentPermission( comment.getUser().getId(), "chỉnh sửa");
        comment.setContent(commentDTO.getComment());
        return convertToDTO(commentRepository.save(comment));
    }
    private CommentDTO convertToDTO(Comment comment) {
        String avatarImgUrl = UrlUtils.toPublicUrl(comment.getUser().getAvatarUrl());
        Integer replyId = comment.getReply() != null ? comment.getReply().getId() : null;

        if (comment.getIsDeleted()) {
            return CommentDTO.builder()
                    .id(comment.getId())
                    .chapId(comment.getChapter() != null ? comment.getChapter().getId() : null)
                    .mangaId(comment.getManga().getId())
                    .comment("Comment này đã bị xóa")
                    .isDeleted(true)
                    .userName(comment.getUser().getFullName())
                    .avatarUrl(avatarImgUrl)
                    .updatedAt(comment.getUpdatedAt())
                    .reply(replyId)
                    .build();
        }

        return CommentDTO.builder()
                .id(comment.getId())
                .chapId(comment.getChapter() != null ? comment.getChapter().getId() : null)
                .mangaId(comment.getManga().getId())
                .comment(comment.getContent())
                .isDeleted(false)
                .userName(comment.getUser().getFullName())
                .avatarUrl(avatarImgUrl)
                .updatedAt(comment.getUpdatedAt())
                .reply(replyId)
                .build();
    }



    public CommentDTO createReplyComment(Integer parentCommentId, CommentDTO commentDTO) {
        Comment parent = findCommentById(parentCommentId);
        if (parent.getIsDeleted()) {
            throw new IllegalArgumentException("Không thể reply một comment đã bị xóa");
        }

        User user = permissionService.getCurrentUser();
        Comment reply = new Comment();
        reply.setManga(parent.getManga());
        reply.setChapter(parent.getChapter());
        reply.setUser(user);
        reply.setContent(commentDTO.getComment());
        reply.setIsDeleted(false);
        reply.setReply(parent); // <-- thiết lập comment cha

        return convertToDTO(commentRepository.save(reply));
    }

    public List<CommentDTO> getAllCommentReply(int commentId) {
        Comment parent = findCommentById(commentId);

        return commentRepository.findByReplyOrderByUpdatedAtDesc(parent).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

}
