package com.robotino.helperClass;

import com.robotino.logistics.Coordinate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Kevin Zahn
 * @description Da mit verschiedenen Koordinatensystemen gearbeitet wird, müssen diese
 *              Konvertiert werden. Bei den Koordinatensystemen handelt es sich um das
 *              Koordinatensystem des Robotinos, welches in 1000er Schritten arbeitet
 *              (von Feld zu Fled) und
 *              das System der Refbox welches in 1er Schritten arbeitet. In dieser Klasse
 *              werden diese Systeme auf das jeweils andere Umgerechnet. Auch wird das
 *              AStar Grid Koordinate System umgewandelt.
 */

public class ConvertPosition {

    private final int fieldSize = Data.getFIELD_SIZE_IN_MM(); // Bestimmt die Feldgrösse in mm
    private final double halfJavaFieldSize = (double)Data.getFIELD_SIZE_IN_M() / 2; // Ist ein halbes Feld in m bezogen auf das Java-Koordinaten-System

    /**
     * Berechnet die Fahrkoordinate aus die der Roboter fahren mus, um die JavaCoordinate zu erreichen
     * @param javaCoordinate Coordinate wohin der Roboter fahren soll: Example {0.5, 2.5, 0} ist FeldNr C_Z13
     * @param roboStartCoordinate Coordinate wo der Roboter gestartet ist: Example {-4.5, 0.5, 0} ist FeldNr M_Z51
     * @return Gibt ein Coordinate Objekt zurück mit den Distanzen die gefahren werden müssen aus der Sicht vom Roboter
     */
    public Coordinate gamefieldCoordinateToRoboCoordinate(Coordinate javaCoordinate, Coordinate roboStartCoordinate){

        double x = (roboStartCoordinate.getX() - javaCoordinate.getX()) * fieldSize;
        double y = Math.abs((roboStartCoordinate.getY() - javaCoordinate.getY()) * fieldSize);
        double a = javaCoordinate.getA();

        return new Coordinate(x, y, a);
    }

    /**
     * Wandel eine Roboter-Koordinate in eine Java-Position um, die Roboter-Koordinate ist bezogen auf die
     * Startkoordinate des Roboters
     * @param roboPosition Coordinate wo der Roboter aktuell steht: Example {1000, 1000, 0}
     * @param roboStartPosition Coordinate wo der Roboter startete: Example {-4.5, 0.5, 0}
     * @return Gibt das umgerechnete Coordinate Objekt zurück: Example {-5.5, 1.5, 0}
     */
    public Coordinate roboCoordinateToGamefieldCoordinate(Coordinate roboPosition,
                                                          Coordinate roboStartPosition){
        //if(! roboPosition.isAnRoboCoordinate()) System.err.println("roboPosition is not a RoboterCoordinate: " + roboPosition.toString());
        double x = roboStartPosition.getX() - roboPosition.getX()  / fieldSize;
        double y = roboStartPosition.getY() + roboPosition.getY() / fieldSize ;
        return new Coordinate(x, y,  roboPosition.getA());
    }

    /**
     * Konvertiert eine Zone in eine Koordinate um
     * @param zone Zone die umgewandelt werden soll: Example {M_Z51}
     * @param rotation da eine Zone keine Rotation hat, wird diese hier übergeben: Example {180}
     * @return Gibt das konvertierte Coordinate Objekt zurück: Example {-4.5, 0.5, 180}
     */
    public Coordinate zoneToCoordinate(String zone, int rotation) {
        // Spliten bei "_Z" die Zone auf und löscht dies gerade so kann auf die Feldnummer und auf M oder C zugegriffen werden
        String[] s = zone.split("_Z");
        String []  a =  s[1].split("", 2);
        List<String> b =  Arrays.asList(a);
        // Splitet die erste Stelle des s Arrays in alle Einzelteil so kann auf M oder C zugegriffen werden
        List<String> number = new ArrayList<>(b);

        // Rechnet die erste Stelle der Feldnummer in eine Koordinate um
        double x = Double.parseDouble(number.get(number.size() - 2)) - halfJavaFieldSize;
        // Rechnet die zweite Stelle der Feldnummer in eine Koordinate um
        double y = Double.parseDouble(number.get(number.size() - 1)) - halfJavaFieldSize;

        if (s[0].equals("M")) x *= -1; // Wen die Zone auf der Magenta Seite ist mus die x Koordinate negativ sein
        else if (!s[0].equals("C")) // Falls die Zone an der ersten stelle, nicht C ist, wird ein Fehler ausgegeben
            System.err.println("Error in ConverterPosition zone: " + zone + " is not correctly");

        return new Coordinate(x, y, rotation);
    }

