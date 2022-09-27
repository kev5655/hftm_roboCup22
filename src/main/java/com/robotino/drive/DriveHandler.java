package com.robotino.drive;

import com.robotino.eventBus.mqtt.CurrentRoboPosBus;
import com.robotino.logistics.Coordinate;
import com.robotino.robo.Robo;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Handled das Abfahren des Path und triggert immer die nächste Koordinate die angefahren werden soll.
 */
public class DriveHandler  {

    private final Robo robo;
    private final Queue <Coordinate> path;
    private DriveController driveController = null;

    /**
     * Only for Testing git keine Rückmeldung wen der Path abgefahren wurde
     * @param robo Roboter der angesteuert wird
     * @param path Path der zufahren ist
     * @deprecated
     */
    public DriveHandler(Robo robo, List<Coordinate> path){
        this.robo = robo;
        this.path = new LinkedList<>(path);
        getFirstCoordinate();
    }


    public DriveHandler(Robo robo, List<Coordinate> path, DriveController driveController){
        this.robo = robo;
        List<Coordinate> reversePath = new LinkedList<>(path);
        Collections.reverse(reversePath);
        this.path = new LinkedList<>(reversePath);
        this.driveController = driveController;
        getFirstCoordinate();
    }

    private void getFirstCoordinate(){
        if(path.size() > 5){ // Übersprint die erste vier Positionen um Rucken des Roboters zu verhindern
            path.poll();
            path.poll();
            path.poll();
            path.poll();
        }
        //
        new RoboPositionHandler(robo, path.peek(), this);
        if (! path.isEmpty()) {
            robo.drive(path.poll()); // Steuert den Roboter an
        }

    }


    /**
     * Wird vom RoboPositionHandler aufgerufen, wen die Postion erreicht wurde so das eine neue Position angefahren werden soll
     * @param roboPositionHandler Wird verwendet, um die Ziel Koordinaten des RoboPositionsHandler zu aktualisieren.
     */
    protected void driveToNextTarget(RoboPositionHandler roboPositionHandler) {
        //DriveToNextTargetEvent driveToNextTarget = (DriveToNextTargetEvent) event;
        if (! path.isEmpty()) { //driveToNextTarget.getRoboNr() == robo.getRoboNr() && (!
            Coordinate coordinate = path.poll();
            roboPositionHandler.updateGoalCoordinate(coordinate);
            robo.drive(coordinate); // Steuert den Roboter an
        } else {
            if(driveController != null){
                driveController.finishDrive();
                CurrentRoboPosBus.getInstance().unsubscribe(roboPositionHandler);
            }
        }
    }
}
