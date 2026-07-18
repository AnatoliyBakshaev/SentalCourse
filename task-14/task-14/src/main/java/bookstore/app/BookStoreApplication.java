package bookstore.app;

import bookstore.config.AppConfig;
import bookstore.controller.MenuController;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class BookStoreApplication {
    public static void main(String[] args) {
        // Создаем Spring контекст
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        // Получаем MenuController из контекста
        MenuController menuController = context.getBean(MenuController.class);

        // Запускаем приложение
        menuController.run();

        // Закрываем контекст при завершении
        ((AnnotationConfigApplicationContext) context).close();
    }
}