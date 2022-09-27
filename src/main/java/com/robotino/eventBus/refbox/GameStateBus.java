package com.robotino.eventBus.refbox;

import com.robotino.eventBus.Bus;
import com.robotino.eventBus.Event;
import com.robotino.eventBus.Subscriber;

import java.util.*;

public class GameStateBus implements Bus {

    private final List<Subscriber> subscriberList = new LinkedList<>();
    public final Event lastEvent = null;


    private static final GameStateBus GAME_STATE_BUS_INSTANCE = new GameStateBus();
    public static GameStateBus getInstance(){ return GAME_STATE_BUS_INSTANCE; }
    private GameStateBus(){}

    @Override
    public void publish(Event event) {
        int size = subscriberList.size();
        for (int i = 0; i < size; i++) {
            try{
                Subscriber s = subscriberList.get(i);
                s.onReceive(event);
            }catch (Exception e) {
                System.out.println("Nachricht wurde verloren ist aber nicht schlimm: " + event + " Exception: " + e);
            }
        }
    }

    @Override
    public void publish(Subscriber subscriber) {
        subscriber.onReceive(lastEvent);
    }

    @Override
    public void subscribe(Subscriber subscriber) {
        subscriberList.add(subscriber);
        if(lastEvent != null){
            publish(subscriber); // Wen sich jemand neues subscribed wird das letzte Event geschickt
        }
    }

    @Override
    public void unsubscribe(Subscriber subscriber) {
        subscriberList.remove(subscriber);
    }
}
