package com.robotino.communication.refbox.receive;

import com.grips.refbox.RefboxHandler;
import com.robotino.game.Order;
import com.robotino.helperClass.Log;
import org.robocup_logistics.llsf_msgs.OrderInfoProtos;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;
/**
 * Wertet der empfangenen Order von der Refbox aus
 */
public class OrderInfo {
    final Queue<Integer> producingQueue = new LinkedList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9)); // Reihenfolge der abzuarbeitende Orders nach dem index
    final RefboxHandler handler;
    final RingInfo ringInfo;
    final List<Integer> orderIds = new ArrayList<>();
    private boolean onceTime = true;


    public OrderInfo(RefboxHandler handler, RingInfo ringInfo){
        this.handler = handler;
        this.ringInfo = ringInfo;
    }

    public void startReceiveMsg() {

        Consumer<OrderInfoProtos.OrderInfo> orderInfo = incomingOrder -> {
            for (int i = 0; i < incomingOrder.getOrdersCount(); i++) {
                OrderInfoProtos.Order order = incomingOrder.getOrders(i);
                try {
                    prep(order);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        handler.setOrderInfoCallback(orderInfo);
    }
    private void prep(OrderInfoProtos.Order order) throws InterruptedException {
            if(!producingQueue.isEmpty()){
                if (order.getId() == producingQueue.peek()){ //counter
                    producingQueue.poll();
                    if(! order.getCompetitive()){ // Filter alle Competitive Orders → wer schneller den Job erledigt hat von den beiden Teams bekommt punkte
                        if(! orderIds.contains(order.getId())){ // Wen die Order schon hinzugefügt wurde nicht nochmal hinzufügen
                            orderIds.add(order.getId());
                            if(order.getQuantityRequested() > 1){
                                addOrder(order); // Fügt die Order zweimal hinzu
                                Log.incomingRefboxMsg.info("Add Order: " + order);
                            }
                            addOrder(order);
                            Log.incomingRefboxMsg.info("Add Order: " + order);
                        }
                    }
                }

                if(onceTime){
                    onceTime = false;
                    ringInfo.startReceiveMsg(); // Starte des Empfanges der RingInfo nachricht
                }
                Thread.sleep(2000);
            }
    }

    private void addOrder(OrderInfoProtos.Order order){
        new Order(
                order.getId(),
                order.getBaseColor().toString(),
                order.getCapColor().toString(),
                order.getRingColorsList(),
                order.getComplexity().toString(),
                order.getQuantityRequested(),
                order.getDeliveryGate(),
                order.getDeliveryPeriodBegin(),
                order.getDeliveryPeriodEnd()
        );
    }
}
