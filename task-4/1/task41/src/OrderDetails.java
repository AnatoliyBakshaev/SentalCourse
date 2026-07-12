import java.time.LocalDateTime;
import java.util.List;
import java.time.format.DateTimeFormatter;

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

    public void displayDetails() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        System.out.println("детали заказа" + orderId);

        System.out.println("Информация о заказе:");
        System.out.println("  Статус: " + status.getDescription());
        System.out.println("  Дата заказа: " + orderDate.format(formatter));
        System.out.println("  Дата выполнения: " + (completionDate != null ?
                completionDate.format(formatter) : "Не выполнен"));
        System.out.println("  Общая сумма: " + String.format("%.2f", totalPrice) + " руб.");
        System.out.println("Данные заказчика:");
        System.out.println("  Имя: " + customerName);
        System.out.println("  Телефон: " + customerPhone);
        System.out.println("  Email: " + customerEmail);
        System.out.println("  Адрес: " + customerAddress);
        System.out.println("Заказанные книги:");
        for (Book book : books) {
            System.out.println("  • " + book.getTitle() + " - " + book.getAuthor() +
                    " (" + String.format("%.2f", book.getPrice()) + " руб.)");
        }
    }
}
