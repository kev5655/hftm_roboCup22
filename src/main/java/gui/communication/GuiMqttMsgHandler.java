package gui.communication;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.fasterxml.jackson.databind.JsonNode;
import com.robotino.helperClass.Log;
import gui.data.GuiData;
import gui.node.ShowMpsState;
import gui.node.ShowPathAndObstacle;
import gui.node.ShowRingStation;
import gui.node.ShowTextOutput;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

/**
 * @author Kevin Zahn
 * @description Die Msg von der Klasse MqttSubscribe werden in dieser Klasse
 *              auf den entsprechenden EventBus gesendet und anschließend
 *              in den entsprechenden Klassen weiter verwendet.
 *
 *              Diese Klasse darf jeweils nur einmal aufgerufen werden, weshalb
 *              diese mit dem Singleton-Pattern realisiert wurde.
 *              (Aufruf via wie folgt "MqttMsgHandler.getInstance()")
 *
 *              Hereinkommende Msg/Events werden in einer BlockingQueue gespeichert,
 *              um die thread sicherheit zu gewährleisten.
 *
 *              Der MqttMsgHandler wird gestattet, sobald die Topic subscribe werden
 */

public class GuiMqttMsgHandler implements Runnable, Observable {

    private final BlockingQueue<JsonNode> queueEvent = new ArrayBlockingQueue<>(1024);

    final List<InvalidationListener> listeners = new LinkedList<>();
    InvalidationListener showMpsState;
    InvalidationListener showPath;
    InvalidationListener showTextOutput;
    InvalidationListener showOrder;
    InvalidationListener showRingStation;


    final Map<Integer, List<int[]>> spot = new HashMap<>();
    final Map<Integer, List<int[]>> spotOnlyInGame = new HashMap<>();
    LinkedList<String[]> stations = new LinkedList<>();
    LinkedList<String[]> orders = new LinkedList<>();
    LinkedList<String[]> ringStations = new LinkedList<>();
    private String log = null;

    /**
     * Singleton-Pattern und getInstance Methode
     */
    private static final GuiMqttMsgHandler MQTT_MSG_HANDLER_INSTANCE = new GuiMqttMsgHandler();

    public static GuiMqttMsgHandler getInstance() {
        return MQTT_MSG_HANDLER_INSTANCE;
    }

    private GuiMqttMsgHandler() {
    }

    /**
     * Msg werden der Queue hinzugefügt.
     * Wird in der Klasse MqttSubscribe verwendet.
     *
     * @param msg Msg von Node-Red als Json-Node.
     */
    public void addQueueEvent(JsonNode msg) {
        this.queueEvent.add(msg);
        if (queueEvent.size() == 100) {
            Log.logVisu.warn("Achtung Queue von MqttMsgHandler ist am voll laufen, aktuelle Grösse: " + queueEvent.size());
        } else if (queueEvent.size() == 500) {
            Log.logVisu.warn("Achtung Queue von MqttMsgHandler ist am voll laufen, aktuelle Grösse: " + queueEvent.size());
        } else if (queueEvent.size() == 1000) {
            Log.logVisu.error("Queue ist viel zugross bitte Problem suchen, aktuelle Grösse: " + queueEvent.size());
        }
    }

