package dao;

import model.Book;

import java.sql.SQLException;
import java.util.List;

/**
 * DAO для работы с книгами.
 */
public interface BookDAO extends GenericDAO<Book> {

    /**
     * Найти книгу по ISBN.
     */
    Book findByIsbn(String isbn) throws SQLException;

    /**
     * Найти книги по статусу.
     */
    List<Book> findByStatus(String status) throws SQLException;

    /**
     * Найти книги по автору (частичное совпадение).
     */
    List<Book> findByAuthor(String author) throws SQLException;

    /**
     * Найти залежавшиеся книги (на складе более N месяцев).
     */
    List<Book> findOldBooks(int months) throws SQLException;

    /**
     * Обновить количество книг.
     */
    boolean updateQuantity(Long id, int quantity) throws SQLException;

    /**
     * Найти книги по названию (частичное совпадение).
     */
    List<Book> findByTitle(String title) throws SQLException;

    /**
     * Найти книги по жанру.
     */
    List<Book> findByGenre(String genre) throws SQLException;

    /**
     * Получить количество книг.
     */
    int getCount() throws SQLException;
}