
package com.example.library.service;

import com.example.library.entity.Category;
import com.example.library.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.library.repository.BookRepository;

import java.util.List;

@Service
public class CategoryService {


    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;

    public CategoryService(
            CategoryRepository categoryRepository,
            BookRepository bookRepository) {

        this.categoryRepository = categoryRepository;
        this.bookRepository = bookRepository;
    }

    public boolean hasBooks(Long categoryId) {
        return bookRepository.existsByCategories_Id(categoryId);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Category not found: " + id));
    }

    public boolean isNameTaken(String name, Long categoryId) {

        if (categoryId == null) {
            return categoryRepository.existsByName(name);
        }

        return categoryRepository.existsByNameAndIdNot(
                name, categoryId);
    }

    @Transactional
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
    }
}
