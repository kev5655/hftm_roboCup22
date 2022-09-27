package localGui;


import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.LinkedList;
import java.util.List;

public class ShowGridAndField {

    private final int height;
    private final int width;
    private final int fieldSize;
    private final int gridSize;
    private final int halfMargin;

    public ShowGridAndField(Group group, int height, int width, int fieldSize, int gridSize, int halfMargin){
        this.height = height; this.width = width; this.fieldSize = fieldSize; this.gridSize = gridSize; this.halfMargin = halfMargin;
        createGrid(group);
        createField(group);
    }

    private void createGrid(Group group){
        List<Line> grid = new LinkedList<>();
        for(int x = 0; x <= width; x += gridSize){
            int lineX = x + halfMargin;
            Line line = new Line(lineX, halfMargin, lineX, height + halfMargin);
            line.setStyle("-fx-stroke: rgba(0, 0, 0, 0.2);");
            grid.add(line);
        }
        for(int y = 0; y <= height; y += gridSize){
            int lineY = y+ halfMargin;
            Line line = new Line(halfMargin, lineY, width + halfMargin, lineY);
            line.setStyle("-fx-stroke: rgba(0, 0, 0, 0.2);");
            grid.add(line);
        }
        group.getChildren().addAll(grid);
    }

    private void createField(Group group){
        List<Line> field = new LinkedList<>();
        for(int x = 0; x <= width; x += fieldSize){
            int lineX = x + halfMargin;
            Line line = new Line(lineX, halfMargin, lineX, height + halfMargin);
            line.setStroke(Color.RED);
            field.add(line);
        }
        for(int y = 0; y <= height; y += fieldSize){
            int lineY = y + halfMargin;
            Line line = new Line(halfMargin, lineY, width + halfMargin, lineY);
            line.setStroke(Color.RED);
            field.add(line);
        }
        group.getChildren().addAll(field);
    }

}
