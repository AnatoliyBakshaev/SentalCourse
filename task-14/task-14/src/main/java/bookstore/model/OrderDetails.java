package bookstore.model;

import bookstore.model.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public class OrderDetails {
    private int orderId;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String customerAddress;
    private List<Book> books;
    private double totalPrice;
    private LocalDateTime orderDate;
    private LocalDateTime completionDate;
    private OrderStatus status;

    public OrderDetails(int orderId, String customerName, String customerPhone,
                        String customerEmail, String customerAddress, List<Book> books,
                        double totalPrice, LocalDateTime orderDate,
                        LocalDateTime completionDate, OrderStatus status) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.customerEmail = customerEmail;
        this.customerAddress = customerAddress;
        this.books = books;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
        this.completionDate = completionDate;
        this.status = status;
    }

    public int getOrderId() { return orderId; }
    public String getCustomerName() { return customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public String getCustomerEmail() { return customerEmail; }
    public String getCustomerAddress() { return customerAddress; }
    public List<Book> getBooks() { return books; }
    public double getTotalPrice() { return totalPrice; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public LocalDateTime getCompletionDate() { return completionDate; }
    public OrderStatus getStatus() { return status; }
}
