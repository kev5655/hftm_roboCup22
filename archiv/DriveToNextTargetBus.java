package com.robotino.eventBus.intern;

import com.robotino.eventBus.Bus;
import com.robotino.eventBus.Event;
import com.robotino.eventBus.Subscriber;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
 *              Die Klasse wird mit dem Singelton-Pattern umgesetzt und über die Methode
 *              getInstance abgerufen. Damit diese jeweils nur einmal aufgerufen wird.
 *
 *              Falls zu spät auf den Bus subscribe wird, wird das letzte Event noch geschickt
 */

public class DriveToNextTargetBus implements Bus {

    private static final DriveToNextTargetBus DRIVE_PATH_POS_REACHED =  new DriveToNextTargetBus();
    private Set<Subscriber> subscriberList = Collections.synchronizedSet(new HashSet<>());

    private DriveToNextTargetBus(){}
    public static DriveToNextTargetBus getInstance(){
        return DRIVE_PATH_POS_REACHED;
    }

    Event lastEvent = null;

    @Override
    public void publish(Event event) {
        lastEvent = event;
        for(Subscriber s : subscriberList){
            s.onReceive(event);
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
        }
    }

    @Override
    public void unsubscribe(Subscriber subscriber) {
        subscriberList.remove(subscriber);
    }
}