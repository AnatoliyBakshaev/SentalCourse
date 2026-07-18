package bookstore.app;

import bookstore.annotation.di.DIContainer;
import bookstore.controller.BookStoreController;
import bookstore.controller.MenuController;
import bookstore.controller.interfaces.IMenuController;
import bookstore.model.Book;
import bookstore.model.enums.BookStatus;
import bookstore.service.BookStoreService;
import bookstore.view.MenuBuilder;
import bookstore.utils.LoggerUtil;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class BookStoreApplication {
    private static final Logger logger = LoggerUtil.getLogger(BookStoreApplication.class);

    public static void main(String[] args) {
        logger.info("============================================");
        logger.info("ЗАПУСК ПРИЛОЖЕНИЯ: Электронный книжный магазин");
        logger.info("============================================");

        try {
            // ===== 1. ИНИЦИАЛИЗАЦИЯ DI КОНТЕЙНЕРА =====
            logger.info("Инициализация DI контейнера");
            DIContainer di = DIContainer.getInstance();
            di.scan("bookstore.config", "bookstore.service", "bookstore.controller", "bookstore.view");
            logger.info("DI контейнер инициализирован");

            // ===== 2. ПОЛУЧАЕМ СЕРВИС =====
            logger.info("Получение сервиса из DI контейнера");
            BookStoreService service = di.getComponent(BookStoreService.class);

            if (service == null) {
                logger.warn("Сервис не найден в DI, создаем вручную");
                List<Book> initialBooks = getInitialBooks();
                service = new BookStoreService(initialBooks);
                di.registerInstance(BookStoreService.class, service);
                logger.info("Сервис создан вручную с {} начальными книгами", initialBooks.size());
            } else {
                logger.info("Сервис получен из DI контейнера");
            }

            // Загружаем состояние
            logger.info("Загрузка состояния");
            service.loadState();

            // Сохранение при выходе
            final BookStoreService finalService = service;
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Сохранение состояния перед выходом...");
                finalService.saveState();
                logger.info("Состояние сохранено");
            }));
            logger.info("Shutdown hook зарегистрирован");

            // ===== 3. ПОЛУЧАЕМ КОНТРОЛЛЕР =====
            logger.info("Получение контроллера из DI контейнера");
            BookStoreController controller = di.getComponent(BookStoreController.class);

            if (controller == null) {
                logger.warn("Контроллер не найден в DI, создаем вручную");
                controller = new BookStoreController(service);
                di.registerInstance(BookStoreController.class, controller);
                logger.info("Контроллер создан вручную");
            } else {
                logger.info("Контроллер получен из DI контейнера");
            }

            // ===== 4. ЗАПУСКАЕМ =====
            logger.info("Создание и запуск меню");
            MenuBuilder menuBuilder = new MenuBuilder(controller);
            MenuController menuController = new MenuController(menuBuilder);
            menuController.run();

            // Сохраняем состояние
            logger.info("Сохранение состояния после завершения работы");
            finalService.saveState();
            logger.info("Состояние сохранено");

            logger.info("============================================");
            logger.info("ПРИЛОЖЕНИЕ УСПЕШНО ЗАВЕРШИЛО РАБОТУ");
            logger.info("============================================");

        } catch (Exception e) {
            logger.error("КРИТИЧЕСКАЯ ОШИБКА при запуске приложения", e);
            System.err.println("Ошибка при запуске приложения: " + e.getMessage());
            System.exit(1);
        }
    }

    private static List<Book> getInitialBooks() {
        logger.debug("Создание начального списка книг");
        List<Book> books = Arrays.asList(
                new Book("война и мир", "лев толстой", "978-5-17-118636-8",
                        BookStatus.IN_STOCK, 1500.0, LocalDate.of(1869, 1, 1),
                        "эпический роман", "роман", "эксмо", 1300),
                new Book("преступление и наказание", "федор достоевский", "978-5-04-105222-3",
                        BookStatus.OUT_OF_STOCK, 1200.0, LocalDate.of(1866, 1, 1),
                        "психологический роман", "роман", "азбука", 700),
                new Book("мастер и маргарита", "михаил булгаков", "978-5-17-090381-2",
                        BookStatus.IN_STOCK, 1350.0, LocalDate.of(1967, 1, 1),
                        "философский роман", "роман", "аст", 480),
                new Book("1984", "джордж оруэлл", "978-5-17-102235-8",
                        BookStatus.OUT_OF_STOCK, 1100.0, LocalDate.of(1949, 6, 8),
                        "антиутопия", "фантастика", "эксмо", 320),
                new Book("анна каренина", "лев толстой", "978-5-04-103456-4",
                        BookStatus.IN_STOCK, 1400.0, LocalDate.of(1877, 1, 1),
                        "трагическая история любви", "роман", "эксмо", 900)
        );
        logger.debug("Создано {} начальных книг", books.size());
        return books;
    }
}