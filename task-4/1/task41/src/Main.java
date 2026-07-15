// BookStoreTest.java
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Тест");
        // Инициализация книг с расширенными данными
        List<Book> initialBooks = Arrays.asList(
                new Book(1, "Война и мир", "Лев Толстой", "978-5-17-118636-8",
                        BookStatus.IN_STOCK, 1500.0, LocalDate.of(1869, 1, 1),
                        "Эпический роман о жизни русского общества в эпоху Наполеоновских войн",
                        "Роман", "Эксмо", 1300),
                new Book(2, "Преступление и наказание", "Федор Достоевский", "978-5-04-105222-3",
                        BookStatus.OUT_OF_STOCK, 1200.0, LocalDate.of(1866, 1, 1),
                        "Психологический роман о студенте Раскольникове, совершившем убийство",
                        "Роман", "Азбука", 700),
                new Book(3, "Мастер и Маргарита", "Михаил Булгаков", "978-5-17-090381-2",
                        BookStatus.IN_STOCK, 1350.0, LocalDate.of(1967, 1, 1),
                        "Философский роман о визите сатаны в Москву 1930-х годов",
                        "Роман", "АСТ", 480),
                new Book(4, "1984", "Джордж Оруэлл", "978-5-17-102235-8",
                        BookStatus.OUT_OF_STOCK, 1100.0, LocalDate.of(1949, 6, 8),
                        "Антиутопия о тоталитарном режиме и Большом Брате",
                        "Фантастика", "Эксмо", 320),
                new Book(5, "Улисс", "Джеймс Джойс", "978-5-17-101234-2",
                        BookStatus.IN_STOCK, 2000.0, LocalDate.of(1922, 2, 2),
                        "Модернистский роман, описывающий один день в Дублине",
                        "Модернизм", "Иностранка", 750),
                new Book(6, "Анна Каренина", "Лев Толстой", "978-5-04-103456-4",
                        BookStatus.IN_STOCK, 1400.0, LocalDate.of(1877, 1, 1),
                        "Трагическая история любви замужней женщины и офицера Вронского",
                        "Роман", "Эксмо", 900)
        );

        // Устанавливаем дату поступления для "залежавшихся" книг
        Book book = initialBooks.get(0); // Война и мир
        book.setReceivedDate(LocalDate.now().minusMonths(8));
        book = initialBooks.get(2); // Мастер и Маргарита
        book.setReceivedDate(LocalDate.now().minusMonths(3));

        // Создание магазина
        BookStore store = new BookStore(initialBooks);

        // ========== ТЕСТЫ ОПЕРАЦИЙ ==========

        System.out.println("ТЕСТ 1: Создание заказа на книгу в наличии");
        store.createOrder(1, "Иван Петров", "+7-999-123-45-67",
                "ivan@mail.ru", "ул. Ленина, д. 1");

        System.out.println("ТЕСТ 2: Создание заказа на отсутствующую книгу");
        store.createOrder(2, "Мария Сидорова", "+7-999-234-56-78",
                "maria@mail.ru", "ул. Пушкина, д. 5");

        System.out.println("ТЕСТ 3: Добавление книги на склад");
        store.addBookToStock(2, 2);

        System.out.println("ТЕСТ 4: Изменение статуса заказа на выполнен");
        store.changeOrderStatus(2, OrderStatus.COMPLETED);

        System.out.println("ТЕСТ 5: Оставление запроса на книгу");
        store.leaveRequest(4, "Алексей Смирнов", "+7-999-345-67-89");
        store.leaveRequest(4, "Ольга Новикова", "+7-999-456-78-90");

        System.out.println("ТЕСТ 6: Добавление книги с запросами");
        store.addBookToStock(4, 3);

        // ========== ТЕСТЫ ПРОСМОТРА ==========

        System.out.println("ТЕСТ 7: Просмотр книг (сортировка по цене)");
        store.getBooks(SortType.BY_PRICE_DESC);

        System.out.println("ТЕСТ 8: Просмотр книг (сортировка по автору)");
        store.getBooks(SortType.BY_AUTHOR_ASC);

        System.out.println("ТЕСТ 9: Просмотр заказов (сортировка по дате)");
        store.getOrders(SortType.BY_ORDER_DATE_DESC);

        System.out.println("ТЕСТ 10: Просмотр запросов");
        store.getRequests(SortType.BY_CUSTOMER_NAME_ASC);

        System.out.println("ТЕСТ 11: Выполненные заказы за период");
        store.getCompletedOrders(LocalDate.now().minusDays(7), LocalDate.now(),
                SortType.BY_TOTAL_PRICE_DESC);

        System.out.println("ТЕСТ 12: Статистика выручки");
        store.getTotalRevenue(LocalDate.now().minusMonths(1), LocalDate.now());
        store.getCompletedOrdersCount(LocalDate.now().minusMonths(1), LocalDate.now());

        System.out.println("ТЕСТ 13: Залежавшиеся книги");
        store.getOldBooks();


        System.out.println("ТЕСТ 14: Детали заказа");
        store.getOrderDetails(2);

        System.out.println("ТЕСТ 15: Описание книги");

        store.getBookDescription(1);


    }
}