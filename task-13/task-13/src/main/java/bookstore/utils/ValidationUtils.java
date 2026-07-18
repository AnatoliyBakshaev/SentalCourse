package bookstore.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class ValidationUtils {

    // Паттерны для валидации
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+?[0-9\\-\\s]{10,15}$");

    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[a-zA-Zа-яА-Я\\s\\-]{2,50}$");

    private static final Pattern ISBN_PATTERN =
            Pattern.compile("^(97[89])?[0-9]{10,13}$");

    // Валидация email
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    // Валидация телефона
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }

    // Валидация имени
    public static boolean isValidName(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        return NAME_PATTERN.matcher(name).matches();
    }

    // Валидация ISBN
    public static boolean isValidIsbn(String isbn) {
        if (isbn == null || isbn.isEmpty()) {
            return false;
        }
        return ISBN_PATTERN.matcher(isbn).matches();
    }

    // Валидация цены
    public static boolean isValidPrice(double price) {
        return price > 0 && price < 1000000;
    }

    // Валидация количества
    public static boolean isValidQuantity(int quantity) {
        return quantity > 0 && quantity < 10000;
    }

    // Валидация ID
    public static boolean isValidId(int id) {
        return id > 0;
    }

    // Валидация даты
    public static boolean isValidDate(LocalDate date) {
        return date != null && !date.isAfter(LocalDate.now());
    }

    // Валидация даты в строке
    public static boolean isValidDateString(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return false;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            LocalDate.parse(dateStr, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    // Валидация диапазона дат
    public static boolean isValidDateRange(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            return false;
        }
        return !start.isAfter(end);
    }

    // Проверка на положительное число
    public static boolean isPositive(double value) {
        return value > 0;
    }

    // Проверка на неотрицательное число
    public static boolean isNonNegative(int value) {
        return value >= 0;
    }

    // Валидация статуса заказа
    public static boolean isValidOrderStatus(String status) {
        if (status == null) {
            return false;
        }
        return status.equals("новый") ||
                status.equals("выполнен") ||
                status.equals("отменен");
    }

    // Валидация статуса книги
    public static boolean isValidBookStatus(String status) {
        if (status == null) {
            return false;
        }
        return status.equals("в наличии") ||
                status.equals("отсутствует") ||
                status.equals("списана");
    }

    // Очистка строки от лишних пробелов
    public static String cleanString(String input) {
        if (input == null) {
            return "";
        }
        return input.trim().replaceAll("\\s+", " ");
    }

    // Проверка на пустую строку
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    // Валидация адреса
    public static boolean isValidAddress(String address) {
        if (address == null || address.isEmpty()) {
            return false;
        }
        return address.length() >= 5 && address.length() <= 200;
    }
}