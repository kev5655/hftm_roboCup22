package com.robotino.eventBus;

/**
 * @author Kevin Zahn
 * @description Interface für die Methoden publish, subscribe und unsubscribe
 *              für den EventBus.
 */

public interface Bus {

    void publish(Event event);

    /**
     * nur in der Bus-Klasse verwenden
     * @param subscriber subscriber der ein Event bekommt
     */
    void publish(Subscriber subscriber);

    void subscribe(Subscriber subscriber);

    void unsubscribe(Subscriber subscriber);
    
}