package view;

import controller.BookStoreController;
import view.interfaces.IBuilder;
import view.interfaces.IMenu;

import java.util.Arrays;
import java.util.List;

public class MenuBuilder implements IBuilder {
    private IMenu rootMenu;
    private final BookStoreController controller;

    public MenuBuilder(BookStoreController controller) {
        this.controller = controller;
    }

    @Override
    public void buildMenu() {
        IMenu mainMenu = new Menu("главное меню");
        IMenu operationsMenu = createOperationsMenu();
        IMenu viewMenu = createViewMenu();
        IMenu importExportMenu = createImportExportMenu();

        mainMenu.addMenuItem(new MenuItem(
                "операции с книгами и заказами",
                () -> System.out.println("переход в меню операций..."),
                operationsMenu
        ));

        mainMenu.addMenuItem(new MenuItem(
                "просмотр информации",
                () -> System.out.println("переход в меню просмотра..."),
                viewMenu
        ));

        mainMenu.addMenuItem(new MenuItem(
                "импорт / экспорт данных",
                () -> System.out.println("переход в меню импорта/экспорта..."),
                importExportMenu
        ));

        mainMenu.addMenuItem(new MenuItem(
                "статистика",
                this::showStatistics
        ));

        mainMenu.addMenuItem(new MenuItem(
                "выход",
                () -> {
                    System.out.println("до свидания!");
                    System.exit(0);
                }
        ));

        this.rootMenu = mainMenu;
        addBackToSubMenu(operationsMenu);
        addBackToSubMenu(viewMenu);
        addBackToSubMenu(importExportMenu);
    }

    private IMenu createOperationsMenu() {
        IMenu menu = new Menu("операции");

        List<MenuItem> items = Arrays.asList(
                new MenuItem("создать заказ", () -> controller.createOrder()),
                new MenuItem("отменить заказ", () -> controller.cancelOrder()),
                new MenuItem("изменить статус заказа", () -> controller.changeOrderStatus()),
                new MenuItem("добавить книгу на склад", () -> controller.addBookToStock()),
                new MenuItem("оставить запрос на книгу", () -> controller.leaveRequest()),
                new MenuItem("списать книгу", () -> controller.writeOffBook())
        );

        items.forEach(menu::addMenuItem);
        return menu;
    }

    private IMenu createViewMenu() {
        IMenu menu = new Menu("просмотр");

        List<MenuItem> items = Arrays.asList(
                new MenuItem("список книг", () -> controller.viewBooks()),
                new MenuItem("список заказов", () -> controller.viewOrders()),
                new MenuItem("список запросов", () -> controller.viewRequests()),
                new MenuItem("выполненные заказы за период", () -> controller.viewCompletedOrders()),
                new MenuItem("выручка за период", () -> controller.viewRevenue()),
                new MenuItem("количество выполненных заказов", () -> controller.viewCompletedOrdersCount()),
                new MenuItem("залежавшиеся книги", () -> controller.viewOldBooks()),
                new MenuItem("детали заказа", () -> controller.viewOrderDetails()),
                new MenuItem("описание книги", () -> controller.viewBookDescription())
        );

        items.forEach(menu::addMenuItem);
        return menu;
    }

    private IMenu createImportExportMenu() {
        IMenu menu = new Menu("импорт / экспорт");

        // Импорт
        menu.addMenuItem(new MenuItem("--- импорт ---", () -> {}));
        menu.addMenuItem(new MenuItem("импорт книг", () -> controller.importBooks()));
        menu.addMenuItem(new MenuItem("импорт заказов", () -> controller.importOrders()));
        menu.addMenuItem(new MenuItem("импорт запросов", () -> controller.importRequests()));

        // Экспорт
        menu.addMenuItem(new MenuItem("--- экспорт ---", () -> {}));
        menu.addMenuItem(new MenuItem("экспорт книг", () -> controller.exportBooks()));
        menu.addMenuItem(new MenuItem("экспорт заказов", () -> controller.exportOrders()));
        menu.addMenuItem(new MenuItem("экспорт запросов", () -> controller.exportRequests()));

        return menu;
    }

    private void addBackToSubMenu(IMenu subMenu) {
        subMenu.addMenuItem(new MenuItem(
                "назад в главное меню",
                () -> System.out.println("возврат в главное меню..."),
                this.rootMenu
        ));
    }

    private void showStatistics() {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("  статистика");
        System.out.println("==================================================");
        System.out.println("  выберите период для просмотра статистики:");
        System.out.println("  1. выручка за период");
        System.out.println("  2. количество выполненных заказов");
        System.out.println("==================================================");
        System.out.print("выбор: ");

        try {
            java.util.Scanner scanner = new java.util.Scanner(System.in);
            int choice = Integer.parseInt(scanner.nextLine().trim());

            switch (choice) {
                case 1:
                    controller.viewRevenue();
                    break;
                case 2:
                    controller.viewCompletedOrdersCount();
                    break;
                default:
                    System.out.println("неверный выбор");
            }
        } catch (Exception e) {
            System.out.println("ошибка: " + e.getMessage());
        }
    }

    @Override
    public IMenu getRootMenu() {
        if (rootMenu == null) {
            buildMenu();
        }
        return rootMenu;
    }
}