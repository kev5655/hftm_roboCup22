package com.robotino.eventBus.intern;

import com.robotino.eventBus.Bus;
import com.robotino.eventBus.Event;
import com.robotino.eventBus.Subscriber;

import java.util.LinkedList;
import java.util.List;

public class RingInfoBus implements Bus {

    private static final RingInfoBus RING_INFO_BUS_INSTANCE = new RingInfoBus();
    private final List<Subscriber> subscriberList = new LinkedList<>();

    private RingInfoBus(){}
    public static RingInfoBus getInstance(){
        return RING_INFO_BUS_INSTANCE;
    }

    Event lastEvent = null;

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
        }
    }

    @Override
    public void unsubscribe(Subscriber subscriber) {
        subscriberList.remove(subscriber);
    }
}
