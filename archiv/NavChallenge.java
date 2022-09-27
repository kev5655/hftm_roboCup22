package com.robotino.production;


import java.util.LinkedList;

import com.robotino.communication.mqtt.MqttMsgHandler;
//import com.robotino.drive.DrivePath;
import com.robotino.eventBus.*;
//import com.robotino.eventBus.RoboIsOnPosEvent;
import com.robotino.eventBus.intern.DriveToNextTargetBus;
import com.robotino.eventBus.intern.DriveToNextTargetEvent;
import com.robotino.eventBus.refbox.MachineInfoBus;
import com.robotino.eventBus.refbox.MachineInfoEvent;
import com.robotino.eventBus.refbox.NavigationRouteBus;
import com.robotino.eventBus.refbox.NavigationRouteEvent;
import com.robotino.helperClass.ConvertPosition;
import com.robotino.helperClass.Data;
import com.robotino.logistics.Coordinate;
import com.robotino.logistics.Gamefield;
import com.robotino.logistics.Stations.Station;
import com.robotino.robo.Robo;


public class NavChallenge implements Subscriber{

    private int pathCounter = 0; // Zählt die abgefahrene Positionen
    //private boolean finishChallenge = false;
    // Dieser Boolean ist zuständig, dass die NavigationRoute nur einmal geladen werden
    // und das der MqttHandler nur einmal gestartet wird.
    private boolean isFirstTime = true;
    // Setzt die Zeit, die der Roboter auf dem Feld warte, wen er eine Zone erreicht hat
    public static final int WAIT_TIME = 5500;

    private LinkedList<Coordinate> sortedCoordinateList;

    Robo robo;
    ConvertPosition convert = new ConvertPosition();

    public NavChallenge(){
        if(Data.getWHO_MANY_ROBO() == 1){ // Wen nur ein Roboter verwendet wird, wird ein Roboter instanziiert
            robo = new Robo(0, Data.getROBOS_START_FILDS().get(0));
        }
        // Abonniert oder subscribed die benötigten Eventbuse
        NavigationRouteBus.getInstance().subscribe(this);
        DriveToNextTargetBus.getInstance().subscribe(this);
        MachineInfoBus.getInstance().subscribe(this);


        Gamefield gf = Gamefield.getInstance();
        Station s1 = new Station("blockingStation1", "M_Z51", "idel", "idel", 0);
        Station s2 = new Station("blockingStation2", "M_Z41", "idel", "idel", 0);
        gf.addStation(s1);
        gf.addStation(s2);
    }

    /**
     * Wir während dem Betrieb mehrer male aufgerufen, bis alle Zonen abgefahren wurden
     * und zähl die abgefahrenen Positionen so das immer die nächste Zone angefahren wird.
     * Befühlt den Drive Algorithms mit zwei neuen Coordinate die angefahren werden sollen
     */
    private void loadTwoTargetPos(){
        if(pathCounter < sortedCoordinateList.size() - 1){
            LinkedList<Coordinate> twoCoords = new LinkedList<>();
            // Holt sich die nächsten zwei Zonen, die erste ist die aktuelle Zone,
            // die zweite Zone ist die zu anfahrende Zone
            twoCoords.add(sortedCoordinateList.get(pathCounter).clone());
            twoCoords.add(sortedCoordinateList.get(pathCounter + 1).clone());
            // Instanziiert ein neuen DrivePath der dan die Zonen abfährt
            //new DrivePath(robo, twoCoords);
        } else{
            //if(!finishChallenge){
                goToHome();
            //}else{
                System.out.println("NAVIGATION CHALLENGE FINISH");
                System.exit(0);
            //}
        }
    }

    /**
     * Wen die Navigation-Challenge fast fertig ist das heist die letzte Coordinate
     * angefahren wurde muss der Roboter wieder auf die Home Position fahren.
     * Die Funktion setzt eine boolean auf true der dan am Schluss die Challenge beendet
     */
    private void goToHome(){
        //finishChallenge = true;
        // Holt sich die letzte Coordinate die angefahren wurde
        Coordinate first = sortedCoordinateList.get(sortedCoordinateList.size() - 1);
        //Holt sich die roboter Start Coordinate
        Coordinate second = robo.getStartCoordinate();
        // Erzeugt die neue liste die der Roboter anfahren soll
        LinkedList<Coordinate> twoCoords = new LinkedList<>();
        twoCoords.add(first);
        twoCoords.add(second);
        //new DrivePath(robo, twoCoords);

        // Sendet den DrivePath ein Event, das eine neue Position angefahren werden soll
        //RoboIsOnPosEvent event = new RoboIsOnPosEvent();
        //RoboIsOnPosBus.getInstance().publish(event);
    }

