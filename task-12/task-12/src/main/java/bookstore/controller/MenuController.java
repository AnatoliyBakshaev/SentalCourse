package bookstore.controller;

import bookstore.controller.interfaces.IMenuController;
import bookstore.view.Navigator;
import bookstore.view.interfaces.IBuilder;
import bookstore.view.interfaces.IMenu;
import bookstore.utils.LoggerUtil;
import org.apache.logging.log4j.Logger;

public class MenuController implements IMenuController {
    private static final Logger logger = LoggerUtil.getLogger(MenuController.class);

    private final IBuilder builder;
    private final Navigator navigator;

    public MenuController(IBuilder builder) {
        this.builder = builder;
        this.navigator = new Navigator(builder.getRootMenu());
        logger.info("MenuController инициализирован");
    }

    @Override
    public void run() {
        logger.info("============================================");
        logger.info("ЗАПУСК ПРИЛОЖЕНИЯ: Электронный книжный магазин");
        logger.info("============================================");

        System.out.println("  добро пожаловать в электронный книжный магазин");
        System.out.println();

        try {
            navigator.navigate();
            logger.info("Навигация по меню завершена успешно");
        } catch (Exception e) {
            logger.error("Критическая ошибка при навигации по меню", e);
            throw e;
        }

        System.out.println();
        System.out.println("  спасибо за использование программы");
        logger.info("Приложение завершило работу");
    }

    // Дополнительные методы для управления меню

    public void restart() {
        logger.info("Перезапуск навигации");
        IMenu rootMenu = builder.getRootMenu();
        navigator.setCurrentMenu(rootMenu);
        navigator.restart();
        navigator.navigate();
        logger.info("Навигация перезапущена");
    }

    public void goToMainMenu() {
        logger.info("Переход в главное меню");
        IMenu rootMenu = builder.getRootMenu();
        navigator.setCurrentMenu(rootMenu);
    }

    public Navigator getNavigator() {
        return navigator;
    }

    public IBuilder getBuilder() {
        return builder;
    }
}