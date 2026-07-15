public class CarAssemblyLine implements IAssemblyLine {
    private ILineStep step1;
    private ILineStep step2;
    private ILineStep step3;

    public CarAssemblyLine() {
        this.step1 = new InstallBodyStep();
        this.step2 = new InstallChassisStep();
        this.step3 = new InstallEngineStep();
    }

    @Override
    public IProduct assembleProduct(IProduct product) {
        System.out.println("НАЧАЛО СБОРКИ АВТОМОБИЛЯ");
        System.out.println("=========================================");

        if (!(product instanceof Car)) {
            System.out.println("Ошибка: передан неподходящий продукт!");
            return null;
        }

        Car car = (Car) product;

        System.out.println("Шаг 1: Установка кузова");
        IProductPart part1 = step1.buildProductPart();
        car.installFirstPart(part1);

        System.out.println("Шаг 2: Установка шасси");
        IProductPart part2 = step2.buildProductPart();
        car.installSecondPart(part2);

        System.out.println("Шаг 3: Установка двигателя");
        IProductPart part3 = step3.buildProductPart();
        car.installThirdPart(part3);

        System.out.println("СБОРКА ЗАВЕРШЕНА!");

        car.showProduct();

        return car;
    }
}