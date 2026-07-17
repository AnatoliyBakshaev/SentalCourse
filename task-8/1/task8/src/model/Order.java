package model;

import model.enums.OrderStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Order implements Serializable {
    private static final long serialVersionUID = 1L;
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

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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