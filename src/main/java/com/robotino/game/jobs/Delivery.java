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
import com.robotino.helperClass.Log;
import com.robotino.logistics.Station;
import com.robotino.logistics.Stations.CapStation1;
import com.robotino.logistics.Stations.CapStation2;
import com.robotino.logistics.Stations.DeliveryStation;
import com.robotino.robo.Robo;

public class Delivery implements Executable, Subscriber {

    private final Robo robo;
    private Station pickUpStation = null; // CP1 CP2
    private final int oderId;
    private final int deliveryGate;
    private int counter = 0;
    private boolean onceTime = true;

    public Delivery(Robo robo, Station pickUpStation, int oderId, int deliveryGate){
        this.robo = robo;
        if(pickUpStation instanceof CapStation1 capStation1){
            this.pickUpStation = capStation1;
        }else if(pickUpStation instanceof  CapStation2 capStation2){
            this.pickUpStation = capStation2;
        }
        Log.jobCreator.info(getJobName() + " - pickUpStation of Delivery is: " + pickUpStation);
        this.oderId = oderId;
        this.deliveryGate = deliveryGate;

    }

    @Override
    public void execute() {
        FinishDriveBus.getInstance().subscribe(this);
        GrippingStatusBus.getInstance().subscribe(this);
        Log.game.info(getJobName() + " - Start Delivery Job subscribe on FinishDriveBus and GrippingStatusBus");
        driveTo(pickUpStation, Station.Side.OUTPUT);
    }

    @Override
    public String getJobName() {
        return "Job-Delivery";
    }


    private void driveTo(Station station, Station.Side side){
        Log.game.info(getJobName() + " - Drive to: Station=" + station + ", Side=" + side);
        new DriveController(robo, station, side);
    }

    private void startGripping(GrippingMsg.MSG grippingType) {
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
        Log.game.info(getJobName() + " - Send Gripping Msg: grippingType=" + grippingType);
        new GrippingMsg(robo, grippingType,true);
        new GrippingMsg(robo, grippingType,false); // Braucht es das?
    }

    private void prep() {
        DeliveryStation deliveryStation = DeliveryStation.getInstance();
        boolean onceTime = true;
        // ToDo kann mit einem Scheduler gelöst werden
        while(deliveryStation.getState() != MachineClientUtils.MachineState.IDLE){
            if(onceTime){
                onceTime = false;
                Log.game.info(getJobName() + " - Waiting for Machine IDLE Status to prepare DeliveryStation");
            }
            Log.logger.info("Waiting of pickUpStation State: " + deliveryStation.getState() + " Station:  "  + deliveryStation);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        Log.game.info(getJobName() + " - Prepare DeliveryStation: deliveryGate=" + deliveryGate + ", orderId=" + oderId);
        DeliveryStation.getInstance().prep(deliveryGate, oderId);
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
                    case 1 -> driveTo(DeliveryStation.getInstance(), Station.Side.INPUT);
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
