package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
    private static AppConfig instance;
    private final Properties properties;

    // Константы по умолчанию
    private static final int DEFAULT_OLD_MONTHS = 6;
    private static final boolean DEFAULT_AUTO_FULFILL = true;
    private static final String DEFAULT_DELIMITER = ",";
    private static final boolean DEFAULT_SKIP_HEADER = true;
    private static final String DEFAULT_LOG_LEVEL = "INFO";
    private static final boolean DEFAULT_SHOW_STACKTRACE = false;

    private AppConfig() {
        this.properties = new Properties();
        loadProperties();
    }

    public static AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("config/application.properties")) {

            if (input == null) {
                System.out.println("файл конфигурации не найден, используются значения по умолчанию");
                return;
            }

            properties.load(input);
            System.out.println("конфигурация загружена успешно");

        } catch (IOException e) {
            System.out.println("ошибка загрузки конфигурации: " + e.getMessage());
            System.out.println("используются значения по умолчанию");
        }
    }

    // ===== ГЕТТЕРЫ ДЛЯ НАСТРОЕК =====

    public int getOldMonths() {
        try {
            String value = properties.getProperty("book.old.months");
            if (value == null || value.trim().isEmpty()) {
                return DEFAULT_OLD_MONTHS;
            }
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            System.out.println("ошибка парсинга book.old.months, используется значение по умолчанию: " + DEFAULT_OLD_MONTHS);
            return DEFAULT_OLD_MONTHS;
        }
    }

    public boolean isAutoFulfillRequests() {
        try {
            String value = properties.getProperty("book.request.auto.fulfill");
            if (value == null || value.trim().isEmpty()) {
                return DEFAULT_AUTO_FULFILL;
            }
            return Boolean.parseBoolean(value.trim());
        } catch (Exception e) {
            System.out.println("ошибка парсинга book.request.auto.fulfill, используется значение по умолчанию: " + DEFAULT_AUTO_FULFILL);
            return DEFAULT_AUTO_FULFILL;
        }
    }

    public String getCsvDelimiter() {
        String value = properties.getProperty("csv.export.delimiter");
        if (value == null || value.trim().isEmpty()) {
            return DEFAULT_DELIMITER;
        }
        return value.trim();
    }

    public boolean isSkipHeader() {
        String value = properties.getProperty("csv.import.skip.header");
        if (value == null || value.trim().isEmpty()) {
            return DEFAULT_SKIP_HEADER;
        }
        return Boolean.parseBoolean(value.trim());
    }

    public String getLogLevel() {
        String value = properties.getProperty("log.level");
        if (value == null || value.trim().isEmpty()) {
            return DEFAULT_LOG_LEVEL;
        }
        return value.trim().toUpperCase();
    }

    public boolean isShowStacktrace() {
        String value = properties.getProperty("log.show.stacktrace");
        if (value == null || value.trim().isEmpty()) {
            return DEFAULT_SHOW_STACKTRACE;
        }
        return Boolean.parseBoolean(value.trim());
    }

    // ===== МЕТОД ДЛЯ ОТОБРАЖЕНИЯ ТЕКУЩИХ НАСТРОЕК =====

    public void showConfig() {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("  текущие настройки");
        System.out.println("==================================================");
        System.out.println("  залежавшиеся книги (месяцев): " + getOldMonths());
        System.out.println("  авто-выполнение запросов: " + (isAutoFulfillRequests() ? "включено" : "отключено"));
        System.out.println("  разделитель csv: '" + getCsvDelimiter() + "'");
        System.out.println("  пропуск заголовка csv: " + (isSkipHeader() ? "да" : "нет"));
        System.out.println("  уровень логирования: " + getLogLevel());
        System.out.println("==================================================");
    }

    // ===== МЕТОДЫ ДЛЯ ОБНОВЛЕНИЯ НАСТРОЕК (ОПЦИОНАЛЬНО) =====

    public void setOldMonths(int months) {
        if (months > 0) {
            properties.setProperty("book.old.months", String.valueOf(months));
        }
    }

    public void setAutoFulfillRequests(boolean autoFulfill) {
        properties.setProperty("book.request.auto.fulfill", String.valueOf(autoFulfill));
    }
}