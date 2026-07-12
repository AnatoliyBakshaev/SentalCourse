//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        Warehouse warehouse = new Warehouse();


        Product laptop = new Electronics("Ноутбук", 2.5, "Apple", "MacBook Pro");
        Product smartphone = new Electronics("Смартфон", 0.2, "Samsung", "Galaxy S23");
        Product tv = new Electronics("Телевизор", 8.0, "Sony", "Bravia XR");
        Product headphones = new Electronics("Наушники", 0.3, "Sony", "WH-1000XM5");

        Product apple = new Food("Яблоко", 0.15, "2026-08-01", true);
        Product rice = new Food("Рис", 1.0, "2027-12-31", false);
        Product milk = new Food("Молоко", 1.0, "2026-07-20", true);
        Product bread = new Food("Хлеб", 0.5, "2026-07-15", true);
        Product pasta = new Food("Макароны", 0.8, "2028-01-01", false);

        System.out.println("Заполняем склад до предела (максимум 50 кг)\n");


        warehouse.addProduct(laptop, 5);        // 5 * 2.5 = 12.5 кг
        warehouse.addProduct(smartphone, 20);   // 20 * 0.2 = 4.0 кг
        warehouse.addProduct(tv, 2);            // 2 * 8.0 = 16.0 кг
        warehouse.addProduct(headphones, 10);   // 10 * 0.3 = 3.0 кг

        warehouse.addProduct(apple, 30);        // 30 * 0.15 = 4.5 кг
        warehouse.addProduct(rice, 5);          // 5 * 1.0 = 5.0 кг
        warehouse.addProduct(milk, 3);          // 3 * 1.0 = 3.0 кг
        warehouse.addProduct(bread, 2);         // 2 * 0.5 = 1.0 кг
        // warehouse.addProduct(pasta, 1);


        warehouse.printWarehouseStatus();

        warehouse.printAllProducts();

        System.out.printf("Общий вес всех товаров на складе: %.2f кг\n",
                warehouse.getTotalWeight());

        // Подсчёт отдельно по категориям
        double electronicsWeight = 0;
        double foodWeight = 0;

        for (Product product : warehouse.getProducts()) {
            if (product instanceof Electronics) {
                electronicsWeight += product.getTotalWeight();
            } else if (product instanceof Food) {
                foodWeight += product.getTotalWeight();
            }
        }

        System.out.printf("\n Вес по категориям:\n");
        System.out.printf("Электроника: %.2f кг\n", electronicsWeight);
        System.out.printf("Продукты питания: %.2f кг\n", foodWeight);
    }
}