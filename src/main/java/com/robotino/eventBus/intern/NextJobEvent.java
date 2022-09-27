package com.robotino.eventBus.intern;

import com.robotino.eventBus.Event;
import com.robotino.game.jobs.Executable;
/**
 * Daten Klasse für den NextJobBus
 */
public class NextJobEvent extends Event {

    private final Executable executableClass;

    public NextJobEvent(Executable executableClass){
        this.executableClass = executableClass;
    }

    public Executable getExecutableClass() {
        return executableClass;
    }

    @Override
    public String toString() {
        return "NextJobEvent{" +
                "executableClass=" + executableClass +
                '}';
    }
}
