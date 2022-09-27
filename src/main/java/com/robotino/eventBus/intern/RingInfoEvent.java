package com.robotino.eventBus.intern;

import com.robotino.eventBus.Event;
/**
 * Daten Klasse für den RingInfoBus
 */
public class RingInfoEvent extends Event {

    public RingInfoEvent(){

    }

    @Override
    public String toString() {
        return "RingInfoEvent{only a Event with no Data}";
    }
}
