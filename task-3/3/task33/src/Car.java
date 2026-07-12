public class Car implements IProduct {
    private IProductPart body;
    private IProductPart chassis;
    private IProductPart engine;

    @Override
    public void installFirstPart(IProductPart part) {
        this.body = part;
        System.out.println("Установлен кузов: " + part.getPartName());
    }

    @Override
    public void installSecondPart(IProductPart part) {
        this.chassis = part;
        System.out.println("Установлено шасси: " + part.getPartName());
    }

    @Override
    public void installThirdPart(IProductPart part) {
        this.engine = part;
        System.out.println("Установлен двигатель: " + part.getPartName());
    }

    @Override
    public void showProduct() {
        System.out.println("ГОТОВЫЙ АВТОМОБИЛЬ:");
        System.out.println(body.getPartName());
        System.out.println(chassis.getPartName());
        System.out.println(engine.getPartName());
        System.out.println("Автомобиль успешно собран и готов к эксплуатации!");
    }
}