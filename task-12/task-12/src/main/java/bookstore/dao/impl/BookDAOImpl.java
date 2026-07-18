package bookstore.dao.impl;

import bookstore.dao.BookDAO;
import bookstore.model.Book;
import bookstore.model.enums.BookStatus;
import bookstore.config.DatabaseConfig;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class BookDAOImpl implements BookDAO {
    private final DatabaseConfig dbConfig;

    public BookDAOImpl() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    @Override
    public Book getById(Long id) throws SQLException {
        String sql = "SELECT * FROM books WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToBook(rs);
            }
            return null;
        }
    }

    @Override
    public List<Book> getAll() throws SQLException {
        String sql = "SELECT * FROM books ORDER BY id";
        List<Book> books = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        }
        return books;
    }

    @Override
    public List<Book> findAll() throws SQLException {
        return getAll();
    }

    @Override
    public boolean save(Book book) throws SQLException {
        String sql = "INSERT INTO books (title, author, isbn, status, quantity, price, " +
                "publication_date, received_date, description, genre, publisher, pages) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = dbConfig.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                setBookParameters(stmt, book);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    ResultSet generatedKeys = stmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        book.setId(generatedKeys.getLong(1));
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
    public boolean update(Book book) throws SQLException {
        String sql = "UPDATE books SET title = ?, author = ?, isbn = ?, status = ?, " +
                "quantity = ?, price = ?, publication_date = ?, received_date = ?, " +
                "description = ?, genre = ?, publisher = ?, pages = ? WHERE id = ?";
        Connection conn = null;
        try {
            conn = dbConfig.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                setBookParameters(stmt, book);
                stmt.setLong(13, book.getId());
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
        String sql = "DELETE FROM books WHERE id = ?";
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
    public Book findByIsbn(String isbn) throws SQLException {
        String sql = "SELECT * FROM books WHERE isbn = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToBook(rs);
            }
            return null;
        }
    }

    @Override
    public List<Book> findByStatus(String status) throws SQLException {
        String sql = "SELECT * FROM books WHERE status = ? ORDER BY title";
        List<Book> books = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        }
        return books;
    }

    @Override
    public List<Book> findByAuthor(String author) throws SQLException {
        String sql = "SELECT * FROM books WHERE author ILIKE ? ORDER BY title";
        List<Book> books = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + author + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        }
        return books;
    }

    @Override
    public List<Book> findOldBooks(int months) throws SQLException {
        LocalDate thresholdDate = LocalDate.now().minus(months, ChronoUnit.MONTHS);
        String sql = "SELECT * FROM books WHERE status = 'IN_STOCK' AND received_date <= ? ORDER BY received_date";
        List<Book> books = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(thresholdDate));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        }
        return books;
    }

    @Override
    public boolean updateQuantity(Long id, int quantity) throws SQLException {
        String sql = "UPDATE books SET quantity = ? WHERE id = ?";
        Connection conn = null;
        try {
            conn = dbConfig.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, quantity);
                stmt.setLong(2, id);
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
    public List<Book> findByTitle(String title) throws SQLException {
        String sql = "SELECT * FROM books WHERE title ILIKE ? ORDER BY title";
        List<Book> books = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + title + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        }
        return books;
    }

    @Override
    public List<Book> findByGenre(String genre) throws SQLException {
        String sql = "SELECT * FROM books WHERE genre = ? ORDER BY title";
        List<Book> books = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, genre);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        }
        return books;
    }

    @Override
    public int getCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM books";
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

    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setId(rs.getLong("id"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setIsbn(rs.getString("isbn"));
        String status = rs.getString("status");
        book.setStatus(status != null ? BookStatus.valueOf(status) : null);
        book.setQuantity(rs.getInt("quantity"));
        book.setPrice(rs.getDouble("price"));
        book.setPublicationDate(rs.getDate("publication_date") != null ?
                rs.getDate("publication_date").toLocalDate() : null);
        book.setReceivedDate(rs.getDate("received_date") != null ?
                rs.getDate("received_date").toLocalDate() : null);
        book.setDescription(rs.getString("description"));
        book.setGenre(rs.getString("genre"));
        book.setPublisher(rs.getString("publisher"));
        book.setPages(rs.getInt("pages"));
        return book;
    }

    private void setBookParameters(PreparedStatement stmt, Book book) throws SQLException {
        stmt.setString(1, book.getTitle());
        stmt.setString(2, book.getAuthor());
        stmt.setString(3, book.getIsbn());
        stmt.setString(4, book.getStatus() != null ? book.getStatus().name() : null);
        stmt.setInt(5, book.getQuantity());
        stmt.setDouble(6, book.getPrice());
        stmt.setDate(7, book.getPublicationDate() != null ?
                Date.valueOf(book.getPublicationDate()) : null);
        stmt.setDate(8, book.getReceivedDate() != null ?
                Date.valueOf(book.getReceivedDate()) : null);
        stmt.setString(9, book.getDescription());
        stmt.setString(10, book.getGenre());
        stmt.setString(11, book.getPublisher());
        stmt.setInt(12, book.getPages());
    }
}