    /**
     * Sortiert die Zone nach dem schnellsten Weg
     * @param coordinateList unsortierte Coordinate Objekt Liste
     * @return gibt die sortierte Coordinate Objekt Liste zurück
     */
    private LinkedList<Coordinate> sortListToFastestWay(LinkedList<Coordinate> coordinateList){
        LinkedList<Coordinate> fastWay = new LinkedList<>();

        double xStart = robo.getCurrentX();
        double yStart = robo.getCurrentY();

        while(coordinateList.size() > 0){
            int nrOfNextPoint = 0;
            double minS = 100000;
            for(int i = 0; i < coordinateList.size(); i++){
                double x = coordinateList.get(i).getX();
                double y = coordinateList.get(i).getY();
                // Diff muss immer Positiv sein
                double xDiff = Math.abs(Math.abs(xStart) - Math.abs(x)); // Differenz in X
                double yDiff = Math.abs(Math.abs(yStart) - Math.abs(y)); // Differenz in Y

                double tempMin = Math.sqrt(Math.pow(xDiff, 2) + Math.pow(yDiff, 2)); // Berechnet die Distanz vom StartPukt zum

                if(tempMin < minS){
                    minS = tempMin;
                    nrOfNextPoint = i;
                }
            }

            fastWay.add(coordinateList.get(nrOfNextPoint));
            xStart = coordinateList.get(nrOfNextPoint).getX();
            yStart = coordinateList.get(nrOfNextPoint).getY();
            coordinateList.remove(nrOfNextPoint);

        }
        /*for(int [] zon : fastWay) {
            System.out.println("x: " + zon[0] + " y: " + zon[1] + " a: " + zon[2]);
        }*/
        return fastWay;
    }

    /**
     * Bekommt vom Navigation-Challenge Eventbus eine Meldung wen die Zonen bekannt sind
     * oder bekommt ein Event vom DrivePath Eventbus wen die zu anfahrende Position erreicht wurde
     * oder bekommt ein Event vom MachineInfo Eventbus wen die Positionen der MPS Station bekannt sind
     * @param event mögliche Objekte DrivePathPosReachedEvent, NavigationRouteEvent, MachineInfoEvent
     */
    @Override
    public void onReceive(Event event) {
        if(event instanceof DriveToNextTargetEvent){
            eventDrivePathPosReached((DriveToNextTargetEvent)event);

        }else if (event instanceof NavigationRouteEvent){
            eventNavigationRoute((NavigationRouteEvent) event);

        }else if(event instanceof MachineInfoEvent){
            eventMachineInfo((MachineInfoEvent) event);
        }
    }

    /**
     * Diese Methode wird getriggert, wen der DrivePath-Algorithmus seine endgültige Position erreicht hat
     * @param e DrivePathPosReachedEvent
     */
    private void eventDrivePathPosReached(DriveToNextTargetEvent e){
                pathCounter ++;
            try {
                System.out.print("Robo Wait for: 5 sek");
                Thread.sleep(WAIT_TIME);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            System.out.println("  finish");
            loadTwoTargetPos();
            //Event event = new RoboIsOnPosEvent();
            //RoboIsOnPosBus.getInstance().publish(event);
        //}
    }

    /**
     * @description Sobald diese Methode getriggert wird, startet die NavChallenge
     *              Achtung darf nur einmal aufgerufen werden
     * @param e NavigationRouteEvent wird benötigt, um die Koordinaten von der Refbox
     *         entgegenzunehmen, sodass die Route berechnet werden kann
     */
    private void eventNavigationRoute(NavigationRouteEvent e){
        if(isFirstTime){ // Navigationsrouten dürfen nur einmal ankommen,
            isFirstTime = false;
            LinkedList<String> zoneList =  new LinkedList<String>(e.getZoneList());
            LinkedList<Coordinate> coordinateList = new LinkedList<>();

            System.out.println("Robo Start Pos x: " + robo.getCurrentX() + " y: " + robo.getCurrentY() + " a: " + robo.getCurrentA());
            System.out.println("Empfange zoneList in NavChallenge");

            for (String zone : zoneList) {
                Coordinate coordinate = convert.zoneToCoordinate(zone, 0);
                coordinateList.add(coordinate);
                //System.out.println("FieldNr: " +  filedNr + " x: " + pos[0] + " y: " + pos[1] + " a: " + pos[2]);
            }

            sortedCoordinateList = new LinkedList<Coordinate>(sortListToFastestWay(coordinateList));
            sortedCoordinateList.add(0, robo.getCurrentCoordinate());

            for(Coordinate pos : sortedCoordinateList){
                System.out.println(" x: " + pos.getX() + " y: " + pos.getY() + " a: " + pos.getA());
            }
            loadTwoTargetPos();
            // Starten den MqttMsgHandler so werden die Mqtt nachrichten empfangen
            Thread mqttThread = new Thread(MqttMsgHandler.getInstance(),"mqttHandler");
            mqttThread.start();
        }

    }

    private void eventMachineInfo(MachineInfoEvent e){
        for(Station station : e.getStations()){
            Gamefield.getInstance().addStation(station);
        }


    }

}
