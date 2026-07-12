import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class Order {
    private int id;
    private int bookId;
    private String customerName;
    private OrderStatus status;
    private LocalDateTime orderDate;

    public Order(int id, int bookId, String customerName) {
        this.id = id;
        this.bookId = bookId;
        this.customerName = customerName;
        this.status = OrderStatus.NEW;
        this.orderDate = LocalDateTime.now();
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public int getBookId() { return bookId; }
    public String getCustomerName() { return customerName; }
    public OrderStatus getStatus() { return status; }
    public LocalDateTime getOrderDate() { return orderDate; }

    public void changeStatus(OrderStatus newStatus) {
        this.status = newStatus;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return String.format("Заказ #%d: книга #%d, клиент: %s, статус: %s, дата: %s",
                id, bookId, customerName, status.getDescription(),
                orderDate.format(formatter));
    }
}
