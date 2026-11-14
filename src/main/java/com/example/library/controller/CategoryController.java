
package com.example.library.controller;

import com.example.library.entity.Category;
import com.example.library.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.validation.BindingResult;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories",
                categoryService.getAllCategories());
        return "categories/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("category", new Category());
        return "categories/form";
    }

    @PostMapping("/save")
    public String saveCategory(
            @Valid @ModelAttribute("category") Category category,
            BindingResult bindingResult) {

        if (category.getName() != null
                && !category.getName().isBlank()
                && categoryService.isNameTaken(
                category.getName(), category.getId())) {

            bindingResult.rejectValue(
                    "name",
                    "name.duplicate",
                    "A category with this name already exists"
            );
        }

        if (bindingResult.hasErrors()) {
            return "categories/form";
        }

        categoryService.saveCategory(category);

        return "redirect:/categories";
    }

    @GetMapping("/edit/{id}")
    public String editCategory(@PathVariable Long id, Model model) {
        model.addAttribute("category",
                categoryService.getCategoryById(id));
        return "categories/form";
    }

    @PostMapping("/delete/{id}")
    public String deleteCategory(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        if (categoryService.hasBooks(id)) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Cannot delete category. This category is assigned to one or more books."
            );

            return "redirect:/categories";
        }

        categoryService.deleteCategory(id);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Category deleted successfully."
        );

        return "redirect:/categories";
    }
}
