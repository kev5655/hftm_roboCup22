package com.robotino.communication.mqtt;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import com.fasterxml.jackson.databind.JsonNode;
import com.robotino.eventBus.*;
import com.robotino.eventBus.mqtt.*;
import com.robotino.helperClass.ConvertPosition;
import com.robotino.helperClass.Data;
import com.robotino.helperClass.Log;
import com.robotino.logistics.Coordinate;

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

public class MqttMsgHandler implements Runnable {

    private final BlockingQueue<JsonNode> queueEvent = new ArrayBlockingQueue<>(1024);

    private boolean isStated = false;

    /**
     * Singleton-Pattern und getInstance Methode
     */
    private static final MqttMsgHandler MQTT_MSG_HANDLER_INSTANCE = new MqttMsgHandler();
    public static MqttMsgHandler getInstance() {
        return MQTT_MSG_HANDLER_INSTANCE;
    }
    private MqttMsgHandler() {}

    /**
     * Msg werden der Queue hinzugefügt.
     * Wird in der Klasse MqttSubscribe verwendet.
     * @param msg Msg von Node-Red als Json-Node.
     */
    public void addQueueEvent(JsonNode msg) {
        this.queueEvent.add(msg);
        if(queueEvent.size() == 100) {
            Log.incomingMqttMsg.warn("Achtung Queue von MqttMsgHandler ist am voll laufen, aktuelle Grösse: " + queueEvent.size());
        } else if(queueEvent.size() == 500) {
            Log.incomingMqttMsg.warn("Achtung Queue von MqttMsgHandler ist am voll laufen, aktuelle Grösse: " + queueEvent.size());
        } else if(queueEvent.size() == 1000) {
            Log.incomingMqttMsg.error("Queue ist viel zugross bitte Problem suchen, aktuelle Grösse: " + queueEvent.size());
        }
    }

    /**
     * Run Methode (thread) da immer wieder Msg empfangen werden und auf den entsprechenden EventBus
     * weitergegeben werden müssen.
     */
    @Override
    public void run() {
        int roboNr;
        isStated = true;
        Event event;
        JsonNode json;

        ConvertPosition convert = new ConvertPosition();

        Log.startedSystem.info("Startet MqttMsgHandler");

        // ToDo umbauen auf einen Scheduler
        while (true) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(!(queueEvent.isEmpty())){
                //System.out.println("Queue Size: " + queueEvent.size());
                try {
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
                        //Wert des erkannten AR-Tags.
                        case 20 -> {
                            roboNr = json.get("roboNr").asInt();
                            int tagId = json.get("idTag").asInt();
                            int xPosCam = json.get("xPos").asInt();
                            int yPosCam = json.get("yPos").asInt();
                            int aPosCam = json.get("angle").asInt();
                            int distance = json.get("distance").asInt();

                            int[] tagPosition = new int[]{xPosCam, yPosCam, aPosCam, distance, tagId};

                            event = new ArTagEvent(tagPosition, roboNr);
                            ArTagBus.getInstance().publish(event);
                            Log.incomingMqttMsg.info("Nachricht auf EventBus weiterleiten: " + json);
                        }

                        //Aktuelle Position des Robotinos (Gamefield Cord.).
                        case 21 -> {
                            roboNr = json.get("roboNr").asInt() - 1;
                            int yPosRobo = json.get("yPos").asInt();
                            int xPosRobo = json.get("xPos").asInt();
                            int aPosRobo = json.get("angle").asInt();

                            Coordinate roboCord = new Coordinate(xPosRobo, yPosRobo, aPosRobo);
                            Coordinate roboOnGamefieldCord = convert.roboCoordinateToGamefieldCoordinate(
                                    roboCord,
                                    Data.getROBOTS_START_FIELDS().get(roboNr).getCoordinate()
                            );

                            event = new CurrentRoboPosEvent(roboOnGamefieldCord, roboNr + 1);
                            CurrentRoboPosBus.getInstance().publish(event);
                            Log.incomingMqttMsg.info("Nachricht auf EventBus weiterleiten: " + json);
                        }

                        // Gripping Status true: Competed false: Failed
                        case 22 -> {
                            roboNr = json.get("roboNr").asInt();
                            boolean isGrippingCompleted = json.get("grippingDone").asBoolean(); //1:Done 2:Fail

                            event = new GrippingStatusEvent(roboNr, isGrippingCompleted);
                            GrippingStatusBus.getInstance().publish(event);
                            Log.incomingMqttMsg.info("Nachricht auf EventBus weiterleiten: " + json);
                        }

                        //Default Case im Falle unbekannter MsgType.
                        default -> throw new IllegalArgumentException(Integer.toString(Data.getCHALLENGE()));
                    }

                } catch (NumberFormatException e ) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isStated() {
        return isStated;
    }
}