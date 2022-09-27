package gui.data;

public class Station {

    String name;
    String type;
    String state;
    String teamColor;
    String rotation;
    String capColors;
    final String nameName = "Name: ";
    final String typeType= "Type: ";
    final String stateState = "State: ";
    final String teamColorTeamColor = "Team Color: ";
    final String rotationRotation ="Rotation: ";
    final String capColorsCapColors ="Cap Colors: ";

    public Station(String name, String type, String state, String teamColor, String rotation, String capColors){
        this.name = name;
        this.type = type;
        this.state = state;
        this.teamColor = teamColor;
        this.rotation = rotation;
        this.capColors = capColors;
    }

    public Station(String name, String type, String state, String teamColor, String rotation) {
        this.name = name;
        this.type = type;
        this.state = state;
        this.teamColor = teamColor;
        this.rotation = rotation;
    }

    public Station(int i) {
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getState() {
        return state;
    }

    public String getTeamColor() {
        return teamColor;
    }

    public String getNameName() {
        return nameName;
    }

    public String getStateState() {
        return stateState;
    }

    public String getTypeType() {
        return typeType;
    }

    public String getTeamColorTeamColor() {
        return teamColorTeamColor;
    }

    public String getRotation() {
        return rotation;
    }

    public String getRotationRotation() {
        return rotationRotation;
    }

    public String getCapColors() {
        return capColors;
    }

    public String getCapColorsCapColors() {
        return capColorsCapColors;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setTeamColor(String teamColor) {
        this.teamColor = teamColor;
    }

    public void setRotation(String rotation) {
        this.rotation = rotation;
    }

    public void setCapColors(String capColors) {
        this.capColors = capColors;
    }
}
