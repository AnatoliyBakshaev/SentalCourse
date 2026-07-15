import java.util.ArrayList;
import java.util.List;

public class Warehouse {
    private static final int MAX_CAPACITY = 50; //кг вместимость
    private List<Product> products;
    private double currentWeight;

    public Warehouse() {
        this.products = new ArrayList<>();
        this.currentWeight = 0;
    }


    public boolean addProduct(Product product, int quantity) {
        double totalWeight = product.getWeight() * quantity;


        if (currentWeight + totalWeight > MAX_CAPACITY) {
            System.out.println("Ошибка: Превышение вместимости склада!");
            System.out.printf("Свободно: %.2f кг, Требуется: %.2f кг\n",
                    MAX_CAPACITY - currentWeight, totalWeight);
            return false;
        }

        product.addToStock(quantity);
        products.add(product);
        currentWeight += totalWeight;

        System.out.printf("Добавлено: %d шт. товара \"%s\" (%.2f кг)\n",
                quantity, product.getName(), totalWeight);
        return true;
    }

    public double getTotalWeight() {
        return currentWeight;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void printAllProducts() {
        if (products.isEmpty()) {
            System.out.println("Склад пуст.");
            return;
        }

        System.out.println("\nсодержимое");
        for (Product product : products) {
            System.out.println(product.getInfo());
        }
    }

    public void printWarehouseStatus() {
        System.out.printf("Занято: %.2f кг из %.0f кг\n", currentWeight, (double)MAX_CAPACITY);
        System.out.printf("Свободно: %.2f кг\n", MAX_CAPACITY - currentWeight);
        System.out.printf("Загруженность: %.1f%%\n", (currentWeight / MAX_CAPACITY) * 100);
        System.out.printf("Количество видов товаров: %d\n", products.size());
    }
}

