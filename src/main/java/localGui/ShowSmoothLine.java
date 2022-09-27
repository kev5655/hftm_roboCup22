package localGui;


import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.paint.Color;

import java.util.LinkedList;
import java.util.List;

public class ShowSmoothLine {

    final Group group;

    public ShowSmoothLine(Group group){
        this.group = group;
    }

    public void drawLine(List<Line> lines){
        List<javafx.scene.shape.Line> fxLines = new LinkedList<>();

        for(Line line : lines){
            float x1 = (float) line.x1;
            float y1 = (float) line.y1;
            float x2 = (float) line.x2;
            float y2 = (float) line.y2;
            javafx.scene.shape.Line fxLine = new javafx.scene.shape.Line(x1, y1, x2, y2);
            fxLine.setStroke(Color.rgb(245, 40, 145, 1));
            fxLine.setStrokeWidth(3);
            fxLines.add(fxLine);
        }
        Platform.runLater(() -> group.getChildren().addAll(fxLines));

    }
}
