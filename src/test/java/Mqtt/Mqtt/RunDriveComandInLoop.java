package Mqtt.Mqtt;

import com.robotino.drive.DriveController;
import com.robotino.eventBus.Event;
import com.robotino.eventBus.Subscriber;
import com.robotino.eventBus.intern.FinishDriveBus;
import com.robotino.helperClass.Data;
import com.robotino.logistics.Coordinate;
import com.robotino.robo.Robo;

import java.util.Random;

public class RunDriveComandInLoop implements Subscriber {

    RunDriveComandInLoop(){
        FinishDriveBus.getInstance().subscribe(this);
        startDrive();
    }

    public void startDrive(){
        Robo robo = new Robo(1, Data.getROBOTS_START_FIELDS().get(1));
        new DriveController(robo, generateRandomCoordinate(),
                generateRandomCoordinate());
    }

    public Coordinate generateRandomCoordinate(){
        return new Coordinate(new Random().nextDouble(14) - 7,
                new Random().nextDouble(8));
    }


    public static void main(String[] args) {
        new RunDriveComandInLoop();
    }

    @Override
    public void onReceive(Event event) {
        startDrive();
    }
}
