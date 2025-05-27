package com.lowquality.serverwebm.controller;

import com.lowquality.serverwebm.models.DTO.AuthorDTO;
import com.lowquality.serverwebm.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {

    private final AuthorService authorService;

    @Autowired
    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    public ResponseEntity<List<AuthorDTO>> getAllAuthors() {
        return ResponseEntity.ok(authorService.getAllAuthors());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Integer id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<AuthorDTO> createAuthor(@RequestParam String authorName) {
        // Note: You'll need to add a createAuthor method in AuthorService
        AuthorDTO authorDTO = authorService.createAuthor(authorName);
        return ResponseEntity.ok(authorDTO);
    }
}