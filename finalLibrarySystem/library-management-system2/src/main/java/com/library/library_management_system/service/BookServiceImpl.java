package com.library.library_management_system.service;

import com.library.library_management_system.dto.BookDto;
import com.library.library_management_system.entity.Book;
import com.library.library_management_system.entity.Category;
import com.library.library_management_system.repository.BookRepository;
import com.library.library_management_system.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookServiceImpl.class);

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    @Transactional
    public Book createBook(BookDto dto) {
        logger.info("Создание новой книги: {}", dto.getTitle());

        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setIsbn(dto.getIsbn());
        book.setPublisher(dto.getPublisher());
        book.setPublicationYear(dto.getPublicationYear());
        book.setDescription(dto.getDescription());
        book.setTotalCopies(dto.getTotalCopies());
        book.setAvailableCopies(dto.getTotalCopies());
        book.setCategories(new HashSet<>());

        if (dto.getCategoryIds() != null) {
            for (Long catId : dto.getCategoryIds()) {
                Category category = categoryRepository.findById(catId)
                        .orElseThrow(() -> {
                            logger.error("Категория не найдена: ID={}", catId);
                            return new RuntimeException("Категория не найдена: " + catId);
                        });
                book.getCategories().add(category);
            }
        }

        Book savedBook = bookRepository.save(book);
        logger.info("Книга создана: ID={}, title={}, copies={}", savedBook.getId(), savedBook.getTitle(), savedBook.getTotalCopies());
        return savedBook;
    }

    @Override
    @Transactional
    public Book updateBook(Long id, BookDto dto) {
        logger.info("Обновление книги ID={}: {}", id, dto.getTitle());

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Книга не найдена для обновления: ID={}", id);
                    return new RuntimeException("Книга не найдена");
                });

        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setIsbn(dto.getIsbn());
        book.setPublisher(dto.getPublisher());
        book.setPublicationYear(dto.getPublicationYear());
        book.setDescription(dto.getDescription());

        if (dto.getTotalCopies() != null) {
            int diff = dto.getTotalCopies() - book.getTotalCopies();
            book.setTotalCopies(dto.getTotalCopies());
            book.setAvailableCopies(book.getAvailableCopies() + diff);
            logger.info("Обновлено количество копий: {}, доступно: {}", dto.getTotalCopies(), book.getAvailableCopies());
        }

        if (dto.getCategoryIds() != null) {
            book.getCategories().clear();
            for (Long catId : dto.getCategoryIds()) {
                Category category = categoryRepository.findById(catId)
                        .orElseThrow(() -> {
                            logger.error("Категория не найдена: ID={}", catId);
                            return new RuntimeException("Категория не найдена: " + catId);
                        });
                book.getCategories().add(category);
            }
        }

        Book updatedBook = bookRepository.save(book);
        logger.info("Книга обновлена: ID={}, title={}", updatedBook.getId(), updatedBook.getTitle());
        return updatedBook;
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {
        logger.warn("Удаление книги: ID={}", id);
        bookRepository.deleteById(id);
        logger.info("Книга удалена: ID={}", id);
    }

    @Override
    public Optional<Book> findById(Long id) {
        logger.debug("Поиск книги по ID: {}", id);
        return bookRepository.findById(id);
    }

    @Override
    public List<Book> findAllBooks() {
        logger.info("Запрос списка всех книг");
        return bookRepository.findAll();
    }

    @Override
    public List<Book> findAvailableBooks() {
        logger.info("Запрос списка доступных книг");
        return bookRepository.findAvailableBooks();
    }

    @Override
    public List<Book> searchBooks(String keyword) {
        logger.info("Поиск книг по ключевому слову: {}", keyword);
        return bookRepository.search(keyword);
    }

    @Override
    public List<Book> findByCategory(Long categoryId) {
        logger.debug("Поиск книг по категории: ID={}", categoryId);
        return bookRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<Book> findByAuthor(String author) {
        logger.debug("Поиск книг по автору: {}", author);
        return bookRepository.findByAuthorContainingIgnoreCase(author);
    }

    @Override
    public List<Book> findByTitle(String title) {
        logger.debug("Поиск книг по названию: {}", title);
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    @Override
    @Transactional
    public Book addCategoryToBook(Long bookId, Long categoryId) {
        logger.info("Добавление категории ID={} к книге ID={}", categoryId, bookId);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> {
                    logger.error("Книга не найдена: ID={}", bookId);
                    return new RuntimeException("Книга не найдена");
                });
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    logger.error("Категория не найдена: ID={}", categoryId);
                    return new RuntimeException("Категория не найдена");
                });

        book.getCategories().add(category);
        Book savedBook = bookRepository.save(book);
        logger.info("Категория добавлена к книге: bookId={}, categoryId={}", bookId, categoryId);
        return savedBook;
    }

    @Override
    @Transactional
    public Book removeCategoryFromBook(Long bookId, Long categoryId) {
        logger.info("Удаление категории ID={} из книги ID={}", categoryId, bookId);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> {
                    logger.error("Книга не найдена: ID={}", bookId);
                    return new RuntimeException("Книга не найдена");
                });
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    logger.error("Категория не найдена: ID={}", categoryId);
                    return new RuntimeException("Категория не найдена");
                });

        book.getCategories().remove(category);
        Book savedBook = bookRepository.save(book);
        logger.info("Категория удалена из книги: bookId={}, categoryId={}", bookId, categoryId);
        return savedBook;
    }
}