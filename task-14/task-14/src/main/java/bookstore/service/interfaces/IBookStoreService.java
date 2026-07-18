package bookstore.service.interfaces;

import bookstore.model.*;
import bookstore.model.enums.OrderStatus;
import bookstore.model.enums.SortType;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface IBookStoreService {
    // Основные операции
    void createOrder(int bookId, String customerName, String customerPhone,
                     String customerEmail, String customerAddress);
    void cancelOrder(int orderId);
    void changeOrderStatus(int orderId, OrderStatus status);
    void addBookToStock(int bookId, int quantity);
    void leaveRequest(int bookId, String customerName, String customerPhone);
    void writeOffBook(int bookId);

    // Просмотр
    List<Book> getBooks(SortType sortBy);
    List<Order> getOrders(SortType sortBy);
    List<Request> getRequests(SortType sortBy);
    List<Order> getCompletedOrders(LocalDate startDate, LocalDate endDate, SortType sortBy);

    // Статистика
    double getTotalRevenue(LocalDate startDate, LocalDate endDate);
    int getCompletedOrdersCount(LocalDate startDate, LocalDate endDate);
    List<Book> getOldBooks();

    // Детали
    OrderDetails getOrderDetails(int orderId);
    String getBookDescription(int bookId);
    void importBooks(String filePath) throws IOException;
    void importOrders(String filePath) throws IOException;
    void importRequests(String filePath) throws IOException;

    // Экспорт
    void exportBooks(String filePath) throws IOException;
    void exportOrders(String filePath) throws IOException;
    void exportRequests(String filePath) throws IOException;
    // КОНФИГУРАЦИЯ

    void showConfig();
    void setOldMonths(int months);
    void setAutoFulfillRequests(boolean autoFulfill);

    //
    void saveState();
    void loadState();
}