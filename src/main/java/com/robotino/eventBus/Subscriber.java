package com.robotino.eventBus;

/**
 * @author Kevin Zahn
 * @description Interface für die Methode onReceive für den EventBus,
 */

public interface Subscriber {
    void onReceive(Event event);
}