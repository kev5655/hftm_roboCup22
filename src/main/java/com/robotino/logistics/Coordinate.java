package com.robotino.logistics;

import java.util.Arrays;
import java.util.List;

/**
 * @author Kevin Zahn
 * @description Koordinaten Klasse welche die x, y und Rotation beinhaltet.
 *              Wird für das Speichern neuer Koordinaten verwendet.
 */

public class Coordinate {

    private final Double x;
    private final Double y;
    private final Double a;


    /**
     *  Konstruktor nur für x und y ohne Rotation oder ob es eine RoboCoordinate ist
     *  Rotation und isRoboCoordinate wird auf null gesetzt
     * @param x Position
     * @param y Position
     */
    public Coordinate(double x, double y){
        this.x = x;
        this.y = y;
        this.a = null;
    }

    /**
     * Konstruktor zuweisen der Positionen, wenn Daten einzeln mitgegeben werden.
     * @param x Position
     * @param y Position
     * @param a Rotation
     */
    public Coordinate(double x, double y, double a){
        this.x = x;
        this.y = y;
        this.a = a;

    }

    /**
     * Klonen und zurückgeben einer Koordinate.
     * @return Klon der Coordinate Objekt.
     */
    @Override
    public Coordinate clone() {
        if(this.a == null){
            return new Coordinate(this.x, this.y);
        }
        return new Coordinate(this.x, this.y, this.a);
    }

    /**
     * Vergleichen zweier Koordinaten
     * @param c1 Koordinate 1
     * @param c2 Koordinate 2
     * @return true, wenn es dieselben sind, false wenn nicht.
     */
    public static boolean compare(Coordinate c1, Coordinate c2){
        return Arrays.equals(c1.getPointAsArray(), c2.getPointAsArray());
    }

    /**
     * Überprüft, ob eine Koordinate in einer Liste ist
     * @param c1 Koordinate auf die überprüft wird
     * @param cList Koordinate list auf die abgefragt wird, ob die Koordinate in der Liste ist
     * @return True wen die Koordinate in der Liste ist
     */
    public static boolean compare(Coordinate c1, List<Coordinate> cList){
        for(Coordinate c : cList){
            if(Coordinate.compare(c, c1)){
                return true;
            }
        }
        return false;
    }

    public static Coordinate diff(Coordinate c1, Coordinate c2){
        double x = (c1.getX() + c2.getX()) / 2;
        double y = (c1.getY() + c2.getY()) / 2;
        return  new Coordinate(x, y);
    }


    /**
     * Getter Methode der Koordinate als Array
     * @return double [] Array
     */
    public double[] getPointAsArray(){
        if(this.a == null){
            return new double[]{x,y};
        }
        return new double[]{this.x,this.y,this.a};
    }


    /* --- Getters einzelne Position --- */
    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getA() {
        return this.a;
    }

    /**
     * Koordinate als String
     * @return String der Koordinate
     */
    @Override
    public String toString() {
        return "Coordinate [x=" + x + ", y=" + y + ", a=" + a + "]";
    }
}