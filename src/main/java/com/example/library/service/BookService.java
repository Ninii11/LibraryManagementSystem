
package com.example.library.service;

import com.example.library.entity.Book;
import com.example.library.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Book not found: " + id));
    }

    public List<Book> searchBooks(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<Book> getBooksByYear(int year) {
        return bookRepository.findBooksPublishedAfter(year);
    }

    public List<Book> getBooksByAuthor(Long authorId) {
        return bookRepository.findBooksByAuthorId(authorId);
    }

    public Page<Book> getBooksPage(int page, int size) {
        return bookRepository.findAll(
                PageRequest.of(page, size, Sort.by("title").ascending())
        );
    }

    @Transactional
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    @Transactional
    public Book saveBookWithCover(Book book, MultipartFile coverFile)
            throws IOException {

        if (book.getId() != null) {
            Book existingBook = getBookById(book.getId());
            book.setCoverImagePath(existingBook.getCoverImagePath());
        }

        if (coverFile != null && !coverFile.isEmpty()) {
            String contentType = coverFile.getContentType();

            if (!"image/jpeg".equals(contentType)
                    && !"image/png".equals(contentType)) {
                throw new IllegalArgumentException(
                        "Only JPG and PNG images are allowed");
            }

            if (coverFile.getSize() > 5 * 1024 * 1024) {
                throw new IllegalArgumentException(
                        "Image must be smaller than 5 MB");
            }

            Path uploadDirectory = Paths.get("uploads")
                    .toAbsolutePath()
                    .normalize();

            Files.createDirectories(uploadDirectory);

            // Generate a unique filename
            String extension = "image/png".equals(contentType)
                    ? ".png" : ".jpg";

            String filename = UUID.randomUUID() + extension;

            Path destination = uploadDirectory.resolve(filename);

            try (InputStream inputStream = coverFile.getInputStream()) {
                Files.copy(inputStream, destination,
                        StandardCopyOption.REPLACE_EXISTING);
            }

            book.setCoverImagePath("uploads/" + filename);
        }

        return bookRepository.save(book);
    }

    @Transactional
    public void deleteBook(Long id) {
        Book book = getBookById(id);
        bookRepository.delete(book);
    }


    public boolean isIsbnTaken(String isbn, Long bookId) {

        if (bookId == null) {
            return bookRepository.existsByIsbn(isbn);
        }

        return bookRepository.existsByIsbnAndIdNot(
                isbn, bookId);
    }

}
