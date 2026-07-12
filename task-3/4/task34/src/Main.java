// BookStoreTest.java
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("тест");

        List<Book> initialBooks = Arrays.asList(
                new Book(1, "Война и мир", "Лев Толстой", "978-5-17-118636-8", BookStatus.IN_STOCK),
                new Book(2, "Преступление и наказание", "Федор Достоевский", "978-5-04-105222-3", BookStatus.OUT_OF_STOCK),
                new Book(3, "Мастер и Маргарита", "Михаил Булгаков", "978-5-17-090381-2", BookStatus.IN_STOCK),
                new Book(4, "1984", "Джордж Оруэлл", "978-5-17-102235-8", BookStatus.OUT_OF_STOCK),
                new Book(5, "Улисс", "Джеймс Джойс", "978-5-17-101234-2", BookStatus.IN_STOCK)
        );


        IBookStore store = new BookStore(initialBooks);


        System.out.println("ТЕСТ 1: Создание заказа на книгу в наличии");
        store.createOrder(1, "Иван Петров");

        System.out.println("ТЕСТ 2: Создание заказа на отсутствующую книгу");
        store.createOrder(2, "Мария Сидорова");

        System.out.println("ТЕСТ 3: Создание заказа на списанную книгу");
        store.writeOffBook(5);
        store.createOrder(5, "Петр Иванов");

        System.out.println("ТЕСТ 4: Добавление книги на склад");
        store.addBookToStock(2, 3);

        System.out.println("ТЕСТ 5: Изменение статуса заказа");
        store.changeOrderStatus(2, OrderStatus.COMPLETED);

        System.out.println("ТЕСТ 6: Оставление запроса на книгу");
        store.leaveRequest(4, "Алексей Смирнов");

        System.out.println("ТЕСТ 7: Отмена заказа");
        store.cancelOrder(1);

        System.out.println("ТЕСТ 8: Попытка отменить выполненный заказ");
        store.cancelOrder(2);

        System.out.println("ТЕСТ 9: Добавление книги, на которую есть запросы");
        store.leaveRequest(4, "Сергей Козлов");
        store.addBookToStock(4, 2);

        System.out.println("ТЕСТ 10: Изменение статуса заказа на отменен");
        store.changeOrderStatus(3, OrderStatus.CANCELLED);

    }
}