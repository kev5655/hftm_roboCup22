package com.robotino.game.jobs;

import com.grips.model.teamserver.MachineClientUtils;
import com.robotino.drive.DriveController;
import com.robotino.eventBus.Event;
import com.robotino.eventBus.Subscriber;
import com.robotino.eventBus.intern.FinishDriveBus;
import com.robotino.eventBus.intern.FinishDriveEvent;
import com.robotino.eventBus.intern.NextJobBus;
import com.robotino.eventBus.intern.NextJobEvent;
import com.robotino.helperClass.Log;
import com.robotino.logistics.Station;
import com.robotino.logistics.Stations.BaseStation;
import com.robotino.robo.Robo;

public class PrepareBase implements Executable, Subscriber {

    private final MachineClientUtils.BaseColor baseColor;
    private final Robo robo;

    public PrepareBase(Robo robo,MachineClientUtils.BaseColor baseColor){
        this.robo = robo;
        this.baseColor = baseColor;
    }

    @Override
    public void execute() {
        FinishDriveBus.getInstance().subscribe(this);
        Log.game.info(getJobName() + " - Start PrepareBase Job subscribe on FinishDriveBus");
        Log.game.info(getJobName() + " - Drive to: Station=" + BaseStation.getInstance() + ", Side=" + Station.Side.OUTPUT);
        new DriveController(robo, BaseStation.getInstance(), Station.Side.OUTPUT);
    }

    @Override
    public String getJobName() {
        return "PrepareBase-Job";
    }

    @Override
    public void onReceive(Event event) {
        if(event instanceof FinishDriveEvent){
            Log.game.info(getJobName() + " - Incoming FinishDriveEvent");
            FinishDriveBus.getInstance().unsubscribe(this);
            Log.game.info(getJobName() + " - unsubscribe on FinishDriveBus");
            BaseStation baseStation = BaseStation.getInstance();
            boolean onceTime = true;
            while(baseStation.getState() != MachineClientUtils.MachineState.IDLE){
                if(onceTime){
                    onceTime = false;
                    Log.game.info("Waiting for Machine IDLE Status in Job PrepareBase for prepare BaseStation");
                }
                Log.logger.info("Waiting of pickUpStation State: " + baseStation.getState() + " Station:  "  + baseStation);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            Log.game.info(getJobName() + " - Prepare BaseStation: side=" + MachineClientUtils.MachineSide.Input + ", baseColor=" + this.baseColor);
            baseStation.prep(MachineClientUtils.MachineSide.Output, this.baseColor);
            Log.game.info(getJobName() + " - Finish!");
            NextJobBus.getInstance().publish(new NextJobEvent(this));
        }
    }
}
