package com.library.library_management_system.service;

import com.library.library_management_system.entity.Category;
import com.library.library_management_system.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category parentCategory;
    private Category childCategory;

    @BeforeEach
    void setUp() {
        parentCategory = new Category("Fiction");
        parentCategory.setId(1L);
        parentCategory.setDescription("Fiction books");

        childCategory = new Category("Novels", parentCategory);
        childCategory.setId(2L);
        childCategory.setDescription("Novels category");
    }

    @Test
    void createCategory_ShouldReturnSavedCategory() {
        when(categoryRepository.save(any(Category.class))).thenReturn(parentCategory);

        Category result = categoryService.createCategory("Fiction", "Fiction books", null);

        assertNotNull(result);
        assertEquals("Fiction", result.getName());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void createCategory_WithParent_ShouldReturnSavedCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(parentCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(childCategory);

        Category result = categoryService.createCategory("Novels", "Novels category", 1L);

        assertNotNull(result);
        assertEquals("Novels", result.getName());
        assertNotNull(result.getParent());
        assertEquals("Fiction", result.getParent().getName());
    }

    @Test
    void findById_ShouldReturnCategory_WhenExists() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(parentCategory));

        Optional<Category> result = categoryService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Fiction", result.get().getName());
    }

    @Test
    void findRootCategories_ShouldReturnRootCategories() {
        when(categoryRepository.findRootCategories()).thenReturn(List.of(parentCategory));

        List<Category> result = categoryService.findRootCategories();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(result.get(0).getParent());
    }


    @Test
    void deleteCategory_ShouldDeleteCategory_WhenNoChildren() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(parentCategory));
        when(categoryRepository.findByParentId(1L)).thenReturn(new ArrayList<>());

        categoryService.deleteCategory(1L);

        verify(categoryRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteCategory_ShouldThrowException_WhenHasChildren() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(parentCategory));
        when(categoryRepository.findByParentId(1L)).thenReturn(List.of(childCategory));

        assertThrows(RuntimeException.class, () -> categoryService.deleteCategory(1L));
        verify(categoryRepository, never()).deleteById(1L);
    }

    @Test
    void updateCategory_ShouldUpdateAndReturnCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(parentCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(parentCategory);

        Category result = categoryService.updateCategory(1L, "Updated", "Updated desc", null);

        assertNotNull(result);
        assertEquals("Updated", result.getName());
    }

    @Test
    void getCategoryPath_ShouldReturnPath() {
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(childCategory));

        List<Category> path = categoryService.getCategoryPath(2L);

        assertNotNull(path);
        assertEquals(2, path.size());
        assertEquals("Fiction", path.get(0).getName());
        assertEquals("Novels", path.get(1).getName());
    }
}