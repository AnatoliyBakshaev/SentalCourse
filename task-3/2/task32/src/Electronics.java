class Electronics extends Product {
    private String brand;
    private String model;

    public Electronics(String name, double weight, String brand, String model) {
        super(name, weight);
        this.brand = brand;
        this.model = model;
    }

    @Override
    public String getInfo() {
        return String.format("Электроника: %s, Бренд: %s, Модель: %s, Вес: %.2f кг, Количество: %d",
                name, brand, model, weight, quantity);
    }
}
