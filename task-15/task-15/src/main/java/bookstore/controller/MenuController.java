package bookstore.controller;

import bookstore.controller.interfaces.IMenuController;
import bookstore.view.Navigator;
import bookstore.view.interfaces.IBuilder;
import bookstore.view.interfaces.IMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MenuController implements IMenuController {
    private final IBuilder builder;
    private final Navigator navigator;

    @Autowired
    public MenuController(IBuilder builder) {
        this.builder = builder;
        this.navigator = new Navigator(builder.getRootMenu());
        System.out.println("MenuController создан Spring-контейнером");
    }

    @Override
    public void run() {
        System.out.println("  добро пожаловать в электронный книжный магазин");
        System.out.println();
        navigator.navigate();
        System.out.println();
        System.out.println("  спасибо за использование программы");
    }

    public void restart() {
        IMenu rootMenu = builder.getRootMenu();
        navigator.setCurrentMenu(rootMenu);
        navigator.restart();
        navigator.navigate();
    }

    public void goToMainMenu() {
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