package com.robotino.eventBus.intern;

import com.robotino.eventBus.Event;
import com.robotino.game.Order;
/**
 * Daten Klasse für den OrderBus
 */
public class OrderEvent extends Event {

    final Order order;

    public OrderEvent(Order order){
        super();
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }

    @Override
    public String toString() {
        return "OrderEvent{" +
                "order=" + order +
                '}';
    }
}
