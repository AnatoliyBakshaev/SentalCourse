
public class Main {
    public static void main(String[] args) {
        System.out.println("тест");

        // Создаем сборочную линию
        IAssemblyLine assemblyLine = new CarAssemblyLine();

        // Создаем заготовку автомобиля (пустой продукт)
        IProduct car = new Car();

        // Запускаем сборку
        IProduct assembledCar = assemblyLine.assembleProduct(car);

        System.out.println("Тест завершен!");
        System.out.println("Собранный автомобиль: " + assembledCar.getClass().getSimpleName());
    }
}