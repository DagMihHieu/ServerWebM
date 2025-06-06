package com.lowquality.serverwebm.service;

import com.lowquality.serverwebm.models.DTO.AuthorDTO;
import com.lowquality.serverwebm.models.entity.Author;
import com.lowquality.serverwebm.repository.AuthorRepository;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorService {
    AuthorRepository authorRepository;
    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }
    public void deleteAuthor(Integer id) {
        authorRepository.deleteById(id);
    }
    public List<AuthorDTO> getAllAuthors(){
        return authorRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    public AuthorDTO convertToDTO(Author author){
        return AuthorDTO.builder()
                .id(author.getId())
                .author_name(author.getAuthor_name())
                .build();
    }

    public AuthorDTO createAuthor(String authorName) {
        Author author = new Author();
        author.setAuthor_name(authorName);
        author = authorRepository.save(author);
        return convertToDTO(author);
    }

    public Author findById(Integer id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found: " + id));
    }
}
