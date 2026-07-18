package bookstore.view;

import bookstore.view.interfaces.IAction;
import bookstore.view.interfaces.IMenu;

public class MenuItem {
    private String title;
    private IAction action;
    private IMenu nextMenu;

    // Конструктор с действием и следующим меню
    public MenuItem(String title, IAction action, IMenu nextMenu) {
        this.title = title;
        this.action = action;
        this.nextMenu = nextMenu;
    }

    // Конструктор только с действием (без следующего меню)
    public MenuItem(String title, IAction action) {
        this(title, action, null);
    }

    // Конструктор только с названием (без действия)
    public MenuItem(String title) {
        this(title, null, null);
    }

    // Геттеры и сеттеры
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public IAction getAction() {
        return action;
    }

    public void setAction(IAction action) {
        this.action = action;
    }

    public IMenu getNextMenu() {
        return nextMenu;
    }

    public void setNextMenu(IMenu nextMenu) {
        this.nextMenu = nextMenu;
    }

    // Выполнение действия
    public void execute() {
        if (action != null) {
            action.execute();
        }
    }

    // Проверка, есть ли действие
    public boolean hasAction() {
        return action != null;
    }

    // Проверка, есть ли следующее меню
    public boolean hasNextMenu() {
        return nextMenu != null;
    }

    @Override
    public String toString() {
        return title;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MenuItem menuItem = (MenuItem) obj;
        return title != null ? title.equals(menuItem.title) : menuItem.title == null;
    }

    @Override
    public int hashCode() {
        return title != null ? title.hashCode() : 0;
    }
}