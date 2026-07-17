abstract class Product {
    protected String name;
    protected double weight;
    protected int quantity;

    public Product(String name, double weight) {
        this.name = name;
        this.weight = weight;
        this.quantity = 0;
    }

    public String getName() {
        return name;
    }

    public double getWeight() {
        return weight;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void addToStock(int count) {
        this.quantity += count;
    }

    public abstract String getInfo();

    public double getTotalWeight() {
        return weight * quantity;
    }
}