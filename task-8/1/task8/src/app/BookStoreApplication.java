package app;

import annotation.di.DIContainer;
import controller.BookStoreController;
import controller.MenuController;
import controller.interfaces.IMenuController;
import model.Book;
import model.enums.BookStatus;
import service.BookStoreService;
import view.MenuBuilder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class BookStoreApplication {
    public static void main(String[] args) {
        // ===== 1. ИНИЦИАЛИЗАЦИЯ DI КОНТЕЙНЕРА =====
        DIContainer di = DIContainer.getInstance();
        di.scan("config", "service", "controller", "view");

        // ===== 2. ПОЛУЧАЕМ СЕРВИС =====
        BookStoreService service = di.getComponent(BookStoreService.class);

        if (service == null) {
            System.out.println("сервис не найден в DI, создаем вручную");
            List<Book> initialBooks = getInitialBooks();
            service = new BookStoreService(initialBooks);
            di.registerInstance(BookStoreService.class, service);
        }

        // Загружаем состояние
        service.loadState();

        // Сохранение при выходе
        final BookStoreService finalService = service;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("сохранение состояния перед выходом...");
            finalService.saveState();
        }));

        // ===== 3. ПОЛУЧАЕМ КОНТРОЛЛЕР =====
        BookStoreController controller = di.getComponent(BookStoreController.class);

        if (controller == null) {
            System.out.println("контроллер не найден в DI, создаем вручную");
            controller = new BookStoreController(service);
            di.registerInstance(BookStoreController.class, controller);
        }

        // ===== 4. ЗАПУСКАЕМ =====
        MenuBuilder menuBuilder = new MenuBuilder(controller);
        MenuController menuController = new MenuController(menuBuilder);
        menuController.run();

        // Сохраняем состояние
        finalService.saveState();
    }

    private static List<Book> getInitialBooks() {
        return Arrays.asList(
                new Book(1, "война и мир", "лев толстой", "978-5-17-118636-8",
                        BookStatus.IN_STOCK, 1500.0, LocalDate.of(1869, 1, 1),
                        "эпический роман", "роман", "эксмо", 1300),
                new Book(2, "преступление и наказание", "федор достоевский", "978-5-04-105222-3",
                        BookStatus.OUT_OF_STOCK, 1200.0, LocalDate.of(1866, 1, 1),
                        "психологический роман", "роман", "азбука", 700),
                new Book(3, "мастер и маргарита", "михаил булгаков", "978-5-17-090381-2",
                        BookStatus.IN_STOCK, 1350.0, LocalDate.of(1967, 1, 1),
                        "философский роман", "роман", "аст", 480),
                new Book(4, "1984", "джордж оруэлл", "978-5-17-102235-8",
                        BookStatus.OUT_OF_STOCK, 1100.0, LocalDate.of(1949, 6, 8),
                        "антиутопия", "фантастика", "эксмо", 320),
                new Book(5, "анна каренина", "лев толстой", "978-5-04-103456-4",
                        BookStatus.IN_STOCK, 1400.0, LocalDate.of(1877, 1, 1),
                        "трагическая история любви", "роман", "эксмо", 900)
        );
    }
}