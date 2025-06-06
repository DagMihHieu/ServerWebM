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

    // GET comment by ID
    @GetMapping("/{id}")
    public ResponseEntity<CommentDTO> getCommentById(@PathVariable int id) {
        CommentDTO comment = commentService.getCommentById(id);
        return ResponseEntity.ok(comment);
    }

    // POST create a new comment
    @PostMapping
    public ResponseEntity<CommentDTO> createComment(@RequestBody CommentDTO commentDTO) {
        CommentDTO created = commentService.createComment(commentDTO);
        return ResponseEntity.ok(created);
    }

    // PUT update a comment
    @PutMapping("/{id}")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable int id, @RequestBody CommentDTO commentDTO) {
        commentDTO.setId(id); // gán ID từ path vào DTO
        CommentDTO updated = commentService.editComment(commentDTO);
        return ResponseEntity.ok(updated);
    }

    // DELETE comment by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable int id) {
        commentService.deleteCommentById(id);
        return ResponseEntity.noContent().build();
    }
}
