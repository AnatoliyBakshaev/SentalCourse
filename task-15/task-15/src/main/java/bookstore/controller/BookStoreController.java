package bookstore.controller;

import bookstore.config.AppConfig;
import bookstore.controller.interfaces.IBookStoreController;
import bookstore.model.Book;
import bookstore.model.Order;
import bookstore.model.OrderDetails;
import bookstore.model.Request;
import bookstore.model.enums.OrderStatus;
import bookstore.model.enums.SortType;
import bookstore.service.interfaces.IBookStoreService;
import bookstore.view.ConsoleView;
import bookstore.utils.LoggerUtil;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Controller
public class BookStoreController implements IBookStoreController {
    private static final Logger logger = LoggerUtil.getLogger(BookStoreController.class);
    @Autowired
    private IBookStoreService service;
    @Autowired
    private ConsoleView view;
    @Autowired
    private AppConfig config;

    public BookStoreController() {
        logger.info("BookStoreController создан Spring-контейнером");
    }

    // Конструктор с параметрами (для ручного создания)
    public BookStoreController(IBookStoreService service) {
        this.service = service;
        this.view = ConsoleView.getInstance();
        logger.info("BookStoreController создан вручную с сервисом");
    }

    // ===== ОПЕРАЦИИ =====

    public void createOrder() {
        logger.info("Начало создания заказа");
        int bookId = 0;
        String name = null;
        try {
            view.printHeader("создание заказа");
            bookId = view.readInt("id книги: ");
            name = view.readLine("имя клиента: ");
            String phone = view.readLine("телефон: ");
            String email = view.readLine("email: ");
            String address = view.readLine("адрес: ");

            service.createOrder(bookId, name, phone, email, address);
            view.showSuccess("заказ успешно создан");
            logger.info("Заказ успешно создан для книги ID: {}, клиент: {}", bookId, name);

        } catch (IllegalArgumentException e) {
            logger.error("Ошибка данных при создании заказа для книги {}: {}", bookId, e.getMessage());
            view.showError("ошибка в данных: " + e.getMessage());
            view.showInfo("проверьте правильность введенных данных");
        } catch (IllegalStateException e) {
            logger.error("Ошибка состояния при создании заказа для книги {}: {}", bookId, e.getMessage());
            view.showError("ошибка состояния: " + e.getMessage());
            view.showInfo("возможно, книга отсутствует или списана");
        } catch (Exception e) {
            logger.error("Непредвиденная ошибка при создании заказа", e);
            view.showError("непредвиденная ошибка: " + e.getMessage());
        } finally {
            view.waitForEnter();
        }
    }

    public void cancelOrder() {
        logger.info("Начало отмены заказа");
        int orderId = 0;
        try {
            view.printHeader("отмена заказа");
            orderId = view.readInt("id заказа: ");

            service.cancelOrder(orderId);
            view.showSuccess("заказ #" + orderId + " отменен");
            logger.info("Заказ #{} успешно отменен", orderId);

        } catch (IllegalArgumentException e) {
            logger.error("Заказ #{} не найден: {}", orderId, e.getMessage());
            view.showError("заказ не найден: " + e.getMessage());
            view.showInfo("проверьте правильность id заказа");
        } catch (IllegalStateException e) {
            logger.error("Нельзя отменить заказ #{}: {}", orderId, e.getMessage());
            view.showError("нельзя отменить заказ: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Непредвиденная ошибка при отмене заказа #{}", orderId, e);
            view.showError("непредвиденная ошибка: " + e.getMessage());
        } finally {
            view.waitForEnter();
        }
    }

    public void changeOrderStatus() {
        logger.info("Начало изменения статуса заказа");
        int orderId = 0;
        try {
            view.printHeader("изменение статуса заказа");
            orderId = view.readInt("id заказа: ");

            view.showStatusMenu();
            int choice = view.readInt("выбор: ");
            OrderStatus status = view.getOrderStatus(choice);

            if (status == null) {
                logger.warn("Неверный выбор статуса для заказа #{}", orderId);
                view.showError("неверный выбор статуса");
                view.waitForEnter();
                return;
            }

            service.changeOrderStatus(orderId, status);
            view.showSuccess("статус заказа изменен на: " + status.getDescription());
            logger.info("Статус заказа #{} изменен на: {}", orderId, status.getDescription());

        } catch (IllegalArgumentException e) {
            logger.error("Заказ #{} не найден: {}", orderId, e.getMessage());
            view.showError("заказ не найден: " + e.getMessage());
        } catch (IllegalStateException e) {
            logger.error("Ошибка изменения статуса заказа #{}: {}", orderId, e.getMessage());
            view.showError("ошибка изменения статуса: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Непредвиденная ошибка при изменении статуса заказа #{}", orderId, e);
            view.showError("непредвиденная ошибка: " + e.getMessage());
        } finally {
            view.waitForEnter();
        }
    }

