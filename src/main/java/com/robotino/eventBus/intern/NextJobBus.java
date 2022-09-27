package com.robotino.eventBus.intern;


import com.robotino.eventBus.Bus;
import com.robotino.eventBus.Event;
import com.robotino.eventBus.Subscriber;

import java.util.*;

public class NextJobBus implements Bus {

    private static final NextJobBus NEXT_JOB_BUS_INSTANCE = new NextJobBus();
    private final List<Subscriber> subscriberList = new LinkedList<>();

    private NextJobBus(){}
    public static NextJobBus getInstance() {
        return NEXT_JOB_BUS_INSTANCE;
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
