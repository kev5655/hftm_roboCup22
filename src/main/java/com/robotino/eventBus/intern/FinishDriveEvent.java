package com.robotino.eventBus.intern;

import com.robotino.drive.DriveController;
import com.robotino.eventBus.Event;
import com.robotino.robo.Robo;
/**
 * Daten Klasse für den FinishDriveBus
 */
public class FinishDriveEvent extends Event {

    final DriveController driveController;
    final Robo robo;

    public FinishDriveEvent(DriveController driveController, Robo robo){
        super();
        this.driveController = driveController;
        this.robo = robo;
    }

    public DriveController getDriveController() {
        return driveController;
    }

    public Robo getRobo() {
        return robo;
    }

    @Override
    public String toString() {
        return "FinishDriveEvent{" +
                "driveController=" + driveController +
                ", robo=" + robo +
                '}';
    }
}
