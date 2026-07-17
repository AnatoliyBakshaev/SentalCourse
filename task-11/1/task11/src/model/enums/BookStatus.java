package model.enums;

public enum BookStatus {
    IN_STOCK("В наличии"),
    OUT_OF_STOCK("Отсутствует"),
    WRITTEN_OFF("Списана");

    private final String description;

    BookStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
