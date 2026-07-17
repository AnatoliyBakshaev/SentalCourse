package view.enums;

public enum MenuOption {
    // Основные операции
    CREATE_ORDER(1, "Создать заказ"),
    CANCEL_ORDER(2, "Отменить заказ"),
    CHANGE_ORDER_STATUS(3, "Изменить статус заказа"),
    ADD_BOOK_TO_STOCK(4, "Добавить книгу на склад"),
    LEAVE_REQUEST(5, "Оставить запрос на книгу"),
    WRITE_OFF_BOOK(6, "Списать книгу со склада"),

    // Просмотр
    VIEW_BOOKS(7, "Просмотреть список книг"),
    VIEW_ORDERS(8, "Просмотреть список заказов"),
    VIEW_REQUESTS(9, "Просмотреть список запросов"),
    VIEW_COMPLETED_ORDERS(10, "Просмотреть выполненные заказы"),
    VIEW_REVENUE(11, "Просмотреть выручку"),
    VIEW_OLD_BOOKS(12, "Просмотреть залежавшиеся книги"),
    VIEW_ORDER_DETAILS(13, "Просмотреть детали заказа"),
    VIEW_BOOK_DESCRIPTION(14, "Просмотреть описание книги"),

    EXIT(0, "Выход");

    private final int code;
    private final String description;

    MenuOption(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() { return code; }
    public String getDescription() { return description; }

    public static MenuOption fromCode(int code) {
        for (MenuOption option : values()) {
            if (option.code == code) {
                return option;
            }
        }
        return null;
    }
}