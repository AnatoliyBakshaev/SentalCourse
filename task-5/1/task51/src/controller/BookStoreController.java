package controller;


import controller.interfaces.IBookStoreController;
import model.OrderDetails;
import model.enums.OrderStatus;
import model.enums.SortType;
import service.interfaces.IBookStoreService;
import view.ConsoleView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
            System.out.println("\n СОЗДАНИЕ ЗАКАЗА");
            System.out.print("ID книги: ");
            int bookId = Integer.parseInt(scanner.nextLine());
            System.out.print("Имя клиента: ");
            String name = scanner.nextLine();
            System.out.print("Телефон: ");
            String phone = scanner.nextLine();
            System.out.print("Email: ");
            String email = scanner.nextLine();
            System.out.print("Адрес: ");
            String address = scanner.nextLine();

            service.createOrder(bookId, name, phone, email, address);
            view.showSuccess("Заказ успешно создан!");
        } catch (Exception e) {
            view.showError(e.getMessage());
        }
    }

    public void cancelOrder() {
        try {
            System.out.println("\n ОТМЕНА ЗАКАЗА");
            System.out.print("ID заказа: ");
            int orderId = Integer.parseInt(scanner.nextLine());

            service.cancelOrder(orderId);
            view.showSuccess("Заказ #" + orderId + " отменен!");
        } catch (Exception e) {
            view.showError(e.getMessage());
        }
    }

    public void changeOrderStatus() {
        try {
            System.out.println("\n ИЗМЕНЕНИЕ СТАТУСА ЗАКАЗА");
            System.out.print("ID заказа: ");
            int orderId = Integer.parseInt(scanner.nextLine());

            System.out.println("Выберите статус:");
            System.out.println("1. Выполнен");
            System.out.println("2. Отменен");
            System.out.print("Выбор: ");
            int choice = Integer.parseInt(scanner.nextLine());

            OrderStatus status;
            switch (choice) {
                case 1:
                    status = OrderStatus.COMPLETED;
                    break;
                case 2:
                    status = OrderStatus.CANCELLED;
                    break;
                default:
                    view.showError("Неверный выбор!");
                    return;
            }

            service.changeOrderStatus(orderId, status);
            view.showSuccess("Статус заказа изменен!");
        } catch (Exception e) {
            view.showError(e.getMessage());
        }
    }

    public void addBookToStock() {
        try {
            System.out.println("\n ДОБАВЛЕНИЕ КНИГИ НА СКЛАД");
            System.out.print("ID книги: ");
            int bookId = Integer.parseInt(scanner.nextLine());
            System.out.print("Количество: ");
            int quantity = Integer.parseInt(scanner.nextLine());

            service.addBookToStock(bookId, quantity);
            view.showSuccess("Книга добавлена на склад!");
        } catch (Exception e) {
            view.showError(e.getMessage());
        }
    }

    public void leaveRequest() {
        try {
            System.out.println("\n ЗАПРОС НА КНИГУ");
            System.out.print("ID книги: ");
            int bookId = Integer.parseInt(scanner.nextLine());
            System.out.print("Имя клиента: ");
            String name = scanner.nextLine();
            System.out.print("Телефон: ");
            String phone = scanner.nextLine();

            service.leaveRequest(bookId, name, phone);
            view.showSuccess("Запрос создан!");
        } catch (Exception e) {
            view.showError(e.getMessage());
        }
    }

    public void writeOffBook() {
        try {
            System.out.println("\n СПИСАНИЕ КНИГИ");
            System.out.print("ID книги: ");
            int bookId = Integer.parseInt(scanner.nextLine());

            service.writeOffBook(bookId);
            view.showSuccess("Книга списана!");
        } catch (Exception e) {
            view.showError(e.getMessage());
        }
    }


    public void viewBooks() {
        try {
            System.out.println("\n СОРТИРОВКА КНИГ");
            System.out.println("1. По названию (А-Я)");
            System.out.println("2. По названию (Я-А)");
            System.out.println("3. По автору (А-Я)");
            System.out.println("4. По автору (Я-А)");
            System.out.println("5. По цене (возрастание)");
            System.out.println("6. По цене (убывание)");
            System.out.println("7. По статусу");
            System.out.print("Выбор: ");
            int choice = Integer.parseInt(scanner.nextLine());

            SortType sortType = getBookSortType(choice);
            var books = service.getBooks(sortType);
            view.showBooks(books);
        } catch (Exception e) {
            view.showError(e.getMessage());
        }
    }

    public void viewOrders() {
        try {
            System.out.println("\n СОРТИРОВКА ЗАКАЗОВ");
            System.out.println("1. По дате (старые → новые)");
            System.out.println("2. По дате (новые → старые)");
            System.out.println("3. По статусу");
            System.out.println("4. По сумме (возрастание)");
            System.out.println("5. По сумме (убывание)");
            System.out.print("Выбор: ");
            int choice = Integer.parseInt(scanner.nextLine());

            SortType sortType = getOrderSortType(choice);
            var orders = service.getOrders(sortType);
            view.showOrders(orders);
        } catch (Exception e) {
            view.showError(e.getMessage());
        }
    }

    public void viewRequests() {
        try {
            System.out.println("\n ОРТИРОВКА ЗАПРОСОВ");
            System.out.println("1. По имени клиента (А-Я)");
            System.out.println("2. По имени клиента (Я-А)");
            System.out.println("3. По количеству запросов (возрастание)");
            System.out.println("4. По количеству запросов (убывание)");
            System.out.print("Выбор: ");
            int choice = Integer.parseInt(scanner.nextLine());

            SortType sortType = getRequestSortType(choice);
            var requests = service.getRequests(sortType);
            view.showRequests(requests);
        } catch (Exception e) {
            view.showError(e.getMessage());
        }
    }

    public void viewCompletedOrders() {
        try {
            System.out.println("\n ВЫПОЛНЕННЫЕ ЗАКАЗЫ ЗА ПЕРИОД");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            System.out.print("Дата начала (дд.мм.гггг): ");
            LocalDate start = LocalDate.parse(scanner.nextLine(), formatter);
            System.out.print("Дата конца (дд.мм.гггг): ");
            LocalDate end = LocalDate.parse(scanner.nextLine(), formatter);

            System.out.println("Сортировка:");
            System.out.println("1. По дате выполнения");
            System.out.println("2. По сумме");
            System.out.print("Выбор: ");
            int choice = Integer.parseInt(scanner.nextLine());

            SortType sortType = (choice == 1) ?
                    SortType.BY_COMPLETION_DATE_ASC :
                    SortType.BY_TOTAL_PRICE_ASC;

            var orders = service.getCompletedOrders(start, end, sortType);
            view.showOrders(orders);
        } catch (Exception e) {
            view.showError(e.getMessage());
        }
    }

    public void viewOrderDetails() {
        try {
            System.out.println("\n ДЕТАЛИ ЗАКАЗА");
            System.out.print("ID заказа: ");
            int orderId = Integer.parseInt(scanner.nextLine());

            OrderDetails details = service.getOrderDetails(orderId);
            view.showOrderDetails(details);
        } catch (Exception e) {
            view.showError(e.getMessage());
        }
    }

    public void viewBookDescription() {
        try {
            System.out.println("\n ОПИСАНИЕ КНИГИ");
            System.out.print("ID книги: ");
            int bookId = Integer.parseInt(scanner.nextLine());

            String description = service.getBookDescription(bookId);
            view.printHeader("ОПИСАНИЕ КНИГИ");
            System.out.println(description);
        } catch (Exception e) {
            view.showError(e.getMessage());
        }
    }

    public void viewOldBooks() {
        try {
            var oldBooks = service.getOldBooks();
            if (oldBooks.isEmpty()) {
                view.showInfo("Залежавшихся книг не найдено");
            } else {
                view.printHeader("ЗАЛЕЖАВШИЕСЯ КНИГИ (> 6 месяцев)");
                view.showBooks(oldBooks);
            }
        } catch (Exception e) {
            view.showError(e.getMessage());
        }
    }


    public void showRevenue() {
        try {
            System.out.println("\n ВЫРУЧКА ЗА ПЕРИОД");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            System.out.print("Дата начала (дд.мм.гггг): ");
            LocalDate start = LocalDate.parse(scanner.nextLine(), formatter);
            System.out.print("Дата конца (дд.мм.гггг): ");
            LocalDate end = LocalDate.parse(scanner.nextLine(), formatter);

            double revenue = service.getTotalRevenue(start, end);
            view.showRevenue(revenue, start, end);
        } catch (Exception e) {
            view.showError(e.getMessage());
        }
    }

    public void showCompletedOrdersCount() {
        try {
            System.out.println("\nКОЛИЧЕСТВО ВЫПОЛНЕННЫХ ЗАКАЗОВ");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            System.out.print("Дата начала (дд.мм.гггг): ");
            LocalDate start = LocalDate.parse(scanner.nextLine(), formatter);
            System.out.print("Дата конца (дд.мм.гггг): ");
            LocalDate end = LocalDate.parse(scanner.nextLine(), formatter);

            int count = service.getCompletedOrdersCount(start, end);
            view.showOrdersCount(count, start, end);
        } catch (Exception e) {
            view.showError(e.getMessage());
        }
    }


    private SortType getBookSortType(int choice) {
        switch (choice) {
            case 1: return SortType.BY_TITLE_ASC;
            case 2: return SortType.BY_TITLE_DESC;
            case 3: return SortType.BY_AUTHOR_ASC;
            case 4: return SortType.BY_AUTHOR_DESC;
            case 5: return SortType.BY_PRICE_ASC;
            case 6: return SortType.BY_PRICE_DESC;
            case 7: return SortType.BY_STATUS_ASC;
            default: return SortType.BY_TITLE_ASC;
        }
    }

    private SortType getOrderSortType(int choice) {
        switch (choice) {
            case 1: return SortType.BY_ORDER_DATE_ASC;
            case 2: return SortType.BY_ORDER_DATE_DESC;
            case 3: return SortType.BY_STATUS_ORDER_ASC;
            case 4: return SortType.BY_TOTAL_PRICE_ASC;
            case 5: return SortType.BY_TOTAL_PRICE_DESC;
            default: return SortType.BY_ORDER_DATE_ASC;
        }
    }

    private SortType getRequestSortType(int choice) {
        switch (choice) {
            case 1: return SortType.BY_CUSTOMER_NAME_ASC;
            case 2: return SortType.BY_CUSTOMER_NAME_DESC;
            case 3: return SortType.BY_REQUESTS_COUNT_ASC;
            case 4: return SortType.BY_REQUESTS_COUNT_DESC;
            default: return SortType.BY_CUSTOMER_NAME_ASC;
        }
    }

    public void viewRevenue() {
        try {
            view.printHeader("выручка за период");
            LocalDate start = view.readDate("дата начала (дд.мм.гггг): ");
            LocalDate end = view.readDate("дата конца (дд.мм.гггг): ");

            double revenue = service.getTotalRevenue(start, end);
            view.showRevenue(revenue, start, end);
            view.waitForEnter();
        } catch (Exception e) {
            view.showError(e.getMessage());
            view.waitForEnter();
        }
    }

    public void viewCompletedOrdersCount() {
        try {
            view.printHeader("количество выполненных заказов");
            LocalDate start = view.readDate("дата начала (дд.мм.гггг): ");
            LocalDate end = view.readDate("дата конца (дд.мм.гггг): ");

            int count = service.getCompletedOrdersCount(start, end);
            view.showOrdersCount(count, start, end);
            view.waitForEnter();
        } catch (Exception e) {
            view.showError(e.getMessage());
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

