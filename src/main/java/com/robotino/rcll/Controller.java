package com.robotino.rcll;

import com.robotino.challenge.GraspingChallenge;
import com.robotino.challenge.NavChallenge;
import com.robotino.communication.refbox.receive.GameState;
import com.robotino.eventBus.intern.OrderBus;
import com.robotino.eventBus.intern.OrderEvent;
import com.robotino.eventBus.intern.RingInfoBus;
import com.robotino.eventBus.intern.RingInfoEvent;
import com.robotino.eventBus.refbox.*;
import com.robotino.eventBus.Event;
import com.robotino.eventBus.Subscriber;
import com.robotino.game.ProductHandler;
import com.robotino.helperClass.Data;
import com.robotino.helperClass.Log;
import com.robotino.robo.Robo;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Kevin Zahn
 * @description Kontroller Klasse welcher die im XML-File angegebene Challenge startet
 */

public class Controller implements Subscriber {
    private boolean gotMachineInfo = false;
    final Robo robo;
    private boolean gotOrder = false;
    private boolean gotRings = false;
    private GameState.State lastState;

    public Controller(){
        //Roboter mit dem das Spiel gestartet wird
        robo = new Robo(1, Data.getROBOTS_START_FIELDS().get(1));
        // Events abonnieren
        MachineInfoBus.getInstance().subscribe(this);
        OrderBus.getInstance().subscribe(this);
        RingInfoBus.getInstance().subscribe(this);
        GameStateBus.getInstance().subscribe(this);

        // Grasping Challenge kann direkt gestartet werden alle anderen Challenges brauchen irgendwelche Events
        if(Data.getCHALLENGE() == 1){
            start();
        }
    }

    /**
     * Starte die verschiedenen Challenges die start Methode wird durch den Konstruktor
     * oder die verschiedenen Events getriggert
     */
    private void start(){
        switch (Data.getCHALLENGE()) {
            case 0 -> {
                Log.startedSystem.info("Started Navigation Challenge");
                new NavChallenge(robo);
            }
            case 1 -> {
                Log.startedSystem.info("Started Grasping Challenge");
                List<Robo> robos = new LinkedList<>(List.of(robo));
                new GraspingChallenge(robos);
            }
            case 2 -> {
                Log.startedSystem.info("Started new Product Handler");
                new ProductHandler(robo);
            }
            default -> throw new IllegalArgumentException("Die Challenge Nummer gibt es nicht: " + Data.getCHALLENGE());
        }
    }

    @Override
    public void onReceive(Event event) {
        if (event instanceof MachineInfoEvent) {
            if (!gotMachineInfo) {
                gotMachineInfo = true;
                Log.startedSystem.info("Got Machine Info");
                if (isAllDataAvailableForNavChallenge()) { // Starten der Navigations Challenge
                    start();
                }
                if (isAllDataAvailableForProdChallenge()) { // Starten der Productions Challenge
                    start();
                }
                MachineInfoBus.getInstance().unsubscribe(this);
            }
        } else if (event instanceof OrderEvent) {
            if (!gotOrder){
                gotOrder = true;
                Log.startedSystem.info("Got Order Info");
                if (isAllDataAvailableForProdChallenge()) { // Starten der Productions Challenge
                    start();
                }
            }
        } else if (event instanceof RingInfoEvent) {
            if (!gotRings){
                gotRings = true;
                Log.startedSystem.info("Got Ring Info");
                if (isAllDataAvailableForProdChallenge()) {
                    start();
                }
            }
        } else if (event instanceof GameStateEvent gameStateEvent) {
            if(lastState != gameStateEvent.getState()){
                lastState = gameStateEvent.getState();
                if (gameStateEvent.getState() == GameState.State.PAUSED) {
                    Log.game.info("Game stop from Refbox");
                    robo.stop();
                } else if (gameStateEvent.getState() == GameState.State.RUNNING) {
                    Log.game.info("Game startes again");
                    robo.start();
                }
            }
        }
    }

    private boolean isAllDataAvailableForProdChallenge() {
        return Data.getCHALLENGE() == 2 && gotMachineInfo && gotOrder && gotRings;
    }

    private boolean isAllDataAvailableForNavChallenge() {
        return Data.getCHALLENGE() == 0 && gotMachineInfo;
    }
}