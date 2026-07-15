package controller;


import controller.interfaces.IMenuController;
import view.Navigator;
import view.interfaces.IBuilder;
import view.interfaces.IMenu;

public class MenuController implements IMenuController {
    private final IBuilder builder;
    private final Navigator navigator;

    public MenuController(IBuilder builder) {
        this.builder = builder;
        // навигатор с корневым меню
        IMenu rootMenu = builder.getRootMenu();
        this.navigator = new Navigator(rootMenu);
    }

    @Override
    public void run() {
        //  приветствие
        System.out.println("  добро пожаловать в электронный книжный магазин");
        System.out.println();

        //  навигация по меню
        navigator.navigate();

        // Прощание
        System.out.println();
        System.out.println("  спасибо за использование программы");
    }

    // Дополнительные методы для управления меню

    public void restart() {
        // Перезапуск навигации
        IMenu rootMenu = builder.getRootMenu();
        navigator.setCurrentMenu(rootMenu);
        navigator.restart();
        navigator.navigate();
    }

    public void goToMainMenu() {
        // Переход в главное меню
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