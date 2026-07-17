package dao;

import model.Order;
import model.enums.OrderStatus;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DAO для работы с заказами.
 */
public interface OrderDAO extends GenericDAO<Order> {

    /**
     * Найти заказы по статусу.
     */
    List<Order> findByStatus(String status) throws SQLException;

    /**
     * Найти заказы по имени клиента (частичное совпадение).
     */
    List<Order> findByCustomer(String customerName) throws SQLException;

    /**
     * Найти выполненные заказы за период.
     */
    List<Order> findCompletedBetween(LocalDateTime startDate, LocalDateTime endDate) throws SQLException;

    /**
     * Получить общую выручку за период.
     */
    double getTotalRevenue(LocalDateTime startDate, LocalDateTime endDate) throws SQLException;

    /**
     * Изменить статус заказа.
     */
    boolean changeOrderStatus(Long orderId, OrderStatus newStatus) throws SQLException;

    /**
     * Получить количество заказов по статусу.
     */
    int getCountByStatus(String status) throws SQLException;

    /**
     * Получить заказы по ID книги.
     */
    List<Order> findByBookId(Long bookId) throws SQLException;
}