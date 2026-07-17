package view;

import controller.BookStoreController;
import view.interfaces.IBuilder;
import view.interfaces.IMenu;

public class MenuBuilder implements IBuilder {
    private IMenu rootMenu;
    private final BookStoreController controller;

    public MenuBuilder(BookStoreController controller) {
        this.controller = controller;
    }

    @Override
    public void buildMenu() {
        // Сначала создаем все подменю БЕЗ ссылки на rootMenu
        IMenu operationsMenu = createOperationsMenuWithoutBack();
        IMenu viewMenu = createViewMenuWithoutBack();

        // Создаем главное меню
        IMenu mainMenu = new Menu("главное меню");

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

        // Теперь добавляем пункт "назад" в подменю, используя уже созданный rootMenu
        addBackToSubMenu(operationsMenu);
        addBackToSubMenu(viewMenu);
    }

    private IMenu createOperationsMenuWithoutBack() {
        IMenu menu = new Menu("операции");

        menu.addMenuItem(new MenuItem(
                "создать заказ",
                () -> controller.createOrder()
        ));

        menu.addMenuItem(new MenuItem(
                "отменить заказ",
                () -> controller.cancelOrder()
        ));

        menu.addMenuItem(new MenuItem(
                "изменить статус заказа",
                () -> controller.changeOrderStatus()
        ));

        menu.addMenuItem(new MenuItem(
                "добавить книгу на склад",
                () -> controller.addBookToStock()
        ));

        menu.addMenuItem(new MenuItem(
                "оставить запрос на книгу",
                () -> controller.leaveRequest()
        ));

        menu.addMenuItem(new MenuItem(
                "списать книгу",
                () -> controller.writeOffBook()
        ));

        return menu;
    }

    private IMenu createViewMenuWithoutBack() {
        IMenu menu = new Menu("просмотр");

        menu.addMenuItem(new MenuItem(
                "список книг",
                () -> controller.viewBooks()
        ));

        menu.addMenuItem(new MenuItem(
                "список заказов",
                () -> controller.viewOrders()
        ));

        menu.addMenuItem(new MenuItem(
                "список запросов",
                () -> controller.viewRequests()
        ));

        menu.addMenuItem(new MenuItem(
                "выполненные заказы за период",
                () -> controller.viewCompletedOrders()
        ));

        menu.addMenuItem(new MenuItem(
                "выручка за период",
                () -> controller.viewRevenue()
        ));

        menu.addMenuItem(new MenuItem(
                "количество выполненных заказов",
                () -> controller.viewCompletedOrdersCount()
        ));

        menu.addMenuItem(new MenuItem(
                "залежавшиеся книги",
                () -> controller.viewOldBooks()
        ));

        menu.addMenuItem(new MenuItem(
                "детали заказа",
                () -> controller.viewOrderDetails()
        ));

        menu.addMenuItem(new MenuItem(
                "описание книги",
                () -> controller.viewBookDescription()
        ));

        return menu;
    }

    private void addBackToSubMenu(IMenu subMenu) {
        // Добавляем пункт "назад" с ссылкой на rootMenu
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