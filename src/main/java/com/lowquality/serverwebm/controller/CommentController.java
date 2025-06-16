package com.lowquality.serverwebm.controller;

import com.lowquality.serverwebm.models.DTO.CommentDTO;
import com.lowquality.serverwebm.service.CommentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    // GET all comments in a chapter
    @GetMapping("/chapter/{chapterId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByChapter(@PathVariable int chapterId) {
        List<CommentDTO> comments = commentService.getAllCommentInChapter(chapterId);
        return ResponseEntity.ok(comments);
    }

    // GET all comments in a manga
    @GetMapping("/manga/{mangaId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByManga(@PathVariable int mangaId) {
        List<CommentDTO> comments = commentService.getAllCommentInManga(mangaId);
        return ResponseEntity.ok(comments);
    }
    // lấy reply comment
    @GetMapping("/{id}/reply")
    public ResponseEntity<List<CommentDTO>> getReplies(@PathVariable int id) {
        List<CommentDTO> replies = commentService.getAllCommentReply(id);
        return ResponseEntity.ok(replies);
    }
//    // GET comment by ID
//    @GetMapping("/{id}")
//    public ResponseEntity<CommentDTO> getCommentById(@PathVariable int id) {
//        CommentDTO comment = commentService.getCommentById(id);
//        return ResponseEntity.ok(comment);
//    }

    // POST create a new comment
    @PostMapping
    public ResponseEntity<CommentDTO> createComment(
    @RequestBody CommentDTO commentDTO
    ) {
        CommentDTO created = commentService.createComment(commentDTO);
        return ResponseEntity.ok(created);
    }

    @PostMapping("/{id}/reply")
    public ResponseEntity<CommentDTO> createComment(
            @PathVariable Integer id,
            @RequestBody CommentDTO commentDTO
    ) {
        CommentDTO created = commentService.createReplyComment(id,commentDTO);
        return ResponseEntity.ok(created);
    }

    // PUT update a comment
    @PutMapping("/{id}")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable int id, @RequestBody CommentDTO commentDTO) {
        commentDTO.setId(id);
        CommentDTO updated = commentService.editComment(commentDTO);
        return ResponseEntity.ok(updated);
    }

    // DELETE comment by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<CommentDTO> deleteComment(@PathVariable int id) {

        return ResponseEntity.ok(commentService.deleteCommentById(id));
    }
}
