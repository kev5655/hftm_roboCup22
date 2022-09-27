package gui.node;

import gui.communication.GuiMqttMsgHandler;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.Group;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.LinkedList;
import java.util.List;

public class ShowRingStation implements InvalidationListener {
    final Group group;
    VBox vBox;
    HBox hBox;

    private final List<String []> ringStation = new LinkedList<>();

    public ShowRingStation(Group group) {
        this.group = group;
        GuiMqttMsgHandler.getInstance().addListener(this);
    }

    private void draw(List<String[]> values) {
        Color color;
        this.hBox = new HBox(20);
        for (String[] s : values) {
            String nameStation = s[0].replace("\"", "");
            color = switch (nameStation) {
                case "C-RS1" -> Color.rgb(58, 134, 255, 1);
                case "C-RS2" -> Color.rgb(190, 190, 190, 1);
                default -> Color.rgb(0, 0, 0, 1);
            };

            this.vBox = new VBox(10);

            Text name = new Text(s[0].replace("\"", ""));
            Text color1 = new Text(s[1].replace("\"", ""));
            Text prize1 = new Text(s[2].replace("\"", ""));
            Text color2 = new Text(s[3].replace("\"", ""));
            Text prize2 = new Text(s[4].replace("\"", ""));
            Text payed = new Text(s[5].replace("\"", ""));

            vBox.getChildren().addAll(name, color1, prize1, color2, prize2, payed);
            hBox.getChildren().add(vBox);
        }
        group.getChildren().add(hBox);
    }

    @Override
    public void invalidated(Observable observable) {
        Platform.runLater(() -> {
            List<String []> stationsEntry = ((GuiMqttMsgHandler)observable).getRingStations();

            group.getChildren().clear();

            for (String[] strings : stationsEntry) {
                ringStation.add(new String[]{strings[0],
                        strings[1],
                        strings[2],
                        strings[3],
                        strings[4],
                        strings[5]});
                draw(ringStation);
            }
        });
    }
}
