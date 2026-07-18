package bookstore.view;

import bookstore.view.interfaces.IMenu;
import java.util.Scanner;

public class Navigator {
    private IMenu currentMenu;
    private final Scanner scanner;
    private boolean isRunning;

    public Navigator(IMenu rootMenu) {
        this.currentMenu = rootMenu;
        this.scanner = new Scanner(System.in);
        this.isRunning = true;
    }

    public IMenu getCurrentMenu() {
        return currentMenu;
    }

    public void setCurrentMenu(IMenu menu) {
        this.currentMenu = menu;
    }

    public void navigate() {
        while (isRunning && currentMenu != null) {
            // Отображаем текущее меню
            if (currentMenu instanceof Menu) {
                ((Menu) currentMenu).display();
            }

            // Получаем выбор пользователя
            int choice = getChoice();

            if (choice == 0) {
                System.out.println("выход из программы...");
                isRunning = false;
                break;
            }

            // Проверяем валидность выбора
            if (choice < 0 || choice > currentMenu.getMenuItems().size()) {
                System.out.println("неверный выбор. попробуйте снова.");
                continue;
            }

            // Получаем выбранный пункт меню
            MenuItem selectedItem = currentMenu.getMenuItem(choice - 1);

            if (selectedItem != null) {
                // Выполняем действие
                selectedItem.execute();

                // Переходим в следующее меню, если указано
                if (selectedItem.getNextMenu() != null) {
                    currentMenu = selectedItem.getNextMenu();
                }
            } else {
                System.out.println("ошибка: пункт меню не найден");
            }
        }
    }

    private int getChoice() {
        try {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return -1;
            }
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("ошибка: введите число");
            return -1;
        }
    }

    public void doAction() {
        // Выполняет действие текущего меню
        if (currentMenu != null && !currentMenu.getMenuItems().isEmpty()) {
            MenuItem firstItem = currentMenu.getMenuItem(0);
            if (firstItem != null) {
                firstItem.execute();
            }
        }
    }

    public void stop() {
        this.isRunning = false;
    }

    public void restart() {
        this.isRunning = true;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void goBack() {
        // Возврат к предыдущему меню
        if (currentMenu instanceof Menu) {
            // Здесь можно реализовать навигацию назад
            System.out.println("возврат в предыдущее меню...");
        }
    }

    public void refresh() {
        // Обновление текущего меню
        if (currentMenu instanceof Menu) {
            ((Menu) currentMenu).display();
        }
    }

    public void clearScreen() {
        // Очистка консоли (работает не во всех терминалах)
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // Игнорируем ошибки очистки
        }
    }

    @Override
    public String toString() {
        return "Navigator{currentMenu=" + (currentMenu != null ? currentMenu.getName() : "null") + "}";
    }
}