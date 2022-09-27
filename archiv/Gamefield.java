//package com.robotino.logistics;
//
//import com.robotino.helperClass.Data;
//import com.robotino.robo.Robo;
//
//import java.util.ArrayList;
//
///**
// * @author Fabian Leuenberger
// * @date 29.04.2022
// * @version 2
// * @description Goals of this class:
// *  -Output if a Field is free or used by a Robotino/Station
// *  -Search a Station and Output the found Station for further use like getting In-/Output Position of the Station
// *  -Class is in a Singelton Pattern so, it can only be instanced onece and every implementation gets the same data
// */
//
//public class Gamefield {
//
//    private final String configFilePath = "./rsc/config/gamePlayRefboxTaric.xml";
//
//    //Generation of Lists for Robots and Stations
//    ArrayList <Station> stations = new ArrayList<>();
//    ArrayList<Robo> robotinos = new ArrayList<>();
//    //Define Walls in Gamefield
//    private ArrayList[][] wallList = new ArrayList[6][2];
//
//    private boolean isGamefieldSize5X5 = false;
//    private boolean isGamefieldSize7X8 = false;
//    private boolean isGamefieldSize14X8 = false;
//
//
//    // Creating new Gamefield for Singelton Pattern
//    private static final Gamefield GAMEFIELD_INSTANCE = new Gamefield();
//
//
//    /** Singelton Pattern, so the class can only be instanced once
//     * @return returns Gamefield Instance
//     */
//    public static Gamefield getInstance(){
//        return GAMEFIELD_INSTANCE;
//    }
//
//    private Gamefield(){
//        if(Data.getFIELD_WIDTH_IN_MM() == 5000 ){
//            isGamefieldSize5X5 = true;
//        }else if(Data.getFIELD_WIDTH_IN_MM() == 7000){
//            isGamefieldSize7X8 = true;
//        }else if(Data.getFIELD_WIDTH_IN_MM() == 14000){
//            isGamefieldSize14X8 = true;
//        }
//    }
//
//
//    /** Add Station to stations List
//     * @param station Station you want to add
//     */
//    public void addStation(Station station){
//        if(!(stations.contains(station))){
//            stations.add(station);
//        }
//    }
//
//    public void removeSation(String name){
//        for(int i = 0; i < stations.size(); i++){
//            if(stations.get(i).getName().equals(name)){
//                stations.remove(i);
//            }
//        }
//        System.out.println(stations);
//    }
//
//    /** Add Robotino to robotinos List
//     * @param robotino Robotino you want to add
//     */
//    public void addRobotino(Robo robotino){
//        if(!(robotinos.contains(robotino))){
//            robotinos.add(robotino);
//        }
//    }
//
//     /** Check if Field is free
//     * @param coordinate
//     * @return true if free and false if used by robotino or station
//      * @deprecated
//     */
//    public boolean isFieldFree(Coordinate coordinate){
//        boolean isFieldFree = true;
//        boolean fieldIsUsedByStation = false;
//        boolean fieldIsUsedByRobotino = false;
//
//        for (int i = 0; i < stations.size(); i++) {
//            Coordinate tempStationPos = stations.get(i).getCoordinate();
//            if (tempStationPos.getX() == coordinate.getX() && tempStationPos.getY() == coordinate.getY()) {
//                fieldIsUsedByStation = true;
//            }
//        }
//
//        for (int i = 0; i < robotinos.size(); i++) {
//            Coordinate roboCoordinate = robotinos.get(i).getCurrentCoordinate();
//            if (roboCoordinate.getX() == coordinate.getX() && roboCoordinate.getY() == coordinate.getY()){
//                fieldIsUsedByRobotino = true;
//            }
//        }
//
//        if (fieldIsUsedByRobotino || fieldIsUsedByStation){
//            return false;
//        }
//        return isFieldFree;
//    }
//
//    /** Search Station
//     * @param stationName as String of the to be searched Station
//     * @return Station Object of the found Station, if no Station is found return null
//     */
//    public Station findStation(String stationName){
//        Station station = null;
//        for (int i = 0; i < stations.size(); i++) {
//            if (stations.get(i).getName().equals(stationName)){
//                return stations.get(i);
//            }
//        }
//        return station;
//    }
//
//    /**
//     * @return get the height of the gamefield
//    */
//    public int getHeight() {
//        return Data.getFIELD_HEIGHT();
//    }
//
//    /**
//     * @return get the width of the gamefield
//    */
//    public int getWidth() {
//        return Data.getFIELD_WIDTH();
//    }
//
//    /**
//     * @return get the size of the gamefield
//    */
//    public int getSize() {
//        return Data.getFIELD_HEIGHT() * Data.getFIELD_WIDTH();
//    }
//
//    public boolean isGamefieldSize5X5() {
//        return isGamefieldSize5X5;
//    }
//
//    public boolean isGamefieldSize7X8() {
//        return isGamefieldSize7X8;
//    }
//
//    public boolean isGamefieldSize14X8() {
//        return isGamefieldSize14X8;
//    }
//
//}


