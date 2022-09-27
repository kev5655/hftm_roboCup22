package com.robotino.eventBus.mqtt;

import com.robotino.eventBus.Bus;
import com.robotino.eventBus.Event;
import com.robotino.eventBus.Subscriber;

import java.util.*;

/**
 * @author Kevin Zahn
 * @description Diese Klasse beschäftigt sich mit den subscriber zu dem
 *              gewählten Bus, die subscriber werden einem HashSet hinzugefügt
 *              und können über die Methoden subscribe/unsubscribe der Liste
 *              hinzugefügt bzw. entfernt werden.
 *
 *              Über die Methode publish wird auf den Bus und an die subscriber
 *              gesendet.
 *
 *              Die Klasse wird mit dem Singleton-Pattern umgesetzt und über die Methode
 *              getInstance abgerufen. Damit diese jeweils nur einmal aufgerufen wird.
 *
 *              Falls zu spät auf den Bus subscribe wird, wird das letzte Event noch geschickt
 */

public class CurrentRoboPosBus implements Bus {

    private static final CurrentRoboPosBus WHERE_IS_ROBO_BUS = new CurrentRoboPosBus();
    private final List<Subscriber> subscriberList = new LinkedList<>();//Collections.synchronizedSet(new HashSet<>());

    private CurrentRoboPosBus(){}
    public static CurrentRoboPosBus getInstance(){
        return WHERE_IS_ROBO_BUS;
    }

    Event lastEvent = null;

    @Override
    public synchronized void publish(Event event) {
        lastEvent = event;

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
    public synchronized void subscribe(Subscriber subscriber) {
        subscriberList.add(subscriber);
        if(lastEvent != null){
            publish(subscriber);
            //Log.incomingMqttMsg.info("Nachricht: " + lastEvent.toString() + " wurde nachträglich an " + subscriber.getClass() + " übermittelt");
        }
    }

    @Override
    public synchronized void unsubscribe(Subscriber subscriber) {
        subscriberList.remove(subscriber);
    }
}