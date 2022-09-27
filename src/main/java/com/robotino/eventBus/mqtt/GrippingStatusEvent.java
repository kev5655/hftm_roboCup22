package com.robotino.eventBus.mqtt;

import com.robotino.eventBus.Event;

/**
 * Daten Klasse für den GrippingStatusBus
 */
public class GrippingStatusEvent extends Event {

        private final int roboNr;
        private final boolean isGrippingCompleted;

        public GrippingStatusEvent(int roboNr, boolean isGrippingCompleted) {
            super();
            this.roboNr = roboNr;
            this.isGrippingCompleted = isGrippingCompleted;
        }

    public int getRoboNr() { return roboNr; }
    public boolean isGrippingCompleted() { return isGrippingCompleted; }

    @Override
    public String toString() {
        return "GrippingStatusEvent{" +
                "roboNr=" + roboNr +
                ", isGrippingCompleted=" + isGrippingCompleted +
                '}';
    }
}