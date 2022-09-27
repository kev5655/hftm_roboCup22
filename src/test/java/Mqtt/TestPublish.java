package Mqtt;

import com.robotino.communication.message.toRobo.DriveMsg;
import com.robotino.communication.message.toRobo.StopProcessMsg;
import com.robotino.communication.message.toVisu.ObstacleMsg;
import com.robotino.communication.message.toVisu.RouteMsg;
import com.robotino.drive.Obstacle;
import com.robotino.drive.Path;
import com.robotino.drive.Spot;
import com.robotino.helperClass.Data;
import com.robotino.logistics.Coordinate;
import com.robotino.robo.Robo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TestPublish {

    public TestPublish(){
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(interupptRun, 0, 5, TimeUnit.SECONDS);
    }

    final Runnable interupptRun = () -> {
        //driveComamand();
        //grippingComamand();
        //startProcessComamand();

        sendVisuMsgs();
    };

    private void startProcessComamand() {
        Robo robo = new Robo(1, Data.getROBOTS_START_FIELDS().get(0));
        StopProcessMsg msg = new StopProcessMsg(robo, true);
    }

    public void driveComamand(){
        Coordinate c = new Coordinate(0,1000,0);
        new DriveMsg(new Robo(1, Data.getROBOTS_START_FIELDS().get(0)), c, Data.getROBO_SPEED());
    }

    public void sendVisuMsgs(){
        List<Obstacle> ob = new LinkedList<>(Obstacle.generateDefaultStations());

        new ObstacleMsg(ob);

        new RouteMsg(new Robo(1,
                Data.getROBOTS_START_FIELDS().get(1)),
                generateRandomPath(10));
        new RouteMsg(new Robo(2,
                Data.getROBOTS_START_FIELDS().get(1)),
                generateRandomPath(10));
        new RouteMsg(new Robo(3,
                Data.getROBOTS_START_FIELDS().get(1)),
                generateRandomPath(10));
    }
    public Path generateRandomPath(int length){
        List<Spot> list = new ArrayList<>();
        int xMax = Data.getMAX_X_INDEX();
        int yMax = Data.getMAX_Y_INDEX();
        for (int i = 0; i < length; i++) {
            list.add(new Spot(new Random().nextInt(xMax), new Random().nextInt(yMax)));
        }
        return new Path(list, 0);
    }

    public static void main(String[] args) {
        new TestPublish();
    }
}