    public void addBookToStock() {
        logger.info("Начало добавления книги на склад");
        int bookId = 0;
        int quantity = 0;
        try {
            view.printHeader("добавление книги на склад");
            bookId = view.readInt("id книги: ");
            quantity = view.readInt("количество: ");

            if (quantity <= 0) {
                logger.warn("Попытка добавить неположительное количество ({}) для книги ID: {}", quantity, bookId);
                view.showError("количество должно быть положительным числом");
                view.waitForEnter();
                return;
            }

            service.addBookToStock(bookId, quantity);
            view.showSuccess("книга добавлена на склад (количество: " + quantity + ")");
            logger.info("Книга ID: {} добавлена на склад в количестве: {}", bookId, quantity);

        } catch (IllegalArgumentException e) {
            logger.error("Книга ID: {} не найдена: {}", bookId, e.getMessage());
            view.showError("книга не найдена: " + e.getMessage());
        } catch (IllegalStateException e) {
            logger.error("Ошибка добавления книги ID: {}: {}", bookId, e.getMessage());
            view.showError("ошибка: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Непредвиденная ошибка при добавлении книги ID: {}", bookId, e);
            view.showError("непредвиденная ошибка: " + e.getMessage());
        } finally {
            view.waitForEnter();
        }
    }

    public void leaveRequest() {
        logger.info("Начало создания запроса на книгу");
        int bookId = 0;
        String name = null;
        try {
            view.printHeader("запрос на книгу");
            bookId = view.readInt("id книги: ");
            name = view.readLine("имя клиента: ");
            String phone = view.readLine("телефон: ");

            service.leaveRequest(bookId, name, phone);
            view.showSuccess("запрос на книгу #" + bookId + " создан");
            logger.info("Запрос на книгу ID: {} создан для клиента: {}", bookId, name);

        } catch (IllegalArgumentException e) {
            logger.error("Книга ID: {} не найдена: {}", bookId, e.getMessage());
            view.showError("книга не найдена: " + e.getMessage());
        } catch (IllegalStateException e) {
            logger.error("Ошибка создания запроса для книги ID: {}: {}", bookId, e.getMessage());
            view.showError("ошибка создания запроса: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Непредвиденная ошибка при создании запроса для книги ID: {}", bookId, e);
            view.showError("непредвиденная ошибка: " + e.getMessage());
        } finally {
            view.waitForEnter();
        }
    }

    public void writeOffBook() {
        logger.info("Начало списания книги");
        int bookId = 0;
        try {
            view.printHeader("списание книги");
            bookId = view.readInt("id книги: ");

            service.writeOffBook(bookId);
            view.showSuccess("книга #" + bookId + " списана");
            logger.info("Книга ID: {} списана", bookId);

        } catch (IllegalArgumentException e) {
            logger.error("Книга ID: {} не найдена: {}", bookId, e.getMessage());
            view.showError("книга не найдена: " + e.getMessage());
        } catch (IllegalStateException e) {
            logger.error("Ошибка списания книги ID: {}: {}", bookId, e.getMessage());
            view.showError("ошибка списания: " + e.getMessage());
            view.showInfo("возможно, есть активные заказы на эту книгу");
        } catch (Exception e) {
            logger.error("Непредвиденная ошибка при списании книги ID: {}", bookId, e);
            view.showError("непредвиденная ошибка: " + e.getMessage());
        } finally {
            view.waitForEnter();
        }
    }

    // ===== ИМПОРТ =====

