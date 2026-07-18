package bookstore.view.interfaces;

import bookstore.view.MenuItem;
import java.util.List;

public interface IMenu {
    String getName();
    void setName(String name);
    List<MenuItem> getMenuItems();
    void setMenuItems(List<MenuItem> menuItems);
    void addMenuItem(MenuItem menuItem);
    MenuItem getMenuItem(int index);
}
