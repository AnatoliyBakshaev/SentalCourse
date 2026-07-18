package bookstore.view;

import bookstore.config.AppConfig;
import bookstore.model.Book;
import bookstore.model.Order;
import bookstore.model.OrderDetails;
import bookstore.model.Request;
import bookstore.model.enums.BookStatus;
import bookstore.model.enums.OrderStatus;
import bookstore.model.enums.SortType;
import bookstore.utils.ValidationUtils;
import bookstore.view.enums.MenuOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

@Component
public class ConsoleView {
    @Autowired
    private AppConfig config;
    private static ConsoleView instance;
    private final Scanner scanner;

    public ConsoleView() {
        this.scanner = new Scanner(System.in);
    }

    public static ConsoleView getInstance() {
        if (instance == null) {
            instance = new ConsoleView();
        }
        return instance;
    }


    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("ошибка: введите число");
            }
        }
    }

    public double readDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("ошибка: введите число");
            }
        }
    }

    public LocalDate readDate(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                return LocalDate.parse(input, formatter);
            } catch (Exception e) {
                System.out.println("ошибка: введите дату в формате дд.мм.гггг");
            }
        }
    }

    public void waitForEnter() {
        System.out.println("нажмите enter для продолжения...");
        try {
            scanner.nextLine();
        } catch (Exception e) {
            // ignore
        }
    }


    public void showWelcome() {
        printHeader("добро пожаловать в электронный книжный магазин");
    }

    public void showGoodbye() {
        printHeader("до свидания!");
    }

    public void printHeader(String title) {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("  " + title);
        System.out.println("==================================================");
    }

    public void printDivider() {
        System.out.println("--------------------------------------------------");
    }

    public void showMenu() {
        System.out.println();
        System.out.println("главное меню:");
        System.out.println("==================================================");
        System.out.println(" 0. выход");

        System.out.println();
        System.out.println("операции:");
        System.out.println(" 1. создать заказ");
        System.out.println(" 2. отменить заказ");
        System.out.println(" 3. изменить статус заказа");
        System.out.println(" 4. добавить книгу на склад");
        System.out.println(" 5. оставить запрос на книгу");
        System.out.println(" 6. списать книгу");

        System.out.println();
        System.out.println("просмотр:");
        System.out.println(" 7. список книг");
        System.out.println(" 8. список заказов");
        System.out.println(" 9. список запросов");
        System.out.println("10. выполненные заказы за период");
        System.out.println("11. выручка за период");
        System.out.println("12. залежавшиеся книги");
        System.out.println("13. детали заказа");
        System.out.println("14. описание книги");
        System.out.println("==================================================");
        System.out.print("выберите опцию: ");
    }

    public void showBooks(List<Book> books) {
        if (books == null || books.isEmpty()) {
            System.out.println("книги не найдены");
            return;
        }

        printHeader("список книг (" + books.size() + " шт.)");
        for (Book book : books) {
            showBook(book);
            System.out.println();
        }
    }

    public void showBook(Book book) {
        if (book == null) {
            System.out.println("книга не найдена");
            return;
        }

        System.out.println("id: " + book.getId());
        System.out.println("название: " + book.getTitle());
        System.out.println("автор: " + book.getAuthor());
        System.out.println("жанр: " + book.getGenre());
        System.out.println("издательство: " + book.getPublisher());
        System.out.println("isbn: " + book.getIsbn());
        System.out.println("цена: " + String.format("%.2f", book.getPrice()) + " руб.");
        System.out.println("статус: " + book.getStatus().getDescription());
        System.out.println("количество: " + book.getQuantity());
        System.out.println("дата поступления: " + book.getReceivedDate());
    }

    public void showOrders(List<Order> orders) {
        if (orders == null || orders.isEmpty()) {
            System.out.println("заказы не найдены");
            return;
        }

        printHeader("список заказов (" + orders.size() + " шт.)");
        for (Order order : orders) {
            showOrder(order);
            System.out.println();
        }
    }

    public void showOrder(Order order) {
        if (order == null) {
            System.out.println("заказ не найден");
            return;
        }

        System.out.println("id заказа: " + order.getId());
        System.out.println("id книги: " + order.getBookId());
        System.out.println("клиент: " + order.getCustomerName());
        System.out.println("телефон: " + order.getCustomerPhone());
        System.out.println("статус: " + order.getStatus().getDescription());
        System.out.println("сумма: " + String.format("%.2f", order.getTotalPrice()) + " руб.");
        System.out.println("дата заказа: " + order.getOrderDate());
        if (order.getCompletionDate() != null) {
            System.out.println("дата выполнения: " + order.getCompletionDate());
        }
    }

    public void showRequests(List<Request> requests) {
        if (requests == null || requests.isEmpty()) {
            System.out.println("запросы не найдены");
            return;
        }

        printHeader("список запросов (" + requests.size() + " шт.)");
        for (Request request : requests) {
            showRequest(request);
            System.out.println();
        }
    }

    public void showRequest(Request request) {
        if (request == null) {
            System.out.println("запрос не найден");
            return;
        }

        System.out.println("id книги: " + request.getBookId());
        System.out.println("клиент: " + request.getCustomerName());
        System.out.println("телефон: " + request.getCustomerPhone());
        System.out.println("дата запроса: " + request.getRequestDate());
        System.out.println("выполнен: " + (request.isFulfilled() ? "да" : "нет"));
    }

    public void showOrderDetails(OrderDetails details) {
        if (details == null) {
            System.out.println("детали заказа не найдены");
            return;
        }

        printHeader("детали заказа #" + details.getOrderId());

        System.out.println("информация о заказе:");
        System.out.println("  статус: " + details.getStatus().getDescription());
        System.out.println("  дата заказа: " + details.getOrderDate());
        if (details.getCompletionDate() != null) {
            System.out.println("  дата выполнения: " + details.getCompletionDate());
        }
        System.out.println("  общая сумма: " + String.format("%.2f", details.getTotalPrice()) + " руб.");

        System.out.println();
        System.out.println("данные заказчика:");
        System.out.println("  имя: " + details.getCustomerName());
        System.out.println("  телефон: " + details.getCustomerPhone());
        System.out.println("  email: " + details.getCustomerEmail());
        System.out.println("  адрес: " + details.getCustomerAddress());

        System.out.println();
        System.out.println("заказанные книги:");
        if (details.getBooks() != null && !details.getBooks().isEmpty()) {
            for (Book book : details.getBooks()) {
                System.out.println("  - " + book.getTitle() + " (" + book.getAuthor() + ")");
                System.out.println("    цена: " + String.format("%.2f", book.getPrice()) + " руб.");
            }
        } else {
            System.out.println("  книги не найдены");
        }
    }

    public void showBookDescription(String title, String author, String genre,
                                    double price, String description) {
        printHeader("описание книги");
        System.out.println("название: " + title);
        System.out.println("автор: " + author);
        System.out.println("жанр: " + genre);
        System.out.println("цена: " + String.format("%.2f", price) + " руб.");
        System.out.println("описание: " + description);
    }

    public void showRevenue(double revenue, LocalDate startDate, LocalDate endDate) {
        printHeader("выручка за период");
        System.out.println("период: с " + startDate + " по " + endDate);
        System.out.println("сумма: " + String.format("%.2f", revenue) + " руб.");
    }

    public void showOrdersCount(int count, LocalDate startDate, LocalDate endDate) {
        printHeader("количество выполненных заказов");
        System.out.println("период: с " + startDate + " по " + endDate);
        System.out.println("количество: " + count);
    }

    public void showOldBooks(List<Book> books) {
        if (books == null || books.isEmpty()) {
            System.out.println("залежавшихся книг не найдено");
            return;
        }

        printHeader("залежавшиеся книги (> 6 месяцев)");
        for (Book book : books) {
            System.out.println("- " + book.getTitle() + " (" + book.getAuthor() + ")");
            System.out.println("  цена: " + String.format("%.2f", book.getPrice()) + " руб.");
            System.out.println("  поступила: " + book.getReceivedDate());
            System.out.println();
        }
    }

    public void showSuccess(String message) {
        System.out.println("успешно: " + message);
    }

    public void showError(String message) {
        System.out.println("ошибка: " + message);
    }

    public void showInfo(String message) {
        System.out.println("информация: " + message);
    }

    public void showWarning(String message) {
        System.out.println("предупреждение: " + message);
    }

    // ===== МЕНЮ СОРТИРОВКИ =====

    public void showBookSortMenu() {
        System.out.println();
        System.out.println("сортировка книг:");
        System.out.println("1. по названию (а-я)");
        System.out.println("2. по названию (я-а)");
        System.out.println("3. по автору (а-я)");
        System.out.println("4. по автору (я-а)");
        System.out.println("5. по цене (возрастание)");
        System.out.println("6. по цене (убывание)");
        System.out.println("7. по статусу");
        System.out.print("выберите сортировку: ");
    }

    public void showOrderSortMenu() {
        System.out.println();
        System.out.println("сортировка заказов:");
        System.out.println("1. по дате (старые -> новые)");
        System.out.println("2. по дате (новые -> старые)");
        System.out.println("3. по статусу");
        System.out.println("4. по сумме (возрастание)");
        System.out.println("5. по сумме (убывание)");
        System.out.print("выберите сортировку: ");
    }

    public void showRequestSortMenu() {
        System.out.println();
        System.out.println("сортировка запросов:");
        System.out.println("1. по имени клиента (а-я)");
        System.out.println("2. по имени клиента (я-а)");
        System.out.println("3. по количеству запросов (возрастание)");
        System.out.println("4. по количеству запросов (убывание)");
        System.out.print("выберите сортировку: ");
    }

    public void showStatusMenu() {
        System.out.println();
        System.out.println("выберите статус:");
        System.out.println("1. выполнен");
        System.out.println("2. отменен");
        System.out.print("выбор: ");
    }

    public SortType getBookSortType(int choice) {
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

    public SortType getOrderSortType(int choice) {
        switch (choice) {
            case 1: return SortType.BY_ORDER_DATE_ASC;
            case 2: return SortType.BY_ORDER_DATE_DESC;
            case 3: return SortType.BY_STATUS_ORDER_ASC;
            case 4: return SortType.BY_TOTAL_PRICE_ASC;
            case 5: return SortType.BY_TOTAL_PRICE_DESC;
            default: return SortType.BY_ORDER_DATE_ASC;
        }
    }

    public SortType getRequestSortType(int choice) {
        switch (choice) {
            case 1: return SortType.BY_CUSTOMER_NAME_ASC;
            case 2: return SortType.BY_CUSTOMER_NAME_DESC;
            case 3: return SortType.BY_REQUESTS_COUNT_ASC;
            case 4: return SortType.BY_REQUESTS_COUNT_DESC;
            default: return SortType.BY_CUSTOMER_NAME_ASC;
        }
    }

    public OrderStatus getOrderStatus(int choice) {
        switch (choice) {
            case 1: return OrderStatus.COMPLETED;
            case 2: return OrderStatus.CANCELLED;
            default: return null;
        }
    }
    public String readValidName(String prompt) {
        while (true) {
            String name = readLine(prompt);
            if (ValidationUtils.isValidName(name)) {
                return ValidationUtils.cleanString(name);
            }
            System.out.println("ошибка: имя должно содержать от 2 до 50 букв");
        }
    }

    public String readValidPhone(String prompt) {
        while (true) {
            String phone = readLine(prompt);
            if (ValidationUtils.isValidPhone(phone)) {
                return phone;
            }
            System.out.println("ошибка: введите корректный номер телефона");
        }
    }

    public String readValidEmail(String prompt) {
        while (true) {
            String email = readLine(prompt);
            if (ValidationUtils.isValidEmail(email)) {
                return email;
            }
            System.out.println("ошибка: введите корректный email");
        }
    }

    public String readValidAddress(String prompt) {
        while (true) {
            String address = readLine(prompt);
            if (ValidationUtils.isValidAddress(address)) {
                return ValidationUtils.cleanString(address);
            }
            System.out.println("ошибка: адрес должен содержать от 5 до 200 символов");
        }
    }

    public int readValidId(String prompt) {
        while (true) {
            int id = readInt(prompt);
            if (ValidationUtils.isValidId(id)) {
                return id;
            }
            System.out.println("ошибка: id должен быть положительным числом");
        }
    }

    public double readValidPrice(String prompt) {
        while (true) {
            double price = readDouble(prompt);
            if (ValidationUtils.isValidPrice(price)) {
                return price;
            }
            System.out.println("ошибка: цена должна быть от 0 до 1 000 000");
        }
    }

    public int readValidQuantity(String prompt) {
        while (true) {
            int quantity = readInt(prompt);
            if (ValidationUtils.isValidQuantity(quantity)) {
                return quantity;
            }
            System.out.println("ошибка: количество должно быть от 1 до 9999");
        }
    }
    public void showBookDescription(String description) {
        printHeader("описание книги");
        System.out.println(description);
    }

    public void showImportExportMenu() {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("  импорт / экспорт данных");
        System.out.println("==================================================");
        System.out.println("  импорт:");
        System.out.println("  1. импорт книг");
        System.out.println("  2. импорт заказов");
        System.out.println("  3. импорт запросов");
        System.out.println("  экспорт:");
        System.out.println("  4. экспорт книг");
        System.out.println("  5. экспорт заказов");
        System.out.println("  6. экспорт запросов");
        System.out.println("  0. назад");
        System.out.println("==================================================");
        System.out.print("выберите опцию: ");
    }
}