    public void importBooks() {
        logger.info("Начало импорта книг из CSV");
        String filePath = null;
        try {
            view.printHeader("импорт книг из csv");
            filePath = view.readLine("путь к файлу: ");

            if (filePath == null || filePath.trim().isEmpty()) {
                logger.warn("Попытка импорта с пустым путем к файлу");
                view.showError("путь к файлу не может быть пустым");
                view.waitForEnter();
                return;
            }

            service.importBooks(filePath);
            view.showSuccess("книги импортированы");
            logger.info("Книги успешно импортированы из файла: {}", filePath);

        } catch (IOException e) {
            logger.error("Ошибка чтения файла при импорте книг: {}", e.getMessage());
            view.showError("ошибка чтения файла: " + e.getMessage());
            view.showInfo("проверьте, что файл существует и доступен для чтения");
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка данных при импорте книг: {}", e.getMessage());
            view.showError("ошибка данных: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Непредвиденная ошибка при импорте книг", e);
            view.showError("непредвиденная ошибка: " + e.getMessage());
        } finally {
            view.waitForEnter();
        }
    }

    public void importOrders() {
        logger.info("Начало импорта заказов из CSV");
        String filePath = null;
        try {
            view.printHeader("импорт заказов из csv");
            filePath = view.readLine("путь к файлу: ");

            if (filePath == null || filePath.trim().isEmpty()) {
                logger.warn("Попытка импорта с пустым путем к файлу");
                view.showError("путь к файлу не может быть пустым");
                view.waitForEnter();
                return;
            }

            service.importOrders(filePath);
            view.showSuccess("заказы импортированы");
            logger.info("Заказы успешно импортированы из файла: {}", filePath);

        } catch (IOException e) {
            logger.error("Ошибка чтения файла при импорте заказов: {}", e.getMessage());
            view.showError("ошибка чтения файла: " + e.getMessage());
            view.showInfo("проверьте, что файл существует и доступен для чтения");
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка данных при импорте заказов: {}", e.getMessage());
            view.showError("ошибка данных: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Непредвиденная ошибка при импорте заказов", e);
            view.showError("непредвиденная ошибка: " + e.getMessage());
        } finally {
            view.waitForEnter();
        }
    }

    public void importRequests() {
        logger.info("Начало импорта запросов из CSV");
        String filePath = null;
        try {
            view.printHeader("импорт запросов из csv");
            filePath = view.readLine("путь к файлу: ");

            if (filePath == null || filePath.trim().isEmpty()) {
                logger.warn("Попытка импорта с пустым путем к файлу");
                view.showError("путь к файлу не может быть пустым");
                view.waitForEnter();
                return;
            }

            service.importRequests(filePath);
            view.showSuccess("запросы импортированы");
            logger.info("Запросы успешно импортированы из файла: {}", filePath);

        } catch (IOException e) {
            logger.error("Ошибка чтения файла при импорте запросов: {}", e.getMessage());
            view.showError("ошибка чтения файла: " + e.getMessage());
            view.showInfo("проверьте, что файл существует и доступен для чтения");
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка данных при импорте запросов: {}", e.getMessage());
            view.showError("ошибка данных: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Непредвиденная ошибка при импорте запросов", e);
            view.showError("непредвиденная ошибка: " + e.getMessage());
        } finally {
            view.waitForEnter();
        }
    }

    // ===== ЭКСПОРТ =====

    public void exportBooks() {
        logger.info("Начало экспорта книг в CSV");
        String filePath = null;
        try {
            view.printHeader("экспорт книг в csv");
            filePath = view.readLine("путь для сохранения: ");

            if (filePath == null || filePath.trim().isEmpty()) {
                logger.warn("Попытка экспорта с пустым путем");
                view.showError("путь для сохранения не может быть пустым");
                view.waitForEnter();
                return;
            }

            if (!filePath.toLowerCase().endsWith(".csv")) {
                filePath += ".csv";
            }

            service.exportBooks(filePath);
            view.showSuccess("книги экспортированы в: " + filePath);
            logger.info("Книги успешно экспортированы в файл: {}", filePath);

        } catch (IOException e) {
            logger.error("Ошибка записи файла при экспорте книг: {}", e.getMessage());
            view.showError("ошибка записи файла: " + e.getMessage());
            view.showInfo("проверьте, что у вас есть права на запись в указанную папку");
        } catch (Exception e) {
            logger.error("Непредвиденная ошибка при экспорте книг", e);
            view.showError("непредвиденная ошибка: " + e.getMessage());
        } finally {
            view.waitForEnter();
        }
    }

