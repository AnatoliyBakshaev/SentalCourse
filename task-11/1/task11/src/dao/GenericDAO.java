package dao;


import java.sql.SQLException;
import java.util.List;

/**
 * Базовый интерфейс DAO с общими CRUD-методами.
 * @param <T> тип сущности
 */
public interface GenericDAO<T> {

    /**
     * Получить сущность по ID.
     */
    T getById(Long id) throws SQLException;

    /**
     * Получить все сущности.
     */
    List<T> getAll() throws SQLException;

    /**
     * Сохранить сущность.
     */
    boolean save(T entity) throws SQLException;

    /**
     * Обновить сущность.
     */
    boolean update(T entity) throws SQLException;

    /**
     * Удалить сущность по ID.
     */
    boolean delete(Long id) throws SQLException;

    /**
     * Получить все сущности (альтернатива getAll).
     */
    List<T> findAll() throws SQLException;
}
