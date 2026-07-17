package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConfig {
    private static DatabaseConfig instance;
    private Properties properties;
    private Connection connection;

    private DatabaseConfig() {
        loadProperties();
    }

    public static DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }

    private void loadProperties() {
        this.properties = new Properties();

        String[] paths = {
                "bookstore/config/jdbc.properties",
                "config/jdbc.properties",
                "jdbc.properties",
                "src/bookstore/config/jdbc.properties"
        };

        for (String path : paths) {
            try (InputStream input = getClass().getClassLoader()
                    .getResourceAsStream(path)) {
                if (input != null) {
                    properties.load(input);
                    System.out.println("jdbc.properties загружен из: " + path);
                    loadDriver();
                    return;
                }
            } catch (IOException e) {
                // Игнорируем
            }
        }

        // Пробуем из файловой системы
        try (InputStream input = new FileInputStream("jdbc.properties")) {
            properties.load(input);
            System.out.println("jdbc.properties загружен из файловой системы");
            loadDriver();
            return;
        } catch (IOException e) {
            throw new RuntimeException("jdbc.properties not found!", e);
        }
    }

    private void loadDriver() {
        String driver = properties.getProperty("db.driver");
        if (driver != null && !driver.isEmpty()) {
            try {
                Class.forName(driver);
                System.out.println("Драйвер загружен: " + driver);
            } catch (ClassNotFoundException e) {
                System.err.println("Драйвер не найден: " + driver);
                System.err.println("Скачайте PostgreSQL JDBC драйвер и добавьте в classpath");
                System.err.println("https://jdbc.postgresql.org/download/");
            }
        }
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            String url = properties.getProperty("db.url");
            String username = properties.getProperty("db.username");
            String password = properties.getProperty("db.password");

            connection = DriverManager.getConnection(url, username, password);
            connection.setAutoCommit(false);
            System.out.println("Подключение к БД установлено");
        }
        return connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Подключение к БД закрыто");
            } catch (SQLException e) {
                System.err.println("Ошибка закрытия соединения: " + e.getMessage());
            }
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}