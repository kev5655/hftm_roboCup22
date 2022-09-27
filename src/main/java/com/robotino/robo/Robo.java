package com.robotino.robo;

import com.robotino.communication.message.toRobo.DriveMsg;
import com.robotino.communication.message.toRobo.StopProcessMsg;
import com.robotino.communication.refbox.send.BeaconSignal;
import com.robotino.eventBus.Event;
import com.robotino.eventBus.Subscriber;
import com.robotino.eventBus.mqtt.CurrentRoboPosBus;
import com.robotino.eventBus.mqtt.CurrentRoboPosEvent;
import com.robotino.helperClass.ConvertPosition;
import com.robotino.helperClass.Data;
import com.robotino.logistics.Field;
import com.robotino.logistics.Coordinate;

/**
 *
 * @author Kevin Zahn
 *  -Ausgeben die aktuelle Position
 *  -Ausgeben der Startposition
 *  - Die drive Funktion versendet automatisch Fahrbefehle
 */
public class Robo implements Subscriber {

    final ConvertPosition convert = new ConvertPosition();

    // Erster Roboter 0 zweiter 1 dritter 2 braucht es um in der Message zu wissen um welcher Roboter es get
    private final int number;
    private final Field startField;
    private Coordinate driveToCoordinate;
    private Coordinate originalCoordinate;

    private final int roboSpeed = Data.getROBO_SPEED();


    /**
     * @description Instanziiert ein Roboter er braucht ein Startfeld das eine Java Koordinate ist
     * @param number Roboternummer wird verwendet, um der Drive-Message die Roboternummer zu übergeben,
     *               so das die nachfolgenden Systeme wissen ume welchen Roboter es geht
     * @param startField Startfeld Objekt wird verwendet, um alle Umrechnungen zu tätigen, vom
     *                   Robo-Koordinaten-System auf das Java-Koordinaten-System
     */
    public Robo(int number, Field startField) {
        if(number < 1 || number > 3){
            throw new IllegalArgumentException("Ungültige Roboter Nummer: " + number + " muss zwischen 1 und 3 sein");
        }
        this.number = number;
        this.startField = startField;
        this.originalCoordinate = startField.getCoordinate();
        this.driveToCoordinate = startField.getCoordinate();
        CurrentRoboPosBus.getInstance().subscribe(this);
        BeaconSignal.getInstance().startSendingBeaconSignal(this);
    }

    /**
     * @description drive Methode ist dafür zuständig, dass der Roboter auf das übergebene Feld fährt.
     *              Diese Methode hat keine ausweiche mechanismen Implementiert
     * @param toField Feld Objekt dei der Roboter anfahren soll
     */
    public void drive(Field toField){
        updatePosition(toField.getCoordinate());
        Coordinate roboCoordinate = convert.gamefieldCoordinateToRoboCoordinate(toField.getCoordinate(), startField.getCoordinate());
        // Jede Nachricht die versendet wird weis um welchen Roboter es handelt
        new DriveMsg(this, roboCoordinate, roboSpeed);
    }

    public void drive(Coordinate coordinate){
        //System.out.println("Drive to " + coordinate.toString());
        updatePosition(coordinate);
        Coordinate roboCoordinate = convert.gamefieldCoordinateToRoboCoordinate(coordinate, startField.getCoordinate());
        // Jede Nachricht die versendet wird weis um welchen Roboter es handelt
        new DriveMsg(this, roboCoordinate, roboSpeed);
    }

    private void updatePosition(Coordinate currentField) {
        this.driveToCoordinate = currentField;
    }

    public void stop(){
        new StopProcessMsg(this, true);
    }
    public void start(){
        new StopProcessMsg(this, false);
    }

    @Override
    public void onReceive(Event event) {
        CurrentRoboPosEvent currentRoboPos = (CurrentRoboPosEvent) event;
        this.originalCoordinate = new Coordinate(currentRoboPos.getX(),
                        currentRoboPos.getY(),
                        currentRoboPos.getA());
    }

    /**
     * Koordinate wird nicht umgerechnet in eine Roboter Coordinate,
     * Koordinate wird direkt an den Roboter gesendet
     * Achtung currentCoordinate werden nicht aktualisiert
     * @param coordinate Koordinate de angefahren werden soll
     * @deprecated
     */
    public void driveFromRoboPosition(Coordinate coordinate){
        new DriveMsg(this, coordinate, roboSpeed);
    }


    public Coordinate getDriveToCoordinate() {
        return driveToCoordinate;
    }

    public double getCurrentX(){
        return driveToCoordinate.getX();
    }

    public int getNumber(){
        return number;
    }

    public double getCurrentY(){
        return driveToCoordinate.getY();
    }

    public double getCurrentA(){
        return driveToCoordinate.getA();
    }

    public Field getStartField(){
        return startField;
    }

    public Coordinate getStartCoordinate() {
        return startField.getCoordinate();
    }

    public Coordinate getOriginalCoordinate() { return originalCoordinate; }

    public double getOriginalX(){ return originalCoordinate.getX(); }

    public double getOriginalY(){ return originalCoordinate.getY(); }

    public double getOriginalA(){ return originalCoordinate.getA(); }

    @Override
    public String toString() {
        return "Robo{" +
                "number=" + number +
                ", startField=" + startField +
                ", currentField=" + driveToCoordinate +
                ", originalField=" + originalCoordinate +
                ", roboSpeed=" + roboSpeed +
                '}';
    }
}