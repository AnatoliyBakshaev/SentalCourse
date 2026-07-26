package com.library.library_management_system.service;

import com.library.library_management_system.dto.BookDto;
import com.library.library_management_system.entity.Book;
import com.library.library_management_system.entity.Category;
import com.library.library_management_system.repository.BookRepository;
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
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book testBook;
    private Category testCategory;
    private BookDto bookDto;

    @BeforeEach
    void setUp() {
        testCategory = new Category("Fiction");
        testCategory.setId(1L);

        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setIsbn("978-5-17-102543-1");
        testBook.setTotalCopies(5);
        testBook.setAvailableCopies(5);
        testBook.setCategories(new HashSet<>(Set.of(testCategory)));

        bookDto = new BookDto();
        bookDto.setTitle("New Book");
        bookDto.setAuthor("New Author");
        bookDto.setIsbn("978-5-17-113456-0");
        bookDto.setTotalCopies(3);
        bookDto.setCategoryIds(new HashSet<>(Set.of(1L)));
    }

    @Test
    void createBook_ShouldReturnSavedBook() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        Book result = bookService.createBook(bookDto);

        assertNotNull(result);
        assertEquals(testBook.getTitle(), result.getTitle());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void findAllBooks_ShouldReturnListOfBooks() {
        when(bookRepository.findAll()).thenReturn(List.of(testBook));

        List<Book> result = bookService.findAllBooks();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testBook.getTitle(), result.get(0).getTitle());
    }

    @Test
    void findById_ShouldReturnBook_WhenExists() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        Optional<Book> result = bookService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(testBook.getTitle(), result.get().getTitle());
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotExists() {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Book> result = bookService.findById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void updateBook_ShouldUpdateAndReturnBook() {
        bookDto.setTitle("Updated Title");
        bookDto.setAuthor("Updated Author");
        bookDto.setTotalCopies(10);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        Book result = bookService.updateBook(1L, bookDto);

        assertNotNull(result);
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void deleteBook_ShouldDeleteBook() {
        doNothing().when(bookRepository).deleteById(1L);

        bookService.deleteBook(1L);

        verify(bookRepository, times(1)).deleteById(1L);
    }

    @Test
    void searchBooks_ShouldReturnMatchingBooks() {
        when(bookRepository.search("Test")).thenReturn(List.of(testBook));

        List<Book> result = bookService.searchBooks("Test");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findAvailableBooks_ShouldReturnAvailableBooks() {
        when(bookRepository.findAvailableBooks()).thenReturn(List.of(testBook));

        List<Book> result = bookService.findAvailableBooks();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}