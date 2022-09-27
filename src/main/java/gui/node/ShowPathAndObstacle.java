package gui.node;

import gui.communication.GuiMqttMsgHandler;
import gui.data.GuiData;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ShowPathAndObstacle implements InvalidationListener {

    public static Group group;
    static final List<Rectangle> rects = new LinkedList<>();
    private final List<Rectangle> lastRect = new LinkedList<>();

    public ShowPathAndObstacle(Group group){
        ShowPathAndObstacle.group = group;
        GuiMqttMsgHandler.getInstance().addListener(this);
    }

    public static void deletePath(ActionEvent event) {
        group.getChildren().removeAll(rects);
        rects.clear();
    }

    private void draw(Integer msgType, List<int []> coords){
        
        for (int i = 0; i < coords.size(); i++) {
            Color color = null;
            switch (msgType) {
                case 101: // Robo 1
                    color = Color.rgb(255, 0, 110, 1); //Red
                    break;
                case 102: // Robo 2
                    color = Color.rgb(131, 56, 236, 1); //Purple
                    break;
                case 103: // Robo 3
                    color = Color.rgb(58, 134, 255, 1); //Blue
                    break;
                case 104:
                    color = switch (coords.get(i)[2]) {
                        case 1 -> Color.rgb(255, 0, 110, 1);
                        case 2 -> Color.rgb(131, 56, 236, 1);
                        case 3 -> Color.rgb(58, 134, 255, 1);
                        case 4 -> Color.rgb(190, 190, 190, 1);
                        case 5 -> Color.rgb(255, 102, 255, 1);
                        case 6 -> Color.rgb(255, 153, 51, 1);
                        case 7 -> Color.rgb(102, 102, 153, 1);
                        case 8 -> Color.rgb(0, 0, 0, 1);
                        default -> Color.rgb(0, 0, 0);
                    };
            }

            Rectangle r = new Rectangle();
            r.setX(coords.get(i)[0]);
            r.setY(coords.get(i)[1]);
            r.setHeight(GuiData.getGRID_SIZE() - 1);
            r.setWidth(GuiData.getGRID_SIZE() - 1);
            r.setFill(color);
            rects.add(r);
        }
    }

    public static boolean isValidIndex(int[] arr, int index) {
        try {
            Objects.checkIndex(index, arr.length);
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        return true;
    }

    @Override
    public void invalidated(Observable observable) {
        Platform.runLater(() -> {
            Map<Integer, List<int []>> msg = ((GuiMqttMsgHandler)observable).getSpot();

            group.getChildren().removeAll(rects);
            rects.clear();

            for(Map.Entry<Integer, List<int []>> entry : msg.entrySet()) {
                //removeElementsOutsidOfGamefield(entry.getValue());
                draw(entry.getKey(), entry.getValue());
            }
            group.getChildren().addAll(rects);
            //((GuiMqttMsgHandler)observable).clearObstacles();
        });
    }
}
