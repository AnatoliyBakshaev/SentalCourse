package dao.impl;

import dao.RequestDAO;
import model.Request;
import config.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RequestDAOImpl implements RequestDAO {
    private final DatabaseConfig dbConfig;

    public RequestDAOImpl() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    @Override
    public Request getById(Long id) throws SQLException {
        String sql = "SELECT * FROM requests WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToRequest(rs);
            }
            return null;
        }
    }

    @Override
    public List<Request> getAll() throws SQLException {
        String sql = "SELECT * FROM requests ORDER BY id";
        List<Request> requests = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                requests.add(mapResultSetToRequest(rs));
            }
        }
        return requests;
    }

    @Override
    public List<Request> findAll() throws SQLException {
        return getAll();
    }

    @Override
    public boolean save(Request request) throws SQLException {
        String sql = "INSERT INTO requests (book_id, customer_name, customer_phone, " +
                "request_date, fulfilled) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = dbConfig.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setLong(1, request.getBookId());
                stmt.setString(2, request.getCustomerName());
                stmt.setString(3, request.getCustomerPhone());
                stmt.setTimestamp(4, request.getRequestDate() != null ?
                        Timestamp.valueOf(request.getRequestDate()) : null);
                stmt.setBoolean(5, request.isFulfilled());
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    ResultSet generatedKeys = stmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        request.setId(generatedKeys.getLong(1));
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
    public boolean update(Request request) throws SQLException {
        String sql = "UPDATE requests SET book_id = ?, customer_name = ?, customer_phone = ?, " +
                "request_date = ?, fulfilled = ? WHERE id = ?";
        Connection conn = null;
        try {
            conn = dbConfig.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, request.getBookId());
                stmt.setString(2, request.getCustomerName());
                stmt.setString(3, request.getCustomerPhone());
                stmt.setTimestamp(4, request.getRequestDate() != null ?
                        Timestamp.valueOf(request.getRequestDate()) : null);
                stmt.setBoolean(5, request.isFulfilled());
                stmt.setLong(6, request.getId());
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
        String sql = "DELETE FROM requests WHERE id = ?";
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
    public List<Request> findByBookId(Long bookId) throws SQLException {
        String sql = "SELECT * FROM requests WHERE book_id = ? ORDER BY request_date DESC";
        List<Request> requests = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, bookId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                requests.add(mapResultSetToRequest(rs));
            }
        }
        return requests;
    }

    @Override
    public List<Request> findByCustomer(String customerName) throws SQLException {
        String sql = "SELECT * FROM requests WHERE customer_name ILIKE ? ORDER BY request_date DESC";
        List<Request> requests = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + customerName + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                requests.add(mapResultSetToRequest(rs));
            }
        }
        return requests;
    }

    @Override
    public List<Request> findUnfulfilled() throws SQLException {
        String sql = "SELECT * FROM requests WHERE fulfilled = false ORDER BY request_date";
        List<Request> requests = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, false);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                requests.add(mapResultSetToRequest(rs));
            }
        }
        return requests;
    }

    @Override
    public List<Request> findFulfilled() throws SQLException {
        String sql = "SELECT * FROM requests WHERE fulfilled = true ORDER BY request_date DESC";
        List<Request> requests = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, true);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                requests.add(mapResultSetToRequest(rs));
            }
        }
        return requests;
    }

    @Override
    public boolean markFulfilled(Long requestId) throws SQLException {
        String sql = "UPDATE requests SET fulfilled = true WHERE id = ?";
        Connection conn = null;
        try {
            conn = dbConfig.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, requestId);
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
    public int getCountByBookId(Long bookId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM requests WHERE book_id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, bookId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }

    @Override
    public int getUnfulfilledCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM requests WHERE fulfilled = false";
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }

    // ===== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ =====

    private Request mapResultSetToRequest(ResultSet rs) throws SQLException {
        Request request = new Request();
        request.setId(rs.getLong("id"));
        request.setBookId(rs.getLong("book_id"));
        request.setCustomerName(rs.getString("customer_name"));
        request.setCustomerPhone(rs.getString("customer_phone"));
        request.setRequestDate(rs.getTimestamp("request_date") != null ?
                rs.getTimestamp("request_date").toLocalDateTime() : null);
        request.setFulfilled(rs.getBoolean("fulfilled"));
        return request;
    }
}