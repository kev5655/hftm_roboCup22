package com.robotino.eventBus.intern;

import com.robotino.eventBus.Bus;
import com.robotino.eventBus.Event;
import com.robotino.eventBus.Subscriber;
import com.robotino.helperClass.Log;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class RingStationBus implements Bus {

    private static final RingStationBus RING_STATION_BUS = new RingStationBus();
    private Set<Subscriber> subscriberList = Collections.synchronizedSet(new HashSet<>());

    private RingStationBus(){}

    public static RingStationBus getInstance(){
        return RING_STATION_BUS;
    }

    Event lastEvent = null;

    @Override
    public void publish(Event event) {
        lastEvent = event;
        for(Subscriber s : subscriberList){
            s.onReceive(event);
            Log.incomingRefboxMsg.info("Nachricht " + event.toString() +  " wurde übermittelt an: " + s.getClass());
        }
        if (subscriberList.isEmpty()){
            Log.incomingRefboxMsg.warn("Nachricht " + event.toString() + " konnte an niemanden übermittelt werden!");
        }
    }

    @Override
    public void publish(Subscriber subscriber) {
        subscriber.onReceive(lastEvent);
    }

    @Override
    public void subscribe(Subscriber subscriber) {
        subscriberList.add(subscriber);
        if (lastEvent != null){
            publish(subscriber);
            Log.incomingRefboxMsg.info("Nachricht: " + lastEvent.toString() + "wurde nachträglich an " + subscriber.getClass() + " übermittelt");
        }
    }

    @Override
    public void unsubscribe(Subscriber subscriber) {
        subscriberList.remove(subscriber);
    }
}
