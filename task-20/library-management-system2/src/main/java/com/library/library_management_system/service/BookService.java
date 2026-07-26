package com.library.library_management_system.service;

import com.library.library_management_system.dto.BookDto;
import com.library.library_management_system.entity.Book;
import com.library.library_management_system.entity.Category;

import java.util.List;
import java.util.Optional;

public interface BookService {

    Book createBook(BookDto dto);
    Book updateBook(Long id, BookDto dto);
    void deleteBook(Long id);
    Optional<Book> findById(Long id);
    List<Book> findAllBooks();
    List<Book> findAvailableBooks();
    List<Book> searchBooks(String keyword);
    List<Book> findByCategory(Long categoryId);
    List<Book> findByAuthor(String author);
    List<Book> findByTitle(String title);
    Book addCategoryToBook(Long bookId, Long categoryId);
    Book removeCategoryFromBook(Long bookId, Long categoryId);
}