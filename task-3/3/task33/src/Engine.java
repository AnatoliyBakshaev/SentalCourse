public class Engine implements IProductPart {
    private double volume;
    private int power;

    public Engine(double volume, int power) {
        this.volume = volume;
        this.power = power;
    }

    @Override
    public String getPartName() {
        return "Двигатель (объем: " + volume + "л, мощность: " + power + "л.с.)";
    }
}