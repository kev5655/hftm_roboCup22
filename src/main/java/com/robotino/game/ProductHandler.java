package com.robotino.game;

import com.robotino.eventBus.Event;
import com.robotino.eventBus.Subscriber;
import com.robotino.eventBus.intern.NextJobBus;
import com.robotino.eventBus.intern.NextJobEvent;
import com.robotino.eventBus.intern.OrderBus;
import com.robotino.eventBus.intern.OrderEvent;
import com.robotino.game.jobs.Executable;
import com.robotino.helperClass.Log;
import com.robotino.robo.Robo;

import java.util.*;

/**
 * Handelt das Abarbeiten der JobListe.
 * Empfängt die Order
 */
public class ProductHandler implements Subscriber {

    private static final Queue<Order> orderList = new LinkedList<>();
    private Order activOrder;
    private static final Map<Order, List<Executable>> jobList = new HashMap();
    final Robo robo;
    private boolean activeJob = false;
    private int counter = 0;

    public ProductHandler(Robo robo){
        this.robo = robo;
        OrderBus.getInstance().subscribe(this);
        NextJobBus.getInstance().subscribe(this);
    }

    public void startProducing(){
        if(!orderList.isEmpty()){
            Order order = orderList.peek();
            List<Executable> jobs = jobList.get(order);

            if(activOrder != order){
                activOrder = order;
                Log.game.info("Start with Order: " + activOrder.getType());
            }

            if(counter <= jobs.size() - 1){
                Log.game.info("Job: [" + jobs.get(counter).getJobName() + "]");
                jobs.get(counter).execute();
            }else {
                Log.game.info("Finish Order:  [" + orderList.peek().getType() + "] done!");
                orderList.poll();
                counter = 0;
                startProducing();
            }
        }else{
            activeJob = false;
            Log.game.info("Finish all Orders: ");
            for(Order order : jobList.keySet()){
                Log.game.info("- [" + order.getType() + "]");
            }
        }
    }


    @Override
    public void onReceive(Event event) {
        if(event instanceof OrderEvent orderEvent){
            Order order =  orderEvent.getOrder();
            orderList.add(order);
            JobCreator jc = new JobCreator(order, robo);
            jobList.put(order, jc.getJobList());
            if (!activeJob) {
                activeJob = true;
                startProducing();
            }
        }else if(event instanceof NextJobEvent nextJobEvent){
            Log.game.info("Finish Job: " + nextJobEvent.getExecutableClass().getJobName());
            counter++;
            startProducing();
        }
    }
}
