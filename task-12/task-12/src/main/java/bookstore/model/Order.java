package bookstore.model;

import bookstore.model.enums.OrderStatus;
import java.time.LocalDateTime;

public class Order {
    private Long id;                    // ← БЫЛО int, СТАЛО Long
    private Long bookId;               // ← БЫЛО int, СТАЛО Long
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String customerAddress;
    private OrderStatus status;
    private LocalDateTime orderDate;
    private LocalDateTime completionDate;
    private double totalPrice;
    private int quantity;

    // ===== КОНСТРУКТОРЫ =====

    public Order() {}

    public Order(Long bookId, String customerName, String customerPhone,
                 String customerEmail, String customerAddress, double totalPrice) {
        this.bookId = bookId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.customerEmail = customerEmail;
        this.customerAddress = customerAddress;
        this.status = OrderStatus.NEW;
        this.orderDate = LocalDateTime.now();
        this.totalPrice = totalPrice;
        this.quantity = 1;
    }

    // ===== ГЕТТЕРЫ И СЕТТЕРЫ =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getCustomerAddress() { return customerAddress; }
    public void setCustomerAddress(String customerAddress) { this.customerAddress = customerAddress; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public LocalDateTime getCompletionDate() { return completionDate; }
    public void setCompletionDate(LocalDateTime completionDate) { this.completionDate = completionDate; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    // ===== БИЗНЕС-МЕТОДЫ =====

    public void complete() {
        this.status = OrderStatus.COMPLETED;
        this.completionDate = LocalDateTime.now();
    }

    public void cancel() {
        this.status = OrderStatus.CANCELLED;
    }

    @Override
    public String toString() {
        return String.format("Order{id=%d, bookId=%d, customer='%s', status=%s, total=%.2f}",
                id, bookId, customerName, status, totalPrice);
    }
}