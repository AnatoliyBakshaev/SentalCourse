// BookStore.java
import java.util.*;

public class BookStore implements IBookStore {
    private Map<Integer, Book> books;
    private Map<Integer, Order> orders;
    private List<Request> requests;
    private int orderCounter;

    public BookStore(List<Book> initialBooks) {
        this.books = new HashMap<>();
        this.orders = new LinkedHashMap<>();
        this.requests = new ArrayList<>();
        this.orderCounter = 1;

        for (Book book : initialBooks) {
            this.books.put(book.getId(), book);
        }

        System.out.println("Книжный магазин инициализирован");
        System.out.println("Доступно книг: " + this.books.size());
        printAllBooks();
    }

    @Override
    public void createOrder(int bookId, String customerName) {
        System.out.println("СОЗДАНИЕ ЗАКАЗА:");
        System.out.println("  Книга ID: " + bookId + ", Клиент: " + customerName);

        Book book = findBook(bookId);
        if (book == null) {
            System.out.println("Книга не найдена!");
            return;
        }


        if (book.getStatus() == BookStatus.WRITTEN_OFF) {
            System.out.println("Ошибка: Книга списана и не может быть заказана!");
            return;
        }

        if (book.getStatus() == BookStatus.OUT_OF_STOCK) {
            System.out.println("Книги нет в наличии, автоматически создается запрос...");
            leaveRequest(bookId, customerName);
        }

        Order order = new Order(orderCounter++, bookId, customerName);
        orders.put(order.getId(), order);

        if (book.getStatus() == BookStatus.IN_STOCK) {
            book.reduceQuantity(1);
            System.out.println("Заказ #" + order.getId() + " создан (книга списана со склада)");
        } else {
            System.out.println("Заказ #" + order.getId() + " создан (ожидает поступления книги)");
        }

        System.out.println("  " + order);
        printBookStatus(bookId);
    }

    @Override
    public void cancelOrder(int orderId) {
        System.out.println("ОТМЕНА ЗАКАЗА #" + orderId);

        Order order = findOrder(orderId);
        if (order == null) {
            System.out.println("Ошибка: Заказ не найден!");
            return;
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            System.out.println("Заказ уже отменен!");
            return;
        }

        if (order.getStatus() == OrderStatus.COMPLETED) {
            System.out.println("Нельзя отменить выполненный заказ!");
            return;
        }

        Book book = findBook(order.getBookId());
        if (book != null && order.getStatus() == OrderStatus.NEW) {
            book.addQuantity(1);
            System.out.println("Книга возвращена на склад");
        }

        order.changeStatus(OrderStatus.CANCELLED);
        System.out.println("Заказ #" + orderId + " отменен");
        printBookStatus(order.getBookId());
    }

    @Override
    public void changeOrderStatus(int orderId, OrderStatus status) {
        System.out.println("ИЗМЕНЕНИЕ СТАТУСА ЗАКАЗА #" + orderId);
        System.out.println("Новый статус: " + status.getDescription());

        Order order = findOrder(orderId);
        if (order == null) {
            System.out.println("Ошибка: Заказ не найден!");
            return;
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            System.out.println("Нельзя изменить статус отмененного заказа!");
            return;
        }

        if (order.getStatus() == OrderStatus.COMPLETED) {
            System.out.println("Нельзя изменить статус выполненного заказа!");
            return;
        }

        if (status == OrderStatus.COMPLETED) {
            Book book = findBook(order.getBookId());
            if (book == null || book.getStatus() != BookStatus.IN_STOCK) {
                System.out.println("Нельзя выполнить заказ: книга отсутствует в наличии!");
                return;
            }
            System.out.println("Заказ выполнен, книга передана клиенту");
        }

        order.changeStatus(status);
        System.out.println("Статус заказа изменен на: " + status.getDescription());
    }

