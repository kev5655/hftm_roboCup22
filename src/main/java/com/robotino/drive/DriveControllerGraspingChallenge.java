package com.robotino.drive;

import com.robotino.logistics.Station;
import com.robotino.robo.Robo;
import java.util.LinkedList;
import java.util.List;

/**
 * Wird nur für die Grasping Challenge verwendet, da das Drive Packet auf Stationen von der Refbox wartet und man
 * diese nicht bekommt in der Grasping Challenge muss das Drive Packet ander gestartet werden.
 */
public class DriveControllerGraspingChallenge {

    public DriveControllerGraspingChallenge(Robo robo, Station station, Station.Side side, @org.jetbrains.annotations.NotNull List<Station> stations){
        List<Obstacle> obstacles = new LinkedList<>();
        for(Station s : stations){
            obstacles.add(new Obstacle(s));
        }
        new DriveController().startDrive(robo, robo.getDriveToCoordinate(), station.getSide(side), obstacles);
    }
}
