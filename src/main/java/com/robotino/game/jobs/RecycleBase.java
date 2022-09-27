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
import com.robotino.logistics.Stations.RingStation1;
import com.robotino.logistics.Stations.RingStation2;
import com.robotino.robo.Robo;

public class RecycleBase implements Executable, Subscriber {

    final Station pickUpStation; // BS or CS1 or CS2
    final Station deliveryStation; // RS1 or RS2
    final Robo robo;
    private int counter = 0;
    private boolean onceTime;

    public RecycleBase(Robo robo, Station pickUpStation, Station deliveryStation){
        this.robo = robo;
        this.pickUpStation = pickUpStation;
        this.deliveryStation = deliveryStation;
        // Zahlt die Ringkosten virtuell
        if(deliveryStation instanceof RingStation1){
            RingStation1.getInstance().payment();
        }else if(deliveryStation instanceof RingStation2){
            RingStation2.getInstance().payment();
        }
    }

    
    @Override
    public void execute() {
        FinishDriveBus.getInstance().subscribe(this);
        GrippingStatusBus.getInstance().subscribe(this);
        Log.game.info(getJobName() + " - Start RecycleBase Job subscribe on FinishDriveBus and GrippingStatusBus");

        driveTo(pickUpStation, Station.Side.OUTPUT);
    }

    @Override
    public String getJobName() {
        return "RecycleBase-Job";
    }

    private void driveTo(Station station, Station.Side side) {
        Log.game.info(getJobName() + " - Drive to: Station=" + station + ", Side=" + side);
        new DriveController(robo, station, side);
    }

    private void startGripping(GrippingMsg.MSG grippingType) {
        Log.game.info(getJobName() + " - puckUPStation State: " + pickUpStation.getState());
        if(GrippingMsg.MSG.OUTPUT_GRIPPING == grippingType){
            // ToDo kann mit einem Scheduler gelöst werden
            while(MachineClientUtils.MachineState.READY_AT_OUTPUT != pickUpStation.getState() &&
                    MachineClientUtils.MachineState.IDLE != pickUpStation.getState()){
                Log.game.info(getJobName() + " - puckUPStation State: " + pickUpStation.getState());
                if(onceTime){
                    Log.game.info(getJobName() + " - Waiting of output gripping ready on Station: " + pickUpStation);
                    onceTime = false;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.game.info(getJobName() + " - Send Gripping Msg: grippingType=" + grippingType);
        new GrippingMsg(robo, grippingType, true);
        new GrippingMsg(robo, grippingType, false);
    }



    @Override
    public void onReceive(Event event) {
        //System.out.println("DelEvent" + event + "Counter: " + counter);
        if(event instanceof FinishDriveEvent){
            Log.game.info(getJobName() + " - Incoming FinishDriveEvent");
            switch (counter){
                case 0 -> {
                    counter = 1;
                    startGripping(GrippingMsg.MSG.OUTPUT_GRIPPING);
                }
                case 1 -> {
                    counter = 2;
                    startGripping(GrippingMsg.MSG.SLIDE_GRIPPING);

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
                        Log.game.info(getJobName() + " - Finish!");
                        NextJobBus.getInstance().publish(new NextJobEvent(this));
                    }
                    default -> throw new IllegalArgumentException("Status gibt es nicht: " + counter);
                }
            }
        }
    }
}