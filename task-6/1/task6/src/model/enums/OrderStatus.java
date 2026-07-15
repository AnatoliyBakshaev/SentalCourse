package model.enums;

public enum OrderStatus {
    NEW("Новый"),
    COMPLETED("Выполнен"),
    CANCELLED("Отменен");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
