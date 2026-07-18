package bookstore.app;

import bookstore.config.AppConfig;
import bookstore.config.DatabaseConfig;
import bookstore.controller.MenuController;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class BookStoreApplication {
    public static void main(String[] args) {
        // Создаем Spring контекст
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        // Выполняем Flyway миграции
        DatabaseConfig dbConfig = context.getBean(DatabaseConfig.class);
        dbConfig.runMigrations();

        // Запускаем приложение
        MenuController menuController = context.getBean(MenuController.class);
        menuController.run();

        // Закрываем контекст
        ((AnnotationConfigApplicationContext) context).close();
    }
}