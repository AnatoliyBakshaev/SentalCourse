import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String completion = completionDate != null ? completionDate.format(formatter) : "Не завершен";
        return String.format("Заказ #%d: книга #%d, клиент: %s, статус: %s, сумма: %.2f руб., дата: %s, завершен: %s",
                id, bookId, customerName, status.getDescription(), totalPrice,
                orderDate.format(formatter), completion);
    }
}