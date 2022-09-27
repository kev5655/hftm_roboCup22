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
import com.robotino.game.Order;
import com.robotino.helperClass.Data;
import com.robotino.helperClass.Log;
import com.robotino.logistics.Station;
import com.robotino.logistics.Stations.CapStation1;
import com.robotino.logistics.Stations.CapStation2;
import com.robotino.robo.Robo;


public class PutCapOnBurger implements Executable, Subscriber {

    private int counter = 0;
    private final Robo robo;
    private final Station pickUpStation; // BS RS1 RS2
    private Station deliveryStation; // CS1 or CS2
    public boolean onceTime;

    public PutCapOnBurger(Robo robo, Station pickUpStation, Order.CapColor capColor){
        this.robo = robo;
        this.pickUpStation = pickUpStation;
        if(Data.hasCS1_STATION() || Data.isGAME()){
            if(CapStation1.getInstance().hasCapColor(capColor)){
                deliveryStation = CapStation1.getInstance();
                ((CapStation1)deliveryStation).removeCapInStock();
            }
        }
        if(Data.hasCS2_STATION() || Data.isGAME()){
            if (CapStation2.getInstance().hasCapColor(capColor)){
                deliveryStation = CapStation2.getInstance();
                ((CapStation2)deliveryStation).removeCapInStock();
            }
        }

        Log.jobCreator.info("Delivery Station of PutCapOnBurger is: " + deliveryStation);
    }

    public static Station getBurgerPickUpPosition(Order.CapColor capColor){
        if(Data.hasCS1_STATION() || Data.isGAME()){
            if(CapStation1.getInstance().hasCapColor(capColor)){
                return CapStation1.getInstance();
            }
        }
        if(Data.hasCS2_STATION() || Data.isGAME()){
            if (CapStation2.getInstance().hasCapColor(capColor)){
                return CapStation2.getInstance();
            }
        }

        return null;
    }

    @Override
    public void execute() {
        FinishDriveBus.getInstance().subscribe(this);
        GrippingStatusBus.getInstance().subscribe(this);
        Log.game.info(getJobName() + " - Start PutCapOnBurger Job subscribe on FinishDriveBus and GrippingStatusBus");
        driveTo(pickUpStation, Station.Side.OUTPUT);
    }

    @Override
    public String getJobName() {
        return "PutCapOnBurger-Job";
    }

    public void driveTo(Station station, Station.Side side){
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
                    throw new RuntimeException(e);
                }
            }
        }
        new GrippingMsg(robo, grippingType,true);
        new GrippingMsg(robo, grippingType,false); // Braucht es das?
    }

    private void prep() {
        if(deliveryStation instanceof CapStation1 capStation1){
            boolean onceTime = true;
            // ToDo kann mit einem Scheduler gelöst werden
            while(capStation1.getState() != MachineClientUtils.MachineState.IDLE){
                if(onceTime){
                    onceTime = false;
                    Log.game.info(getJobName() + " - Waiting for Machine IDLE Status in Job PutCapOnBurger for prepare capStation1");
                }
                Log.logger.info("Waiting of pickUpStation State: " + capStation1.getState() + " Station:  "  + capStation1);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            Log.game.info(getJobName() + " - Prepare CapStation1: operation=" + MachineClientUtils.CSOp.MOUNT_CAP);
            capStation1.prep(MachineClientUtils.CSOp.MOUNT_CAP);



        }else if(deliveryStation instanceof  CapStation2 capStation2){
            boolean onceTime = true;
            // ToDo kann mit einem Scheduler gelöst werden
            while(capStation2.getState() != MachineClientUtils.MachineState.IDLE){
                if(onceTime){
                    onceTime = false;
                    Log.game.info(getJobName() + " - Waiting for Machine IDLE Status in Job PutCapOnBurger for prepare capStation2");
                }
                Log.logger.info("Waiting of pickUpStation State: " + capStation2.getState() + " Station:  "  + capStation2);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            Log.game.info(getJobName() + " - Prepare CapStation2: operation=" + MachineClientUtils.CSOp.MOUNT_CAP);
            capStation2.prep(MachineClientUtils.CSOp.MOUNT_CAP);

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