package bookstore.view;

import bookstore.view.interfaces.IMenu;
import java.util.ArrayList;
import java.util.List;

public class Menu implements IMenu {
    private String name;
    private List<MenuItem> menuItems;

    public Menu(String name) {
        this.name = name;
        this.menuItems = new ArrayList<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    @Override
    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    @Override
    public void addMenuItem(MenuItem menuItem) {
        this.menuItems.add(menuItem);
    }

    @Override
    public MenuItem getMenuItem(int index) {
        if (index >= 0 && index < menuItems.size()) {
            return menuItems.get(index);
        }
        return null;
    }

    public void display() {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("  " + name);
        System.out.println("==================================================");

        for (int i = 0; i < menuItems.size(); i++) {
            System.out.printf("  %d. %s%n", i + 1, menuItems.get(i).getTitle());
        }

        System.out.println("  0. выход");
        System.out.println("==================================================");
        System.out.print("выберите опцию: ");
    }

    public int getSize() {
        return menuItems.size();
    }

    public boolean isEmpty() {
        return menuItems.isEmpty();
    }

    public void clear() {
        menuItems.clear();
    }

    @Override
    public String toString() {
        return "Menu{name='" + name + "', items=" + menuItems.size() + "}";
    }
}