package com.robotino.game.jobs;

import com.grips.model.teamserver.MachineClientUtils;
import com.robotino.communication.message.toRobo.GrippingMsg;
import com.robotino.drive.DriveController;
import com.robotino.eventBus.Event;
import com.robotino.eventBus.Subscriber;
import com.robotino.eventBus.intern.FinishDriveBus;
import com.robotino.eventBus.intern.FinishDriveEvent;
import com.robotino.eventBus.intern.NextJobBus;
import com.robotino.eventBus.intern.NextJobEvent;
import com.robotino.eventBus.mqtt.GrippingStatusBus;
import com.robotino.eventBus.mqtt.GrippingStatusEvent;
import com.robotino.helperClass.Data;
import com.robotino.helperClass.Log;
import com.robotino.logistics.Station;
import com.robotino.logistics.Stations.RingStation1;
import com.robotino.logistics.Stations.RingStation2;
import com.robotino.robo.Robo;

public class PutRingOnBurger implements Executable, Subscriber {
    private int counter = 0;

    private final Robo robo;
    private final Station pickUpStation;
    private Station deliveryStation;
    private final MachineClientUtils.RingColor ringColor;
    private boolean onceTime;

    public PutRingOnBurger(Robo robo, Station pickUpStation, MachineClientUtils.RingColor ringColor){
        this.robo = robo;
        this.pickUpStation = pickUpStation;
        this.ringColor = ringColor;
        if(Data.hasRS1_STATION() || Data.isGAME()){
            if(RingStation1.getInstance().hasRingColor(ringColor)){
                RingStation1 ringStation1 = RingStation1.getInstance();
                deliveryStation = ringStation1;
                int cost = ringStation1.getRingCost(ringColor);
                ringStation1.payout(cost); // Zieht die Ringkosten für die RingFarbe ab
                Log.jobCreator.info(getJobName() +  " - Pay RingCost of Station: " + ringStation1 + " the costs are: " + cost);
            }
        }
        if(Data.hasRS2_STATION() || Data.isGAME()){
            if (RingStation2.getInstance().hasRingColor(ringColor)){
                RingStation2 ringStation2 = RingStation2.getInstance();
                deliveryStation = ringStation2;
                int cost = ringStation2.getRingCost(ringColor);
                ringStation2.payout(cost); // Zieht die Ringkosten für die RingFarbe ab
                Log.jobCreator.info(getJobName() +  " - Pay RingCost of Station: " + ringStation2 + " the costs are: " + cost);
            }
        }
        Log.jobCreator.info(getJobName() +  " - deliveryStation of PutRingOnBurger is: " + deliveryStation);

    }

    @Override
    public void execute() {
        FinishDriveBus.getInstance().subscribe(this);
        GrippingStatusBus.getInstance().subscribe(this);
        Log.game.info(getJobName() + " - Start PutRingOnBurger Job subscribe on FinishDriveBus and GrippingStatusBus");
        driveTo(pickUpStation, Station.Side.OUTPUT);
    }

    @Override
    public String getJobName() {
        return "PutRingOnBurger-Job";
    }

    private void driveTo(Station station, Station.Side side) {
        Log.game.info(getJobName() + " - Drive to: Station=" + station + ", Side=" + side);
        new DriveController(robo, station, side);
    }

    private void startGripping(GrippingMsg.MSG grippingType) {
        Log.game.info(getJobName() + " - Send Gripping Msg: grippingType=" + grippingType);
        if(GrippingMsg.MSG.OUTPUT_GRIPPING == grippingType){
            // ToDo kann mit einem Scheduler gelöst werden
            while(MachineClientUtils.MachineState.READY_AT_OUTPUT != pickUpStation.getState() &&
                    MachineClientUtils.MachineState.IDLE != pickUpStation.getState()){
                if(onceTime){
                    Log.game.info(getJobName() + " - Waiting of output gripping ready on Station: " + pickUpStation);
                    onceTime = false;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        new GrippingMsg(robo, grippingType,true);
        new GrippingMsg(robo, grippingType,false); // Braucht es das?
    }

    private void prep() {
        if(deliveryStation instanceof RingStation1 ringStation1){
            boolean onceTime = true;
            // ToDo kann mit einem Scheduler gelöst werden
            while(ringStation1.getState() != MachineClientUtils.MachineState.IDLE){
                if(onceTime){
                    onceTime = false;
                    Log.game.info(getJobName() + " - Waiting for Machine IDLE Status in Job PutRingOnBurger for prepare RingStation1");
                }
                Log.logger.info("Waiting of pickUpStation State: " + ringStation1.getState() + " Station:  "  + ringStation1);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            Log.game.info(getJobName() + " - Prepare RingStation1: ringColor=" + ringColor);
            ringStation1.prep(ringColor);
        }
        else if (deliveryStation instanceof RingStation2 ringStation2){
            boolean onceTime = true;
            // ToDo kann mit einem Scheduler gelöst werden
            while(ringStation2.getState() != MachineClientUtils.MachineState.IDLE){
                if(onceTime){
                    onceTime = false;
                    Log.game.info(getJobName() + " - Waiting for Machine IDLE Status in Job PutRingOnBurger for prepare RingStation2");
                }
                Log.logger.info("Waiting of pickUpStation State: " + ringStation2.getState() + " Station:  "  + ringStation2);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            ringStation2.prep(ringColor);
            Log.game.info(getJobName() + " - Prepare RingStation1: ringColor=" + ringColor);
        }
    }

    @Override
    public void onReceive(Event event) {
        if(event instanceof FinishDriveEvent){
            Log.game.info(getJobName() + " - Incoming FinishDriveEvent");
            switch (counter){
                case 0 -> {
                    this.counter = 1;
                    startGripping(GrippingMsg.MSG.OUTPUT_GRIPPING);
                }
                case 1 -> {
                    this.counter = 2;
                    startGripping(GrippingMsg.MSG.INPUT_GRIPPING);
                }
                default -> throw new IllegalArgumentException("Status gibt es nicht: " + counter);

            }

        }else if (event instanceof GrippingStatusEvent grippingStatus) {
            Log.game.info(getJobName() + " - Incoming GrippingStatusEvent: grippingStatus=" + grippingStatus.isGrippingCompleted());
            if(grippingStatus.isGrippingCompleted()){
                switch (counter){
                    case 1 -> driveTo(deliveryStation, Station.Side.INPUT);
                    case 2 -> {
                        FinishDriveBus.getInstance().unsubscribe(this);
                        GrippingStatusBus.getInstance().unsubscribe(this);
                        Log.game.info(getJobName() + " - unsubscribe on FinishDriveBus and GrippingStatusBus");
                        prep();
                        Log.game.info(getJobName() + " - Finish!");
                        NextJobBus.getInstance().publish(new NextJobEvent(this));
                    }
                    default -> throw new IllegalArgumentException("Status gibt es nicht: " + counter);
                }
            }
        }
    }
}