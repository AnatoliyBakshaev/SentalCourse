import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Request {
    private int bookId;
    private String customerName;
    private String customerPhone;
    private LocalDateTime requestDate;
    private boolean fulfilled;

    public Request(int bookId, String customerName, String customerPhone) {
        this.bookId = bookId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.requestDate = LocalDateTime.now();
        this.fulfilled = false;
    }

    // Геттеры
    public int getBookId() { return bookId; }
    public String getCustomerName() { return customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public LocalDateTime getRequestDate() { return requestDate; }
    public boolean isFulfilled() { return fulfilled; }

    public void markFulfilled() {
        this.fulfilled = true;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return String.format("Запрос: книга #%d, клиент: %s, тел: %s, дата: %s, выполнен: %s",
                bookId, customerName, customerPhone, requestDate.format(formatter),
                fulfilled ? "Да" : "Нет");
    }
}