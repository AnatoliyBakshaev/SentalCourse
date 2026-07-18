package bookstore.service;

import bookstore.annotation.di.Component;
import bookstore.annotation.di.Inject;
import bookstore.config.AppConfig;
import bookstore.dao.BookDAO;
import bookstore.dao.DAOFactory;
import bookstore.dao.OrderDAO;
import bookstore.dao.RequestDAO;
import bookstore.model.*;
import bookstore.model.enums.BookStatus;
import bookstore.model.enums.OrderStatus;
import bookstore.model.enums.SortType;
import bookstore.service.interfaces.IBookStoreService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class BookStoreService implements IBookStoreService {

    // ===== DAO (через фабрику) =====
    private final BookDAO bookDAO;
    private final OrderDAO orderDAO;
    private final RequestDAO requestDAO;

    // ===== КЭШ =====
    private final Map<Long, Book> booksCache;
    private final Map<Long, Order> ordersCache;
    private final List<Request> requestsCache;

    private int orderCounter;
    private double totalRevenue;

    @Inject
    private CsvImportExportService csvService;

    @Inject
    private AppConfig config;

    // ===== КОНСТРУКТОРЫ =====

    public BookStoreService() {
        // Используем JPA-реализации через фабрику
        this.bookDAO = DAOFactory.getBookDAO();
        this.orderDAO = DAOFactory.getOrderDAO();
        this.requestDAO = DAOFactory.getRequestDAO();

        this.booksCache = new HashMap<>();
        this.ordersCache = new LinkedHashMap<>();
        this.requestsCache = new ArrayList<>();

        this.orderCounter = 1;
        this.totalRevenue = 0.0;

        // Инициализация таблиц через JPA (автоматически при создании EntityManagerFactory)
        loadCache();
    }

    public BookStoreService(List<Book> initialBooks) {
        this();
        try {
            for (Book book : initialBooks) {
                bookDAO.save(book);
            }
            loadCache();
        } catch (Exception e) {
            System.err.println("ошибка инициализации БД: " + e.getMessage());
        }
    }

    // ===== ЗАГРУЗКА КЭША =====

    private void loadCache() {
        try {
            booksCache.clear();
            bookDAO.getAll().forEach(b -> booksCache.put(b.getId(), b));

            ordersCache.clear();
            orderDAO.getAll().forEach(o -> ordersCache.put(o.getId(), o));

            requestsCache.clear();
            requestsCache.addAll(requestDAO.getAll());

            orderCounter = ordersCache.size() + 1;

            totalRevenue = orderDAO.getTotalRevenue(
                    LocalDateTime.of(2000, 1, 1, 0, 0),
                    LocalDateTime.now()
            );

            System.out.println("кэш загружен из БД: книг=" + booksCache.size() +
                    ", заказов=" + ordersCache.size() +
                    ", запросов=" + requestsCache.size());
        } catch (Exception e) {
            System.err.println("ошибка загрузки кэша: " + e.getMessage());
        }
    }

    // ===== ОСНОВНЫЕ МЕТОДЫ =====

    @Override
    public void createOrder(int bookId, String customerName, String customerPhone,
                            String customerEmail, String customerAddress) {
        try {
            Book book = findBook(bookId);
            if (book == null) {
                throw new IllegalArgumentException("книга не найдена");
            }
            if (book.getStatus() == BookStatus.WRITTEN_OFF) {
                throw new IllegalStateException("книга списана");
            }

            if (book.getStatus() == BookStatus.OUT_OF_STOCK) {
                leaveRequest(bookId, customerName, customerPhone);
                System.out.println("книга отсутствует, создан запрос");
                return;
            }

            Order order = new Order(
                    (long) bookId,
                    customerName,
                    customerPhone,
                    customerEmail,
                    customerAddress,
                    book.getPrice()
            );

            orderDAO.save(order);
            ordersCache.put(order.getId(), order);

            book.reduceQuantity(1);
            bookDAO.update(book);
            booksCache.put((long) bookId, book);

            System.out.println("заказ #" + order.getId() + " создан");

        } catch (Exception e) {
            System.err.println("ошибка создания заказа: " + e.getMessage());
            throw new RuntimeException("ошибка БД", e);
        }
    }

    @Override
    public void cancelOrder(int orderId) {
        try {
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

            Book book = findBook(order.getBookId().intValue());
            if (book != null && order.getStatus() == OrderStatus.NEW) {
                book.addQuantity(1);
                bookDAO.update(book);
                booksCache.put(book.getId(), book);
            }

            order.cancel();
            orderDAO.update(order);
            ordersCache.put((long) orderId, order);

            System.out.println("заказ #" + orderId + " отменен");

        } catch (Exception e) {
            System.err.println("ошибка отмены заказа: " + e.getMessage());
            throw new RuntimeException("ошибка БД", e);
        }
    }

    @Override
    public void changeOrderStatus(int orderId, OrderStatus status) {
        try {
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
                Book book = findBook(order.getBookId().intValue());
                if (book == null || book.getStatus() != BookStatus.IN_STOCK) {
                    throw new IllegalStateException("книга отсутствует");
                }
                totalRevenue += order.getTotalPrice();
                order.complete();
            } else {
                order.setStatus(status);
            }

            orderDAO.update(order);
            ordersCache.put((long) orderId, order);

            System.out.println("статус заказа #" + orderId + " изменен на " + status.getDescription());

        } catch (Exception e) {
            System.err.println("ошибка изменения статуса: " + e.getMessage());
            throw new RuntimeException("ошибка БД", e);
        }
    }

    @Override
    public void addBookToStock(int bookId, int quantity) {
        try {
            Book book = findBook(bookId);
            if (book == null) {
                throw new IllegalArgumentException("книга не найдена");
            }
            if (book.getStatus() == BookStatus.WRITTEN_OFF) {
                throw new IllegalStateException("книга списана");
            }

            book.addQuantity(quantity);
            bookDAO.update(book);
            booksCache.put((long) bookId, book);

            System.out.println("книга #" + bookId + " добавлена на склад (количество: " + quantity + ")");

            if (config.isAutoFulfillRequests()) {
                processRequests(bookId);
            } else {
                System.out.println("авто-выполнение запросов отключено в настройках");
            }

        } catch (Exception e) {
            System.err.println("ошибка добавления книги: " + e.getMessage());
            throw new RuntimeException("ошибка БД", e);
        }
    }

    @Override
    public void leaveRequest(int bookId, String customerName, String customerPhone) {
        try {
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

            boolean existing = requestDAO.findByBookId((long) bookId).stream()
                    .anyMatch(r -> r.getCustomerName().equals(customerName) && !r.isFulfilled());

            if (existing) {
                throw new IllegalStateException("у вас уже есть активный запрос");
            }

            Request request = new Request((long) bookId, customerName, customerPhone);
            requestDAO.save(request);
            requestsCache.add(request);

            System.out.println("запрос на книгу #" + bookId + " создан");

        } catch (Exception e) {
            System.err.println("ошибка создания запроса: " + e.getMessage());
            throw new RuntimeException("ошибка БД", e);
        }
    }

    @Override
    public void writeOffBook(int bookId) {
        try {
            Book book = findBook(bookId);
            if (book == null) {
                throw new IllegalArgumentException("книга не найдена");
            }
            if (book.getStatus() == BookStatus.WRITTEN_OFF) {
                throw new IllegalStateException("книга уже списана");
            }

            boolean hasActiveOrders = orderDAO.findByBookId((long) bookId).stream()
                    .anyMatch(o -> o.getStatus() == OrderStatus.NEW);

            if (hasActiveOrders) {
                throw new IllegalStateException("есть активные заказы на эту книгу");
            }

            book.changeStatus(BookStatus.WRITTEN_OFF);
            bookDAO.update(book);
            booksCache.put((long) bookId, book);

            System.out.println("книга #" + bookId + " списана");

        } catch (Exception e) {
            System.err.println("ошибка списания книги: " + e.getMessage());
            throw new RuntimeException("ошибка БД", e);
        }
    }

    // ===== ПОЛУЧЕНИЕ ДАННЫХ =====

    @Override
    public List<Book> getBooks(SortType sortBy) {
        try {
            List<Book> bookList = bookDAO.getAll();
            return sortBooks(bookList, sortBy);
        } catch (Exception e) {
            System.err.println("ошибка получения книг: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<Order> getOrders(SortType sortBy) {
        try {
            List<Order> orderList = orderDAO.getAll();
            return sortOrders(orderList, sortBy);
        } catch (Exception e) {
            System.err.println("ошибка получения заказов: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<Request> getRequests(SortType sortBy) {
        try {
            List<Request> requestList = requestDAO.getAll();
            return sortRequests(requestList, sortBy);
        } catch (Exception e) {
            System.err.println("ошибка получения запросов: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<Order> getCompletedOrders(LocalDate startDate, LocalDate endDate, SortType sortBy) {
        try {
            LocalDateTime start = startDate.atStartOfDay();
            LocalDateTime end = endDate.atTime(23, 59, 59);
            List<Order> completed = orderDAO.findCompletedBetween(start, end);
            return sortOrders(completed, sortBy);
        } catch (Exception e) {
            System.err.println("ошибка получения выполненных заказов: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public double getTotalRevenue(LocalDate startDate, LocalDate endDate) {
        try {
            LocalDateTime start = startDate.atStartOfDay();
            LocalDateTime end = endDate.atTime(23, 59, 59);
            return orderDAO.getTotalRevenue(start, end);
        } catch (Exception e) {
            System.err.println("ошибка получения выручки: " + e.getMessage());
            return 0.0;
        }
    }

    @Override
    public int getCompletedOrdersCount(LocalDate startDate, LocalDate endDate) {
        try {
            LocalDateTime start = startDate.atStartOfDay();
            LocalDateTime end = endDate.atTime(23, 59, 59);
            return orderDAO.findCompletedBetween(start, end).size();
        } catch (Exception e) {
            System.err.println("ошибка получения количества заказов: " + e.getMessage());
            return 0;
        }
    }

    @Override
    public List<Book> getOldBooks() {
        try {
            int oldMonths = config.getOldMonths();
            System.out.println("поиск залежавшихся книг (> " + oldMonths + " месяцев)");
            return bookDAO.findOldBooks(oldMonths);
        } catch (Exception e) {
            System.err.println("ошибка получения залежавшихся книг: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public OrderDetails getOrderDetails(int orderId) {
        try {
            Order order = orderDAO.getById((long) orderId);
            if (order == null) {
                throw new IllegalArgumentException("заказ не найден");
            }
            Book book = bookDAO.getById(order.getBookId());
            if (book == null) {
                throw new IllegalArgumentException("книга не найдена");
            }
            return new OrderDetails(
                    order.getId().intValue(),
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
        } catch (Exception e) {
            System.err.println("ошибка получения деталей заказа: " + e.getMessage());
            throw new RuntimeException("ошибка БД", e);
        }
    }

    @Override
    public String getBookDescription(int bookId) {
        try {
            Book book = bookDAO.getById((long) bookId);
            if (book == null) {
                throw new IllegalArgumentException("книга не найдена");
            }
            return String.format("%s\nавтор: %s\nжанр: %s\nцена: %.2f руб.\nописание: %s",
                    book.getTitle(), book.getAuthor(), book.getGenre(),
                    book.getPrice(), book.getDescription());
        } catch (Exception e) {
            System.err.println("ошибка получения описания книги: " + e.getMessage());
            return "ошибка загрузки описания";
        }
    }

    // ===== ИМПОРТ/ЭКСПОРТ =====

    @Override
    public void importBooks(String filePath) throws IOException {
        List<Book> importedBooks = csvService.importBooks(filePath);
        try {
            for (Book book : importedBooks) {
                Book existing = bookDAO.findByIsbn(book.getIsbn());
                if (existing != null) {
                    book.setId(existing.getId());
                    bookDAO.update(book);
                } else {
                    bookDAO.save(book);
                }
            }
            loadCache();
            System.out.println("импортировано книг: " + importedBooks.size());
        } catch (Exception e) {
            System.err.println("ошибка импорта книг: " + e.getMessage());
            throw new IOException("ошибка импорта книг", e);
        }
    }

    @Override
    public void importOrders(String filePath) throws IOException {
        try {
            List<Order> importedOrders = csvService.importOrders(filePath);
            for (Order order : importedOrders) {
                orderDAO.save(order);
            }
            loadCache();
            System.out.println("импортировано заказов: " + importedOrders.size());
        } catch (Exception e) {
            System.err.println("ошибка импорта заказов: " + e.getMessage());
            throw new IOException("ошибка импорта заказов", e);
        }
    }

    @Override
    public void importRequests(String filePath) throws IOException {
        try {
            List<Request> importedRequests = csvService.importRequests(filePath);
            for (Request request : importedRequests) {
                requestDAO.save(request);
            }
            loadCache();
            System.out.println("импортировано запросов: " + importedRequests.size());
        } catch (Exception e) {
            System.err.println("ошибка импорта запросов: " + e.getMessage());
            throw new IOException("ошибка импорта запросов", e);
        }
    }

    @Override
    public void exportBooks(String filePath) throws IOException {
        try {
            List<Book> bookList = bookDAO.getAll();
            csvService.exportBooks(bookList, filePath);
            System.out.println("экспортировано книг: " + bookList.size());
        } catch (Exception e) {
            System.err.println("ошибка экспорта книг: " + e.getMessage());
            throw new IOException("ошибка БД", e);
        }
    }

    @Override
    public void exportOrders(String filePath) throws IOException {
        try {
            List<Order> orderList = orderDAO.getAll();
            csvService.exportOrders(orderList, filePath);
            System.out.println("экспортировано заказов: " + orderList.size());
        } catch (Exception e) {
            System.err.println("ошибка экспорта заказов: " + e.getMessage());
            throw new IOException("ошибка БД", e);
        }
    }

    @Override
    public void exportRequests(String filePath) throws IOException {
        try {
            List<Request> requestList = requestDAO.getAll();
            csvService.exportRequests(requestList, filePath);
            System.out.println("экспортировано запросов: " + requestList.size());
        } catch (Exception e) {
            System.err.println("ошибка экспорта запросов: " + e.getMessage());
            throw new IOException("ошибка БД", e);
        }
    }

    // ===== КОНФИГУРАЦИЯ =====

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

    // ===== СОХРАНЕНИЕ СОСТОЯНИЯ =====

    public void saveState() {
        try {
            for (Book book : booksCache.values()) {
                bookDAO.update(book);
            }
            for (Order order : ordersCache.values()) {
                orderDAO.update(order);
            }
            for (Request request : requestsCache) {
                requestDAO.update(request);
            }
            System.out.println("состояние синхронизировано с БД");
        } catch (Exception e) {
            System.err.println("ошибка синхронизации: " + e.getMessage());
        }
    }

    public void loadState() {
        loadCache();
        System.out.println("состояние загружено из БД");
    }

    // ===== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ =====

    private Book findBook(int bookId) {
        try {
            return bookDAO.getById((long) bookId);
        } catch (Exception e) {
            System.err.println("ошибка поиска книги: " + e.getMessage());
            return null;
        }
    }

    private Order findOrder(int orderId) {
        try {
            return orderDAO.getById((long) orderId);
        } catch (Exception e) {
            System.err.println("ошибка поиска заказа: " + e.getMessage());
            return null;
        }
    }

    private void processRequests(int bookId) {
        try {
            List<Request> fulfilledRequests = requestDAO.findByBookId((long) bookId).stream()
                    .filter(r -> !r.isFulfilled())
                    .collect(Collectors.toList());

            if (fulfilledRequests.isEmpty()) {
                return;
            }

            for (Request request : fulfilledRequests) {
                request.markFulfilled();
                requestDAO.update(request);

                // Создаем заказ для каждого запроса
                Book book = findBook(bookId);
                if (book != null && book.getStatus() == BookStatus.IN_STOCK) {
                    Order order = new Order(
                            (long) bookId,
                            request.getCustomerName(),
                            request.getCustomerPhone(),
                            "email@example.com",
                            "адрес доставки",
                            book.getPrice()
                    );
                    orderDAO.save(order);
                    ordersCache.put(order.getId(), order);

                    book.reduceQuantity(1);
                    bookDAO.update(book);
                    booksCache.put((long) bookId, book);

                    System.out.println("автоматически создан заказ #" + order.getId() +
                            " для запроса клиента: " + request.getCustomerName());
                }
            }

            loadCache();
        } catch (Exception e) {
            System.err.println("ошибка обработки запросов: " + e.getMessage());
        }
    }

    // ===== МЕТОДЫ СОРТИРОВКИ =====

    private List<Book> sortBooks(List<Book> bookList, SortType sortBy) {
        if (sortBy == null) {
            return bookList;
        }
        switch (sortBy) {
            case BY_TITLE_ASC:
                bookList.sort(Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER));
                break;
            case BY_TITLE_DESC:
                bookList.sort(Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER).reversed());
                break;
            case BY_AUTHOR_ASC:
                bookList.sort(Comparator.comparing(Book::getAuthor, String.CASE_INSENSITIVE_ORDER));
                break;
            case BY_AUTHOR_DESC:
                bookList.sort(Comparator.comparing(Book::getAuthor, String.CASE_INSENSITIVE_ORDER).reversed());
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
                bookList.sort(Comparator.comparing(Book::getPublicationDate, Comparator.nullsLast(Comparator.naturalOrder())));
                break;
            case BY_PUBLICATION_DATE_DESC:
                bookList.sort(Comparator.comparing(Book::getPublicationDate, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
                break;
            case BY_RECEIVED_DATE_ASC:
                bookList.sort(Comparator.comparing(Book::getReceivedDate, Comparator.nullsLast(Comparator.naturalOrder())));
                break;
            case BY_RECEIVED_DATE_DESC:
                bookList.sort(Comparator.comparing(Book::getReceivedDate, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
                break;
            default:
                bookList.sort(Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER));
        }
        return bookList;
    }

    private List<Order> sortOrders(List<Order> orderList, SortType sortBy) {
        if (sortBy == null) {
            return orderList;
        }
        switch (sortBy) {
            case BY_ORDER_DATE_ASC:
                orderList.sort(Comparator.comparing(Order::getOrderDate, Comparator.nullsLast(Comparator.naturalOrder())));
                break;
            case BY_ORDER_DATE_DESC:
                orderList.sort(Comparator.comparing(Order::getOrderDate, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
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
                orderList.sort(Comparator.comparing(Order::getOrderDate, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
        }
        return orderList;
    }

    private List<Request> sortRequests(List<Request> requestList, SortType sortBy) {
        if (sortBy == null) {
            return requestList;
        }
        switch (sortBy) {
            case BY_CUSTOMER_NAME_ASC:
                requestList.sort(Comparator.comparing(Request::getCustomerName, String.CASE_INSENSITIVE_ORDER));
                break;
            case BY_CUSTOMER_NAME_DESC:
                requestList.sort(Comparator.comparing(Request::getCustomerName, String.CASE_INSENSITIVE_ORDER).reversed());
                break;
            case BY_REQUESTS_COUNT_ASC:
            case BY_REQUESTS_COUNT_DESC:
                Map<Long, Long> countMap = requestList.stream()
                        .collect(Collectors.groupingBy(Request::getBookId, Collectors.counting()));
                requestList.sort((r1, r2) -> {
                    Long c1 = countMap.getOrDefault(r1.getBookId(), 0L);
                    Long c2 = countMap.getOrDefault(r2.getBookId(), 0L);
                    return sortBy == SortType.BY_REQUESTS_COUNT_ASC ?
                            c1.compareTo(c2) : c2.compareTo(c1);
                });
                break;
            default:
                requestList.sort(Comparator.comparing(Request::getRequestDate, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
        }
        return requestList;
    }

    // ===== ДОПОЛНИТЕЛЬНЫЕ МЕТОДЫ ДЛЯ МЕНЮ =====

    public void shutdown() {
        saveState();
        System.out.println("сервис остановлен");
    }
}