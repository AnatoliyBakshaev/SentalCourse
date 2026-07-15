package model;

import model.enums.OrderStatus;
import java.time.LocalDateTime;

public class Order {
    private int id;
    private int bookId;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String customerAddress;
    private OrderStatus status;
    private LocalDateTime orderDate;
    private LocalDateTime completionDate;
    private double totalPrice;
    private int quantity;

    public Order(int id, int bookId, String customerName, String customerPhone,
                 String customerEmail, String customerAddress, double bookPrice) {
        this.id = id;
        this.bookId = bookId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.customerEmail = customerEmail;
        this.customerAddress = customerAddress;
        this.status = OrderStatus.NEW;
        this.orderDate = LocalDateTime.now();
        this.completionDate = null;
        this.totalPrice = bookPrice;
        this.quantity = 1;
    }

    // Геттеры
    public int getId() { return id; }
    public int getBookId() { return bookId; }
    public String getCustomerName() { return customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public String getCustomerEmail() { return customerEmail; }
    public String getCustomerAddress() { return customerAddress; }
    public OrderStatus getStatus() { return status; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public LocalDateTime getCompletionDate() { return completionDate; }
    public double getTotalPrice() { return totalPrice; }
    public int getQuantity() { return quantity; }

    public void changeStatus(OrderStatus newStatus) {
        this.status = newStatus;
        if (newStatus == OrderStatus.COMPLETED && completionDate == null) {
            this.completionDate = LocalDateTime.now();
        }
    }

    public void setCompletionDate(LocalDateTime completionDate) {
        this.completionDate = completionDate;
    }

    @Override
    public String toString() {
        return String.format("Заказ #%d: книга #%d, клиент: %s, статус: %s, сумма: %.2f руб.",
                id, bookId, customerName, status.getDescription(), totalPrice);
    }
}