    @Override
    public void addBookToStock(int bookId, int quantity) {
        System.out.println("ПОСТУПЛЕНИЕ КНИГИ НА СКЛАД:");
        System.out.println("Книга ID: " + bookId + ", Количество: " + quantity);

        Book book = findBook(bookId);
        if (book == null) {
            System.out.println("Ошибка: Книга не найдена!");
            return;
        }

        if (book.getStatus() == BookStatus.WRITTEN_OFF) {
            System.out.println("Ошибка: Книга списана и не может быть добавлена на склад!");
            return;
        }

        book.addQuantity(quantity);
        System.out.println("Книга добавлена на склад");
        printBookStatus(bookId);

        processRequests(bookId);
    }

    @Override
    public void leaveRequest(int bookId, String customerName) {
        System.out.println("СОЗДАНИЕ ЗАПРОСА:");
        System.out.println("  Книга ID: " + bookId + ", Клиент: " + customerName);

        Book book = findBook(bookId);
        if (book == null) {
            System.out.println("Ошибка: Книга не найдена!");
            return;
        }

        if (book.getStatus() == BookStatus.IN_STOCK) {
            System.out.println("Книга есть в наличии, запрос не требуется!");
            return;
        }

        if (book.getStatus() == BookStatus.WRITTEN_OFF) {
            System.out.println("Ошибка: Книга списана, запрос невозможен!");
            return;
        }

        // Проверяем, есть ли уже запрос от этого клиента
        boolean existingRequest = requests.stream()
                .anyMatch(r -> r.getBookId() == bookId &&
                        r.getCustomerName().equals(customerName) &&
                        !r.isFulfilled());

        if (existingRequest) {
            System.out.println("У вас уже есть активный запрос на эту книгу!");
            return;
        }

        Request request = new Request(bookId, customerName);
        requests.add(request);
        System.out.println("Запрос создан: " + request);
    }

    @Override
    public void writeOffBook(int bookId) {
        System.out.println("СПИСАНИЕ КНИГИ:");
        System.out.println("Книга ID: " + bookId);

        Book book = findBook(bookId);
        if (book == null) {
            System.out.println("Ошибка: Книга не найдена!");
            return;
        }

        if (book.getStatus() == BookStatus.WRITTEN_OFF) {
            System.out.println("Книга уже списана!");
            return;
        }
        boolean hasActiveOrders = orders.values().stream()
                .anyMatch(o -> o.getBookId() == bookId &&
                        o.getStatus() == OrderStatus.NEW);

        if (hasActiveOrders) {
            System.out.println("Ошибка: Есть активные заказы на эту книгу!");
            return;
        }

        book.changeStatus(BookStatus.WRITTEN_OFF);
        System.out.println("Книга списана");
        printBookStatus(bookId);
    }

    private Book findBook(int bookId) {
        return books.get(bookId);
    }

    private Order findOrder(int orderId) {
        return orders.get(orderId);
    }

    private void processRequests(int bookId) {
        boolean hasRequests = requests.stream()
                .anyMatch(r -> r.getBookId() == bookId && !r.isFulfilled());

        if (!hasRequests) {
            System.out.println("Нет активных запросов на эту книгу");
            return;
        }

        System.out.println("Обработка запросов на книгу #" + bookId);

        List<Request> fulfilledRequests = new ArrayList<>();
        for (Request request : requests) {
            if (request.getBookId() == bookId && !request.isFulfilled()) {
                request.markFulfilled();
                fulfilledRequests.add(request);
                System.out.println("Выполнен запрос: " + request.getCustomerName());

                createOrder(bookId, request.getCustomerName());
            }
        }
    }

    private void printAllBooks() {
        System.out.println("Список всех книг:");
        for (Book book : books.values()) {
            System.out.println("  " + book);
        }
    }

    private void printBookStatus(int bookId) {
        Book book = findBook(bookId);
        if (book != null) {
            System.out.println("Текущий статус книги: " + book);
        }
    }
}