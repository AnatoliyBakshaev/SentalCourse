package bookstore.service;


import bookstore.config.AppConfig;
import bookstore.model.Book;
import bookstore.model.Order;
import bookstore.model.Request;
import bookstore.model.enums.BookStatus;
import bookstore.model.enums.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class CsvImportExportService {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    @Autowired
    private AppConfig config;

    public CsvImportExportService() {}

    // ===== ЭКСПОРТ =====

    public void exportBooks(List<Book> books, String filePath) throws IOException {
        ensureDirectoryExists(filePath);
        String delimiter = config.getCsvDelimiter();

        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(filePath), StandardCharsets.UTF_8))) {

            writer.println(String.join(delimiter,
                    "id", "title", "author", "isbn", "status", "quantity",
                    "price", "publicationDate", "receivedDate", "description",
                    "genre", "publisher", "pages"));

            for (Book book : books) {
                writer.println(String.join(delimiter,
                        String.valueOf(book.getId()),
                        escapeCsv(book.getTitle()),
                        escapeCsv(book.getAuthor()),
                        escapeCsv(book.getIsbn()),
                        book.getStatus().name(),
                        String.valueOf(book.getQuantity()),
                        String.valueOf(book.getPrice()),
                        book.getPublicationDate() != null ? book.getPublicationDate().format(DATE_FORMATTER) : "",
                        book.getReceivedDate() != null ? book.getReceivedDate().format(DATE_FORMATTER) : "",
                        escapeCsv(book.getDescription()),
                        escapeCsv(book.getGenre()),
                        escapeCsv(book.getPublisher()),
                        String.valueOf(book.getPages())
                ));
            }
        }
    }

    public void exportOrders(List<Order> orders, String filePath) throws IOException {
        ensureDirectoryExists(filePath);
        String delimiter = config.getCsvDelimiter();

        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(filePath), StandardCharsets.UTF_8))) {

            writer.println(String.join(delimiter,
                    "id", "bookId", "customerName", "customerPhone", "customerEmail",
                    "customerAddress", "status", "orderDate", "completionDate",
                    "totalPrice", "quantity"));

            for (Order order : orders) {
                writer.println(String.join(delimiter,
                        String.valueOf(order.getId()),
                        String.valueOf(order.getBookId()),
                        escapeCsv(order.getCustomerName()),
                        escapeCsv(order.getCustomerPhone()),
                        escapeCsv(order.getCustomerEmail()),
                        escapeCsv(order.getCustomerAddress()),
                        order.getStatus().name(),
                        order.getOrderDate() != null ? order.getOrderDate().format(DATE_TIME_FORMATTER) : "",
                        order.getCompletionDate() != null ? order.getCompletionDate().format(DATE_TIME_FORMATTER) : "",
                        String.valueOf(order.getTotalPrice()),
                        String.valueOf(order.getQuantity())
                ));
            }
        }
    }

    public void exportRequests(List<Request> requests, String filePath) throws IOException {
        ensureDirectoryExists(filePath);
        String delimiter = config.getCsvDelimiter();

        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(filePath), StandardCharsets.UTF_8))) {

            writer.println(String.join(delimiter,
                    "id", "bookId", "customerName", "customerPhone", "requestDate", "fulfilled"));

            for (Request request : requests) {
                writer.println(String.join(delimiter,
                        String.valueOf(request.getId()),
                        String.valueOf(request.getBookId()),
                        escapeCsv(request.getCustomerName()),
                        escapeCsv(request.getCustomerPhone()),
                        request.getRequestDate() != null ? request.getRequestDate().format(DATE_TIME_FORMATTER) : "",
                        String.valueOf(request.isFulfilled())
                ));
            }
        }
    }

    // ===== ИМПОРТ =====

    public List<Book> importBooks(String filePath) throws IOException {
        List<Book> books = new ArrayList<>();
        String delimiter = config.getCsvDelimiter();
        boolean skipHeader = config.isSkipHeader();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(filePath), StandardCharsets.UTF_8))) {

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (lineNumber == 1 && skipHeader) {
                    continue;
                }

                String[] parts = parseCsvLine(line, delimiter);
                if (parts.length < 13) {
                    System.err.println("пропущена строка " + lineNumber + " (недостаточно полей): " + line);
                    continue;
                }

                try {
                    Long id = Long.parseLong(parts[0].trim());
                    String title = parts[1].trim();
                    String author = parts[2].trim();
                    String isbn = parts[3].trim();
                    BookStatus status = BookStatus.valueOf(parts[4].trim());
                    int quantity = Integer.parseInt(parts[5].trim());
                    double price = Double.parseDouble(parts[6].trim());

                    LocalDate publicationDate = parts[7].isEmpty() ? null : LocalDate.parse(parts[7].trim(), DATE_FORMATTER);
                    LocalDate receivedDate = parts[8].isEmpty() ? LocalDate.now() : LocalDate.parse(parts[8].trim(), DATE_FORMATTER);
                    String description = parts[9].trim();
                    String genre = parts[10].trim();
                    String publisher = parts[11].trim();
                    int pages = Integer.parseInt(parts[12].trim());

                    Book book = new Book(
                            id, title, author, isbn, status, price,
                            publicationDate, description, genre, publisher, pages
                    );

                    book.setReceivedDate(receivedDate);

                    if (quantity > 1) {
                        book.addQuantity(quantity - 1);
                    } else if (quantity == 0 && status == BookStatus.IN_STOCK) {
                        book.changeStatus(BookStatus.OUT_OF_STOCK);
                    }

                    books.add(book);

                } catch (NumberFormatException e) {
                    System.err.println("ошибка парсинга числа в строке " + lineNumber + ": " + line);
                    if (config.isShowStacktrace()) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    System.err.println("ошибка парсинга строки " + lineNumber + ": " + line);
                    if (config.isShowStacktrace()) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return books;
    }

    public List<Order> importOrders(String filePath) throws IOException {
        List<Order> orders = new ArrayList<>();
        String delimiter = config.getCsvDelimiter();
        boolean skipHeader = config.isSkipHeader();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(filePath), StandardCharsets.UTF_8))) {

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (lineNumber == 1 && skipHeader) {
                    continue;
                }

                String[] parts = parseCsvLine(line, delimiter);
                if (parts.length < 11) {
                    System.err.println("пропущена строка " + lineNumber + " (недостаточно полей): " + line);
                    continue;
                }

                try {
                    Long id = Long.parseLong(parts[0].trim());
                    Long bookId = Long.parseLong(parts[1].trim());
                    String customerName = parts[2].trim();
                    String customerPhone = parts[3].trim();
                    String customerEmail = parts[4].trim();
                    String customerAddress = parts[5].trim();
                    OrderStatus status = OrderStatus.valueOf(parts[6].trim());
                    LocalDateTime orderDate = LocalDateTime.parse(parts[7].trim(), DATE_TIME_FORMATTER);
                    LocalDateTime completionDate = parts[8].isEmpty() ? null : LocalDateTime.parse(parts[8].trim(), DATE_TIME_FORMATTER);
                    double totalPrice = Double.parseDouble(parts[9].trim());
                    int quantity = Integer.parseInt(parts[10].trim());

                    Order order = new Order();
                    order.setId(id);
                    order.setBookId(bookId);
                    order.setCustomerName(customerName);
                    order.setCustomerPhone(customerPhone);
                    order.setCustomerEmail(customerEmail);
                    order.setCustomerAddress(customerAddress);
                    order.setOrderDate(orderDate);
                    order.setCompletionDate(completionDate);
                    order.setTotalPrice(totalPrice);
                    order.setQuantity(quantity);
                    if (status != OrderStatus.NEW) {
                        order.setStatus(status);
                    }

                    orders.add(order);

                } catch (Exception e) {
                    System.err.println("ошибка парсинга строки " + lineNumber + ": " + line);
                    if (config.isShowStacktrace()) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return orders;
    }

    public List<Request> importRequests(String filePath) throws IOException {
        List<Request> requests = new ArrayList<>();
        String delimiter = config.getCsvDelimiter();
        boolean skipHeader = config.isSkipHeader();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(filePath), StandardCharsets.UTF_8))) {

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (lineNumber == 1 && skipHeader) {
                    continue;
                }

                String[] parts = parseCsvLine(line, delimiter);
                if (parts.length < 6) {
                    System.err.println("пропущена строка " + lineNumber + " (недостаточно полей): " + line);
                    continue;
                }

                try {
                    Long id = Long.parseLong(parts[0].trim());
                    Long bookId = Long.parseLong(parts[1].trim());
                    String customerName = parts[2].trim();
                    String customerPhone = parts[3].trim();
                    LocalDateTime requestDate = parts[4].isEmpty() ? LocalDateTime.now() : LocalDateTime.parse(parts[4].trim(), DATE_TIME_FORMATTER);
                    boolean fulfilled = Boolean.parseBoolean(parts[5].trim());

                    Request request = new Request();
                    request.setId(id);
                    request.setBookId(bookId);
                    request.setCustomerName(customerName);
                    request.setCustomerPhone(customerPhone);
                    request.setRequestDate(requestDate);
                    request.setFulfilled(fulfilled);

                    requests.add(request);

                } catch (Exception e) {
                    System.err.println("ошибка парсинга строки " + lineNumber + ": " + line);
                    if (config.isShowStacktrace()) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return requests;
    }

    // ===== MERGE МЕТОДЫ =====

    // ✅ ИСПРАВЛЕНО: Map<Long, Book>
    public void mergeBooks(List<Book> importedBooks, Map<Long, Book> existingBooks) {
        int updated = 0;
        int added = 0;

        for (Book imported : importedBooks) {
            if (existingBooks.containsKey(imported.getId())) {
                Book existing = existingBooks.get(imported.getId());
                existing.setTitle(imported.getTitle());
                existing.setAuthor(imported.getAuthor());
                existing.setIsbn(imported.getIsbn());
                existing.setPrice(imported.getPrice());
                existing.setDescription(imported.getDescription());
                existing.setGenre(imported.getGenre());
                existing.setPublisher(imported.getPublisher());
                existing.setPages(imported.getPages());
                existing.setReceivedDate(imported.getReceivedDate());

                int diff = imported.getQuantity() - existing.getQuantity();
                if (diff > 0) {
                    existing.addQuantity(diff);
                } else if (diff < 0) {
                    for (int i = 0; i < Math.abs(diff); i++) {
                        existing.reduceQuantity(1);
                    }
                }
                updated++;
            } else {
                existingBooks.put(imported.getId(), imported);
                added++;
            }
        }

        System.out.println("обновлено: " + updated + ", добавлено: " + added);
    }

    // ✅ ИСПРАВЛЕНО: Map<Long, Order>
    public void mergeOrders(List<Order> importedOrders, Map<Long, Order> existingOrders) {
        int updated = 0;
        int added = 0;

        for (Order imported : importedOrders) {
            if (existingOrders.containsKey(imported.getId())) {
                Order existing = existingOrders.get(imported.getId());
                existing.setCustomerName(imported.getCustomerName());
                existing.setCustomerPhone(imported.getCustomerPhone());
                existing.setCustomerEmail(imported.getCustomerEmail());
                existing.setCustomerAddress(imported.getCustomerAddress());
                existing.setCompletionDate(imported.getCompletionDate());
                if (imported.getStatus() != existing.getStatus()) {
                    existing.setStatus(imported.getStatus());
                }
                updated++;
            } else {
                existingOrders.put(imported.getId(), imported);
                added++;
            }
        }

        System.out.println("обновлено: " + updated + ", добавлено: " + added);
    }

    // ✅ ИСПРАВЛЕНО: сравнение Long через equals()
    public void mergeRequests(List<Request> importedRequests, List<Request> existingRequests) {
        int updated = 0;
        int added = 0;

        for (Request imported : importedRequests) {
            boolean found = false;
            for (int i = 0; i < existingRequests.size(); i++) {
                if (existingRequests.get(i).getId().equals(imported.getId())) {
                    existingRequests.set(i, imported);
                    found = true;
                    updated++;
                    break;
                }
            }
            if (!found) {
                existingRequests.add(imported);
                added++;
            }
        }

        System.out.println("обновлено: " + updated + ", добавлено: " + added);
    }

    // ===== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ =====

    private void ensureDirectoryExists(String filePath) throws IOException {
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new IOException("не удалось создать директорию: " + parentDir.getAbsolutePath());
            }
        }
    }

    private String escapeCsv(String value) {
        String delimiter = config.getCsvDelimiter();
        if (value == null) return "";
        if (value.contains(delimiter) || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private String[] parseCsvLine(String line, String delimiter) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder field = new StringBuilder();

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == delimiter.charAt(0) && !inQuotes) {
                result.add(field.toString().trim());
                field.setLength(0);
            } else {
                field.append(c);
            }
        }
        result.add(field.toString().trim());
        return result.toArray(new String[0]);
    }
}