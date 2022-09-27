package com.robotino.helperClass;

import com.grips.model.teamserver.MachineClientUtils;
import com.robotino.logistics.Coordinate;
import com.robotino.logistics.Field;
import com.robotino.logistics.Station;
import com.robotino.logistics.Stations.RingStation1;
import com.robotino.logistics.Stations.RingStation2;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * @author Fabian Leuenberger
 * @description Holt die Gameplay Daten vom XML File und macht diese Statisch verfügbar,
 *              damit nicht in jeder Klasse das XML File eingeladen werden muss.
 */


public class Data {
    
    private static final String xmlPath = "./rsc/config/gamePlay.xml";

    /* Robo Parameter */
    private static final List<Field> ROBO_START_FIELD = new ArrayList<>();
    private static int ROBO_SPEED;

    /*-- GAME PLAY INFOS --*/
    private static int CHALLENGE;
    private static boolean IS_GAME = false;
    private static final List<MachineClientUtils.Machine> MPS_STATIONS = new LinkedList<>();
    private static boolean hasENTRY_WALL;
    private static boolean IS_MIRROR_ON;

    /*-- NETWORK --*/
    private static String REFBOX_IP;
    private static String TEAM_NAME;
    private static String TEAM_COLOR;
    private static int REFBOX_PUBLIC_PEER_RECEIVE_PORT;
    private static int REFBOX_PUBLIC_PEER_SEND_PORT;
    private static int REFBOX_PRIVATE_MAGENTA_PEER_RECEIVE_PORT;
    private static int REFBOX_PRIVATE_MAGENTA_PEER_SEND_PORT;
    private static int REFBOX_PRIVATE_CYAN_PEER_RECEIVE_PORT;
    private static int REFBOX_PRIVATE_CYAN_PEER_SEND_PORT;
    private static String ENCRYPTION_KEY;
    private static String MQTT_BROKER_IP;

    /*-- FIELD SIZE --*/
    private static int FIELD_WIDTH; // in mm 5000 7000 14000
    private static int FIELD_HEIGHT; // in mm 5000 8000
    private static int FIELD_DIVISOR_IS_FIELD_BIG;
    private static boolean isGamefieldSize5X5;
    private static boolean isGamefieldSize7X8;
    private static boolean isGamefieldSize14X8;

    /*-- AStar --*/
    private static final int FIELD_SIZE = 1000; // in mm
    private static final int GRID_SIZE = 100; // in mm default 100
    private static  final int ROBO_SIZE = 450; // in mm default 450
    private static final int OFFSET = 201; // in mm default 100 Achtung Offset daf nicht grösse als 225 sein sonst gibt es problem bei der Entry Zone
    private static final int FACTOR_M_TO_CM = 100;
    private static final int FACTOR_M_TO_MM = 1000;
    private static final int FACTOR_MM_TO_PIXEL = 10;
    private static final int MARGIN = 400;

    /* Line Smoother */
    private static final double WEIGHT = 1.0;


    /* Robo Parameter */
    public static List<Field> getROBOTS_START_FIELDS() { return ROBO_START_FIELD;}
    public static int getROBO_SPEED(){ return  ROBO_SPEED; }
    /*-- GAME PLAY INFOS --*/
    public static int getCHALLENGE() { return CHALLENGE; }
    public static boolean isGAME() { return IS_GAME; }
    public static boolean hasENTRY_WALL() { return hasENTRY_WALL; }
    public static boolean is_MIRROR_ON() { return IS_MIRROR_ON; }
    public static List<MachineClientUtils.Machine> getMPS_STATIONS() { return MPS_STATIONS; }
    public static boolean hasBS_STATION() { return MPS_STATIONS.contains(MachineClientUtils.Machine.BS); }
    public static boolean hasDS_STATION() { return MPS_STATIONS.contains(MachineClientUtils.Machine.DS); }
    public static boolean hasRS1_STATION() { return MPS_STATIONS.contains(MachineClientUtils.Machine.RS1); }
    public static boolean hasRS2_STATION() { return MPS_STATIONS.contains(MachineClientUtils.Machine.RS2); }
    public static boolean hasCS1_STATION() { return MPS_STATIONS.contains(MachineClientUtils.Machine.CS1); }
    public static boolean hasCS2_STATION() { return MPS_STATIONS.contains(MachineClientUtils.Machine.CS2); }
    public static boolean hasSS_STATION() { return MPS_STATIONS.contains(MachineClientUtils.Machine.SS); }
    public static Station giveAnRingStation() {
        if(hasRS1_STATION()) return RingStation1.getInstance();
        if(hasRS2_STATION()) return RingStation2.getInstance();
        else throw new IllegalArgumentException("Es gibt kein RingStation");
    }

