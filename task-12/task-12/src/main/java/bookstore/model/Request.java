package bookstore.model;

import java.time.LocalDateTime;

public class Request {
    private Long id;                    // ← БЫЛО int, СТАЛО Long
    private Long bookId;               // ← БЫЛО int, СТАЛО Long
    private String customerName;
    private String customerPhone;
    private LocalDateTime requestDate;
    private boolean fulfilled;

    // ===== КОНСТРУКТОРЫ =====

    public Request() {}

    public Request(Long bookId, String customerName, String customerPhone) {
        this.bookId = bookId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.requestDate = LocalDateTime.now();
        this.fulfilled = false;
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

    public LocalDateTime getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDateTime requestDate) { this.requestDate = requestDate; }

    public boolean isFulfilled() { return fulfilled; }
    public void setFulfilled(boolean fulfilled) { this.fulfilled = fulfilled; }

    // ===== БИЗНЕС-МЕТОДЫ =====

    public void markFulfilled() {
        this.fulfilled = true;
    }

    @Override
    public String toString() {
        return String.format("Request{id=%d, bookId=%d, customer='%s', fulfilled=%s}",
                id, bookId, customerName, fulfilled);
    }
}