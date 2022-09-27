//package com.robotino.logistics;
//
//import com.robotino.helperClass.ConvertPosition;
//
///**
// * @author Fabian Leuenberger, Kevin Zahn
// * @description Klasse der Stationen welche erfasst oder bekannt gegeben
// *              werden. Alle Informationen der Stationen werdne hierbei
// *              abgespeichert.
// */
//public class Station {
//    private final int WIDTH = 700; // in mm
//    private final int HEIGHT = 350; // in mm
//
//
//    private String name;
//    private String zone;
//    private String state;
//    private String type;
//    private double a;
//    private Field stationField;
//    private Coordinate inputCoordinate;
//    private Coordinate outputCoordinate;
//
//    ConvertPosition convert = new ConvertPosition();
//
//    /**
//     * Konstruktor
//     *
//     * @param name     der Station: Example {CS1}
//     * @param zone     in welcher sich die Station befindet: Example {M_Z51}
//     * @param state    in der sich die Station befindet: Example {error}, {idle}
//     * @param type     der Station
//     * @param a        Winkel der Station
//     */
//    public Station(String name, String zone, String state, String type, double a) {
//        this.name = name;
//        this.zone = zone;
//        this.state = state;
//        this.type = type;
//        this.a = a;
//        this.stationField = new Field(zone);
//
//        setInAndOutputSide();
//
//        //System.out.println("Added new Station: " + this.toString());
//    }
//
//    /* --- Getters ---*/
//    public String getName() {
//        return name;
//    }
//
//    public String getZone() {
//        return zone;
//    }
//
//    public String getState() {
//        return state;
//    }
//
//    public String getType() {
//        return type;
//    }
//
//    public double getA() {
//        return a;
//    }
//
//    public Coordinate getStationCoordinate() {
//        return stationField.getCoordinate();
//    }
//
//    public Coordinate getInputCoordinate() {
//        return inputCoordinate;
//    }
//
//    public Coordinate getOutputCoordinate() {
//        return outputCoordinate;
//    }
//
//    public int getWIDTH() {
//        return WIDTH;
//    }
//
//    public int getHEIGHT() {
//        return HEIGHT;
//    }
//
//    /**
//     * Anhand der Rotation die Input bzw. Output Kordinate erhalten.
//     * Bzw. Zone vor der Seite der Station.
//     */
//    private void setInAndOutputSide() {
//        double stationX = this.stationField.getX();
//        double stationY = this.stationField.getY();
//        double xInput, yInput, xOutput, yOutput;
//
//        switch ((int) a) {
//            case 0:
//                xInput = stationX + 1;
//                yInput = stationY;
//                xOutput = stationX - 1;
//                yOutput = stationY;
//                break;
//
//            case 45:
//                xInput = stationX + 1;
//                yInput = stationY + 1;
//                xOutput = stationX - 1;
//                yOutput = stationY - 1;
//                break;
//
//            case 90:
//                xInput = stationX;
//                yInput = stationY + 1;
//                xOutput = stationX;
//                yOutput = stationY - 1;
//                break;
//
//            case 135:
//                xInput = stationX - 1;
//                yInput = stationY + 1;
//                xOutput = stationX + 1;
//                yOutput = stationY - 1;
//                break;
//
//            case 180:
//                xInput = stationX - 1;
//                yInput = stationY;
//                xOutput = stationX + 1;
//                yOutput = stationY;
//                break;
//
//            case 225:
//                xInput = stationX - 1;
//                yInput = stationY - 1;
//                xOutput = stationX + 1;
//                yOutput = stationY + 1;
//                break;
//
//            case 270:
//                xInput = stationX;
//                yInput = stationY - 1;
//                xOutput = stationX;
//                yOutput = stationY + 1;
//                break;
//
//            case 315:
//                xInput = stationX + 1;
//                yInput = stationY - 1;
//                xOutput = stationX - 1;
//                yOutput = stationY + 1;
//                break;
//            default:
//                throw new IllegalArgumentException(Integer.toString((int) a));
//
//        }
//        inputCoordinate = new Coordinate(xInput, yInput, 0);
//        outputCoordinate = new Coordinate(xOutput, yOutput, 0);
//    }
//
//    /**
//     * Daten der Station als String
//     * @return Daten der Station als String
//     */
//    @Override
//    public String toString() {
//        return "Station{" +
//                "name='" + name + '\'' +
//                ", zone='" + zone + '\'' +
//                ", state='" + state + '\'' +
//                ", type='" + type + '\'' +
//                ", rotation=" + a +
//                ", stationField=" + stationField.toString() +
//                ", inputCoordinate=" + inputCoordinate.toString() +
//                ", outputCoordinate=" + outputCoordinate.toString() +
//                '}';
//    }
//}