    public void exportOrders() {
        logger.info("Начало экспорта заказов в CSV");
        String filePath = null;
        try {
            view.printHeader("экспорт заказов в csv");
            filePath = view.readLine("путь для сохранения: ");

            if (filePath == null || filePath.trim().isEmpty()) {
                logger.warn("Попытка экспорта с пустым путем");
                view.showError("путь для сохранения не может быть пустым");
                view.waitForEnter();
                return;
            }

            if (!filePath.toLowerCase().endsWith(".csv")) {
                filePath += ".csv";
            }

            service.exportOrders(filePath);
            view.showSuccess("заказы экспортированы в: " + filePath);
            logger.info("Заказы успешно экспортированы в файл: {}", filePath);

        } catch (IOException e) {
            logger.error("Ошибка записи файла при экспорте заказов: {}", e.getMessage());
            view.showError("ошибка записи файла: " + e.getMessage());
            view.showInfo("проверьте, что у вас есть права на запись в указанную папку");
        } catch (Exception e) {
            logger.error("Непредвиденная ошибка при экспорте заказов", e);
            view.showError("непредвиденная ошибка: " + e.getMessage());
        } finally {
            view.waitForEnter();
        }
    }

    public void exportRequests() {
        logger.info("Начало экспорта запросов в CSV");
        String filePath = null;
        try {
            view.printHeader("экспорт запросов в csv");
            filePath = view.readLine("путь для сохранения: ");

            if (filePath == null || filePath.trim().isEmpty()) {
                logger.warn("Попытка экспорта с пустым путем");
                view.showError("путь для сохранения не может быть пустым");
                view.waitForEnter();
                return;
            }

            if (!filePath.toLowerCase().endsWith(".csv")) {
                filePath += ".csv";
            }

            service.exportRequests(filePath);
            view.showSuccess("запросы экспортированы в: " + filePath);
            logger.info("Запросы успешно экспортированы в файл: {}", filePath);

        } catch (IOException e) {
            logger.error("Ошибка записи файла при экспорте запросов: {}", e.getMessage());
            view.showError("ошибка записи файла: " + e.getMessage());
            view.showInfo("проверьте, что у вас есть права на запись в указанную папку");
        } catch (Exception e) {
            logger.error("Непредвиденная ошибка при экспорте запросов", e);
            view.showError("непредвиденная ошибка: " + e.getMessage());
        } finally {
            view.waitForEnter();
        }
    }

    // ===== ПРОСМОТР =====

    public void viewBooks() {
        logger.info("Начало просмотра списка книг");
        try {
            view.showBookSortMenu();
            int choice = view.readInt("выбор: ");
            SortType sortType = view.getBookSortType(choice);

            List<Book> books = service.getBooks(sortType);
            view.showBooks(books);
            logger.info("Показано {} книг", books != null ? books.size() : 0);

        } catch (IllegalArgumentException e) {
            logger.error("Ошибка данных при просмотре книг: {}", e.getMessage());
            view.showError("ошибка данных: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Непредвиденная ошибка при просмотре книг", e);
            view.showError("непредвиденная ошибка: " + e.getMessage());
        } finally {
            view.waitForEnter();
        }
    }

    public void viewOrders() {
        logger.info("Начало просмотра списка заказов");
        try {
            view.showOrderSortMenu();
            int choice = view.readInt("выбор: ");
            SortType sortType = view.getOrderSortType(choice);

            List<Order> orders = service.getOrders(sortType);
            view.showOrders(orders);
            logger.info("Показано {} заказов", orders != null ? orders.size() : 0);

        } catch (IllegalArgumentException e) {
            logger.error("Ошибка данных при просмотре заказов: {}", e.getMessage());
            view.showError("ошибка данных: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Непредвиденная ошибка при просмотре заказов", e);
            view.showError("непредвиденная ошибка: " + e.getMessage());
        } finally {
            view.waitForEnter();
        }
    }

    public void viewRequests() {
        logger.info("Начало просмотра списка запросов");
        try {
            view.showRequestSortMenu();
            int choice = view.readInt("выбор: ");
            SortType sortType = view.getRequestSortType(choice);

            List<Request> requests = service.getRequests(sortType);
            view.showRequests(requests);
            logger.info("Показано {} запросов", requests != null ? requests.size() : 0);

        } catch (IllegalArgumentException e) {
            logger.error("Ошибка данных при просмотре запросов: {}", e.getMessage());
            view.showError("ошибка данных: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Непредвиденная ошибка при просмотре запросов", e);
            view.showError("непредвиденная ошибка: " + e.getMessage());
        } finally {
            view.waitForEnter();
        }
    }

