
package com.robotino.drive;


import com.robotino.communication.message.toVisu.ObstacleMsg;
import com.robotino.communication.message.toVisu.RouteMsg;
import com.robotino.eventBus.*;
import com.robotino.eventBus.intern.FinishDriveBus;
import com.robotino.eventBus.intern.FinishDriveEvent;
import com.robotino.eventBus.refbox.MachineInfoBus;
import com.robotino.eventBus.refbox.MachineInfoEvent;
import com.robotino.helperClass.Data;
import com.robotino.logistics.Coordinate;
import com.robotino.logistics.Station;
import com.robotino.robo.Robo;

import java.util.LinkedList;
import java.util.List;

/**
 * Ist für das Fahren der Roboter zuständig.
 * Kann:
 *  - von Start Koordinate zu Ziel Koordinate fahren
 *  - von irgendwo zu einer Station fahren
 *  - meldet über den FinishDrive Bus zurück wen das Ziel erreicht wurde
 */
public class DriveController implements Subscriber {

    Robo robo; // Roboter der angesteuert wird
    Coordinate goal; // Ziel Koordinate
    Coordinate start;
    private static final List<Obstacle> obstacles = new LinkedList<>(); // Liste mit den Hindernissen
    private static boolean stationIsInObstacleList = false; // Verhindert das ein MachineEvent zweimal hinzugefügt wird

    static{
        if(Data.hasENTRY_WALL()){
            addEntryWall(); // fügt der Obstacles Liste die Entry-Wall hinzu
        }
    }

    /**
     * Fügt der Obstacles Liste die Entry-Wall hinzu
     */
    private static void addEntryWall() {
        Obstacle entryWall = new Obstacle();
        entryWall.generateWall();
        entryWall.setNameIdentifier("Wall");
        obstacles.add(entryWall);
    }

    /**
     * Wird nur von der DriveControllerGraspingChallenge verwendet da die Klasse die
     * Hindernisse selber instanziierten und nicht von der Refbox kommen
     * @deprecated
     */
    public DriveController(){}

    /**
     * Ist die Hauptklasse des drive Packets ruft alle Kassen im drive Packet automatisch auf.
     * Diese Klasse muss der Roboter übergeben werden der angesteuerte werden soll und
     * die Ziel Koordinate an die der Roboter hinfahren soll. Das Verwenden der Fahrbefehle wird im
     * Hintergrund automatisch gemacht. Auch wird der AStar verwendet.
     * @param robo der fahren soll
     * @param start von dort aus wird gestartet
     * @param goal nach dorthin soll der Roboter
     */
    public DriveController(Robo robo, Coordinate start, Coordinate goal){
        this.robo = robo;
        this.goal = goal.clone();
        this.start = start.clone();

        // Nur einmal wird auf den Bus subscribed da sich die Positionen der Stationen nicht verändern können
        if(!stationIsInObstacleList){
            MachineInfoBus.getInstance().subscribe(this);
        }else{
            startDrive(); // Startet das drive Program wen die Hindernisse schon bekannt sind
        }
    }

    public DriveController(Robo robo, Coordinate goal){
        this.robo = robo;
        this.start = robo.getDriveToCoordinate();
        this.goal = goal;

        // Nur einmal wird auf den Bus subscribed da sich die Positionen der Stationen nicht verändern können
        if(!stationIsInObstacleList){
            MachineInfoBus.getInstance().subscribe(this);
        }else{
            startDrive(); // Startet das drive Program wen die Hindernisse schon bekannt sind
        }
    }

    /**
     * Ist die Hauptklasse des drive Packets ruft alle Kassen im drive Packet automatisch auf.
     * Diese Klasse muss der Roboter übergeben werden der angesteuerte werden soll und
     * die Ziel Koordinate an die der Roboter hinfahren soll. Das Verwenden der Fahrbefehle wird im
     * Hintergrund automatisch gemacht. Auch wird der AStar verwendet.
     * @param robo der fahren soll
     * @param station die angefahren werden soll
     * @param side die angefahren werden soll
     */
    public DriveController(Robo robo, Station station, Station.Side side){
        this.robo = robo;
        this.start = robo.getDriveToCoordinate();
        this.goal = station.getSide(side);
        // Nur einmal wird auf den Bus subscribed da sich die Positionen der Stationen nicht verändern können
        if(!stationIsInObstacleList){
            MachineInfoBus.getInstance().subscribe(this);
        }else{
            startDrive(); // Startet das drive Program wen die Hindernisse schon bekannt sind
        }
    }

    /**
     * Startet das drive Program
     * - berechnet den schnellsten Weg
     * - und befährt den schnellsten Weg
     */
    private void startDrive() {
        if(this.start.getX() == this.goal.getX()&&
                this.start.getY() == this.goal.getY()){
            System.out.println("At Pos");

            FinishDriveEvent event = new FinishDriveEvent(this, this.robo);
            FinishDriveBus.getInstance().publish(event);
        }else{
            AStar aStar = new AStar(this.start, this.goal, obstacles); // Den schnellsten Weg zum Ziel berechnen
            Path path = aStar.findPath();
            new RouteMsg(this.robo, path);
            List<Coordinate> pathList = new LinkedList<>(path.getPath());
            new DriveHandler(robo, pathList, this); // Abfahren des Path
        }

    }

    public void startDrive(Robo robo, Coordinate start, Coordinate goal, List<Obstacle> obstacles){
        this.robo = robo;
        this.start = start;
        this.goal = goal;
        DriveController.obstacles.addAll(obstacles);
        if(stationIsInObstacleList){
            throw new IllegalArgumentException("Obstacles liste wurde schon befüllt und wird Probleme verursachen");
        }
        startDrive();
    }

    /**
     * Bekommt ein event aus dem DriveHandler wen die Zielkoordinate erreicht wurde.
     */
    protected void finishDrive(){
        FinishDriveEvent finishDriveEvent = new FinishDriveEvent(this, this.robo);
        FinishDriveBus.getInstance().publish(finishDriveEvent);
    }


    /**
     * Wird getriggert wen die Refbox die Stationen schickt
     * @param event MachineInfoEvent wird übergeben
     */
    @Override
    public void onReceive(Event event) {
        // Das Event darf nur einmal ankommen
        if(!stationIsInObstacleList){
            stationIsInObstacleList = true; // So kann kein Event mehr empfange werden
            MachineInfoEvent machineInfoEvent = (MachineInfoEvent) event;
            List<Station> stations = machineInfoEvent.getStations();

            // Fügt die Stationen zur obstacle List hinzu
            obstacles.clear();
            for(Station station : stations){
                 Obstacle obstacle = new Obstacle(station);
                 obstacle.setNameIdentifier(station.getName());
                 obstacles.add(obstacle);
            }

            obstacles.add(new Obstacle().generateWall()); // Adding EntryWall

            new ObstacleMsg(obstacles);
            MachineInfoBus.getInstance().unsubscribe(this);
            startDrive(); // Starte das Drive-Programm wen die Station zu ersten mall bekannt sind
        }
    }

    @Override
    public String toString() {
        return "DriveController{" +
                "robo=" + robo +
                ", goal=" + goal +
                ", start=" + start +
                '}';
    }
}
