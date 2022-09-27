package com.robotino.challenge;

import com.robotino.drive.DriveController;
import com.robotino.eventBus.Event;
import com.robotino.eventBus.Subscriber;
import com.robotino.eventBus.intern.FinishDriveBus;
import com.robotino.eventBus.intern.FinishDriveEvent;
import com.robotino.eventBus.refbox.NavigationRouteBus;
import com.robotino.eventBus.refbox.NavigationRouteEvent;
import com.robotino.helperClass.ConsoleColors;
import com.robotino.helperClass.ConvertPosition;
import com.robotino.helperClass.Log;
import com.robotino.logistics.Coordinate;
import com.robotino.robo.Robo;

import java.util.LinkedList;
import java.util.List;

/**
 * ACHTUNG FUNKTION IST NICHT MEHR GEWÄHRLEISTET
 */
public class NavChallenge implements Subscriber {

    private int pathCounter = 0;
    private boolean challengeFinished = false;
    private boolean first = true;
    public static final int WAIT_TIME = 5500; // Zeit die auf dem Feld gewartet wird bis nächste Koordinate angefahren wird.

   final Robo robo;
    private LinkedList<Coordinate>sortedCoordinateList;
    private final ConvertPosition convertPosition = new ConvertPosition();

    public NavChallenge(Robo r){
        robo = r;
        FinishDriveBus.getInstance().subscribe(this);
        NavigationRouteBus.getInstance().subscribe(this);

        // Warten auf Events
    }

    /**
     * Git den schnellste Path als LinkedList zurück. Achtung es git schnellere
     * Algorithmen die auch ein noch schnelleren Path berechnen doch für den
     * Anfang reicht es.
     * @param coordinates Alle Koordinate die angefahren werden müssen
     * @return Der schnellste Weg als LinkedList von Coordinate Objekte
     */
    private List<Coordinate> getFastestWay(List<Coordinate> coordinates){
        LinkedList<Coordinate> fastestWay = new LinkedList<>();

        double xStart = robo.getCurrentX();
        double yStart = robo.getCurrentY();

        while(coordinates.size() > 0){
            int nrOfNextPoint = 0;
            double minS = 100000;
            for(int i = 0; i < coordinates.size(); i++){
                double x = coordinates.get(i).getX();
                double y = coordinates.get(i).getY();

                double xDiff = Math.abs(Math.abs(xStart) - Math.abs(x));
                double yDiff = Math.abs(Math.abs(yStart) - Math.abs(y));

                double tempMin = Math.sqrt(Math.pow(xDiff, 2) + Math.pow(yDiff, 2));

                if(tempMin < minS){
                    minS = tempMin;
                    nrOfNextPoint = i;
                }
            }
            fastestWay.add(coordinates.get(nrOfNextPoint));
            xStart = coordinates.get(nrOfNextPoint).getX();
            yStart = coordinates.get(nrOfNextPoint).getY();
            coordinates.remove(nrOfNextPoint);
            Log.navigationChallenge.info("Sorted Navigation-Routh: " + fastestWay);
        }
        return fastestWay;
    }

    /**
     * Fährt die Nächste Koordinate an anhand des PathCounter oder kann die
     * Challenge beenden
     */
    private void goToCoordinate() {
        if (pathCounter < sortedCoordinateList.size() - 1) {
            LinkedList<Coordinate> nextTwoCords = new LinkedList<>();
            double x = sortedCoordinateList.get(pathCounter+1).getX();
            double y = sortedCoordinateList.get(pathCounter+1).getY();
            //TakeOut if test done
            int a = 0;
            Coordinate cord = new Coordinate(x,y,a);

            nextTwoCords.add(sortedCoordinateList.get(pathCounter).clone());
            nextTwoCords.add(sortedCoordinateList.get(pathCounter + 1).clone());

            new DriveController(robo, nextTwoCords.get(0), cord);
            Log.navigationChallenge.info("Target position is: " + convertPosition.coordinateToZone(sortedCoordinateList.get(pathCounter)));
            Log.navigationChallenge.info("Driving to: " + convertPosition.coordinateToZone(nextTwoCords.get(1)));
        } else {
            if (!challengeFinished) {
                goHome();
            } else{
                FinishDriveBus.getInstance().unsubscribe(this);
                Log.navigationChallenge.info("Navigation-Challenge finished.");
                System.out.println(ConsoleColors.CYAN_UNDERLINED + ConsoleColors.RED_BACKGROUND + "Nav Challenge Finished" + ConsoleColors.RESET);
                System.exit(0);}
        }
    }

    /**
     * Fährt zur Startposition
     */
    private void goHome(){
        challengeFinished = true;
        Coordinate first = sortedCoordinateList.get(sortedCoordinateList.size() - 1);
        Coordinate second = robo.getStartCoordinate();

        LinkedList<Coordinate> nextTwoCoords = new LinkedList<>();
        nextTwoCoords.add(first);
        nextTwoCoords.add(second);

        new DriveController(robo, nextTwoCoords.get(0), nextTwoCoords.get(1));
        Log.navigationChallenge.info("Reached all positions, driving home to: " + convertPosition.coordinateToZone(nextTwoCoords.get(1)));
    }

    private void eventFinishedDrive() {
        pathCounter++;
        try {
            Thread.sleep(WAIT_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        goToCoordinate();
    }

    private void eventNavigationRout(NavigationRouteEvent event){
        if (first){
            first = false;

            LinkedList<Coordinate> listOfCoords = new LinkedList<>();
            LinkedList<String> listOfZones = new LinkedList<>(event.getZoneList());

            for (String zone : listOfZones){
                Coordinate coordinate = convertPosition.zoneToCoordinate(zone, 0);
                listOfCoords.add(coordinate);
            }

            sortedCoordinateList = new LinkedList<>(getFastestWay(listOfCoords));
            sortedCoordinateList.add(0, robo.getDriveToCoordinate());

            goToCoordinate();

            NavigationRouteBus.getInstance().unsubscribe(this);
        }
    }

    /**
     * FinishDriveEvent kommt von eigenen DriveController und wird nicht von ausserhalb des Programms getriggert
     * NavigationsRouteEvent kommt von der Refbox und starte das Abfahren der Punkte
     */
    @Override
    public void onReceive(Event event) {
        if (event instanceof FinishDriveEvent) {
            eventFinishedDrive();
            Log.navigationChallenge.info("Reached position: " + convertPosition.coordinateToZone(robo.getStartCoordinate()));

        } else if (event instanceof NavigationRouteEvent navRouteEvent){
            eventNavigationRout(navRouteEvent);
            Log.navigationChallenge.info("Got Navigation Routh:" + ((NavigationRouteEvent) event).getZoneList());
        }
    }
}