    /*-- NETWORK --*/
    public static String getREFBOX_IP() { return REFBOX_IP; }
    public static String getTEAM_NAME() { return TEAM_NAME; }
    public static String getTEAM_COLOR() { return TEAM_COLOR; }
    public static int getREFBOX_PUBLIC_PEER_RECEIVE_PORT() { return REFBOX_PUBLIC_PEER_RECEIVE_PORT; }
    public static int getREFBOX_PUBLIC_PEER_SEND_PORT() { return REFBOX_PUBLIC_PEER_SEND_PORT; }
    public static int getREFBOX_PRIVATE_MAGENTA_PEER_RECEIVE_PORT() { return REFBOX_PRIVATE_MAGENTA_PEER_RECEIVE_PORT; }
    public static int getREFBOX_PRIVATE_MAGENTA_PEER_SEND_PORT() { return REFBOX_PRIVATE_MAGENTA_PEER_SEND_PORT; }
    public static int getREFBOX_PRIVATE_CYAN_PEER_RECEIVE_PORT() { return REFBOX_PRIVATE_CYAN_PEER_RECEIVE_PORT; }
    public static int getREFBOX_PRIVATE_CYAN_PEER_SEND_PORT() { return REFBOX_PRIVATE_CYAN_PEER_SEND_PORT; }
    public static String getENCRYPTION_KEY() { return ENCRYPTION_KEY; }
    public static String getMQTT_BROKER_IP() { return MQTT_BROKER_IP; }
    public static int getFIELD_WIDTH() { return FIELD_WIDTH / FACTOR_MM_TO_PIXEL; }
    public static int getFIELD_WIDTH_IN_MM() { return FIELD_WIDTH; }
    public static int getFIELD_WIDTH_IN_M() { return FIELD_WIDTH / FACTOR_M_TO_MM; }
    public static int getFIELD_HEIGHT() { return FIELD_HEIGHT / FACTOR_MM_TO_PIXEL; }
    public static int getFIELD_HEIGHT_IN_MM(){ return FIELD_HEIGHT; }
    public static int getFIELD_HEIGHT_IN_M(){ return FIELD_HEIGHT / FACTOR_M_TO_MM; }
    public static int getFIELD_DIVISOR_IS_FIELD_BIG() { return ( FIELD_WIDTH == 14000 ) ? 2 : 1; }
    public static boolean isGamefieldSize5X5() { return FIELD_WIDTH == 5000; }
    public static boolean isGamefieldSize7X8() { return FIELD_WIDTH == 7000; }
    public static boolean isGamefieldSize14X8() { return FIELD_WIDTH == 14000; }
    public static int getMARGIN(){ return MARGIN; }
    public static int getHALF_MARGIN() { return MARGIN / 2;}


    /* AStar */
    public static int getFIELD_SIZE() { return FIELD_SIZE / FACTOR_MM_TO_PIXEL; }
    public static int getFIELD_SIZE_IN_MM() { return FIELD_SIZE; }
    public static int getFIELD_SIZE_IN_M() { return FIELD_SIZE / FACTOR_M_TO_MM; }
    public static Coordinate getFIELD_COORDINATE_MIN() {
        double xMin = ((double)Data.getFIELD_WIDTH_IN_M() / Data.getFIELD_DIVISOR_IS_FIELD_BIG() ) * -1;
        double yMin = 0;

        return new Coordinate(xMin, yMin); }
    public static Coordinate getFIELD_COORDINATE_MAX() {
        double xMax;
        if(Data.getFIELD_DIVISOR_IS_FIELD_BIG() == 1){
            xMax = 0;
        }else{
            xMax = (double)Data.getFIELD_WIDTH_IN_M() / Data.getFIELD_DIVISOR_IS_FIELD_BIG();
        }
        double yMax = Data.getFIELD_HEIGHT_IN_M();

        return new Coordinate(xMax, yMax); }
    public static int getGRID_SIZE(){ return GRID_SIZE / FACTOR_MM_TO_PIXEL; }
    public static int getGRID_SIZE_IN_MM() { return GRID_SIZE; }
    public static int getMAX_X_INDEX(){ return FIELD_WIDTH / GRID_SIZE; }
    public static int getMAX_Y_INDEX(){ return FIELD_HEIGHT / GRID_SIZE; }
    public static int getROBO_SIZE(){ return ROBO_SIZE / FACTOR_MM_TO_PIXEL; }

