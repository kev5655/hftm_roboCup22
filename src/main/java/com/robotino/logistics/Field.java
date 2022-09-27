package com.robotino.logistics;

import com.robotino.helperClass.ConvertPosition;
import com.robotino.helperClass.Data;

/**
 * @author Kevin
 * Ein Field beinhalte alle Variablen, um ein Field nachzubilden, es kann
 * mithilfe einer Zone oder Coordiante Objekt erzeugt werden und beinhaltet:
 *  - zone                  M_Z51
 *  - fieldName             M_Z
 *  - fieldChar             M
 *  - coordinate Objekt     {x, y, a}
 *  - fieldNr               51
 *  um auf die Coordinate zuzugreifen muss nicht direkt auf das Coordinate Objekt
 *  zugegriffen werden den das Field Objekt macht dies.
 */
public class Field {

    private String zone; // Example M_Z71
    private String fieldName; // Example M_Z
    private char fieldChar; // Example M

    private Coordinate coordinate; // Example new Coordinate(x, y, a);
    private int fieldNr; // 71

    final ConvertPosition convert = new ConvertPosition();

    /**
     *  Erzeugen eines Fields mit einer Zone
     * @param zone String mit Teamfarbe und Feldnummer: Example {M_Z51}
     */
    public Field(String zone){
        setup(zone);
    }

    /**
     *  Erzeugen eines Fields mit einer coordinate Objekt
     * @param coordinate coordinate Objekt mit x, y, und a
     */
    public Field(Coordinate coordinate){
        if(isPossibleConvertToField(coordinate)){
            String zone = convert.coordinateToZone(coordinate);
            setup(zone);
        }else{
            throw new IllegalArgumentException("Kann die Koordinate nicht in eine Zone umwandeln: "
                    + coordinate
                    + " Field grösse ist von: " + Data.getFIELD_COORDINATE_MIN()
                    + " bis: " + Data.getFIELD_COORDINATE_MAX());
        }

    }

    /**
     * Wandelt ein Zone um in die Variablen: fieldName, fieldChar, fieldNr und das coordinate Objekt
     * @param zone String mit Teamfarbe und Feldnummer: Example {M_Z51}
     */
    public void setup(String zone){
        this.zone = zone;
        String [] s = zone.split("_Z");
        this.fieldName = s[0] + "_Z";
        this.fieldChar = s[0].charAt(0);
        this.coordinate = convert.zoneToCoordinate(zone, 0);
        this.fieldNr = Integer.parseInt(s[1]);
    }

    private boolean isPossibleConvertToField(Coordinate c){
        double xMax = Data.getFIELD_COORDINATE_MAX().getX();
        double xMin = Data.getFIELD_COORDINATE_MIN().getX();
        double yMax = Data.getFIELD_COORDINATE_MAX().getY();
        double yMin = Data.getFIELD_COORDINATE_MIN().getY();

        if (c.getX() < xMin || c.getX() > xMax ) {
            return false;
        }

        else return !(c.getY() < yMin) && !(c.getY() > yMax);
    }

    public String getZone() {
        return zone;
    }

    public String getFieldName() {
        return fieldName;
    }

    public char getFieldChar() {
        return fieldChar;
    }

    public int getFieldNr() {
        return fieldNr;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public double[] getCoordinateAsArray() {
        return coordinate.getPointAsArray();
    }

    public double getX(){
        return coordinate.getX();
    }

    public double getY(){
        return coordinate.getY();
    }

    public double getA(){
        return coordinate.getA();
    }

    @Override
    public String toString() {
        return "Field [fullName=" + zone + ", Coordinate=" + coordinate + "]";
    }
}
