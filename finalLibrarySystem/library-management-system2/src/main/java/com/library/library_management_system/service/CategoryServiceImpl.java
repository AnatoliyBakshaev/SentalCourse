package com.library.library_management_system.service;

import com.library.library_management_system.entity.Category;
import com.library.library_management_system.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    @Transactional
    public Category createCategory(String name, String description, Long parentId) {
        Category parent = null;
        if (parentId != null) {
            parent = categoryRepository.findById(parentId)
                    .orElseThrow(() -> new RuntimeException("Родительская категория не найдена"));
        }

        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setParent(parent);

        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public Category updateCategory(Long id, String name, String description, Long parentId) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));

        category.setName(name);
        category.setDescription(description);

        if (parentId != null) {
            Category parent = categoryRepository.findById(parentId)
                    .orElseThrow(() -> new RuntimeException("Родительская категория не найдена"));
            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));

        // Проверяем, есть ли подкатегории
        List<Category> children = categoryRepository.findByParentId(id);
        if (!children.isEmpty()) {
            throw new RuntimeException("Нельзя удалить категорию с подкатегориями");
        }

        categoryRepository.deleteById(id);
    }

    @Override
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public List<Category> findAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        categories.sort((c1, c2) -> c1.getFullPath().compareTo(c2.getFullPath()));
        return categories;
    }

    @Override
    public List<Category> findRootCategories() {
        return categoryRepository.findRootCategories();
    }

    @Override
    public List<Category> findChildren(Long parentId) {
        return categoryRepository.findByParentId(parentId);
    }

    @Override
    @Transactional
    public Category addSubcategory(Long parentId, String name, String description) {
        return createCategory(name, description, parentId);
    }

    @Override
    public List<Category> getCategoryPath(Long categoryId) {
        List<Category> path = new ArrayList<>();
        Category current = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));

        while (current != null) {
            path.add(0, current); // Добавляем в начало
            current = current.getParent();
        }

        return path;
    }
}