package gui.node;

import gui.communication.GuiMqttMsgHandler;
import gui.data.Order;
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

public class ShowOrder implements InvalidationListener {
    final Group group;
    VBox vBox;
    HBox hBox;

    private final List<Order> orders = new LinkedList<>();

    public ShowOrder(Group group){
        this.group = group;
        GuiMqttMsgHandler.getInstance().addListener(this);
    }

    public void draw(List<Order> orders){
        Color color;
        this.hBox = new HBox(20);

        for (Order o : orders) {
            this.vBox = new VBox(10);


            Text id = new Text(o.getIdId() + " " +
                    o.getId().replace("\"", ""));
            Text type = new Text(o.getTypeType() + " " +
                    o.getType().replace("\"", ""));
            Text baseColor = new Text(o.getBaseColorBaseColor() + " " +
                    o.getBaseColor().replace("\"", ""));
            Text ringColor1 = new Text(o.getRingColor1RingColor1() + " " +
                    o.getRingColor1().replace("\"", ""));
            Text ringColor2 = new Text(o.getRingColor2RingColor2() + " " +
                    o.getRingColor2().replace("\"", ""));
            Text ringColor3 = new Text(o.getRingColor3RingColor3() + " " +
                    o.getRingColor3().replace("\"", ""));
            Text capColor = new Text(o.getCapColorCapColor() + " " +
                    o.getCapColor().replace("\"", ""));

            vBox.getChildren().addAll(id, type, baseColor, ringColor1, ringColor2, ringColor3, capColor);
            hBox.getChildren().add(vBox);
        }
        group.getChildren().add(hBox);
    }

    @Override
    public void invalidated(Observable observable) {
        Platform.runLater(() -> {
            List<String []> odersEntry = ((GuiMqttMsgHandler)observable).getStations();

            group.getChildren().clear();
            orders.clear();

            for (String[] strings : odersEntry) {
                orders.add(new Order(
                        strings[0],
                        strings[1],
                        strings[2],
                        strings[3],
                        strings[4],
                        strings[5],
                        strings[6]));
                draw(orders);
            }
        });
    }
}
