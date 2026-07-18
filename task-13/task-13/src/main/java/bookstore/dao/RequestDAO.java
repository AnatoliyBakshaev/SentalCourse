package bookstore.dao;

import bookstore.model.Request;

import java.sql.SQLException;
import java.util.List;

/**
 * DAO для работы с запросами.
 */
public interface RequestDAO extends GenericDAO<Request> {

    /**
     * Найти запросы по ID книги.
     */
    List<Request> findByBookId(Long bookId) throws SQLException;

    /**
     * Найти запросы по имени клиента (частичное совпадение).
     */
    List<Request> findByCustomer(String customerName) throws SQLException;

    /**
     * Найти невыполненные запросы.
     */
    List<Request> findUnfulfilled() throws SQLException;

    /**
     * Найти выполненные запросы.
     */
    List<Request> findFulfilled() throws SQLException;

    /**
     * Отметить запрос как выполненный.
     */
    boolean markFulfilled(Long requestId) throws SQLException;

    /**
     * Получить количество запросов по ID книги.
     */
    int getCountByBookId(Long bookId) throws SQLException;

    /**
     * Получить количество невыполненных запросов.
     */
    int getUnfulfilledCount() throws SQLException;
}