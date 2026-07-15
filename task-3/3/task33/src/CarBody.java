public class CarBody implements IProductPart {
    private String color;
    private String type;

    public CarBody(String color, String type) {
        this.color = color;
        this.type = type;
    }

    @Override
    public String getPartName() {
        return "Кузов (цвет: " + color + ", тип: " + type + ")";
    }
}

