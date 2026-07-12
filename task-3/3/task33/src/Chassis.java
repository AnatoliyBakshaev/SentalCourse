public class Chassis implements IProductPart {
    private String suspensionType;
    private int wheelSize;

    public Chassis(String suspensionType, int wheelSize) {
        this.suspensionType = suspensionType;
        this.wheelSize = wheelSize;
    }

    @Override
    public String getPartName() {
        return "Шасси (подвеска: " + suspensionType + ", колеса: " + wheelSize + "\")";
    }
}