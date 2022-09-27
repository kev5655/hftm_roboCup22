package com.robotino.challenge;


import com.robotino.communication.message.toRobo.GrippingMsg;
import com.robotino.drive.DriveControllerGraspingChallenge;
import com.robotino.eventBus.Event;
import com.robotino.eventBus.Subscriber;
import com.robotino.eventBus.intern.FinishDriveBus;
import com.robotino.eventBus.intern.FinishDriveEvent;
import com.robotino.eventBus.mqtt.GrippingStatusBus;
import com.robotino.eventBus.mqtt.GrippingStatusEvent;
import com.robotino.eventBus.refbox.MachineInfoBus;
import com.robotino.eventBus.refbox.MachineInfoEvent;
import com.robotino.helperClass.ConsoleColors;
import com.robotino.logistics.Station;
import com.robotino.robo.Robo;

import java.util.*;

/**
 * @author Fabian Leuenberger
 * @description Grasping Challenge
 *
 * ACHTUNG FUNKTION IST NICHT MEHR GEWÄHRLEISTET
 *
 */
public class GraspingChallenge implements Subscriber {

    private final List<Robo> robos;
    private final Map<Robo, Station> map = new HashMap<>();
    private final int [] iterations;

    private int numberFinishedRobos = 0;

    private final int numbersOffHowManyRuns = 3;

    public GraspingChallenge(List<Robo> robos){
        this.robos = robos;
        iterations = new int[robos.size()];
        Arrays.fill(iterations, 0);
        MachineInfoBus.getInstance().subscribe(this);
        GrippingStatusBus.getInstance().subscribe(this);
        FinishDriveBus.getInstance().subscribe(this);
    }

    private void startDrive(Robo robo, Station station) {
        Station.Side side;

        if(iterations[robo.getNumber() - 1] % 2 == 0){
            side = Station.Side.OUTPUT;
        }else{
            side = Station.Side.INPUT;
        }

        new DriveControllerGraspingChallenge(robo, station, side, new LinkedList<>(map.values()));
    }

    private void startGripping(Robo robo){

        GrippingMsg.MSG grippingType;
        if(iterations[robo.getNumber() - 1] % 2 == 0){ // Immer bie geraden Zahlen wird der Burger gegriffen
            grippingType = GrippingMsg.MSG.OUTPUT_GRIPPING;
        }else{ // Immer bei ungeraden Zahlen wird der Burger platziert
            grippingType = GrippingMsg.MSG.INPUT_GRIPPING;
        }

        new GrippingMsg(robo, grippingType,true);

        iterations[robo.getNumber() - 1]++;

    }

    /**
     * Durch das MachineInfoEvent wird die Challenges gestartet.
     * Durch das FinishDriveEvent wird der Greifprozess gestartet.
     * Durch das GrippingStatusEvent wird der Fahrprozess gestartet.
     * @param event Kann ein MachineInfo event, ein FinishDrive event oder
     *              ein grippingStatus event sein
     */
    @Override
    public void onReceive(Event event) {
        if(event instanceof MachineInfoEvent machineInfoEvent){
            MachineInfoBus.getInstance().unsubscribe(this);


            for (int i = 0; i < robos.size(); i++) {
                map.put(robos.get(i), machineInfoEvent.getStations().get(i));
                startDrive(robos.get(i), machineInfoEvent.getStations().get(i));
            }

        } else if(event instanceof FinishDriveEvent finishDriveEvent){
            System.out.println("DriveDone");
            System.out.println("Robo Anzahl: " + robos.size());

            for(Robo robo : map.keySet()){
                int nr = robo.getNumber();
                //System.out.println("check if Done: " + iterations[nr - 1] + " For robo: " + robo.getNumber());
                if(nr == finishDriveEvent.getRobo().getNumber() && !(iterations[nr - 1] >= numbersOffHowManyRuns * 2)){
                    if(robo.getNumber() == finishDriveEvent.getRobo().getNumber()){
                            startGripping(robo);
                            break;
                    }
                }
                else {
                    numberFinishedRobos++;
                }
            }
            if (numberFinishedRobos == robos.size()) {
                System.out.println(ConsoleColors.GREEN_BACKGROUND + ConsoleColors.CYAN_BOLD_BRIGHT + "Finish Grasping Challenge" + ConsoleColors.RESET);
                System.exit(0);
            }
        }
        else if(event instanceof GrippingStatusEvent grippingStatusEvent){
            for(Robo robo : map.keySet()){
                    if(grippingStatusEvent.isGrippingCompleted()){
                        System.out.println("Grip Done "+grippingStatusEvent.isGrippingCompleted());
                        new GrippingMsg(robo, GrippingMsg.MSG.INPUT_GRIPPING,false);
                        new GrippingMsg(robo, GrippingMsg.MSG.OUTPUT_GRIPPING,false);
                        startDrive(robo, map.get(robo));
                    }
            }
        }
    }
}
