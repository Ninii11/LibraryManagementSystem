
package com.example.library.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.HashSet;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Book title is required")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "ISBN is required")
    @Column(nullable = false, unique = true)
    private String isbn;

    @NotNull(message = "Publication year is required")
    @Min(value = 1450, message = "Year must be 1450 or later")
    @Max(value = 2100, message = "Year must be 2100 or earlier")
    private int publicationYear;

    @Column(name = "cover_image_path")
    private String coverImagePath;

    @NotNull(message = "Please select an author")
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    @ManyToMany
    @JoinTable(
            name = "book_categories",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();
}
