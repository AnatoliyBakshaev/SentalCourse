package dao.impl;

import dao.OrderDAO;
import model.Order;
import model.enums.OrderStatus;
import config.DatabaseConfig;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDAOImpl implements OrderDAO {
    private final DatabaseConfig dbConfig;

    public OrderDAOImpl() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    @Override
    public Order getById(Long id) throws SQLException {
        String sql = "SELECT * FROM orders WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToOrder(rs);
            }
            return null;
        }
    }

    @Override
    public List<Order> getAll() throws SQLException {
        String sql = "SELECT * FROM orders ORDER BY id";
        List<Order> orders = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
        }
        return orders;
    }

    @Override
    public List<Order> findAll() throws SQLException {
        return getAll();
    }

    @Override
    public boolean save(Order order) throws SQLException {
        String sql = "INSERT INTO orders (book_id, customer_name, customer_phone, " +
                "customer_email, customer_address, status, order_date, " +
                "completion_date, total_price, quantity) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = dbConfig.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                setOrderParameters(stmt, order);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    ResultSet generatedKeys = stmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        order.setId(generatedKeys.getLong(1));
                    }
                    conn.commit();
                    return true;
                }
                conn.rollback();
                return false;
            }
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.setAutoCommit(true);
        }
    }

    @Override
    public boolean update(Order order) throws SQLException {
        String sql = "UPDATE orders SET book_id = ?, customer_name = ?, customer_phone = ?, " +
                "customer_email = ?, customer_address = ?, status = ?, " +
                "completion_date = ?, total_price = ?, quantity = ? WHERE id = ?";
        Connection conn = null;
        try {
            conn = dbConfig.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, order.getBookId());
                stmt.setString(2, order.getCustomerName());
                stmt.setString(3, order.getCustomerPhone());
                stmt.setString(4, order.getCustomerEmail());
                stmt.setString(5, order.getCustomerAddress());
                stmt.setString(6, order.getStatus() != null ? order.getStatus().name() : null);
                stmt.setTimestamp(7, order.getCompletionDate() != null ?
                        Timestamp.valueOf(order.getCompletionDate()) : null);
                stmt.setDouble(8, order.getTotalPrice());
                stmt.setInt(9, order.getQuantity());
                stmt.setLong(10, order.getId());
                int rowsAffected = stmt.executeUpdate();
                conn.commit();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.setAutoCommit(true);
        }
    }

    @Override
    public boolean delete(Long id) throws SQLException {
        String sql = "DELETE FROM orders WHERE id = ?";
        Connection conn = null;
        try {
            conn = dbConfig.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, id);
                int rowsAffected = stmt.executeUpdate();
                conn.commit();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.setAutoCommit(true);
        }
    }

    @Override
    public List<Order> findByStatus(String status) throws SQLException {
        String sql = "SELECT * FROM orders WHERE status = ? ORDER BY order_date DESC";
        List<Order> orders = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
        }
        return orders;
    }

    @Override
    public List<Order> findByCustomer(String customerName) throws SQLException {
        String sql = "SELECT * FROM orders WHERE customer_name ILIKE ? ORDER BY order_date DESC";
        List<Order> orders = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + customerName + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
        }
        return orders;
    }

    @Override
    public List<Order> findCompletedBetween(LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        String sql = "SELECT * FROM orders WHERE status = 'COMPLETED' " +
                "AND completion_date BETWEEN ? AND ? ORDER BY completion_date DESC";
        List<Order> orders = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(startDate));
            stmt.setTimestamp(2, Timestamp.valueOf(endDate));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
        }
        return orders;
    }

    @Override
    public double getTotalRevenue(LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_price), 0) FROM orders " +
                "WHERE status = 'COMPLETED' AND completion_date BETWEEN ? AND ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(startDate));
            stmt.setTimestamp(2, Timestamp.valueOf(endDate));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
            return 0.0;
        }
    }

    @Override
    public boolean changeOrderStatus(Long orderId, OrderStatus newStatus) throws SQLException {
        String sql = "UPDATE orders SET status = ?, completion_date = ? WHERE id = ?";
        Connection conn = null;
        try {
            conn = dbConfig.getConnection();
            conn.setAutoCommit(false);

            // Проверка существования заказа
            Order order = getById(orderId);
            if (order == null) {
                conn.rollback();
                return false;
            }

            // Нельзя изменить статус выполненного или отмененного заказа
            if (order.getStatus() == OrderStatus.COMPLETED ||
                    order.getStatus() == OrderStatus.CANCELLED) {
                conn.rollback();
                return false;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, newStatus.name());
                stmt.setTimestamp(2, newStatus == OrderStatus.COMPLETED ?
                        Timestamp.valueOf(LocalDateTime.now()) : null);
                stmt.setLong(3, orderId);
                int rowsAffected = stmt.executeUpdate();
                conn.commit();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.setAutoCommit(true);
        }
    }

    @Override
    public int getCountByStatus(String status) throws SQLException {
        String sql = "SELECT COUNT(*) FROM orders WHERE status = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }

    @Override
    public List<Order> findByBookId(Long bookId) throws SQLException {
        String sql = "SELECT * FROM orders WHERE book_id = ? ORDER BY order_date DESC";
        List<Order> orders = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, bookId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
        }
        return orders;
    }

    // ===== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ =====

    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getLong("id"));
        order.setBookId(rs.getLong("book_id"));
        order.setCustomerName(rs.getString("customer_name"));
        order.setCustomerPhone(rs.getString("customer_phone"));
        order.setCustomerEmail(rs.getString("customer_email"));
        order.setCustomerAddress(rs.getString("customer_address"));
        String status = rs.getString("status");
        order.setStatus(status != null ? OrderStatus.valueOf(status) : null);
        order.setOrderDate(rs.getTimestamp("order_date") != null ?
                rs.getTimestamp("order_date").toLocalDateTime() : null);
        order.setCompletionDate(rs.getTimestamp("completion_date") != null ?
                rs.getTimestamp("completion_date").toLocalDateTime() : null);
        order.setTotalPrice(rs.getDouble("total_price"));
        order.setQuantity(rs.getInt("quantity"));
        return order;
    }

    private void setOrderParameters(PreparedStatement stmt, Order order) throws SQLException {
        stmt.setLong(1, order.getBookId());
        stmt.setString(2, order.getCustomerName());
        stmt.setString(3, order.getCustomerPhone());
        stmt.setString(4, order.getCustomerEmail());
        stmt.setString(5, order.getCustomerAddress());
        stmt.setString(6, order.getStatus() != null ? order.getStatus().name() : null);
        stmt.setTimestamp(7, order.getOrderDate() != null ?
                Timestamp.valueOf(order.getOrderDate()) : null);
        stmt.setTimestamp(8, order.getCompletionDate() != null ?
                Timestamp.valueOf(order.getCompletionDate()) : null);
        stmt.setDouble(9, order.getTotalPrice());
        stmt.setInt(10, order.getQuantity());
    }
}