
package com.example.library.controller;

import com.example.library.entity.Author;
import com.example.library.service.AuthorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    public String listAuthors(Model model) {
        model.addAttribute("authors", authorService.getAllAuthors());
        return "authors/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("author", new Author());
        return "authors/form";
    }



    @PostMapping("/save")
    public String saveAuthor(
            @Valid @ModelAttribute("author") Author author,
            BindingResult bindingResult) {

        if (author.getEmail() != null
                && !author.getEmail().isBlank()
                && authorService.isEmailTaken(
                author.getEmail(), author.getId())) {

            bindingResult.rejectValue(
                    "email",
                    "email.duplicate",
                    "An author with this email already exists"
            );
        }

        if (bindingResult.hasErrors()) {
            return "authors/form";
        }

        authorService.saveAuthor(author);

        return "redirect:/authors";
    }



    @GetMapping("/edit/{id}")
    public String editAuthor(@PathVariable Long id, Model model) {
        model.addAttribute("author", authorService.getAuthorById(id));
        return "authors/form";
    }

    @PostMapping("/delete/{id}")
    public String deleteAuthor(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        if (authorService.hasBooks(id)) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Cannot delete author. This author has books assigned to them."
            );

            return "redirect:/authors";
        }

        authorService.deleteAuthor(id);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Author deleted successfully."
        );

        return "redirect:/authors";
    }

    @GetMapping("/search")
    public String searchAuthors(@RequestParam String name, Model model) {
        model.addAttribute("authors", authorService.searchAuthors(name));
        return "authors/list";
    }
}
