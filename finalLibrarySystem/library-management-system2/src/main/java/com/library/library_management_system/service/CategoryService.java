package com.library.library_management_system.service;

import com.library.library_management_system.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    Category createCategory(String name, String description, Long parentId);
    Category updateCategory(Long id, String name, String description, Long parentId);
    void deleteCategory(Long id);
    Optional<Category> findById(Long id);
    List<Category> findAllCategories();
    List<Category> findRootCategories();
    List<Category> findChildren(Long parentId);
    Category addSubcategory(Long parentId, String name, String description);
    List<Category> getCategoryPath(Long categoryId);
}