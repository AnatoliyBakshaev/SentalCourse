package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Request implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private int bookId;
    private String customerName;
    private String customerPhone;
    private LocalDateTime requestDate;
    private boolean fulfilled;

    public Request(int id, int bookId, String customerName, String customerPhone) {
        this.id = id;
        this.bookId = bookId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.requestDate = LocalDateTime.now();
        this.fulfilled = false;
    }

    // Геттеры
    public int getId() { return id; }
    public int getBookId() { return bookId; }
    public String getCustomerName() { return customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public LocalDateTime getRequestDate() { return requestDate; }
    public boolean isFulfilled() { return fulfilled; }

    // Сеттеры
    public void setId(int id) { this.id = id; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    public void setRequestDate(LocalDateTime requestDate) { this.requestDate = requestDate; }
    public void setFulfilled(boolean fulfilled) { this.fulfilled = fulfilled; }

    public void markFulfilled() {
        this.fulfilled = true;
    }

    @Override
    public String toString() {
        return String.format("запрос #%d: книга #%d, клиент: %s, выполнен: %s",
                id, bookId, customerName, fulfilled ? "да" : "нет");
    }
}