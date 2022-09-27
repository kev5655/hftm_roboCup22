package com.robotino.communication.refbox.receive;

import com.grips.refbox.RefboxHandler;
import com.robotino.eventBus.refbox.GameStateBus;
import com.robotino.eventBus.refbox.GameStateEvent;
import org.robocup_logistics.llsf_msgs.GameStateProtos;
import org.robocup_logistics.llsf_msgs.TimeProtos;

import java.util.function.Consumer;

/**
 * Wertet den empfangenen Game-State von der Refbox aus
 */
public class GameState {

    final RefboxHandler handler;

    public GameState(RefboxHandler handler){
        this.handler = handler;
        receiveMsg();
    }

    private void receiveMsg() {
        Consumer<GameStateProtos.GameState> gameState = gameStateOut -> {
            String stateStr = gameStateOut.getState().toString();
            TimeProtos.Time time = gameStateOut.getGameTime();
            State state = null;
            //System.out.println("Game State");
            switch (stateStr){
                case "RUNNING" -> state = State.RUNNING;
                case "PAUSED" -> state = State.PAUSED;
                case "WAIT_START" -> state = State.WAIT_START;
                case "INIT" -> state = State.INIT;
            }

            GameStateBus.getInstance().publish(new GameStateEvent(state, time.getSec()));

        };

        handler.setGameStateCallback(gameState);
    }

    public enum State{
        RUNNING,
        PAUSED,
        WAIT_START,
        INIT
    }
}