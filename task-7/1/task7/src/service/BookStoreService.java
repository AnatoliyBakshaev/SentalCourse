package service;

import config.AppConfig;
import model.*;
import model.enums.BookStatus;
import model.enums.OrderStatus;
import model.enums.SortType;
import service.interfaces.IBookStoreService;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class BookStoreService implements IBookStoreService {
    private final Map<Integer, Book> books;
    private final Map<Integer, Order> orders;
    private final List<Request> requests;
    private int orderCounter;
    private double totalRevenue;
    private final CsvImportExportService csvService;
    private final AppConfig config;  //

    public BookStoreService(List<Book> initialBooks) {
        this.books = new HashMap<>();
        this.orders = new LinkedHashMap<>();
        this.requests = new ArrayList<>();
        this.orderCounter = 1;
        this.totalRevenue = 0.0;
        this.csvService = new CsvImportExportService();
        this.config = AppConfig.getInstance();  //

        initialBooks.forEach(book -> this.books.put(book.getId(), book));

        // Показываем текущие настройки при запуске
        config.showConfig();
    }

    @Override
    public void createOrder(int bookId, String customerName, String customerPhone,
                            String customerEmail, String customerAddress) {
        Book book = findBook(bookId);
        if (book == null) {
            throw new IllegalArgumentException("книга не найдена");
        }
        if (book.getStatus() == BookStatus.WRITTEN_OFF) {
            throw new IllegalStateException("книга списана");
        }
        if (book.getStatus() == BookStatus.OUT_OF_STOCK) {
            leaveRequest(bookId, customerName, customerPhone);
        }

        Order order = new Order(orderCounter++, bookId, customerName,
                customerPhone, customerEmail, customerAddress, book.getPrice());
        orders.put(order.getId(), order);

        if (book.getStatus() == BookStatus.IN_STOCK) {
            book.reduceQuantity(1);
        }
    }

    @Override
    public void cancelOrder(int orderId) {
        Order order = findOrder(orderId);
        if (order == null) {
            throw new IllegalArgumentException("заказ не найден");
        }
        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new IllegalStateException("нельзя отменить выполненный заказ");
        }
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("заказ уже отменен");
        }

        Book book = findBook(order.getBookId());
        if (book != null && order.getStatus() == OrderStatus.NEW) {
            book.addQuantity(1);
        }
        order.changeStatus(OrderStatus.CANCELLED);
    }

    @Override
    public void changeOrderStatus(int orderId, OrderStatus status) {
        Order order = findOrder(orderId);
        if (order == null) {
            throw new IllegalArgumentException("заказ не найден");
        }
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("заказ отменен");
        }
        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new IllegalStateException("заказ уже выполнен");
        }
        if (status == OrderStatus.COMPLETED) {
            Book book = findBook(order.getBookId());
            if (book == null || book.getStatus() != BookStatus.IN_STOCK) {
                throw new IllegalStateException("книга отсутствует");
            }
            totalRevenue += order.getTotalPrice();
        }
        order.changeStatus(status);
    }

    @Override
    public void addBookToStock(int bookId, int quantity) {
        Book book = findBook(bookId);
        if (book == null) {
            throw new IllegalArgumentException("книга не найдена");
        }
        if (book.getStatus() == BookStatus.WRITTEN_OFF) {
            throw new IllegalStateException("книга списана");
        }

        book.addQuantity(quantity);
        System.out.println("книга #" + bookId + " добавлена на склад (количество: " + quantity + ")");

        // Проверяем настройку: автоматически выполнять запросы
        if (config.isAutoFulfillRequests()) {
            processRequests(bookId);
        } else {
            System.out.println("авто-выполнение запросов отключено в настройках");
        }
    }

    @Override
    public void leaveRequest(int bookId, String customerName, String customerPhone) {
        Book book = findBook(bookId);
        if (book == null) {
            throw new IllegalArgumentException("книга не найдена");
        }
        if (book.getStatus() == BookStatus.IN_STOCK) {
            throw new IllegalStateException("книга есть в наличии");
        }
        if (book.getStatus() == BookStatus.WRITTEN_OFF) {
            throw new IllegalStateException("книга списана");
        }

        // Stream API для проверки существующего запроса
        boolean existing = requests.stream()
                .anyMatch(r -> r.getBookId() == bookId &&
                        r.getCustomerName().equals(customerName) &&
                        !r.isFulfilled());

        if (existing) {
            throw new IllegalStateException("у вас уже есть активный запрос");
        }

        // Генерация нового ID
        int newId = requests.stream()
                .mapToInt(Request::getId)
                .max()
                .orElse(0) + 1;

        requests.add(new Request(newId, bookId, customerName, customerPhone));
    }

    @Override
    public void writeOffBook(int bookId) {
        Book book = findBook(bookId);
        if (book == null) {
            throw new IllegalArgumentException("книга не найдена");
        }
        if (book.getStatus() == BookStatus.WRITTEN_OFF) {
            throw new IllegalStateException("книга уже списана");
        }

        // Stream API для проверки активных заказов
        boolean hasActiveOrders = orders.values().stream()
                .anyMatch(o -> o.getBookId() == bookId && o.getStatus() == OrderStatus.NEW);

        if (hasActiveOrders) {
            throw new IllegalStateException("есть активные заказы на эту книгу");
        }
        book.changeStatus(BookStatus.WRITTEN_OFF);
    }

    @Override
    public List<Book> getBooks(SortType sortBy) {
        // Stream API для создания копии
        List<Book> bookList = books.values().stream()
                .collect(Collectors.toList());
        return sortBooks(bookList, sortBy);
    }

    @Override
    public List<Order> getOrders(SortType sortBy) {
        // Используем Stream API для создания копии
        List<Order> orderList = orders.values().stream()
                .collect(Collectors.toList());
        return sortOrders(orderList, sortBy);
    }

    @Override
    public List<Request> getRequests(SortType sortBy) {
        // Используем Stream API для создания копии
        List<Request> requestList = requests.stream()
                .collect(Collectors.toList());
        return sortRequests(requestList, sortBy);
    }

    @Override
    public List<Order> getCompletedOrders(LocalDate startDate, LocalDate endDate, SortType sortBy) {
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

    @Override
    public double getTotalRevenue(LocalDate startDate, LocalDate endDate) {
        return orders.values().stream()
                .filter(o -> o.getStatus() == OrderStatus.COMPLETED)
                .filter(o -> {
                    if (o.getCompletionDate() == null) return false;
                    LocalDate completionDate = o.getCompletionDate().toLocalDate();
                    return (completionDate.isEqual(startDate) || completionDate.isAfter(startDate)) &&
                            (completionDate.isEqual(endDate) || completionDate.isBefore(endDate));
                })
                .mapToDouble(Order::getTotalPrice)
                .sum();
    }

    @Override
    public int getCompletedOrdersCount(LocalDate startDate, LocalDate endDate) {
        return (int) orders.values().stream()
                .filter(o -> o.getStatus() == OrderStatus.COMPLETED)
                .filter(o -> {
                    if (o.getCompletionDate() == null) return false;
                    LocalDate completionDate = o.getCompletionDate().toLocalDate();
                    return (completionDate.isEqual(startDate) || completionDate.isAfter(startDate)) &&
                            (completionDate.isEqual(endDate) || completionDate.isBefore(endDate));
                })
                .count();
    }

    @Override
    public List<Book> getOldBooks() {
        // Используем настройку из конфигурации
        int oldMonths = config.getOldMonths();
        LocalDate oldDate = LocalDate.now().minus(oldMonths, ChronoUnit.MONTHS);

        System.out.println("поиск залежавшихся книг (> " + oldMonths + " месяцев)");

        return books.values().stream()
                .filter(b -> b.getStatus() == BookStatus.IN_STOCK)
                .filter(b -> b.getReceivedDate() != null)
                .filter(b -> b.getReceivedDate().isBefore(oldDate))
                .sorted(Comparator.comparing(Book::getReceivedDate))
                .collect(Collectors.toList());
    }

    @Override
    public OrderDetails getOrderDetails(int orderId) {
        Order order = findOrder(orderId);
        if (order == null) {
            throw new IllegalArgumentException("заказ не найден");
        }
        Book book = findBook(order.getBookId());
        if (book == null) {
            throw new IllegalArgumentException("книга не найдена");
        }
        return new OrderDetails(
                order.getId(),
                order.getCustomerName(),
                order.getCustomerPhone(),
                order.getCustomerEmail(),
                order.getCustomerAddress(),
                Collections.singletonList(book),
                order.getTotalPrice(),
                order.getOrderDate(),
                order.getCompletionDate(),
                order.getStatus()
        );
    }

    @Override
    public String getBookDescription(int bookId) {
        Book book = findBook(bookId);
        if (book == null) {
            throw new IllegalArgumentException("книга не найдена");
        }
        return String.format("%s\nавтор: %s\nжанр: %s\nцена: %.2f руб.\nописание: %s",
                book.getTitle(), book.getAuthor(), book.getGenre(),
                book.getPrice(), book.getDescription());
    }


    @Override
    public void importBooks(String filePath) throws IOException {
        List<Book> importedBooks = csvService.importBooks(filePath);
        csvService.mergeBooks(importedBooks, books);
        System.out.println("импортировано книг: " + importedBooks.size());
    }

    @Override
    public void importOrders(String filePath) throws IOException {
        List<Order> importedOrders = csvService.importOrders(filePath);
        csvService.mergeOrders(importedOrders, orders);
        System.out.println("импортировано заказов: " + importedOrders.size());
    }

    @Override
    public void importRequests(String filePath) throws IOException {
        List<Request> importedRequests = csvService.importRequests(filePath);

        int updated = 0;
        int added = 0;

        for (Request imported : importedRequests) {
            boolean found = false;
            for (int i = 0; i < requests.size(); i++) {
                if (requests.get(i).getId() == imported.getId()) {
                    requests.set(i, imported);
                    found = true;
                    updated++;
                    break;
                }
            }
            if (!found) {
                requests.add(imported);
                added++;
            }
        }
        System.out.println("импортировано запросов: " + importedRequests.size() +
                " (обновлено: " + updated + ", добавлено: " + added + ")");
    }


    @Override
    public void exportBooks(String filePath) throws IOException {
        List<Book> bookList = new ArrayList<>(books.values());
        csvService.exportBooks(bookList, filePath);
        System.out.println("экспортировано книг: " + bookList.size());
    }

    @Override
    public void exportOrders(String filePath) throws IOException {
        List<Order> orderList = new ArrayList<>(orders.values());
        csvService.exportOrders(orderList, filePath);
        System.out.println("экспортировано заказов: " + orderList.size());
    }

    @Override
    public void exportRequests(String filePath) throws IOException {
        csvService.exportRequests(requests, filePath);
        System.out.println("экспортировано запросов: " + requests.size());
    }

    //

    public void showConfig() {
        config.showConfig();
    }

    public void setOldMonths(int months) {
        config.setOldMonths(months);
        System.out.println("настройка обновлена: залежавшиеся книги = " + months + " месяцев");
    }

    public void setAutoFulfillRequests(boolean autoFulfill) {
        config.setAutoFulfillRequests(autoFulfill);
        System.out.println("настройка обновлена: авто-выполнение запросов = " +
                (autoFulfill ? "включено" : "отключено"));
    }

    //

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
            return;
        }

        fulfilledRequests.forEach(request -> {
            request.markFulfilled();
            createOrder(bookId, request.getCustomerName(), request.getCustomerPhone(),
                    "email@example.com", "адрес доставки");
        });
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
            case BY_RECEIVED_DATE_ASC:
                bookList.sort(Comparator.comparing(Book::getReceivedDate));
                break;
            case BY_RECEIVED_DATE_DESC:
                bookList.sort(Comparator.comparing(Book::getReceivedDate).reversed());
                break;
            default:
                bookList.sort(Comparator.comparing(Book::getTitle));
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
            case BY_REQUESTS_COUNT_DESC:
                Map<Integer, Long> countMap = requestList.stream()
                        .collect(Collectors.groupingBy(Request::getBookId, Collectors.counting()));
                requestList.sort((r1, r2) -> {
                    Long c1 = countMap.getOrDefault(r1.getBookId(), 0L);
                    Long c2 = countMap.getOrDefault(r2.getBookId(), 0L);
                    return sortBy == SortType.BY_REQUESTS_COUNT_ASC ?
                            c1.compareTo(c2) : c2.compareTo(c1);
                });
                break;
            default:
                requestList.sort(Comparator.comparing(Request::getRequestDate).reversed());
        }
        return requestList;
    }
    public void saveState() {
        try {
            BookStoreState state = new BookStoreState();
            state.setBooks(books);
            state.setOrders(orders);
            state.setRequests(requests);
            state.setOrderCounter(orderCounter);
            state.setTotalRevenue(totalRevenue);

            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream("bookstore.ser"))) {
                oos.writeObject(state);
            }
            System.out.println("состояние сохранено");
        } catch (IOException e) {
            System.out.println("ошибка сохранения: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void loadState() {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream("bookstore.ser"))) {
            BookStoreState state = (BookStoreState) ois.readObject();
            this.books.clear();
            this.books.putAll(state.getBooks());
            this.orders.clear();
            this.orders.putAll(state.getOrders());
            this.requests.clear();
            this.requests.addAll(state.getRequests());
            this.orderCounter = state.getOrderCounter();
            this.totalRevenue = state.getTotalRevenue();
            System.out.println("состояние восстановлено");
        } catch (FileNotFoundException e) {
            System.out.println("файл состояния не найден, создается новое состояние");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("ошибка загрузки: " + e.getMessage());
        }
    }
}