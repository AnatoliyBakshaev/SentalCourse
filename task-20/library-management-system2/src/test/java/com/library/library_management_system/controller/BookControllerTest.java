package com.library.library_management_system.controller;

import com.library.library_management_system.dto.BookDto;
import com.library.library_management_system.entity.Book;
import com.library.library_management_system.entity.User;
import com.library.library_management_system.service.BookService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Mock
    private BookService bookService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private UserService userService;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @InjectMocks
    private BookController bookController;

    private User testUser;
    private Book testBook;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("admin");
        testUser.setFullName("Admin");

        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
    }

    @Test
    void listBooks_ShouldReturnBooksPage_WhenUserLoggedIn() {
        when(session.getAttribute("loggedUser")).thenReturn(testUser);
        when(bookService.findAllBooks()).thenReturn(List.of(testBook));
        when(userService.isAdmin(testUser)).thenReturn(true);

        String result = bookController.listBooks(null, model, session);

        assertEquals("books/list", result);
        verify(model, times(1)).addAttribute(eq("books"), any(List.class));
    }

    @Test
    void listBooks_WithSearch_ShouldReturnFilteredBooks() {
        when(session.getAttribute("loggedUser")).thenReturn(testUser);
        when(bookService.searchBooks("Test")).thenReturn(List.of(testBook));
        when(userService.isAdmin(testUser)).thenReturn(true);

        String result = bookController.listBooks("Test", model, session);

        assertEquals("books/list", result);
        verify(bookService, times(1)).searchBooks("Test");
    }

    @Test
    void listBooks_ShouldRedirectToLogin_WhenUserNotLoggedIn() {
        when(session.getAttribute("loggedUser")).thenReturn(null);

        String result = bookController.listBooks(null, model, session);

        assertEquals("redirect:/login", result);
    }

    @Test
    void showAddForm_ShouldReturnAddPage_WhenAdmin() {
        when(session.getAttribute("loggedUser")).thenReturn(testUser);
        when(userService.isAdmin(testUser)).thenReturn(true);
        when(categoryService.findAllCategories()).thenReturn(List.of());

        String result = bookController.showAddForm(model, session);

        assertEquals("books/add", result);
    }

    @Test
    void showAddForm_ShouldRedirectToLogin_WhenNotAdmin() {
        when(session.getAttribute("loggedUser")).thenReturn(testUser);
        when(userService.isAdmin(testUser)).thenReturn(false);

        String result = bookController.showAddForm(model, session);

        assertEquals("redirect:/login", result);
    }

    @Test
    void viewBook_ShouldReturnViewPage_WhenBookExists() {
        when(session.getAttribute("loggedUser")).thenReturn(testUser);
        when(bookService.findById(1L)).thenReturn(Optional.of(testBook));
        when(userService.isAdmin(testUser)).thenReturn(true);

        String result = bookController.viewBook(1L, model, session);

        assertEquals("books/view", result);
        verify(model, times(1)).addAttribute(eq("book"), any(Book.class));
    }

    @Test
    void viewBook_ShouldRedirectToBooks_WhenBookNotFound() {
        when(session.getAttribute("loggedUser")).thenReturn(testUser);
        when(bookService.findById(999L)).thenReturn(Optional.empty());

        String result = bookController.viewBook(999L, model, session);

        assertEquals("redirect:/books", result);
    }

    @Test
    void deleteBook_ShouldDeleteAndRedirect() {
        when(session.getAttribute("loggedUser")).thenReturn(testUser);
        when(userService.isAdmin(testUser)).thenReturn(true);
        doNothing().when(bookService).deleteBook(1L);

        String result = bookController.deleteBook(1L, session);

        assertEquals("redirect:/books", result);
        verify(bookService, times(1)).deleteBook(1L);
    }
}