    public void viewCompletedOrders() {
        logger.info("Начало просмотра выполненных заказов за период");
        try {
            view.printHeader("выполненные заказы за период");
            LocalDate start = view.readDate("дата начала (дд.мм.гггг): ");
            LocalDate end = view.readDate("дата конца (дд.мм.гггг): ");

            if (start.isAfter(end)) {
                logger.warn("Некорректный период: start={}, end={}", start, end);
                view.showError("дата начала не может быть позже даты конца");
                view.waitForEnter();
                return;
            }

            view.showOrderSortMenu();
            int choice = view.readInt("выбор: ");
            SortType sortType = view.getOrderSortType(choice);

            List<Order> orders = service.getCompletedOrders(start, end, sortType);
            view.showOrders(orders);
            logger.info("Показано {} выполненных заказов за период {} - {}",
                    orders != null ? orders.size() : 0, start, end);

        } catch (IllegalArgumentException e) {
            logger.error("Ошибка данных при просмотре выполненных заказов: {}", e.getMessage());
            view.showError("ошибка данных: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Непредвиденная ошибка при просмотре выполненных заказов", e);
            view.showError("непредвиденная ошибка: " + e.getMessage());
        } finally {
            view.waitForEnter();
        }
    }

    public void viewRevenue() {
        logger.info("Начало просмотра выручки за период");
        try {
            view.printHeader("выручка за период");
            LocalDate start = view.readDate("дата начала (дд.мм.гггг): ");
            LocalDate end = view.readDate("дата конца (дд.мм.гггг): ");

            if (start.isAfter(end)) {
                logger.warn("Некорректный период: start={}, end={}", start, end);
                view.showError("дата начала не может быть позже даты конца");
                view.waitForEnter();
                return;
            }

            double revenue = service.getTotalRevenue(start, end);
            view.showRevenue(revenue, start, end);
            logger.info("Выручка за период {} - {}: {}", start, end, revenue);

        } catch (IllegalArgumentException e) {
            logger.error("Ошибка данных при просмотре выручки: {}", e.getMessage());
            view.showError("ошибка данных: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Непредвиденная ошибка при просмотре выручки", e);
            view.showError("непредвиденная ошибка: " + e.getMessage());
        } finally {
            view.waitForEnter();
        }
    }

    public void viewCompletedOrdersCount() {
        logger.info("Начало подсчета выполненных заказов за период");
        try {
            view.printHeader("количество выполненных заказов");
            LocalDate start = view.readDate("дата начала (дд.мм.гггг): ");
            LocalDate end = view.readDate("дата конца (дд.мм.гггг): ");

            if (start.isAfter(end)) {
                logger.warn("Некорректный период: start={}, end={}", start, end);
                view.showError("дата начала не может быть позже даты конца");
                view.waitForEnter();
                return;
            }

            int count = service.getCompletedOrdersCount(start, end);
            view.showOrdersCount(count, start, end);
            logger.info("Количество выполненных заказов за период {} - {}: {}", start, end, count);

        } catch (IllegalArgumentException e) {
            logger.error("Ошибка данных при подсчете заказов: {}", e.getMessage());
            view.showError("ошибка данных: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Непредвиденная ошибка при подсчете заказов", e);
            view.showError("непредвиденная ошибка: " + e.getMessage());
        } finally {
            view.waitForEnter();
        }
    }

    public void viewOldBooks() {
        logger.info("Начало просмотра залежавшихся книг");
        try {
            List<Book> oldBooks = service.getOldBooks();
            view.showOldBooks(oldBooks);
            logger.info("Показано {} залежавшихся книг", oldBooks != null ? oldBooks.size() : 0);

        } catch (IllegalArgumentException e) {
            logger.error("Ошибка данных при просмотре залежавшихся книг: {}", e.getMessage());
            view.showError("ошибка данных: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Непредвиденная ошибка при просмотре залежавшихся книг", e);
            view.showError("непредвиденная ошибка: " + e.getMessage());
        } finally {
            view.waitForEnter();
        }
    }

