package com.robotino.logistics;

import com.grips.model.teamserver.MachineClientUtils;
import com.robotino.game.Order;
import com.robotino.logistics.Stations.CapStation1;
import com.robotino.logistics.Stations.CapStation2;

/**
 * @author Fabian Leuenberger
 * @date 22.06.2022
 * @description Eine Vaterklasse für die einzelnen Stationen welche es im Spielfeld gibt.
 *              Alle Informationen, welche von der Refbox bekannt gegeben werden, können auch abgespeichert werde.
 *              Die Koordinate der In- & Output-Sides der Station wird berechnet, mit der Rotation welche der
 *              Robotino benötigt um parallel zu dieser zu stehen.
 */

public class Station {
    private String name;                //C-RS1
    private String type;                //RS1 RS2
    private MachineClientUtils.MachineState state; //IDLE, READY_AT_OUTPUT, DOWN, BROKEN, PREPARED, PROCESSING, PROCESSED, WAIT_IDLE, AVAILABLE, UNDEFINED
    private String teamColor;           //CYAN
    private Field stationField;
    private Coordinate inputCoordinate;
    private Coordinate outputCoordinate;
    private int a;               //180

    private final int WIDTH = 700;      // in mm
    private final int HEIGHT = 350;     // in mm

    public Station(){
    }

    public Station(String name,
                   String type,
                   MachineClientUtils.MachineState state,
                   String teamColor,
                   String zone,
                   int a) {
        this.name = name;
        this.type = type;
        this.state = state;
        this.teamColor = teamColor;
        this.stationField = new Field(zone);
        this.a = a;
    }

    public static Order.CapColor whichStationHasLessCaps(){
        int capsOnCapStation1 = CapStation1.getInstance().getCapsInStock();
        int capsOnCapStation2 = CapStation2.getInstance().getCapsInStock();

        if(capsOnCapStation1 > capsOnCapStation2){
            return CapStation2.getInstance().getCapColor();
        }else{
            return CapStation1.getInstance().getCapColor();
        }
    }



    /**********Getters**********/
    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public MachineClientUtils.MachineState getState() {
        return state;
    }

    public String getTeamColor() {
        return teamColor;
    }

    public Coordinate getCoordinate() {
        return stationField.getCoordinate();
    }

    public Coordinate getInputCoordinate() {
        return inputCoordinate;
    }

    public Coordinate getOutputCoordinate() {
        return outputCoordinate;
    }

    public int getA() {
        return a;
    }

    public int getWIDTH() {
        return WIDTH;
    }

    public int getHEIGHT() {
        return HEIGHT;
    }

    public Field getStationField() {
        return stationField;
    }

    /**********Setters*********/
    public Station setName(String name) {
        this.name = name;
        return this;
    }

    public Station setType(String type) {
        this.type = type;
        return this;
    }

    public Station setState(MachineClientUtils.MachineState state) {
        this.state = state;
        return this;
    }

    public Station setTeamColor(String teamColor) {
        this.teamColor = teamColor;
        return this;
    }

    public Station setStationField(String zone) {
        stationField = new Field(zone);
        setupInputSide();
        setupOutputSide();
        return this;
    }

    public Station setA(int a) {
        this.a = a;
        return this;
    }

    /**
     * Berechnet anhand der Position der Station die Position welche angefahren werden muss,
     * um vor der Input-Side zu stehen, mit der richtigen Rotation.
     */
    public void setupInputSide(){
        double xStation = stationField.getCoordinate().getX();
        double yStation = stationField.getCoordinate().getY();
        double xSide;
        double ySide;
        int aSide;

        switch (a) {
            case 0 -> {
                xSide = xStation + 0.7;
                ySide = yStation;
                aSide = a + 90;
            }
            case 45 -> {
                xSide = xStation + 0.5;
                ySide = yStation + 0.5;
                aSide = a + 90;
            }
            case 90 -> {
                xSide = xStation;
                ySide = yStation + 0.7;
                aSide = a + 90;
            }
            case 135 -> {
                xSide = xStation - 0.5;
                ySide = yStation + 0.5;
                aSide = a + 90;
            }
            case 180 -> {
                xSide = xStation - 0.7;
                ySide = yStation;
                aSide = a + 90;
            }
            case 225 -> {
                xSide = xStation - 0.5;
                ySide = yStation - 0.5;
                aSide = a + 90;
            }
            case 270 -> {
                xSide = xStation;
                ySide = yStation - 0.7;
                aSide = a + 90;
            }
            case 315 -> {
                xSide = xStation + 0.5;
                ySide = yStation - 0.5;
                aSide = (a + 90) - 360;
            }

            default -> throw new IllegalArgumentException(Integer.toString(a));
        }
        if (a > 360 ){
            a -= 360;
        }
        this.inputCoordinate = new Coordinate(xSide, ySide, aSide);
    }

    /**
     * Berechnet anhand der Position der Station die Position welche angefahren werden muss,
     * um vor der Output-Side zu stehen, mit der richtigen Rotation.
     */
    public void setupOutputSide(){
        double xStation = stationField.getCoordinate().getX();
        double yStation = stationField.getCoordinate().getY();
        double xSide;
        double ySide;
        int aSide;

        switch (a) {
            case 0 -> {
                xSide = xStation - 0.7;
                ySide = yStation;
                aSide = a + 270;
            }
            case 45 -> {
                xSide = xStation - 0.5;
                ySide = yStation - 0.5;
                aSide = a + 270;
            }
            case 90 -> {
                xSide = xStation;
                ySide = yStation - 0.7;
                aSide = a + 270;
            }
            case 135 -> {
                xSide = xStation + 0.5;
                ySide = yStation - 0.5;
                aSide = (a + 270) - 360;
            }
            case 180 -> {
                xSide = xStation + 0.7;
                ySide = yStation;
                aSide = (a + 270) - 360;
            }
            case 225 -> {
                xSide = xStation + 0.5;
                ySide = yStation + 0.5;
                aSide = (a + 270) - 360;
            }
            case 270 -> {
                xSide = xStation;
                ySide = yStation + 0.7;
                aSide = (a + 270) - 360;
            }
            case 315 -> {
                xSide = xStation - 0.5;
                ySide = yStation + 0.5;
                aSide = (a + 270) - 360;
            }
            default -> throw new IllegalArgumentException(Integer.toString(a));
        }
        if (a > 360 ){
            a -= 360;
        }
        this.outputCoordinate = new Coordinate(xSide, ySide, aSide);
    }

    public Coordinate getSide(Side side){
        switch (side){
            case INPUT -> {
                return getInputCoordinate();
            }
            case OUTPUT -> {
                return getOutputCoordinate();
            }
            default -> throw new IllegalArgumentException("Side gibt es nicht oder es wird in der Switch case Anweisung nicht abgefragt: " + side);
        }
    }

    @Override
    public String toString() {
        return "Station{" +
                "name='" + name + '\'' +
                ", state=" + state +
                ", teamColor='" + teamColor + '\'' +
                ", stationField=" + stationField +
                ", inputCoordinate=" + inputCoordinate +
                ", outputCoordinate=" + outputCoordinate +
                ", a=" + a +
                '}';
    }

    public enum Side{
        INPUT,
        OUTPUT
    }

}