    public static int getROBO_SIZE_IN_MM() { return ROBO_SIZE; }
    public static int getHALF_ROBO_SIZE(){ return ROBO_SIZE / 2; }
    public static int getOFFSET() { return OFFSET / FACTOR_MM_TO_PIXEL; }
    public static int getFACTOR_M_TO_CM(){ return FACTOR_M_TO_CM; }
    public static double getWEIGHT() { return WEIGHT; }




    public static void loadFile() throws IOException {
        Properties prop = new Properties();
        prop.loadFromXML(new FileInputStream(xmlPath));

        CHALLENGE = Integer.parseInt(prop.getProperty("challenge"));

        REFBOX_IP = prop.getProperty("refBoxIp");
        TEAM_NAME = prop.getProperty("teamName");
        TEAM_COLOR = prop.getProperty("teamColor");
        REFBOX_PUBLIC_PEER_RECEIVE_PORT = Integer.parseInt(prop.getProperty("REFBOX_PUBLIC_PEER_RECEIVE_PORT"));
        REFBOX_PUBLIC_PEER_SEND_PORT = Integer.parseInt(prop.getProperty("REFBOX_PUBLIC_PEER_SEND_PORT"));
        REFBOX_PRIVATE_MAGENTA_PEER_RECEIVE_PORT = Integer.parseInt(prop.getProperty("REFBOX_PRIVATE_MAGENTA_PEER_RECEIVE_PORT"));
        REFBOX_PRIVATE_MAGENTA_PEER_SEND_PORT = Integer.parseInt(prop.getProperty("REFBOX_PRIVATE_MAGENTA_PEER_SEND_PORT"));
        REFBOX_PRIVATE_CYAN_PEER_RECEIVE_PORT = Integer.parseInt(prop.getProperty("REFBOX_PRIVATE_CYAN_PEER_RECEIVE_PORT"));
        REFBOX_PRIVATE_CYAN_PEER_SEND_PORT = Integer.parseInt(prop.getProperty("REFBOX_PRIVATE_CYAN_PEER_SEND_PORT"));
        ENCRYPTION_KEY = prop.getProperty("encKey");
        MQTT_BROKER_IP = prop.getProperty("mqttBrokerIp");

        FIELD_WIDTH =  Integer.parseInt(prop.getProperty("fieldWidth"));
        FIELD_HEIGHT = Integer.parseInt(prop.getProperty("fieldHeight"));

        ROBO_SPEED = Integer.parseInt(prop.getProperty("roboSpeed"));

        List<String> stations = List.of(prop.getProperty("Stations").split(";"));
        for(String station : stations){
            switch (station){
                case "BS" -> MPS_STATIONS.add(MachineClientUtils.Machine.BS);
                case "DS" -> MPS_STATIONS.add(MachineClientUtils.Machine.DS);
                case "SS" -> MPS_STATIONS.add(MachineClientUtils.Machine.SS);
                case "RS1" -> MPS_STATIONS.add(MachineClientUtils.Machine.RS1);
                case "RS2" -> MPS_STATIONS.add(MachineClientUtils.Machine.RS2);
                case "CS1" -> MPS_STATIONS.add(MachineClientUtils.Machine.CS1);
                case "CS2" -> MPS_STATIONS.add(MachineClientUtils.Machine.CS2);
                default -> throw new IllegalArgumentException("Es gibt kein Station mit dem namen: " + station);
            }
        }
        if(!MPS_STATIONS.contains(MachineClientUtils.Machine.BS) ||
            !(MPS_STATIONS.contains(MachineClientUtils.Machine.CS1) || MPS_STATIONS.contains(MachineClientUtils.Machine.CS2))){
            throw new IllegalArgumentException("Game kann nicht gestartet werden station fehlt in Gameplay.xml file");
        }

        IS_GAME = prop.getProperty("isGame").equals("true");
        hasENTRY_WALL = prop.getProperty("hasEntryWall").equals("true");
        IS_MIRROR_ON = prop.get("isMirrorOn").equals("true");

        for(int i = 1; i < 4; i++){
            String fullName = prop.getProperty("robo"+ i + "StartPosition");
            Field field = new Field(fullName);
            ROBO_START_FIELD.add(field);
        }
    }
}