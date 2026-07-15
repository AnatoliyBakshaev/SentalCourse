import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class BookStore implements IBookStore {
    private Map<Integer, Book> books;
    private Map<Integer, Order> orders;
    private List<Request> requests;
    private int orderCounter;
    private double totalRevenue;

    public BookStore(List<Book> initialBooks) {
        this.books = new HashMap<>();
        this.orders = new LinkedHashMap<>();
        this.requests = new ArrayList<>();
        this.orderCounter = 1;
        this.totalRevenue = 0.0;

        for (Book book : initialBooks) {
            this.books.put(book.getId(), book);
        }

        System.out.println("Книжный магазин инициализирован");
        System.out.println("Доступно книг: " + this.books.size());
        System.out.println("----------------------------------------");
    }

    // ========== ОСНОВНЫЕ ОПЕРАЦИИ ==========

    @Override
    public void createOrder(int bookId, String customerName, String customerPhone,
                            String customerEmail, String customerAddress) {
        System.out.println("  Книга ID: " + bookId + ", Клиент: " + customerName);

        Book book = findBook(bookId);
        if (book == null) {
            System.out.println("Ошибка: Книга не найдена!");
            return;
        }

        if (book.getStatus() == BookStatus.WRITTEN_OFF) {
            System.out.println("Ошибка: Книга списана!");
            return;
        }

        if (book.getStatus() == BookStatus.OUT_OF_STOCK) {
            System.out.println("Книги нет в наличии, создается запрос");
            leaveRequest(bookId, customerName, customerPhone);
        }

        Order order = new Order(orderCounter++, bookId, customerName,
                customerPhone, customerEmail, customerAddress,
                book.getPrice());
        orders.put(order.getId(), order);

        if (book.getStatus() == BookStatus.IN_STOCK) {
            book.reduceQuantity(1);
            System.out.println("Заказ #" + order.getId() + " создан");
        } else {
            System.out.println("Заказ #" + order.getId() + " создан (ожидает книгу)");
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
            System.out.println("Заказ отменен, изменение статуса невозможно!");
            return;
        }

        if (order.getStatus() == OrderStatus.COMPLETED) {
            System.out.println("Заказ уже выполнен!");
            return;
        }

        if (status == OrderStatus.COMPLETED) {
            Book book = findBook(order.getBookId());
            if (book == null || book.getStatus() != BookStatus.IN_STOCK) {
                System.out.println("Книга отсутствует, заказ не может быть выполнен!");
                return;
            }
            totalRevenue += order.getTotalPrice();
            System.out.println("Добавлено в выручку: " + order.getTotalPrice() + " руб.");
        }

        order.changeStatus(status);
        System.out.println("Статус заказа изменен");
        System.out.println("  " + order);
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
            System.out.println("Ошибка: Книга списана!");
            return;
        }

        book.addQuantity(quantity);
        System.out.println("Книга добавлена на склад");
        printBookStatus(bookId);

        processRequests(bookId);
    }

    @Override
    public void leaveRequest(int bookId, String customerName, String customerPhone) {
        System.out.println("СОЗДАНИЕ ЗАПРОСА:");
        System.out.println("Книга ID: " + bookId + ", Клиент: " + customerName);

        Book book = findBook(bookId);
        if (book == null) {
            System.out.println("Ошибка: Книга не найдена!");
            return;
        }

        if (book.getStatus() == BookStatus.IN_STOCK) {
            System.out.println("Книга есть в наличии!");
            return;
        }

        if (book.getStatus() == BookStatus.WRITTEN_OFF) {
            System.out.println("Книга списана, запрос невозможен!");
            return;
        }

        boolean existing = requests.stream()
                .anyMatch(r -> r.getBookId() == bookId &&
                        r.getCustomerName().equals(customerName) &&
                        !r.isFulfilled());

        if (existing) {
            System.out.println("У вас уже есть активный запрос!");
            return;
        }

        Request request = new Request(bookId, customerName, customerPhone);
        requests.add(request);
        System.out.println("Запрос создан");
        System.out.println("  " + request);
    }

    @Override
    public void writeOffBook(int bookId) {
        System.out.println("СПИСАНИЕ КНИГИ:");
        System.out.println("  Книга ID: " + bookId);

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
            System.out.println("Есть активные заказы на эту книгу!");
            return;
        }

        book.changeStatus(BookStatus.WRITTEN_OFF);
        System.out.println("Книга списана");
        printBookStatus(bookId);
    }

    // ========== ПРОСМОТР СПИСКОВ ==========

    @Override
    public List<Book> getBooks(SortType sortBy) {
        System.out.println("СПИСОК КНИГ (сортировка: " + sortBy + ")");
        List<Book> bookList = new ArrayList<>(books.values());
        return sortBooks(bookList, sortBy);
    }

    @Override
    public List<Order> getOrders(SortType sortBy) {
        System.out.println("СПИСОК ЗАКАЗОВ (сортировка: " + sortBy + ")");
        List<Order> orderList = new ArrayList<>(orders.values());
        return sortOrders(orderList, sortBy);
    }

    @Override
    public List<Request> getRequests(SortType sortBy) {
        System.out.println("СПИСОК ЗАПРОСОВ (сортировка: " + sortBy + ")");
        return sortRequests(new ArrayList<>(requests), sortBy);
    }

    @Override
    public List<Order> getCompletedOrders(LocalDate startDate, LocalDate endDate, SortType sortBy) {
        System.out.println("ВЫПОЛНЕННЫЕ ЗАКАЗЫ за период: " + startDate + " - " + endDate);

        List<Order> completed = orders.values().stream()
                .filter(o -> o.getStatus() == OrderStatus.COMPLETED)
                .filter(o -> {
                    if (o.getCompletionDate() == null) return false;
                    LocalDate completionDate = o.getCompletionDate().toLocalDate();
                    return (completionDate.isEqual(startDate) || completionDate.isAfter(startDate)) &&
                            (completionDate.isEqual(endDate) || completionDate.isBefore(endDate));
                })
                .collect(Collectors.toList());

        return sortOrders(completed, sortBy);
    }

    // ========== СТАТИСТИКА ==========

    @Override
    public double getTotalRevenue(LocalDate startDate, LocalDate endDate) {
        double revenue = orders.values().stream()
                .filter(o -> o.getStatus() == OrderStatus.COMPLETED)
                .filter(o -> {
                    if (o.getCompletionDate() == null) return false;
                    LocalDate completionDate = o.getCompletionDate().toLocalDate();
                    return (completionDate.isEqual(startDate) || completionDate.isAfter(startDate)) &&
                            (completionDate.isEqual(endDate) || completionDate.isBefore(endDate));
                })
                .mapToDouble(Order::getTotalPrice)
                .sum();

        System.out.println("ВЫРУЧКА за период " + startDate + " - " + endDate);
        System.out.println("  Сумма: " + String.format("%.2f", revenue) + " руб.");
        return revenue;
    }

    @Override
    public int getCompletedOrdersCount(LocalDate startDate, LocalDate endDate) {
        long count = orders.values().stream()
                .filter(o -> o.getStatus() == OrderStatus.COMPLETED)
                .filter(o -> {
                    if (o.getCompletionDate() == null) return false;
                    LocalDate completionDate = o.getCompletionDate().toLocalDate();
                    return (completionDate.isEqual(startDate) || completionDate.isAfter(startDate)) &&
                            (completionDate.isEqual(endDate) || completionDate.isBefore(endDate));
                })
                .count();

        System.out.println("КОЛИЧЕСТВО ВЫПОЛНЕННЫХ ЗАКАЗОВ за период " + startDate + " - " + endDate);
        System.out.println("  Количество: " + count);
        return (int) count;
    }

    @Override
    public List<Book> getOldBooks() {
        System.out.println("СПИСОК ЗАЛЕЖАВШИХСЯ КНИГ (> 6 месяцев)");
        LocalDate sixMonthsAgo = LocalDate.now().minus(6, ChronoUnit.MONTHS);

        List<Book> oldBooks = books.values().stream()
                .filter(b -> b.getStatus() == BookStatus.IN_STOCK)
                .filter(b -> b.getReceivedDate() != null)
                .filter(b -> b.getReceivedDate().isBefore(sixMonthsAgo))
                .sorted((b1, b2) -> b1.getReceivedDate().compareTo(b2.getReceivedDate()))
                .collect(Collectors.toList());

        if (oldBooks.isEmpty()) {
            System.out.println("Нет залежавшихся книг");
        } else {
            for (Book book : oldBooks) {
                System.out.println("  • " + book.getTitle() + " - " + book.getAuthor() +
                        " (поступила: " + book.getReceivedDate() + ", цена: " +
                        String.format("%.2f", book.getPrice()) + " руб.)");
            }
        }
        return oldBooks;
    }

    // ========== ДЕТАЛИ ==========

    @Override
    public OrderDetails getOrderDetails(int orderId) {
        System.out.println("ДЕТАЛИ ЗАКАЗА #" + orderId);

        Order order = findOrder(orderId);
        if (order == null) {
            System.out.println("Заказ не найден!");
            return null;
        }

        Book book = findBook(order.getBookId());
        if (book == null) {
            System.out.println("Книга не найдена!");
            return null;
        }

        List<Book> books = Collections.singletonList(book);

        OrderDetails details = new OrderDetails(
                order.getId(),
                order.getCustomerName(),
                order.getCustomerPhone(),
                order.getCustomerEmail(),
                order.getCustomerAddress(),
                books,
                order.getTotalPrice(),
                order.getOrderDate(),
                order.getCompletionDate(),
                order.getStatus()
        );

        details.displayDetails();
        return details;
    }

    @Override
    public String getBookDescription(int bookId) {
        System.out.println("ОПИСАНИЕ КНИГИ #" + bookId);

        Book book = findBook(bookId);
        if (book == null) {
            System.out.println("Книга не найдена!");
            return null;
        }

        String description = book.getFullDescription();
        System.out.println(description);
        return description;
    }

    // ========== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ==========

    private Book findBook(int bookId) {
        return books.get(bookId);
    }

    private Order findOrder(int orderId) {
        return orders.get(orderId);
    }

    private void processRequests(int bookId) {
        List<Request> fulfilledRequests = requests.stream()
                .filter(r -> r.getBookId() == bookId && !r.isFulfilled())
                .collect(Collectors.toList());

        if (fulfilledRequests.isEmpty()) {
            System.out.println("Нет активных запросов на эту книгу");
            return;
        }

        System.out.println("Обработка запросов на книгу #" + bookId);
        for (Request request : fulfilledRequests) {
            request.markFulfilled();
            System.out.println("Выполнен запрос: " + request.getCustomerName());
            createOrder(bookId, request.getCustomerName(), request.getCustomerPhone(),
                    "email@example.com", "Адрес доставки");
        }
    }

    private List<Book> sortBooks(List<Book> bookList, SortType sortBy) {
        switch (sortBy) {
            case BY_TITLE_ASC:
                bookList.sort(Comparator.comparing(Book::getTitle));
                break;
            case BY_TITLE_DESC:
                bookList.sort(Comparator.comparing(Book::getTitle).reversed());
                break;
            case BY_AUTHOR_ASC:
                bookList.sort(Comparator.comparing(Book::getAuthor));
                break;
            case BY_AUTHOR_DESC:
                bookList.sort(Comparator.comparing(Book::getAuthor).reversed());
                break;
            case BY_PRICE_ASC:
                bookList.sort(Comparator.comparing(Book::getPrice));
                break;
            case BY_PRICE_DESC:
                bookList.sort(Comparator.comparing(Book::getPrice).reversed());
                break;
            case BY_STATUS_ASC:
                bookList.sort(Comparator.comparing(Book::getStatus));
                break;
            case BY_STATUS_DESC:
                bookList.sort(Comparator.comparing(Book::getStatus).reversed());
                break;
            case BY_PUBLICATION_DATE_ASC:
                bookList.sort(Comparator.comparing(Book::getPublicationDate));
                break;
            case BY_PUBLICATION_DATE_DESC:
                bookList.sort(Comparator.comparing(Book::getPublicationDate).reversed());
                break;
            default:
                bookList.sort(Comparator.comparing(Book::getTitle));
        }

        for (Book book : bookList) {
            System.out.println("  " + book);
        }
        return bookList;
    }

    private List<Order> sortOrders(List<Order> orderList, SortType sortBy) {
        switch (sortBy) {
            case BY_ORDER_DATE_ASC:
                orderList.sort(Comparator.comparing(Order::getOrderDate));
                break;
            case BY_ORDER_DATE_DESC:
                orderList.sort(Comparator.comparing(Order::getOrderDate).reversed());
                break;
            case BY_COMPLETION_DATE_ASC:
                orderList.sort(Comparator.comparing(o -> o.getCompletionDate() != null ?
                        o.getCompletionDate() : o.getOrderDate()));
                break;
            case BY_COMPLETION_DATE_DESC:
                orderList.sort((o1, o2) -> {
                    LocalDateTime d1 = o1.getCompletionDate() != null ? o1.getCompletionDate() : o1.getOrderDate();
                    LocalDateTime d2 = o2.getCompletionDate() != null ? o2.getCompletionDate() : o2.getOrderDate();
                    return d2.compareTo(d1);
                });
                break;
            case BY_TOTAL_PRICE_ASC:
                orderList.sort(Comparator.comparing(Order::getTotalPrice));
                break;
            case BY_TOTAL_PRICE_DESC:
                orderList.sort(Comparator.comparing(Order::getTotalPrice).reversed());
                break;
            case BY_STATUS_ORDER_ASC:
                orderList.sort(Comparator.comparing(Order::getStatus));
                break;
            case BY_STATUS_ORDER_DESC:
                orderList.sort(Comparator.comparing(Order::getStatus).reversed());
                break;
            default:
                orderList.sort(Comparator.comparing(Order::getOrderDate).reversed());
        }

        if (!orderList.isEmpty()) {
            for (Order order : orderList) {
                System.out.println("  " + order);
            }
        } else {
            System.out.println("Нет заказов");
        }
        return orderList;
    }

    private List<Request> sortRequests(List<Request> requestList, SortType sortBy) {
        switch (sortBy) {
            case BY_CUSTOMER_NAME_ASC:
                requestList.sort(Comparator.comparing(Request::getCustomerName));
                break;
            case BY_CUSTOMER_NAME_DESC:
                requestList.sort(Comparator.comparing(Request::getCustomerName).reversed());
                break;
            case BY_REQUESTS_COUNT_ASC:
                // Группируем по книге и считаем запросы
                Map<Integer, Long> countMap = requestList.stream()
                        .collect(Collectors.groupingBy(Request::getBookId, Collectors.counting()));
                requestList.sort((r1, r2) ->
                        countMap.getOrDefault(r1.getBookId(), 0L)
                                .compareTo(countMap.getOrDefault(r2.getBookId(), 0L)));
                break;
            case BY_REQUESTS_COUNT_DESC:
                Map<Integer, Long> countMapDesc = requestList.stream()
                        .collect(Collectors.groupingBy(Request::getBookId, Collectors.counting()));
                requestList.sort((r1, r2) ->
                        countMapDesc.getOrDefault(r2.getBookId(), 0L)
                                .compareTo(countMapDesc.getOrDefault(r1.getBookId(), 0L)));
                break;
            default:
                requestList.sort(Comparator.comparing(Request::getRequestDate).reversed());
        }

        for (Request request : requestList) {
            System.out.println("  " + request);
        }
        return requestList;
    }

    private void printBookStatus(int bookId) {
        Book book = findBook(bookId);
        if (book != null) {
            System.out.println("Статус книги: " + book);
        }
    }
}