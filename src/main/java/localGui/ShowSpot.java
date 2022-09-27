package localGui;

import com.robotino.helperClass.Data;
import com.robotino.drive.Spot;
import com.robotino.eventBus.intern.AStarBus;
import com.robotino.eventBus.intern.AStarEvent;
import com.robotino.eventBus.Event;
import com.robotino.eventBus.Subscriber;
import com.robotino.logistics.Coordinate;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ShowSpot implements Subscriber {

    final Group group;
    final ConvertPositionFx convert = new ConvertPositionFx();

    private final Queue<Event> queueEvent = new ArrayDeque<>();
    private final List<Coordinate> gridCoordinates = new LinkedList<>();
    private final List<Coordinate> pathCoordinates = new LinkedList();
    private final List<Coordinate> openSetCoordinates = new LinkedList();
    private final List<Coordinate> closeSetCoordinates = new LinkedList();

    private final boolean displayPahtFinder = true;


    public ShowSpot(Group group){
        this.group = group;
        AStarBus.getInstance().subscribe(this);
        List<Coordinate> l = new LinkedList<>();
        for (int i = 0; i < 100; i++) {
            Coordinate c1 = new Coordinate(i,0);
            l.add(c1);
        }
        draw(l, Color.BLACK);
    }

    public boolean isQueueEventEmpty(){
        return queueEvent.isEmpty();
    }

    public int getQueueSize(){
        return queueEvent.size();
    }


    @Override
    public void onReceive(Event event) {
        this.queueEvent.add(event);

    }
    public void draw(){
        if(! queueEvent.isEmpty()){

            AStarEvent e = (AStarEvent) queueEvent.poll();
            //AStarEvent e = (AStarEvent) queueEvent.pop();

            List<Coordinate> coords = new LinkedList<>();

            Spot[][] grid = e.getGrid();
            if(grid != null){
                for (int i = 0; i < grid.length; i++) {
                    for (int j = 0; j < grid[i].length; j++) {
                        if(grid[i][j].isWall()){
                            int x = grid[i][j].x;
                            int y = grid[i][j].y;
                            Coordinate c = new Coordinate(x, y, 0);
                            if(! Coordinate.compare(c, gridCoordinates)){
                                gridCoordinates.add(c);
                                coords.add(c);
                            }
                        }
                    }
                }
                draw(coords, Color.BLACK);
            }

            List<Spot> path = e.getPath();
            if(path != null && displayPahtFinder){
                for(Spot p : path){
                    Coordinate c = new Coordinate(p.x, p.y, 0);
                    if(! Coordinate.compare(c, pathCoordinates)){
                        pathCoordinates.add(c);
                        coords.add(c);
                    }
                }
                draw(coords, Color.rgb(255, 0,0, 0.4));
            }

            List<Spot> openSet = e.getOpenSet();
            if(openSet != null && displayPahtFinder){
                for(Spot o : openSet){
                    Coordinate c = new Coordinate(o.x, o.y, 0);
                    if(! Coordinate.compare(c, openSetCoordinates)){
                        openSetCoordinates.add(c);
                        coords.add(c);
                    }
                }
                draw(coords, Color.GREEN);
            }

            List<Spot> closeSet = e.getCloseSet();
            if(closeSet != null && displayPahtFinder){
                for(Spot cS : closeSet){
                    Coordinate c = new Coordinate(cS.x, cS.y, 0);
                    if(! Coordinate.compare(c, closeSetCoordinates)){
                        closeSetCoordinates.add(c);
                        coords.add(c);
                    }
                }
                draw(coords, Color.rgb(0,0,255,0.3));
            }

            List<Spot> ring = e.getRing();
            if(ring != null && displayPahtFinder){
                for(Spot r : ring){
                    Coordinate c = new Coordinate(r.x - 1, r.y - 2, 0);
                    if(! Coordinate.compare(c, closeSetCoordinates)){
                        closeSetCoordinates.add(c);
                        coords.add(c);
                    }
                }
                draw(coords, Color.rgb(0,255,255,0.3));
            }
        }else{
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void draw(List<Coordinate> coords, Color color){
        List<Rectangle> rects = new LinkedList<>();
        for (int i = 0; i < coords.size(); i++) {
            Rectangle r = new Rectangle();
            Coordinate c = new Coordinate(coords.get(i).getX(), coords.get(i).getY());
            c = convert.AStarGridIndexToGuiCoordinate(c);
            r.setX(c.getX());
            r.setY(c.getY());
            r.setHeight(Data.getGRID_SIZE() - 1);
            r.setWidth(Data.getGRID_SIZE() - 1);
            r.setFill(color);
            rects.add(r);
        }

        Platform.runLater(() -> {
            group.getChildren().addAll(rects);

        });

    }
}
