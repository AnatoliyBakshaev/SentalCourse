package controller;


import controller.interfaces.IBookStoreController;
import model.Book;
import model.Order;
import model.OrderDetails;
import model.Request;
import model.enums.OrderStatus;
import model.enums.SortType;
import service.interfaces.IBookStoreService;
import view.ConsoleView;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class BookStoreController implements IBookStoreController {
    private final IBookStoreService service;
    private final ConsoleView view;
    private final Scanner scanner;

    public BookStoreController(IBookStoreService service) {
        this.service = service;
        this.view = ConsoleView.getInstance();
        this.scanner = new Scanner(System.in);
    }


    public void createOrder() {
        try {
            view.printHeader("создание заказа");
            int bookId = view.readInt("id книги: ");
            String name = view.readLine("имя клиента: ");
            String phone = view.readLine("телефон: ");
            String email = view.readLine("email: ");
            String address = view.readLine("адрес: ");

            service.createOrder(bookId, name, phone, email, address);
            view.showSuccess("заказ успешно создан");

        } catch (IllegalArgumentException e) {
            view.showError("ошибка в данных: " + e.getMessage());
            view.showInfo("проверьте правильность введенных данных");
        } catch (IllegalStateException e) {
            view.showError("ошибка состояния: " + e.getMessage());
            view.showInfo("возможно, книга отсутствует или списана");
        } catch (Exception e) {
            view.showError("непредвиденная ошибка: " + e.getMessage());
        } finally {
            view.waitForEnter();
        }
    }

    public void cancelOrder() {
        try {
            view.printHeader("отмена заказа");
            int orderId = view.readInt("id заказа: ");

            service.cancelOrder(orderId);
            view.showSuccess("заказ #" + orderId + " отменен");

        } catch (IllegalArgumentException e) {
            view.showError("заказ не найден: " + e.getMessage());
            view.showInfo("проверьте правильность id заказа");
        } catch (IllegalStateException e) {
            view.showError("нельзя отменить заказ: " + e.getMessage());
        } catch (Exception e) {
            view.showError("непредвиденная ошибка: " + e.getMessage());
        } finally {
            view.waitForEnter();
        }
    }

    public void changeOrderStatus() {
        try {
            view.printHeader("изменение статуса заказа");
            int orderId = view.readInt("id заказа: ");

            view.showStatusMenu();
            int choice = view.readInt("выбор: ");
            OrderStatus status = view.getOrderStatus(choice);

            if (status == null) {
                view.showError("неверный выбор статуса");
                view.waitForEnter();
                return;
            }

            service.changeOrderStatus(orderId, status);
            view.showSuccess("статус заказа изменен на: " + status.getDescription());

        } catch (IllegalArgumentException e) {
            view.showError("заказ не найден: " + e.getMessage());
        } catch (IllegalStateException e) {
            view.showError("ошибка изменения статуса: " + e.getMessage());
        } catch (Exception e) {
            view.showError("непредвиденная ошибка: " + e.getMessage());
        } finally {
            view.waitForEnter();
        }
    }

    public void addBookToStock() {
        try {
            view.printHeader("добавление книги на склад");
            int bookId = view.readInt("id книги: ");
            int quantity = view.readInt("количество: ");

            if (quantity <= 0) {
                view.showError("количество должно быть положительным числом");
                view.waitForEnter();
                return;
            }

            service.addBookToStock(bookId, quantity);
            view.showSuccess("книга добавлена на склад (количество: " + quantity + ")");

        } catch (IllegalArgumentException e) {
            view.showError("книга не найдена: " + e.getMessage());
        } catch (IllegalStateException e) {
            view.showError("ошибка: " + e.getMessage());
        } catch (Exception e) {
            view.showError("непредвиденная ошибка: " + e.getMessage());
        } finally {
            view.waitForEnter();
        }
    }

    public void leaveRequest() {
        try {
            view.printHeader("запрос на книгу");
            int bookId = view.readInt("id книги: ");
            String name = view.readLine("имя клиента: ");
            String phone = view.readLine("телефон: ");

            service.leaveRequest(bookId, name, phone);
            view.showSuccess("запрос на книгу #" + bookId + " создан");

        } catch (IllegalArgumentException e) {
            view.showError("книга не найдена: " + e.getMessage());
        } catch (IllegalStateException e) {
            view.showError("ошибка создания запроса: " + e.getMessage());
        } catch (Exception e) {
            view.showError("непредвиденная ошибка: " + e.getMessage());
        } finally {
            view.waitForEnter();
        }
    }

    public void writeOffBook() {
        try {
            view.printHeader("списание книги");
            int bookId = view.readInt("id книги: ");

            service.writeOffBook(bookId);
            view.showSuccess("книга #" + bookId + " списана");

        } catch (IllegalArgumentException e) {
            view.showError("книга не найдена: " + e.getMessage());
        } catch (IllegalStateException e) {
            view.showError("ошибка списания: " + e.getMessage());
            view.showInfo("возможно, есть активные заказы на эту книгу");
        } catch (Exception e) {
            view.showError("непредвиденная ошибка: " + e.getMessage());
        } finally {
            view.waitForEnter();
        }
    }

    // ===== ИМПОРТ =====

    public void importBooks() {
        try {
            view.printHeader("импорт книг из csv");
            String filePath = view.readLine("путь к файлу: ");

            if (filePath == null || filePath.trim().isEmpty()) {
                view.showError("путь к файлу не может быть пустым");
                view.waitForEnter();
                return;
            }

            service.importBooks(filePath);
            view.showSuccess("книги импортированы");

        } catch (IOException e) {
            view.showError("ошибка чтения файла: " + e.getMessage());
            view.showInfo("проверьте, что файл существует и доступен для чтения");
        } catch (IllegalArgumentException e) {
            view.showError("ошибка данных: " + e.getMessage());
        } catch (Exception e) {
            view.showError("непредвиденная ошибка: " + e.getMessage());
        } finally {
            view.waitForEnter();
        }
    }

    public void importOrders() {
        try {
            view.printHeader("импорт заказов из csv");
            String filePath = view.readLine("путь к файлу: ");

            if (filePath == null || filePath.trim().isEmpty()) {
                view.showError("путь к файлу не может быть пустым");
                view.waitForEnter();
                return;
            }

            service.importOrders(filePath);
            view.showSuccess("заказы импортированы");

        } catch (IOException e) {
            view.showError("ошибка чтения файла: " + e.getMessage());
            view.showInfo("проверьте, что файл существует и доступен для чтения");
        } catch (IllegalArgumentException e) {
            view.showError("ошибка данных: " + e.getMessage());
        } catch (Exception e) {
            view.showError("непредвиденная ошибка: " + e.getMessage());
        } finally {
            view.waitForEnter();
        }
    }

    public void importRequests() {
        try {
            view.printHeader("импорт запросов из csv");
            String filePath = view.readLine("путь к файлу: ");

            if (filePath == null || filePath.trim().isEmpty()) {
                view.showError("путь к файлу не может быть пустым");
                view.waitForEnter();
                return;
            }

            service.importRequests(filePath);
            view.showSuccess("запросы импортированы");

        } catch (IOException e) {
            view.showError("ошибка чтения файла: " + e.getMessage());
            view.showInfo("проверьте, что файл существует и доступен для чтения");
        } catch (IllegalArgumentException e) {
            view.showError("ошибка данных: " + e.getMessage());
        } catch (Exception e) {
            view.showError("непредвиденная ошибка: " + e.getMessage());
        } finally {
            view.waitForEnter();
        }
    }

    // ===== ЭКСПОРТ =====

    public void exportBooks() {
        try {
            view.printHeader("экспорт книг в csv");
            String filePath = view.readLine("путь для сохранения: ");

            if (filePath == null || filePath.trim().isEmpty()) {
                view.showError("путь для сохранения не может быть пустым");
                view.waitForEnter();
                return;
            }

            if (!filePath.toLowerCase().endsWith(".csv")) {
                filePath += ".csv";
            }

            service.exportBooks(filePath);
            view.showSuccess("книги экспортированы в: " + filePath);

        } catch (IOException e) {
            view.showError("ошибка записи файла: " + e.getMessage());
            view.showInfo("проверьте, что у вас есть права на запись в указанную папку");
        } catch (Exception e) {
            view.showError("непредвиденная ошибка: " + e.getMessage());
        } finally {
            view.waitForEnter();
        }
    }

    public void exportOrders() {
        try {
            view.printHeader("экспорт заказов в csv");
            String filePath = view.readLine("путь для сохранения: ");

            if (filePath == null || filePath.trim().isEmpty()) {
                view.showError("путь для сохранения не может быть пустым");
                view.waitForEnter();
                return;
            }

            if (!filePath.toLowerCase().endsWith(".csv")) {
                filePath += ".csv";
            }

            service.exportOrders(filePath);
            view.showSuccess("заказы экспортированы в: " + filePath);

        } catch (IOException e) {
            view.showError("ошибка записи файла: " + e.getMessage());
            view.showInfo("проверьте, что у вас есть права на запись в указанную папку");
        } catch (Exception e) {
            view.showError("непредвиденная ошибка: " + e.getMessage());
        } finally {
            view.waitForEnter();
        }
    }

    public void exportRequests() {
        try {
            view.printHeader("экспорт запросов в csv");
            String filePath = view.readLine("путь для сохранения: ");

            if (filePath == null || filePath.trim().isEmpty()) {
                view.showError("путь для сохранения не может быть пустым");
                view.waitForEnter();
                return;
            }

            if (!filePath.toLowerCase().endsWith(".csv")) {
                filePath += ".csv";
            }

            service.exportRequests(filePath);
            view.showSuccess("запросы экспортированы в: " + filePath);

        } catch (IOException e) {
            view.showError("ошибка записи файла: " + e.getMessage());
            view.showInfo("проверьте, что у вас есть права на запись в указанную папку");
        } catch (Exception e) {
            view.showError("непредвиденная ошибка: " + e.getMessage());
        } finally {
            view.waitForEnter();
        }
    }

    // ===== ПРОСМОТР =====

    public void viewBooks() {
        try {
            view.showBookSortMenu();
            int choice = view.readInt("выбор: ");
            SortType sortType = view.getBookSortType(choice);

            List<Book> books = service.getBooks(sortType);
            view.showBooks(books);

        } catch (IllegalArgumentException e) {
            view.showError("ошибка данных: " + e.getMessage());
        } catch (Exception e) {
            view.showError("непредвиденная ошибка: " + e.getMessage());
        } finally {
            view.waitForEnter();
        }
    }

    public void viewOrders() {
        try {
            view.showOrderSortMenu();
            int choice = view.readInt("выбор: ");
            SortType sortType = view.getOrderSortType(choice);

            List<Order> orders = service.getOrders(sortType);
            view.showOrders(orders);

        } catch (IllegalArgumentException e) {
            view.showError("ошибка данных: " + e.getMessage());
        } catch (Exception e) {
            view.showError("непредвиденная ошибка: " + e.getMessage());
        } finally {
            view.waitForEnter();
        }
    }

    public void viewRequests() {
        try {
            view.showRequestSortMenu();
            int choice = view.readInt("выбор: ");
            SortType sortType = view.getRequestSortType(choice);

            List<Request> requests = service.getRequests(sortType);
            view.showRequests(requests);

        } catch (IllegalArgumentException e) {
            view.showError("ошибка данных: " + e.getMessage());
        } catch (Exception e) {
            view.showError("непредвиденная ошибка: " + e.getMessage());
        } finally {
            view.waitForEnter();
        }
    }

    public void viewCompletedOrders() {
        try {
            view.printHeader("выполненные заказы за период");
            LocalDate start = view.readDate("дата начала (дд.мм.гггг): ");
            LocalDate end = view.readDate("дата конца (дд.мм.гггг): ");

            if (start.isAfter(end)) {
                view.showError("дата начала не может быть позже даты конца");
                view.waitForEnter();
                return;
            }

            view.showOrderSortMenu();
            int choice = view.readInt("выбор: ");
            SortType sortType = view.getOrderSortType(choice);

            List<Order> orders = service.getCompletedOrders(start, end, sortType);
            view.showOrders(orders);

        } catch (IllegalArgumentException e) {
            view.showError("ошибка данных: " + e.getMessage());
        } catch (Exception e) {
            view.showError("непредвиденная ошибка: " + e.getMessage());
        } finally {
            view.waitForEnter();
        }
    }

    public void viewRevenue() {
        try {
            view.printHeader("выручка за период");
            LocalDate start = view.readDate("дата начала (дд.мм.гггг): ");
            LocalDate end = view.readDate("дата конца (дд.мм.гггг): ");

            if (start.isAfter(end)) {
                view.showError("дата начала не может быть позже даты конца");
                view.waitForEnter();
                return;
            }

            double revenue = service.getTotalRevenue(start, end);
            view.showRevenue(revenue, start, end);

        } catch (IllegalArgumentException e) {
            view.showError("ошибка данных: " + e.getMessage());
        } catch (Exception e) {
            view.showError("непредвиденная ошибка: " + e.getMessage());
        } finally {
            view.waitForEnter();
        }
    }

    public void viewCompletedOrdersCount() {
        try {
            view.printHeader("количество выполненных заказов");
            LocalDate start = view.readDate("дата начала (дд.мм.гггг): ");
            LocalDate end = view.readDate("дата конца (дд.мм.гггг): ");

            if (start.isAfter(end)) {
                view.showError("дата начала не может быть позже даты конца");
                view.waitForEnter();
                return;
            }

            int count = service.getCompletedOrdersCount(start, end);
            view.showOrdersCount(count, start, end);

        } catch (IllegalArgumentException e) {
            view.showError("ошибка данных: " + e.getMessage());
        } catch (Exception e) {
            view.showError("непредвиденная ошибка: " + e.getMessage());
        } finally {
            view.waitForEnter();
        }
    }

    public void viewOldBooks() {
        try {
            List<Book> oldBooks = service.getOldBooks();
            view.showOldBooks(oldBooks);

        } catch (IllegalArgumentException e) {
            view.showError("ошибка данных: " + e.getMessage());
        } catch (Exception e) {
            view.showError("непредвиденная ошибка: " + e.getMessage());
        } finally {
            view.waitForEnter();
        }
    }

    public void viewOrderDetails() {
        try {
            view.printHeader("детали заказа");
            int orderId = view.readInt("id заказа: ");

            OrderDetails details = service.getOrderDetails(orderId);
            view.showOrderDetails(details);

        } catch (IllegalArgumentException e) {
            view.showError("заказ не найден: " + e.getMessage());
        } catch (Exception e) {
            view.showError("непредвиденная ошибка: " + e.getMessage());
        } finally {
            view.waitForEnter();
        }
    }

    public void viewBookDescription() {
        try {
            view.printHeader("описание книги");
            int bookId = view.readInt("id книги: ");

            String description = service.getBookDescription(bookId);
            System.out.println(description);

        } catch (IllegalArgumentException e) {
            view.showError("книга не найдена: " + e.getMessage());
        } catch (Exception e) {
            view.showError("непредвиденная ошибка: " + e.getMessage());
        } finally {
            view.waitForEnter();
        }
    }

    @Override
    public void start() {}

    @Override
    public void exit() {
        System.out.println("до свидания");
        System.exit(0);
    }

    @Override
    public void processCommand(int command) {}
}

