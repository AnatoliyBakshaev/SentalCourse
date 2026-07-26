package com.library.library_management_system.controller;

import com.library.library_management_system.entity.Category;
import com.library.library_management_system.entity.User;
import com.library.library_management_system.service.CategoryService;
import com.library.library_management_system.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @Mock
    private UserService userService;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @InjectMocks
    private CategoryController categoryController;

    private User testUser;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("admin");
        testUser.setFullName("Admin");

        testCategory = new Category("Fiction");
        testCategory.setId(1L);
        testCategory.setDescription("Fiction books");
    }


    @Test
    void listCategories_WhenUserNotLoggedIn_ShouldRedirectToLogin() {
        when(session.getAttribute("loggedUser")).thenReturn(null);

        String result = categoryController.listCategories(model, session);

        assertEquals("redirect:/login", result);
        verify(categoryService, never()).findRootCategories();
    }

    @Test
    void addCategory_WhenSuccess_ShouldRedirectToCategories() {
        when(session.getAttribute("loggedUser")).thenReturn(testUser);
        when(userService.isAdmin(anyLong())).thenReturn(true);
        when(categoryService.createCategory(anyString(), anyString(), any())).thenReturn(testCategory);

        String result = categoryController.addCategory("Fiction", "Fiction books", null, session, model);

        assertEquals("redirect:/categories", result);
        verify(categoryService, times(1)).createCategory(anyString(), anyString(), any());
    }

    @Test
    void addCategory_WhenError_ShouldReturnAddPage() {
        when(session.getAttribute("loggedUser")).thenReturn(testUser);
        when(userService.isAdmin(anyLong())).thenReturn(true);
        when(categoryService.createCategory(anyString(), anyString(), any()))
                .thenThrow(new RuntimeException("Error"));

        String result = categoryController.addCategory("Fiction", "Fiction books", null, session, model);

        assertEquals("categories/add", result);
    }


    @Test
    void viewCategory_WhenCategoryExists_ShouldReturnViewPage() {
        when(session.getAttribute("loggedUser")).thenReturn(testUser);
        when(categoryService.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryService.findChildren(1L)).thenReturn(List.of());
        when(categoryService.getCategoryPath(1L)).thenReturn(List.of(testCategory));
        when(userService.isAdmin(anyLong())).thenReturn(true);

        String result = categoryController.viewCategory(1L, model, session);

        assertEquals("categories/view", result);
        verify(categoryService, times(1)).findById(1L);
    }


    @Test
    void viewCategory_WhenUserNotLoggedIn_ShouldRedirectToLogin() {
        when(session.getAttribute("loggedUser")).thenReturn(null);

        String result = categoryController.viewCategory(1L, model, session);

        assertEquals("redirect:/login", result);
        verify(categoryService, never()).findById(anyLong());
    }

    @Test
    void showEditForm_WhenAdmin_ShouldReturnEditPage() {
        when(session.getAttribute("loggedUser")).thenReturn(testUser);
        when(userService.isAdmin(anyLong())).thenReturn(true);
        when(categoryService.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryService.findRootCategories()).thenReturn(List.of());

        String result = categoryController.showEditForm(1L, model, session);

        assertEquals("categories/edit", result);
    }


    @Test
    void showEditForm_WhenNotAdmin_ShouldRedirectToLogin() {
        when(session.getAttribute("loggedUser")).thenReturn(testUser);
        when(userService.isAdmin(anyLong())).thenReturn(false);

        String result = categoryController.showEditForm(1L, model, session);

        assertEquals("redirect:/login", result);
    }


    @Test
    void updateCategory_WhenSuccess_ShouldRedirectToCategories() {
        when(session.getAttribute("loggedUser")).thenReturn(testUser);
        when(userService.isAdmin(anyLong())).thenReturn(true);
        when(categoryService.updateCategory(anyLong(), anyString(), anyString(), any()))
                .thenReturn(testCategory);

        String result = categoryController.updateCategory(1L, "Updated", "Updated desc", null, session, model);

        assertEquals("redirect:/categories", result);
        verify(categoryService, times(1)).updateCategory(anyLong(), anyString(), anyString(), any());
    }


    @Test
    void updateCategory_WhenError_ShouldReturnEditPage() {
        when(session.getAttribute("loggedUser")).thenReturn(testUser);
        when(userService.isAdmin(anyLong())).thenReturn(true);
        when(categoryService.findById(anyLong())).thenReturn(Optional.of(testCategory));
        when(categoryService.updateCategory(anyLong(), anyString(), anyString(), any()))
                .thenThrow(new RuntimeException("Error"));

        String result = categoryController.updateCategory(1L, "Updated", "Updated desc", null, session, model);

        assertEquals("categories/edit", result);
    }

    @Test
    void deleteCategory_WhenSuccess_ShouldRedirectToCategories() {
        when(session.getAttribute("loggedUser")).thenReturn(testUser);
        when(userService.isAdmin(anyLong())).thenReturn(true);
        doNothing().when(categoryService).deleteCategory(1L);

        String result = categoryController.deleteCategory(1L, session, model);

        assertEquals("redirect:/categories", result);
        verify(categoryService, times(1)).deleteCategory(1L);
    }

    @Test
    void deleteCategory_WhenError_ShouldReturnCategoriesList() {
        when(session.getAttribute("loggedUser")).thenReturn(testUser);
        when(userService.isAdmin(anyLong())).thenReturn(true);
        doThrow(new RuntimeException("Error")).when(categoryService).deleteCategory(1L);

        String result = categoryController.deleteCategory(1L, session, model);

        assertEquals("categories/list", result);
    }


    @Test
    void deleteCategory_WhenNotAdmin_ShouldRedirectToLogin() {
        when(session.getAttribute("loggedUser")).thenReturn(testUser);
        when(userService.isAdmin(anyLong())).thenReturn(false);

        String result = categoryController.deleteCategory(1L, session, model);

        assertEquals("redirect:/login", result);
        verify(categoryService, never()).deleteCategory(anyLong());
    }
}