    /**
     * Wandel eine Koordinate in eine Zone um, die z.B. ein Field Objekt brauch, um sich zu erzeugen
     * @param coordinate Coordinate die umgewandelt werden soll: Example {0.5, 0.5, 0}
     * @return Git die Zone zurück: Example {C_Z11}
     */
    public String coordinateToZone(Coordinate coordinate){

        String zone;
        String xNr;
        if(coordinate.getX() > 0)   zone = "C_Z"; // Wen x positiv dann C_Z Field
        else                        zone = "M_Z"; // Wen x negativ dann M_Z Field

        if(coordinate.getX() > 0)   xNr = String.valueOf(Math.round(coordinate.getX() + halfJavaFieldSize)); // Wen x positiv ist dann + 0.5
        else                        xNr = String.valueOf(Math.round(coordinate.getX() - halfJavaFieldSize)); // Wen x negativ ist dann - 0.5

        String yNr = String.valueOf(Math.round(coordinate.getY() + halfJavaFieldSize));
        // Entfernt das - vor der x Koordinate und setzt die Zone zusammen
        zone += xNr.replace("-", "") + yNr;

        return zone;
    }

    /**
     * Konvertiert eine Java Coordinate ein index das der AStar Algorithmus brauch
     * @param cord Coordinate die umgewandelt wird in ein Grid Index
     * @return Git eine AStar Koordinate zurück
     */
    public int [] gamefieldCoordinateToAStarIndex(Coordinate cord){
        int M_TO_CM = Data.getFACTOR_M_TO_CM();
        int GRID_SIZE = Data.getGRID_SIZE();
        int X_MAX_INDEX = Data.getMAX_X_INDEX();
        int Y_MAX_INDEX = Data.getMAX_Y_INDEX();
        int halfField = Data.getFIELD_DIVISOR_IS_FIELD_BIG();

        int x = (int) (cord.getX() * M_TO_CM);
        int y = (int) (cord.getY() * M_TO_CM);
        // Rechnet das Java Koordinate um in die Array-Stelle vom Algorithms
        int xIndex = (( x + (( X_MAX_INDEX * GRID_SIZE ) / halfField )) / GRID_SIZE );
        int yIndex = (( Y_MAX_INDEX * GRID_SIZE - y ) / GRID_SIZE );

        if(xIndex >= X_MAX_INDEX || xIndex < 0){
            if(xIndex == X_MAX_INDEX){ // So wird ein Koordinate die auf der Außenlinie des Feldes ist noch erkannt
                xIndex = X_MAX_INDEX - 1;
            }

        }
        if(yIndex >= Y_MAX_INDEX || yIndex < 0){ // So kann der Index nicht außerhalb des Feldes sein
            if(yIndex == Y_MAX_INDEX){
                yIndex = Y_MAX_INDEX - 1;
            }
        }

        return new int[]{xIndex, yIndex};
    }


    public Coordinate aStarGridIndexToGamefieldCoordinate(int [] index, double a){
        double xIndex = index[0] * Data.getGRID_SIZE_IN_MM(); //+ ((double)Data.getGRID_SIZE_IN_MM() / 2);
        double yIndex = index[1] * Data.getGRID_SIZE_IN_MM();//+ ((double)Data.getGRID_SIZE_IN_MM() / 2);

        int halfField = Data.getFIELD_DIVISOR_IS_FIELD_BIG();

        double x = (xIndex - ((double)Data.getFIELD_WIDTH_IN_MM() / halfField)) / Data.getFIELD_SIZE_IN_MM(); //+ Data.getGRID_SIZE_IN_MM();
        double y = (Data.getFIELD_HEIGHT_IN_MM() - yIndex) / Data.getFIELD_SIZE_IN_MM(); //+ Data.getGRID_SIZE_IN_MM();

        return new Coordinate(x, y, a);
    }
}

