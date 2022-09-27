package com.robotino.drive;

import com.robotino.eventBus.*;
import com.robotino.eventBus.mqtt.CurrentRoboPosBus;
import com.robotino.eventBus.mqtt.CurrentRoboPosEvent;
import com.robotino.logistics.Coordinate;
import com.robotino.robo.Robo;

/**
 * @author Fabian Leuenberger
 * @description Ziel dieser Klasse ist es die aktuelle Position des Robotinos
 *              mit der Ziel-Koordinate zu vergleichen, wenn sich dies
 *              im gewählten Offset befindet. Ist dies der Fall wird die ein
 *              DrivePathPosReachedEvent verschickt. Welches im Drive Controller
 *              empfangen wird.
 */
public class RoboPositionHandler implements Subscriber {

    private final int roboNr;
    private Coordinate goalCord;
    final DriveHandler driveHandler;
    private static final double OFFSET = 0.15;
    private static final int OFFSET_A = 10;

    public RoboPositionHandler(Robo robo, Coordinate goalCord, DriveHandler driveHandler){
        this.roboNr = robo.getNumber();
        this.goalCord = goalCord;
        this.driveHandler = driveHandler;
        CurrentRoboPosBus.getInstance().subscribe(this);
    }

    /**
     * Vergleicht die aktuelle X und Y Position des Robotinos mit der Ziel-Koordinate.
     * Stimmen dies überein wird ein DrivePathPosReachedEvent verschickt.
     * @param currentXPos aktuelle X-Position des Robotinos
     * @param currentYPos aktuelle Y-Position des Robotinos
     */
    private void comparisonCurrentToGoalPos(double currentXPos, double currentYPos, double currentAPos) {
        double aTarget = goalCord.getA();
        double xLow = goalCord.getX() - OFFSET;
        double xHigh = goalCord.getX() + OFFSET;
        double yLow = goalCord.getY() - OFFSET;
        double yHigh = goalCord.getY() + OFFSET;


        if (aTarget == 0) {
            aTarget = 360;
        }

        double aLow = aTarget - OFFSET_A;
        double aHigh = aTarget + OFFSET_A;

        if (currentAPos <= 0) {
            currentAPos = 360 + currentAPos;
        }
        if (aTarget == 360 && currentAPos < 10) {
            currentAPos += 360;
        }

        boolean isLowerX = currentXPos > xLow;
        boolean isHigherX = currentXPos < xHigh;
        boolean isLowerY = currentYPos > yLow;
        boolean isHigherY = currentYPos < yHigh;


        boolean isLowerA = currentAPos > aLow;
        boolean isHigherA = currentAPos < aHigh;

        //System.out.println("Higher: " + isHigherA + " Lower: " + isLowerA + " Current: "+ currentAPos + " Max: " + aHigh + " Min: " + aLow);
        if ((isLowerX && isHigherX) && (isLowerY && isHigherY) && (isLowerA && isHigherA)) {
            driveHandler.driveToNextTarget(this);
        }
    }

    public void updateGoalCoordinate(Coordinate goalCord){
        //System.out.println("Update Coordinate " + goalCord.toString());
        this.goalCord = goalCord;
    }

    @Override
    public void onReceive(Event event) {
        CurrentRoboPosEvent currentRoboPos = (CurrentRoboPosEvent) event;
        if (currentRoboPos.getRoboNr() == roboNr){
            comparisonCurrentToGoalPos(currentRoboPos.getX(), currentRoboPos.getY(), currentRoboPos.getA());
        }
    }
}
