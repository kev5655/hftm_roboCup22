package gui.data;

public class Order {
    private final String id;
    private final String type;
    private final String baseColor;
    private final String capColor;
    private final String ringColor1;
    private final String ringColor2;
    private final String ringColor3;

    public Order(String id, String type, String baseColor, String capColor, String ringColor1, String ringColor2, String ringColor3) {
        this.id = id;
        this.type = type;
        this.baseColor = baseColor;
        this.capColor = capColor;
        this.ringColor1 = ringColor1;
        this.ringColor2 = ringColor2;
        this.ringColor3 = ringColor3;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getBaseColor() {
        return baseColor;
    }

    public String getCapColor() {
        return capColor;
    }

    public String getRingColor1() {
        return ringColor1;
    }

    public String getRingColor2() {
        return ringColor2;
    }

    public String getRingColor3() {
        return ringColor3;
    }

    public String getIdId() {
        String idId = "Id: ";
        return idId;
    }

    public String getTypeType() {
        String typeType = "Type: ";
        return typeType;
    }

    public String getBaseColorBaseColor() {
        String baseColorBaseColor = "Base Color: ";
        return baseColorBaseColor;
    }

    public String getCapColorCapColor() {
        String capColorCapColor = "Cap Color: ";
        return capColorCapColor;
    }

    public String getRingColor1RingColor1() {
        String ringColor1RingColor1 = "Ring Color 1: ";
        return ringColor1RingColor1;
    }

    public String getRingColor2RingColor2() {
        String ringColor2RingColor2 = "Ring Color 2: ";
        return ringColor2RingColor2;
    }

    public String getRingColor3RingColor3() {
        String ringColor3RingColor3 = "Ring Color 3: ";
        return ringColor3RingColor3;
    }
}
