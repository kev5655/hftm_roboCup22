package com.robotino.eventBus.mqtt;

import com.robotino.eventBus.Event;
import com.robotino.logistics.Coordinate;

/**
 * Daten Klasse für den CurrentRoboPosBus
 */
public class CurrentRoboPosEvent extends Event {

    private final int roboNr;
    private final Coordinate coordinate;

    public CurrentRoboPosEvent(Coordinate coordinate, int roboNr){
        super();
        this.coordinate = coordinate;
        this.roboNr = roboNr;
    }
    
    public double getX() {
        return coordinate.getX();
    }

    public double getY() {
        return coordinate.getY();
    }

    public double getA() {
        return coordinate.getA();
    }

    public double [] getPosition(){
        return coordinate.getPointAsArray();
    }

    public int getRoboNr(){
        return roboNr;
    }

    @Override
    public String toString() {
        return "CurrentRoboPosEvent{" +
                "roboNr=" + roboNr +
                ", coordinate=" + coordinate +
                '}';
    }
}