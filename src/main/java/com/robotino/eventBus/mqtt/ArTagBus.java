package com.robotino.eventBus.mqtt;

import com.robotino.eventBus.Bus;
import com.robotino.eventBus.Event;
import com.robotino.eventBus.Subscriber;

import java.util.LinkedList;
import java.util.List;


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
 *              Falls zu spät auf den Bus subscribe wird, wird das letzte Event noch geschickt.
 *
 *              Wird noch nicht verwendet
 */

public class ArTagBus implements Bus {

    private static final ArTagBus TAG_POS_DATA_BUS = new ArTagBus();
    private final List<Subscriber> subscriberList = new LinkedList<>();

    private ArTagBus(){}
    public static ArTagBus getInstance(){
        return TAG_POS_DATA_BUS;
    }

    public Event lastEvent = null;
    
    @Override
    public void publish(Event event) {
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
    public void subscribe(Subscriber subscriber) {
        subscriberList.add(subscriber);
        if(lastEvent != null){
            publish(subscriber);
            //Log.incomingMqttMsg.info("Nachricht: " + lastEvent.toString() + " wurde nachträglich an " + subscriber.getClass() + " übermittelt");
        }
    }

    @Override
    public void unsubscribe(Subscriber subscriber) {
        subscriberList.remove(subscriber);
    }
}