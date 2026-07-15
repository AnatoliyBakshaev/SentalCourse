public class InstallChassisStep implements ILineStep {
    @Override
    public IProductPart buildProductPart() {
        System.out.println("Сборка шасси...");
        Chassis chassis = new Chassis("Шоссе", 18);
        System.out.println("Шасси готово: " + chassis.getPartName());
        return chassis;
    }
}