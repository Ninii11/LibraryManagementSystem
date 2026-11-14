
package com.example.library.service;

import com.example.library.entity.Author;
import com.example.library.repository.AuthorRepository;
import com.example.library.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthorService {


    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    public AuthorService(
            AuthorRepository authorRepository,
            BookRepository bookRepository) {

        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    public boolean hasBooks(Long authorId) {
        return bookRepository.existsByAuthorId(authorId);
    }


    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    public Author getAuthorById(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Author not found: " + id));
    }

    public List<Author> searchAuthors(String name) {
        return authorRepository.findByNameContainingIgnoreCase(name);
    }

    @Transactional
    public Author saveAuthor(Author author) {
        return authorRepository.save(author);
    }

    @Transactional
    public void deleteAuthor(Long id) {
        Author author = getAuthorById(id);
        authorRepository.delete(author);
    }

    public boolean isEmailTaken(String email, Long authorId) {

        if (authorId == null) {
            return authorRepository.existsByEmail(email);
        }

        return authorRepository.existsByEmailAndIdNot(
                email, authorId);
    }

}
