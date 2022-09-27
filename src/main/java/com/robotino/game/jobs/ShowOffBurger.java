package com.robotino.game.jobs;


import com.robotino.communication.message.toRobo.GrippingMsg;
import com.robotino.communication.message.toRobo.StopProcessMsg;
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
import com.robotino.logistics.Coordinate;
import com.robotino.logistics.Station;
import com.robotino.robo.Robo;

// ToDo Klasse ist noch nicht fertig oder funktioniere noch nicht so wie sie sollte
//  muss aber evlt. auch nicht geändert werden. Denn aktuell sollte der Roboter
//  an seine start Postion fahren und dann das Produkt fallen lassen. Mit den Befehl
//  StopProcess soll der Roboter das Product fallen lassen, dieser Befell funktioniert
//  aber nicht mehr also lässt das Produkt nicht fallen sondern stop den gesamten
//  Prozess. Dies muss jedoch nicht mehr eingebaut werden den neu sollte der
//  Roboter das Product Deliveren also abgeben, jedoch muss das Rulebook noch angepasst
//  werden das der Roboter nach ein Productions Challenge das Produkt Deliveren
//  sollte. Wen dies so im Ruelbook festgelegt wurde kann man auch die Delivery Klasse
//  verwenden um das Produkt abzugeben.

public class ShowOffBurger implements Executable, Subscriber {

    final Robo robo;
    final Station deliveryStation;
    int counter = 0;

    public ShowOffBurger(Robo robo, Station deliveryStation){
        this.robo = robo;
        this.deliveryStation = deliveryStation;
    }

    @Override
    public void execute() {
        FinishDriveBus.getInstance().subscribe(this);
        GrippingStatusBus.getInstance().subscribe(this);
        driveTo(deliveryStation);
    }

    @Override
    public String getJobName() {
        return "ShowOffBurger";
    }

    private void driveTo(Station station) {
        Log.game.info(getJobName() + " - Drive to: Station=" + station + ", Side=" + Station.Side.OUTPUT);
        new DriveController(robo, station, Station.Side.OUTPUT);
    }

    private void startGripping() {
        Log.game.info(getJobName() + " - Send Gripping Msg: grippingType=" + GrippingMsg.MSG.OUTPUT_GRIPPING);
        new GrippingMsg(robo, GrippingMsg.MSG.OUTPUT_GRIPPING, true);
        new GrippingMsg(robo, GrippingMsg.MSG.OUTPUT_GRIPPING, false);
    }

    @Override
    public void onReceive(Event event) {
        if(event instanceof FinishDriveEvent){
            switch (counter){
                case 0 -> {
                    counter = 1;
                    startGripping();
                }
                case 1 -> {
                    new StopProcessMsg(robo, true);
                    FinishDriveBus.getInstance().unsubscribe(this);
                    GrippingStatusBus.getInstance().unsubscribe(this);
                    Log.game.info(getJobName() + " - unsubscribe on FinishDriveBus and GrippingStatusBus");
                    Log.game.info(getJobName() + " - Finish!");
                    NextJobBus.getInstance().publish(new NextJobEvent(this));
                }
                default -> throw new IllegalArgumentException("Status gibt es nicht: " + counter);

            }

        }else if(event instanceof GrippingStatusEvent grippingStatus){
            if(grippingStatus.isGrippingCompleted()){
                switch (counter){
                    case 1 -> new DriveController(robo, new Coordinate(-2.5, 0.5, 0));
                    default -> throw new IllegalArgumentException("Status gibt es nicht: " + counter);
                }
            }
        }
    }
}