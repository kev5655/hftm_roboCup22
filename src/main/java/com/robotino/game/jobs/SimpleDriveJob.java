package com.robotino.game.jobs;

import com.robotino.drive.DriveController;
import com.robotino.eventBus.Event;
import com.robotino.eventBus.Subscriber;
import com.robotino.eventBus.intern.FinishDriveBus;
import com.robotino.eventBus.intern.NextJobBus;
import com.robotino.eventBus.intern.NextJobEvent;
import com.robotino.logistics.Station;
import com.robotino.robo.Robo;

/**
 * Wird nicht mehr verwendet
 */
public class SimpleDriveJob implements Executable, Subscriber {

    final Robo robo;
    final Station station;
    final Station.Side side;

    public SimpleDriveJob(Robo robo, Station station, Station.Side side){
        this.robo = robo;
        this.station = station;
        this.side = side;
    }

    @Override
    public void execute() {
        FinishDriveBus.getInstance().subscribe(this);
        new DriveController(robo, station, side);

    }

    @Override
    public String getJobName() {
        return "SimpleDrive-Job";
    }

    @Override
    public void onReceive(Event event) {
        FinishDriveBus.getInstance().unsubscribe(this);
        NextJobBus.getInstance().publish(new NextJobEvent(this));
    }
}