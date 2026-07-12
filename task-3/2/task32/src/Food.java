class Food extends Product {
    private String expirationDate;
    private boolean isPerishable;

    public Food(String name, double weight, String expirationDate, boolean isPerishable) {
        super(name, weight);
        this.expirationDate = expirationDate;
        this.isPerishable = isPerishable;
    }

    @Override
    public String getInfo() {
        return String.format("Продукт питания: %s, Вес: %.2f кг, Срок годности: %s, Скоропортящийся: %s, Количество: %d",
                name, weight, expirationDate, isPerishable ? "Да" : "Нет", quantity);
    }
}