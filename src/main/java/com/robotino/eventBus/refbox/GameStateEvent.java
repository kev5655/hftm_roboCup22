package com.robotino.eventBus.refbox;

import com.robotino.communication.refbox.receive.GameState;
import com.robotino.eventBus.Event;
/**
 * Daten Klasse für den GameStateBus
 */
public class GameStateEvent extends Event {

    private final GameState.State state;
    private final long timeInSec;

    public GameStateEvent(GameState.State state, long timeInSec){
        super();
        this.state = state;
        this.timeInSec = timeInSec;
    }

    public GameState.State getState() {
        return state;
    }

    public long getTimeInSec() {
        return timeInSec;
    }

    @Override
    public String toString() {
        return "GameStateEvent{" +
                "state=" + state +
                ", timeInSec=" + timeInSec +
                '}';
    }
}