    /**
     * Run Methode (thread) da immer wieder Msg empfangen werden und auf den entsprechenden EventBus
     * weitergegeben werden müssen.
     */
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!(queueEvent.isEmpty())) {
                //System.out.println("Queue Size: " + queueEvent.size());
                try {
                    JsonNode jsonNode;
                    JsonNode json;
                    List<int[]> values = new LinkedList<>();
                    LinkedList<String[]> stationValues = new LinkedList<>();
                    List<String[]> orderValues = new LinkedList<>();
                    LinkedList<String[]> ringStationValues = new LinkedList<>();
                    Iterator<JsonNode> itr;
                    json = queueEvent.poll();
                    int msgType = json.get("msgType").asInt();
                    /*
                     * Case entscheide anhand des MsgType.
                     * Je nach Case wird der Inhalt der Msg auf einen
                     * anderen EventBus verschickt.
                     * Der Inhalt der Msg wird dem Json entnommen und allenfalls
                     * vor dem Senden weiterverarbeitet.
                     */
                    switch (msgType) {
                        case 101, 102, 103, 104 -> { //Robo 1, 2, 3 Path
                            jsonNode = json.get("value");
                            itr = jsonNode.elements();
                            iterateJsonNode(itr, values);
                            spot.put(msgType, values);
                            removeElementsOutsidOfGamefield(msgType);
                            //Log.logVisu.info("Path wird auf den Observer weitergeleitet: " + getValueAsString(spot.get(msgType)));
                            if (showPath != null) inform(showPath);
                        }
                        case 105 -> { //Stations
                            jsonNode = json.get("value");
                            itr = jsonNode.elements();
                            iterateJsonNodeString(itr, stationValues);
                            stations = (LinkedList<String[]>) stationValues.clone();
                            Log.logVisu.info("Stationen wurde auf den Observer weitergeleitet");
                            if (showMpsState != null) inform(showMpsState);
                        }
                        case 106 -> { //Logs
                            jsonNode = json.get("value");
                            log = String.valueOf(json.get("value")).replace("\"", "");
                            Log.logVisu.info("Log wurde auf dem Observer weitergeleitet");
                            if (showTextOutput != null) inform(showTextOutput);
                        }
                        case 107 -> { //Ring Stations
                            jsonNode = json.get("value");
                            itr = jsonNode.elements();
                            iterateJsonNodeRingStation(itr, ringStationValues);
                            ringStations = (LinkedList<String[]>) ringStationValues.clone();
                            Log.logVisu.info("Ring Stationen wurde auf den Observer weitergeleitet");
                            if (showRingStation != null) inform(showRingStation);
                        }


                        //Default Case im Falle unbekannter MsgType.
                        default ->
                                throw new IllegalArgumentException("Invalide MsgType: " + msgType + " Data: " + json);
                    }

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void iterateJsonNode(Iterator<JsonNode> itr, List<int[]> values) {
        while (itr.hasNext()) {
            JsonNode temp = itr.next();
            if (temp.get("nameIdentifier") != null) {
                values.add(new int[]{
                        temp.get("x").asInt() * GuiData.getINDEX_TO_PIXLE() + GuiData.getHALF_MARGIN(),
                        temp.get("y").asInt() * GuiData.getINDEX_TO_PIXLE() + GuiData.getHALF_MARGIN(),
                        temp.get("nameIdentifier").asInt()
                });
            } else {
                values.add(new int[]{
                        temp.get("x").asInt() * GuiData.getINDEX_TO_PIXLE() + GuiData.getHALF_MARGIN(),
                        temp.get("y").asInt() * GuiData.getINDEX_TO_PIXLE() + GuiData.getHALF_MARGIN(),
                });
            }
        }
    }

    public void iterateJsonStringOrder(Iterator<JsonNode> itr, List<String[]> values){
        while(itr.hasNext()) {
            JsonNode temp = itr.next();
                values.add(new String[]{
                        String.valueOf(temp.get("Id")),
                        String.valueOf(temp.get("Type")),
                        String.valueOf(temp.get("BaseColor")),
                        String.valueOf(temp.get("CapColor")),
                        String.valueOf(temp.get("RingColor1")),
                        String.valueOf(temp.get("RingColor2")),
                        String.valueOf(temp.get("RingColor3"))
                });
        }
    }

    public void iterateJsonNodeString(Iterator<JsonNode> itr, List<String []> values){
        while(itr.hasNext()) {
            JsonNode temp = itr.next();
            values.add(new String[]{
                    String.valueOf(temp.get("Name")),
                    String.valueOf(temp.get("Type")),
                    String.valueOf(temp.get("State")),
                    String.valueOf(temp.get("TeamColor")),
                    String.valueOf(temp.get("Rotation"))
            });
        }
    }

    public void iterateJsonNodeRingStation(Iterator<JsonNode> itr, List<String []> values) {
        while (itr.hasNext()) {
            JsonNode temp = itr.next();
            values.add(new String[]{
                    String.valueOf(temp.get("Name")),
                    String.valueOf(temp.get("Color1")),
                    String.valueOf(temp.get("Prize1")),
                    String.valueOf(temp.get("Color2")),
                    String.valueOf(temp.get("Prize2")),
                    String.valueOf(temp.get("Payed"))
            });
        }
    }

    public void removeElementsOutsidOfGamefield(int msgType){
        List<int []> values = new LinkedList<>(spot.get(msgType));

        int xMax = GuiData.getFIELD_WIDTH() + GuiData.getHALF_MARGIN();
        int min = GuiData.getHALF_MARGIN();
        int yMax = GuiData.getFIELD_HEIGHT() + GuiData.getHALF_MARGIN();

        Iterator<int []> itr = values.iterator();
        while (itr.hasNext()){
            int [] coord = itr.next();
            int x = coord[0];
            int y = coord[1];
            if(x < min || x > xMax){
                itr.remove();
            }else if(y < min || y > yMax){
                itr.remove();
            }
        }

        spotOnlyInGame.put(msgType, values);
    }

    public String getValueAsString(List<int []> values){
        String value = "";
        for(int [] val : values){
            value += "[" + val[0] + " " + val[1] + "] ";
        }
        return value;
    }

    public void clearObstacles(){
        if(spot.get(104) != null){
            spot.get(104).clear();
        }
    }

    @Override
    public void addListener(InvalidationListener listener) {
        if(listener.getClass().getName().equals(ShowPathAndObstacle.class.getName())){
            showPath = listener;
        }

        if (listener.getClass().getName().equals(ShowMpsState.class.getName())){
            showMpsState = listener;
        }

        if (listener.getClass().getName().equals(ShowTextOutput.class.getName())){
            showTextOutput = listener;
        }
        if (listener.getClass().getName().equals(ShowRingStation.class.getName())){
            showRingStation = listener;
        }
        //listeners.add(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        listeners.remove(listener);
    }

    public void inform(InvalidationListener listener){
        listener.invalidated(this);
    }

    public Map<Integer, List<int[]>> getSpot() {
        return spotOnlyInGame;
    }

    public List<String []> getStations() {
        return stations;
    }

    public List<String []> getRingStations(){
        return ringStations;
    }

    public String getLog() {
        return log;
    }
}