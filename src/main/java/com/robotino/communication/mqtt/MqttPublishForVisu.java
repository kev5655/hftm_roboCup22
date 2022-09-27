package com.robotino.communication.mqtt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.robotino.helperClass.Data;
import com.robotino.helperClass.Log;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kevin Zahn
 * @description Die Klasse MqttPublish dient dem Versenden von Msg an das Node-Red.
 *              Die Msg wird dabei als String im Json Format publiziert.
 *
 *              Diese Klasse darf jeweils nur einmal aufgerufen werden, weshalb
 *              diese mit dem Singleton-Pattern realisiert wurde.
 *              (Aufruf via wie folgt "MqttPublish.getInstance")
 */

public class MqttPublishForVisu {

    //Broker adresse = "tcp://172.26.107.212:1883" Refbox IP
    private static final String BROKER_IP = "tcp://" + Data.getMQTT_BROKER_IP() + ":1883";
    MqttConnectOptions connOpts;

    //Quality of Service  Level (Level 1 "at least once")
    private static final int QOS = 1;

    /**
     * Generiert alle Topics für alle das Visu
     */
    public static final Map<Integer, String>TOPIC_VISU = new HashMap<>();
    static{
        TOPIC_VISU.put(101, "visu/robo1/route");
        TOPIC_VISU.put(102, "visu/robo2/route");
        TOPIC_VISU.put(103, "visu/robo3/route");
        TOPIC_VISU.put(104, "visu/obstacles");
        TOPIC_VISU.put(105, "visu/stations");
        TOPIC_VISU.put(106, "visu/logs");
        TOPIC_VISU.put(107, "visu/ringStations");
    }


    private MqttClient client = null;

    // Singleton Pattern
    private static final MqttPublishForVisu MQTT_PUBLISH_INSTANCE = new MqttPublishForVisu();
    public static MqttPublishForVisu getInstance() {
        return MQTT_PUBLISH_INSTANCE;
    }

    private MqttPublishForVisu(){
        // client Instanziieren
        try {
            client = new MqttClient(BROKER_IP,
                    String.valueOf(System.nanoTime()),
                    new MemoryPersistence()); // So wird keine Datei erstellt
            Log.publishedMqttMsg.info("Mqtt Client Instanziiert");
        } catch (MqttException e) {
            Log.incomingMqttMsg.error("Instanziieren vom Mqtt client ist fehlgeschlagen: " + e);
            Log.logger.error("Instanziieren vom Mqtt client ist fehlgeschlagen: " + e);
            e.printStackTrace();
        }

        // connection Optionen werde festgelegt
        connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setKeepAliveInterval(1000);

        // connection zum Broker wird aufgebaut
        try {
            client.connect(connOpts);
            Log.publishedMqttMsg.info("Verbunden mit dem Broker über die IP: " + Data.getMQTT_BROKER_IP());
        } catch (MqttException e) {
            Log.incomingMqttMsg.error("Verbindungsaufbau zum Broker mit der IP: " + Data.getMQTT_BROKER_IP() + " ist gescheitert: " + e);
            Log.logger.error("Verbindungsaufbau zum Broker mit der IP: " + Data.getMQTT_BROKER_IP() + " ist gescheitert: " + e);
            e.printStackTrace();
        }
    }


    /**
     * Erstellung eines Json-Objekts aus einem String.
     * Und anschliessendes zusammensetzen der Msg so wie dessen versenden.
     * @param jsonMsg JsonNode der Msg zum Versenden
     *                (eine Node für eine Liste mit Objekt Daten in jedem Node).
     */
    public void createAndSendMsg(ObjectNode jsonMsg){

        //Instanziierung eines Jackson Objekts und rücksetzen des Payloads,
        ObjectMapper mapper = new ObjectMapper();
        String payload = null;
        try {
            payload = mapper.writeValueAsString(jsonMsg);
        } catch (JsonProcessingException e) {
            Log.publishedMqttMsg.error("Die Nachricht konnte nicht in ein JSON-String umgewandelt werden: " + e);
            Log.logger.error("Die Nachricht konnte nicht in ein JSON-String umgewandelt werden: " + e);
            e.printStackTrace();
        }

        int msgNbrType = jsonMsg.get("msgType").asInt();

        assert payload != null; // Beendet das Programm wen die Bedingung falsch ist
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(QOS);
        message.setRetained(true);

        MqttTopic topic = client.getTopic(TOPIC_VISU.get(msgNbrType));

        try {
            topic.publish(message);
            Log.logVisu.info("Nachricht wurde versendet: " + payload + " auf das Topic: " + TOPIC_VISU.get(msgNbrType));
        } catch (MqttException e) {
            Log.logVisu.error("Nachricht konnte nicht versendet werden: " + payload + " auf das Topic: " + TOPIC_VISU.get(msgNbrType));
            Log.logger.error("Nachricht konnte nicht versendet werden: " + payload + " auf das Topic: " + TOPIC_VISU.get(msgNbrType));
            e.printStackTrace();
        }
    }
}