    public void viewOrderDetails() {
        logger.info("Начало просмотра деталей заказа");
        int orderId = 0;
        try {
            view.printHeader("детали заказа");
            orderId = view.readInt("id заказа: ");

            OrderDetails details = service.getOrderDetails(orderId);
            view.showOrderDetails(details);
            logger.info("Показаны детали заказа #{}", orderId);

        } catch (IllegalArgumentException e) {
            logger.error("Заказ #{} не найден: {}", orderId, e.getMessage());
            view.showError("заказ не найден: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Непредвиденная ошибка при просмотре деталей заказа #{}", orderId, e);
            view.showError("непредвиденная ошибка: " + e.getMessage());
        } finally {
            view.waitForEnter();
        }
    }

    public void viewBookDescription() {
        logger.info("Начало просмотра описания книги");
        int bookId = 0;
        try {
            view.printHeader("описание книги");
            bookId = view.readInt("id книги: ");

            String description = service.getBookDescription(bookId);
            view.showBookDescription(description);
            logger.info("Показано описание книги ID: {}", bookId);

        } catch (IllegalArgumentException e) {
            logger.error("Книга ID: {} не найдена: {}", bookId, e.getMessage());
            view.showError("книга не найдена: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Непредвиденная ошибка при просмотре описания книги ID: {}", bookId, e);
            view.showError("непредвиденная ошибка: " + e.getMessage());
        } finally {
            view.waitForEnter();
        }
    }

    // ===== КОНФИГУРАЦИЯ =====

    public void showConfig() {
        logger.info("Начало просмотра конфигурации");
        try {
            view.printHeader("текущие настройки");
            service.showConfig();
            logger.info("Конфигурация показана успешно");
            view.waitForEnter();
        } catch (Exception e) {
            logger.error("Ошибка при просмотре конфигурации", e);
            view.showError("ошибка: " + e.getMessage());
            view.waitForEnter();
        }
    }

    public void saveState() {
        logger.info("Начало сохранения состояния");
        service.saveState();
        view.showSuccess("состояние сохранено");
        logger.info("Состояние успешно сохранено");
    }

    public void loadState() {
        logger.info("Начало загрузки состояния");
        service.loadState();
        view.showSuccess("состояние загружено");
        logger.info("Состояние успешно загружено");
    }

    public void changeOldMonths() {
        logger.info("Начало изменения периода залежавшихся книг");
        int months = 0;
        try {
            view.printHeader("изменение периода залежавшихся книг");
            months = view.readInt("количество месяцев (текущее: " + config.getOldMonths() + "): ");

            if (months <= 0) {
                logger.warn("Попытка установить неположительное количество месяцев: {}", months);
                view.showError("количество месяцев должно быть больше 0");
                view.waitForEnter();
                return;
            }

            service.setOldMonths(months);
            view.showSuccess("период изменен на " + months + " месяцев");
            logger.info("Период залежавшихся книг изменен на {} месяцев", months);
            view.waitForEnter();

        } catch (Exception e) {
            logger.error("Ошибка при изменении периода залежавшихся книг", e);
            view.showError("ошибка: " + e.getMessage());
            view.waitForEnter();
        }
    }

    public void changeAutoFulfill() {
        logger.info("Начало изменения настройки авто-выполнения запросов");
        int choice = 0;
        try {
            view.printHeader("авто-выполнение запросов");
            view.showInfo("текущее состояние: " + (config.isAutoFulfillRequests() ? "включено" : "отключено"));

            choice = view.readInt("1 - включить, 2 - отключить: ");

            boolean newValue;
            if (choice == 1) {
                newValue = true;
            } else if (choice == 2) {
                newValue = false;
            } else {
                logger.warn("Неверный выбор при изменении авто-выполнения: {}", choice);
                view.showError("неверный выбор");
                view.waitForEnter();
                return;
            }

            service.setAutoFulfillRequests(newValue);
            view.showSuccess("авто-выполнение запросов " + (newValue ? "включено" : "отключено"));
            logger.info("Авто-выполнение запросов {}", newValue ? "включено" : "отключено");
            view.waitForEnter();

        } catch (Exception e) {
            logger.error("Ошибка при изменении авто-выполнения запросов", e);
            view.showError("ошибка: " + e.getMessage());
            view.waitForEnter();
        }
    }

    // ===== МЕТОДЫ ИНТЕРФЕЙСА =====

    @Override
    public void start() {
        logger.info("Запуск BookStoreController через start()");
    }

    @Override
    public void exit() {
        logger.info("Выход из приложения");
        System.out.println("до свидания");
        System.exit(0);
    }

    @Override
    public void processCommand(int command) {
        logger.info("Обработка команды: {}", command);
    }
}