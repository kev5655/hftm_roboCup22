package gui.node;

import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.LinkedList;
import java.util.List;

public class ShowHead {

    final Group group;
    HBox hBoxTop;
    HBox hBoxDown;
    VBox vBox;
    FlowPane flowPane;

    public ShowHead(Group group){
        this.group = group;
        draw();
    }

    public void draw(){

        hBoxTop = new HBox(20);
        hBoxDown = new HBox(20);
        vBox = new VBox(20);

        Text title = new Text("Path Farbe von Robo 1 (IP: 172.26.107.251), Robo 2 (IP: 172.26.107.252), Robo 3 (IP: 172.26.107.253)");
        Text robo1 = new Text("Robo 1: ");
        Text robo2 = new Text("Robo 2: ");
        Text robo3 = new Text("Robo 3: ");
        Rectangle colorRobo1 = new Rectangle(10,10, 10, 10);
        Rectangle colorRobo2 = new Rectangle(50, 10, 10, 10);
        Rectangle colorRobo3 = new Rectangle(90, 10, 10, 10);
        colorRobo1.setFill(Color.rgb(255,0,110,1));
        colorRobo2.setFill(Color.rgb(131,56,236,1));
        colorRobo3.setFill(Color.rgb(58,134,255,1));
        Button clearPaths = new Button("Clear Field");
        clearPaths.setOnAction(this::deletePath);
        Button clearLogs = new Button("Clear Logs");
        clearLogs.setOnAction(this::deleteLogs);

        List<Node> roboColors = new LinkedList<>(List.of(colorRobo1, colorRobo2, colorRobo3));

        hBoxTop.getChildren().addAll(title);
        hBoxDown.getChildren().addAll(robo1, colorRobo1, robo2, colorRobo2, robo3, colorRobo3, clearPaths, clearLogs);
        vBox.getChildren().addAll(hBoxTop, hBoxDown);
        group.getChildren().add(vBox);

    }

    private void deleteLogs(ActionEvent actionEvent) {
        ShowTextOutput.deleteLogs(actionEvent);
    }

    private void deletePath(ActionEvent actionEvent) {
        ShowPathAndObstacle.deletePath(actionEvent);
    }


}
