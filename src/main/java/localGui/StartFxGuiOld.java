package localGui;

import com.google.common.base.Stopwatch;
import com.grips.model.teamserver.MachineClientUtils;
import com.robotino.drive.AStar;
import com.robotino.drive.Obstacle;
import com.robotino.helperClass.Data;
import com.robotino.logistics.Coordinate;
import com.robotino.logistics.Field;
import com.robotino.logistics.Station;
import gui.data.GuiData;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


public class StartFxGuiOld extends Application {

    ShowSmoothLine showSmoothLine;

    @Override
    public void start(Stage primaryStage) throws InterruptedException {

        try {
            Data.loadFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        javafx.scene.Group group = new Group();
        ShowSpot showSpot = new ShowSpot(group);
        showSmoothLine = new ShowSmoothLine(group);

        new ShowGridAndField(group,
                Data.getFIELD_HEIGHT(),
                Data.getFIELD_WIDTH(),
                Data.getFIELD_SIZE(),
                Data.getGRID_SIZE(),
                Data.getHALF_MARGIN());

        Scene scene = new Scene(group,
                Data.getFIELD_WIDTH() + Data.getMARGIN(),
                Data.getFIELD_HEIGHT() + Data.getMARGIN());

        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();

        Thread showAStar = new Thread(() -> {
            while(! showSpot.isQueueEventEmpty()){
                showSpot.draw();
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread aStar = new Thread(() -> Platform.runLater(() -> {
            startAStar();
            showAStar.start();
        }));
        aStar.start();
    }

    public void startAStar(){
        //AStar
        Coordinate start = new Field("M_Z51").getCoordinate();
        Coordinate goal = new Field("C_Z51").getCoordinate();

        //Coordinate start = new Field("M_Z47").getCoordinate();
        //Coordinate goal = new Field("C_Z64").getCoordinate();



        List<Obstacle> obstacles = generateObstacles();

        //AStar aStar = new AStar(start, goal);
        AStar aStar = new AStar(start, goal, obstacles); //start, goal
        PathFx pathFx = new PathFx(aStar.findPath());

        if( pathFx != null){
            //List<Line> lines = new LineSmoother().smoothPaht(path);
            showSmoothLine.drawLine(pathFx.getSmoothedFxPath());
        }
    }

    public List<Obstacle> generateObstacles(){

        //Station BS1 = new Station("BS1", "a", MachineClientUtils.MachineState.IDLE, "type","M_Z53", 90);
        //Station BS2 = new Station("BS1", "a", MachineClientUtils.MachineState.IDLE, "type","M_Z62", 180);
        //Station BS3 = new Station("BS1", "a", MachineClientUtils.MachineState.IDLE, "type","M_Z46", 90);
        //Station BS4 = new Station("BS1", "a", MachineClientUtils.MachineState.IDLE, "type","C_Z74", 90);
        //Station BS5 = new Station("BS1", "M_Z42", MachineClientUtils.MachineState.IDLE, "type","C_Z61", 0);


        Station BS1  = new Station("BS1", "BS", MachineClientUtils.MachineState.IDLE, "CYAN", "M_Z72", 45);
        Station BS2  = new Station("BS1", "BS", MachineClientUtils.MachineState.IDLE, "CYAN", "M_Z77", 270);
        Station BS3  = new Station("BS1", "BS", MachineClientUtils.MachineState.IDLE, "CYAN", "M_Z54", 135);
        Station BS4  = new Station("BS1", "BS", MachineClientUtils.MachineState.IDLE, "CYAN", "M_Z36", 225);
        Station BS5  = new Station("BS1", "BS", MachineClientUtils.MachineState.IDLE, "CYAN", "M_Z28", 0);
        Station BS6  = new Station("BS1", "BS", MachineClientUtils.MachineState.IDLE, "CYAN", "M_Z21", 0);
        Station BS7  = new Station("BS1", "BS", MachineClientUtils.MachineState.IDLE, "CYAN", "M_Z15", 90);

        Station BS8  = new Station("BS1", "BS", MachineClientUtils.MachineState.IDLE, "CYAN", "C_Z72", 45);
        Station BS9  = new Station("BS1", "BS", MachineClientUtils.MachineState.IDLE, "CYAN", "C_Z77", 270);
        Station BS10 = new Station("BS1", "BS", MachineClientUtils.MachineState.IDLE, "CYAN", "C_Z54", 135);
        Station BS11 = new Station("BS1", "BS", MachineClientUtils.MachineState.IDLE, "CYAN", "C_Z36", 225);
        Station BS12 = new Station("BS1", "BS", MachineClientUtils.MachineState.IDLE, "CYAN", "C_Z28", 0);
        Station BS13 = new Station("BS1", "BS", MachineClientUtils.MachineState.IDLE, "CYAN", "C_Z21", 0);
        Station BS14 = new Station("BS1", "BS", MachineClientUtils.MachineState.IDLE, "CYAN", "C_Z15", 90);


        Obstacle BS1Obstacle = new Obstacle(BS1);
        Obstacle BS2Obstacle = new Obstacle(BS2);
        Obstacle BS3Obstacle = new Obstacle(BS3);
        Obstacle BS4Obstacle = new Obstacle(BS4);
        Obstacle BS5Obstacle = new Obstacle(BS5);
        Obstacle BS6Obstacle = new Obstacle(BS6);
        Obstacle BS7Obstacle = new Obstacle(BS7);
        Obstacle BS8Obstacle = new Obstacle(BS8);
        Obstacle BS9Obstacle = new Obstacle(BS9);
        Obstacle BS10Obstacle = new Obstacle(BS10);
        Obstacle BS11Obstacle = new Obstacle(BS11);
        Obstacle BS12Obstacle = new Obstacle(BS12);
        Obstacle BS13Obstacle = new Obstacle(BS13);
        Obstacle BS14Obstacle = new Obstacle(BS14);

        Obstacle wallEntryTop = new Obstacle().generateWall();

        List<Obstacle> obstacles = new LinkedList<>();
        obstacles.add(BS1Obstacle);
        obstacles.add(BS2Obstacle);
        obstacles.add(BS3Obstacle);
        obstacles.add(BS4Obstacle);
        obstacles.add(BS5Obstacle);
        obstacles.add(BS6Obstacle);
        obstacles.add(BS7Obstacle);
        obstacles.add(BS8Obstacle);
        obstacles.add(BS9Obstacle);
        obstacles.add(BS10Obstacle);
        obstacles.add(BS11Obstacle);
        obstacles.add(BS12Obstacle);
        obstacles.add(BS13Obstacle);
        obstacles.add(BS14Obstacle);
        obstacles.add(wallEntryTop);

        return obstacles;
    }




    public static void main(String[] args) {
        launch(args);
    }
}
