public class InstallBodyStep implements ILineStep {
    @Override
    public IProductPart buildProductPart() {
        System.out.println("Сборка кузова");
        CarBody body = new CarBody("Красный", "Седанчик");
        System.out.println("Кузов готов: " + body.getPartName());
        return body;
    }
}