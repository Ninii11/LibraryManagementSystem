
package com.example.library.repository;

import com.example.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByTitleContainingIgnoreCase(String title);

    boolean existsByIsbn(String isbn);

    boolean existsByIsbnAndIdNot(String isbn, Long id);

    boolean existsByAuthorId(Long authorId);

    boolean existsByCategories_Id(Long categoryId);


    @Query("SELECT b FROM Book b WHERE b.publicationYear >= :year")
    List<Book> findBooksPublishedAfter(@Param("year") int year);

    @Query("SELECT b FROM Book b WHERE b.author.id = :authorId")
    List<Book> findBooksByAuthorId(@Param("authorId") Long authorId);
}
