package com.robotino.eventBus.mqtt;

import com.robotino.eventBus.Event;

import java.util.Arrays;

/**
 * Daten Klasse für den ARTagBus.
 * Wird noch nicht verwendet
 */
public class ArTagEvent extends Event {

    private final int roboNr;
    private final int [] tagPosition;

    public ArTagEvent(int [] tagPosition, int roboNr){
        super();
        this.tagPosition = tagPosition;
        this.roboNr = roboNr;
    }

    public int getX() {
        return tagPosition[0];
    }

    public int getY() {
        return tagPosition[1];
    }

    public int getAngle() {
        return tagPosition[2];
    }
    
    public int getDistance() {
        return tagPosition[3];
    }
    
    public int getIdTag() {
        return tagPosition[4];
    }

    public int [] getPosition(){
        return tagPosition;
    }

    public int getRoboNr(){
        return roboNr;
    }

    @Override
    public String toString() {
        return "ArTagEvent{" +
                "roboNr=" + roboNr +
                ", tagPosition=" + Arrays.toString(tagPosition) +
                '}';
    }
}