package gui.node;

import gui.communication.GuiMqttMsgHandler;
import gui.data.Station;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.Group;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.*;

public class ShowMpsState implements InvalidationListener {

    final Group group;
    VBox vBox;
    HBox hBox;

    private final List<Station> stations = new LinkedList<>();

    public ShowMpsState(Group group){
        this.group = group;
        GuiMqttMsgHandler.getInstance().addListener(this);
    }

    public void draw(List<Station> stations){
        Color color;
        this.hBox = new HBox(20);

        for (Station s : stations) {
            String nameStation = s.getName().replace("\"", "");
            color = switch (nameStation) {
                case "C-CS1", "M-CS1" -> Color.rgb(255, 0, 110, 1);
                case "C-CS2", "M-CS2" -> Color.rgb(131, 56, 236, 1);
                case "C-RS1", "M-RS1" -> Color.rgb(58, 134, 255, 1);
                case "C-RS2", "M-RS2" -> Color.rgb(190, 190, 190, 1);
                case "C-BS", "M-BS" -> Color.rgb(255, 102, 255, 1);
                case "C-DS", "M-DS" -> Color.rgb(255, 153, 51, 1);
                case "C-SS", "M-SS" -> Color.rgb(102, 102, 153, 1);
                default -> Color.rgb(0, 0, 0, 1);
            };

            this.vBox = new VBox(10);

            Text name = new Text(s.getNameName() + " " +
                    s.getName().replace("\"", ""));
            name.setFill(color);
            Text type = new Text(s.getTypeType() + " " +
                    s.getType().replace("\"", ""));
            Text state = new Text(s.getStateState() + " " +
                    s.getState().replace("\"", ""));
            Text teamColor = new Text(s.getTeamColorTeamColor() + " " +
                    s.getTeamColor().replace("\"", ""));
            Text rotation = new Text(s.getRotationRotation() + " " +
                    s.getRotation().replace("\"",""));

            vBox.getChildren().addAll(name, type, state, teamColor, rotation);
            hBox.getChildren().add(vBox);
        }
        group.getChildren().add(hBox);
    }

    @Override
    public void invalidated(Observable observable) {
        Platform.runLater(() -> {
            List<String []> stationsEntry = ((GuiMqttMsgHandler)observable).getStations();

            group.getChildren().clear();
            stations.clear();

            for (String[] strings : stationsEntry) {
                    stations.add(new Station(strings[0],
                            strings[1],
                            strings[2],
                            strings[3],
                            strings[4]));
                draw(stations);
            }
        });
    }
}
