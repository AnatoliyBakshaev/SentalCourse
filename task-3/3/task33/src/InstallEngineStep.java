public class InstallEngineStep implements ILineStep {
    @Override
    public IProductPart buildProductPart() {
        System.out.println("Сборка двигателя...");
        Engine engine = new Engine(2.5, 200);
        System.out.println("Двигатель готов: " + engine.getPartName());
        return engine;
    }
}