
package com.example.library.controller;

import com.example.library.entity.Book;
import com.example.library.entity.Category;
import com.example.library.service.AuthorService;
import com.example.library.service.BookService;
import com.example.library.service.CategoryService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final AuthorService authorService;
    private final CategoryService categoryService;

    public BookController(
            BookService bookService,
            AuthorService authorService,
            CategoryService categoryService) {

        this.bookService = bookService;
        this.authorService = authorService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String listBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {

        Page<Book> bookPage = bookService.getBooksPage(page, size);

        model.addAttribute("books", bookPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", bookPage.getTotalPages());

        return "books/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {

        model.addAttribute("book", new Book());
        model.addAttribute("authors", authorService.getAllAuthors());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("selectedCategoryIds", Set.of());

        return "books/form";
    }

    @PostMapping("/save")
    public String saveBook(
            @Valid @ModelAttribute("book") Book book,
            BindingResult bindingResult,
            @RequestParam(name = "categoryIds", required = false)
            Set<Long> categoryIds,
            @RequestParam(name = "coverFile", required = false)
            MultipartFile coverFile,
            Model model) throws IOException {

        if (book.getAuthor() == null ||
                book.getAuthor().getId() == null) {

            bindingResult.rejectValue(
                    "author",
                    "author.required",
                    "Please select an author"
            );
        }

        if (book.getIsbn() != null
                && !book.getIsbn().isBlank()
                && bookService.isIsbnTaken(
                book.getIsbn(), book.getId())) {

            bindingResult.rejectValue(
                    "isbn",
                    "isbn.duplicate",
                    "A book with this ISBN already exists"
            );
        }

        if (bindingResult.hasErrors()) {

            model.addAttribute(
                    "authors",
                    authorService.getAllAuthors()
            );

            model.addAttribute(
                    "categories",
                    categoryService.getAllCategories()
            );

            model.addAttribute(
                    "selectedCategoryIds",
                    categoryIds == null ? Set.of() : categoryIds
            );

            return "books/form";
        }

        book.setAuthor(
                authorService.getAuthorById(
                        book.getAuthor().getId()
                )
        );

        Set<Category> selectedCategories = new HashSet<>();

        if (categoryIds != null) {
            for (Long categoryId : categoryIds) {
                selectedCategories.add(
                        categoryService.getCategoryById(categoryId)
                );
            }
        }

        book.setCategories(selectedCategories);

        bookService.saveBookWithCover(book, coverFile);

        return "redirect:/books";
    }

    @GetMapping("/edit/{id}")
    public String editBook(
            @PathVariable Long id,
            Model model) {

        Book book = bookService.getBookById(id);

        model.addAttribute("book", book);
        model.addAttribute(
                "authors",
                authorService.getAllAuthors()
        );
        model.addAttribute(
                "categories",
                categoryService.getAllCategories()
        );

        Set<Long> selectedCategoryIds = book.getCategories()
                .stream()
                .map(Category::getId)
                .collect(Collectors.toSet());

        model.addAttribute(
                "selectedCategoryIds",
                selectedCategoryIds
        );

        return "books/form";
    }

    @PostMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id) {

        bookService.deleteBook(id);

        return "redirect:/books";
    }

    @GetMapping("/search")
    public String searchBooks(
            @RequestParam String title,
            Model model) {

        model.addAttribute(
                "books",
                bookService.searchBooks(title)
        );

        model.addAttribute("currentPage", 0);
        model.addAttribute("totalPages", 0);

        return "books/list";
    }

    @PutMapping("/{id}")
    @ResponseBody
    public String updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookUpdateRequest request) {

        Book existingBook = bookService.getBookById(id);

        if (bookService.isIsbnTaken(request.isbn(), id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A book with this ISBN already exists"
            );
        }

        existingBook.setTitle(request.title());
        existingBook.setIsbn(request.isbn());
        existingBook.setPublicationYear(
                request.publicationYear()
        );

        bookService.saveBook(existingBook);

        return "Book updated successfully";
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public String deleteBookByApi(@PathVariable Long id) {

        bookService.deleteBook(id);

        return "Book deleted successfully";
    }

    public record BookUpdateRequest(
            @NotBlank String title,
            @NotBlank String isbn,
            @NotNull @Min(1450) Integer publicationYear
    ) {
    }
}
