package com.robotino.eventBus.intern;

import com.robotino.eventBus.Event;

/**
 * @author Kevin Zahn, Oliver Martin
 * @description Klasse für das Event welches auf dem entsprechendem
 *              Bus published wird. Besteht aus Konstruktor und den
 *              entsprechenden Getters.
 */

public class DriveToNextTargetEvent extends Event {

    private int roboNr;
    private boolean isPosReached;

    public DriveToNextTargetEvent(int roboNr, boolean isPosReached) {
        super();
        this.roboNr = roboNr;
        this.isPosReached = isPosReached;
    }

    public int getRoboNr(){
        return roboNr;
    }

    public boolean isRoboOnPos() {
        return isPosReached;
    }
}